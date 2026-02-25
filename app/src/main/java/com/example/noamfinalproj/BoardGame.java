package com.example.noamfinalproj;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class BoardGame extends View {


    private boolean isRunning = true;
    private Paint scorePaint, infoPaint, winPaint, goalAnnouncePaint, buttonPaint, buttonTextPaint, uiBoxPaint;
    private Drawable backgroundDrawable, ballDrawable, goalDrawable, goalkeeperDrawable;

    // --- כדור ---
    private int ballX, ballY;
    private float startX, startY, dx = 0, dy = 0;
    private final int ballRadius = 55;
    private boolean isDragging = false, isShot = false, isGoalChecked = false;

    // --- שער ושוער ---
    private int goalLeft, goalRight, goalTop, goalBottom;
    private final int GOAL_HALF_WIDTH = 350;
    private int keeperX, keeperDirection = 1, keeperJumpHeight = 0;
    private final int keeperY = 150, keeperWidth = 220, keeperHeight = 220;
    private boolean keeperJumping = false;

    // --- לוגיקה ורעידה ---
    private int scoreP1 = 0, scoreP2 = 0;
    private int shotsP1 = 0, shotsP2 = 0;
    private final int TOTAL_SHOTS_PER_PLAYER = 5;
    private boolean isPlayer1Turn = true;
    private String gameOverMessage = "";
    private String feedbackText = "";
    private int shakeIntensity = 0;
    private Random random = new Random();

    private RectF restartBtn, homeBtn;

    public BoardGame(Context context) {
        super(context);
        initPaints();
        loadAssets(context);
        new Thread(this::gameLoop).start();
    }

    private void initPaints() {
        scorePaint = new Paint();
        scorePaint.setTextSize(45);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setFakeBoldText(true);

        uiBoxPaint = new Paint();
        uiBoxPaint.setColor(Color.argb(150, 0, 0, 0)); // רקע חצי שקוף לניקוד

        infoPaint = new Paint(scorePaint);
        infoPaint.setTextAlign(Paint.Align.CENTER);
        infoPaint.setTextSize(55);

        winPaint = new Paint();
        winPaint.setColor(Color.YELLOW);
        winPaint.setTextSize(100);
        winPaint.setFakeBoldText(true);
        winPaint.setTextAlign(Paint.Align.CENTER);
        winPaint.setShadowLayer(15, 0, 0, Color.BLACK);

        goalAnnouncePaint = new Paint(winPaint);
        goalAnnouncePaint.setColor(Color.GREEN);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.parseColor("#2E7D32"));
        buttonPaint.setStyle(Paint.Style.FILL);

        buttonTextPaint = new Paint(scorePaint);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void loadAssets(Context context) {
        backgroundDrawable = context.getResources().getDrawable(R.drawable.pitch, null);
        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);
        goalkeeperDrawable = context.getResources().getDrawable(R.drawable.keeper, null);

        SharedPreferences sp = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String ballName = sp.getString("selectedBallName", "ball");
        int resID = context.getResources().getIdentifier(ballName, "drawable", context.getPackageName());
        ballDrawable = context.getResources().getDrawable(resID != 0 ? resID : R.drawable.ball, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backgroundDrawable.setBounds(0, 0, w, h);
        goalLeft = (w / 2) - GOAL_HALF_WIDTH;
        goalRight = (w / 2) + GOAL_HALF_WIDTH;
        goalTop = 120;
        goalBottom = 340;
        keeperX = w / 2 - keeperWidth / 2; // איפוס מיקום שוער בהתחלה

        restartBtn = new RectF(w/2 - 250, h/2 + 50, w/2 + 250, h/2 + 170);
        homeBtn = new RectF(w/2 - 250, h/2 + 200, w/2 + 250, h/2 + 320);

        resetBallToStart();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (shakeIntensity > 0) {
            canvas.translate(random.nextInt(shakeIntensity) - shakeIntensity/2,
                    random.nextInt(shakeIntensity) - shakeIntensity/2);
            shakeIntensity -= 2;
        }

        super.onDraw(canvas);
        backgroundDrawable.draw(canvas);
        goalDrawable.setBounds(goalLeft, goalTop, goalRight, goalBottom);
        goalDrawable.draw(canvas);

        // שוער
        int cKeeperY = keeperY - keeperJumpHeight;
        goalkeeperDrawable.setBounds(keeperX, cKeeperY, keeperX + keeperWidth, cKeeperY + keeperHeight);
        goalkeeperDrawable.draw(canvas);

        // כדור
        if (gameOverMessage.isEmpty()) {
            ballDrawable.setBounds(ballX - ballRadius, ballY - ballRadius, ballX + ballRadius, ballY + ballRadius);
            ballDrawable.draw(canvas);
        }

        drawUI(canvas);
    }

    private void drawUI(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();

        // --- טבלת ניקוד מעוצבת למטה ---
        // רקע לניקוד שחקן 1
        canvas.drawRoundRect(20, h - 180, w / 2 - 10, h - 40, 20, 20, uiBoxPaint);
        // רקע לניקוד שחקן 2
        canvas.drawRoundRect(w / 2 + 10, h - 180, w - 20, h - 40, 20, 20, uiBoxPaint);

        scorePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("P1: " + scoreP1, w / 4, h - 120, scorePaint);
        canvas.drawText("Shots: " + shotsP1 + "/5", w / 4, h - 60, scorePaint);

        canvas.drawText("P2: " + scoreP2, (w / 4) * 3, h - 120, scorePaint);
        canvas.drawText("Shots: " + shotsP2 + "/5", (w / 4) * 3, h - 60, scorePaint);

        if (!gameOverMessage.isEmpty()) {
            canvas.drawARGB(200, 0, 0, 0);
            canvas.drawText(gameOverMessage, w / 2, h / 2 - 100, winPaint);

            canvas.drawRoundRect(restartBtn, 30, 30, buttonPaint);
            canvas.drawText("RESTART", restartBtn.centerX(), restartBtn.centerY() + 20, buttonTextPaint);

            Paint redBtn = new Paint(buttonPaint); redBtn.setColor(Color.RED);
            canvas.drawRoundRect(homeBtn, 30, 30, redBtn);
            canvas.drawText("EXIT", homeBtn.centerX(), homeBtn.centerY() + 20, buttonTextPaint);

        } else if (!feedbackText.isEmpty()) {
            canvas.drawText(feedbackText, w / 2, h / 2, goalAnnouncePaint);
        } else {
            String turn = isPlayer1Turn ? "PLAYER 1" : "PLAYER 2";
            canvas.drawText(turn + " TURN", w / 2, 80, infoPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        if (!gameOverMessage.isEmpty()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (restartBtn.contains(x, y)) resetGame();
                else if (homeBtn.contains(x, y)) ((android.app.Activity)getContext()).finish();
            }
            return true;
        }

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isShot && !isGoalChecked) { startX = x; startY = y; isDragging = true; }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) { ballX = (int) x; ballY = (int) y; invalidate(); }
                break;
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    isDragging = false; isShot = true;
                    dx = (startX - x) / 7; dy = (startY - y) / 7;
                    if (isPlayer1Turn) shotsP1++; else shotsP2++;
                }
                break;
        }
        return true;
    }

    private void gameLoop() {
        while (isRunning) {
            try { Thread.sleep(16); } catch (Exception e) {}

            // תנועת שוער מתוקנת - שימוש ברוחב המסך ישירות למקרה ש-goalLeft לא מוכן
            int leftLimit = (goalLeft != 0) ? goalLeft : 100;
            int rightLimit = (goalRight != 0) ? goalRight - keeperWidth : getWidth() - keeperWidth;

            keeperX += keeperDirection * 15;
            if (keeperX <= leftLimit || keeperX >= rightLimit) {
                keeperDirection *= -1;
            }

            if (isShot) {
                ballX += dx; ballY += dy;
                dx *= 0.985; dy *= 0.985;

                if (!isGoalChecked && ballY <= goalBottom) {
                    checkGoal();
                }

                if (ballY < -100 || ballX < -100 || ballX > getWidth()+100 || (Math.abs(dx) < 0.2 && Math.abs(dy) < 0.2)) {
                    isShot = false;
                    new Handler(Looper.getMainLooper()).postDelayed(this::nextTurn, 1000);
                }
            }

            if (keeperJumping) {
                keeperJumpHeight += 25;
                if (keeperJumpHeight > 120) { keeperJumping = false; keeperJumpHeight = 0; }
            }
            postInvalidate();
        }
    }

    private void checkGoal() {
        isGoalChecked = true;
        boolean inGoal = ballX > goalLeft && ballX < goalRight;
        boolean saved = (ballX + ballRadius > keeperX && ballX - ballRadius < keeperX + keeperWidth)
                && (ballY - ballRadius < keeperY + keeperHeight);

        if (saved) {
            keeperJumping = true;
            feedbackText = "SAVED!";
            goalAnnouncePaint.setColor(Color.RED);
            shakeIntensity = 15;
        } else if (inGoal) {
            feedbackText = "GOAL!!!";
            goalAnnouncePaint.setColor(Color.GREEN);
            shakeIntensity = 40;
            if (isPlayer1Turn) scoreP1++; else scoreP2++;
        } else {
            feedbackText = "MISS!";
            goalAnnouncePaint.setColor(Color.WHITE);
        }
    }

    private void nextTurn() {
        feedbackText = "";
        int remP1 = TOTAL_SHOTS_PER_PLAYER - shotsP1;
        int remP2 = TOTAL_SHOTS_PER_PLAYER - shotsP2;

        if (scoreP1 > scoreP2 + remP2) gameOverMessage = "PLAYER 1 WINS!";
        else if (scoreP2 > scoreP1 + remP1) gameOverMessage = "PLAYER 2 WINS!";
        else if (shotsP1 == 5 && shotsP2 == 5) {
            if (scoreP1 == scoreP2) gameOverMessage = "DRAW!";
            else gameOverMessage = (scoreP1 > scoreP2) ? "PLAYER 1 WINS!" : "PLAYER 2 WINS!";
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            resetBallToStart();
        }
    }

    private void resetGame() {
        scoreP1 = 0; scoreP2 = 0; shotsP1 = 0; shotsP2 = 0;
        isPlayer1Turn = true; gameOverMessage = ""; feedbackText = "";
        resetBallToStart();
    }

    private void resetBallToStart() {
        ballX = getWidth() / 2;
        ballY = (int) (getHeight() * 0.65); // הכדור קצת מעל הניקוד
        dx = 0; dy = 0; isShot = false; isGoalChecked = false;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }
}