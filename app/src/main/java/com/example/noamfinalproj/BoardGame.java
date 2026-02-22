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

public class BoardGame extends View implements FBManager.OnMoveReceivedListener {

    private boolean isRunning = true;
    private ThreadGame threadGame;
    private Handler handler = new Handler(Looper.getMainLooper());
    private FBManager fbManager;

    // --- גרפיקה ועיצוב ---
    private Paint tableBgPaint, tableTextPaint, winPaint;
    private Drawable backgroundDrawable, ballDrawable, goalDrawable, goalkeeperDrawable;

    // --- כדור ושוער ---
    private int ballX, ballY;
    private float startX, startY, dx = 0, dy = 0;
    private final int ballRadius = 55;
    private boolean isDragging = false, isShot = false, isGoalChecked = false;
    private int goalLeft, goalRight, goalTop, goalBottom;
    private int keeperX, keeperDirection = 1, keeperJumpHeight = 0;
    private final int keeperY = 120, keeperWidth = 220, keeperHeight = 220;
    private boolean keeperJumping = false;

    // --- לוגיקת PvP ומולטיפלייר ---
    private int scoreP1 = 0, scoreP2 = 0;
    private int shotsP1 = 0, shotsP2 = 0;
    private final int TOTAL_SHOTS = 5;
    private boolean isPlayer1Turn = true;
    private String gameOverMessage = "";

    // הגדרת תפקיד: במכשיר אחד שנה ל-1, במכשיר השני ל-2 (או העבר ב-Intent)
    private int myRole = 1;

    public BoardGame(Context context) {
        super(context);
        initGraphics(context);

        // אתחול ה-Firebase עם מזהה חדר קבוע לבדיקה
        fbManager = new FBManager("room_test_1", this);

        threadGame = new ThreadGame();
        threadGame.start();
    }

    private void initGraphics(Context context) {
        tableBgPaint = new Paint();
        tableBgPaint.setColor(Color.parseColor("#AA000000")); // שחור שקוף

        tableTextPaint = new Paint();
        tableTextPaint.setColor(Color.WHITE);
        tableTextPaint.setTextSize(45);
        tableTextPaint.setFakeBoldText(true);
        tableTextPaint.setTextAlign(Paint.Align.CENTER);

        winPaint = new Paint(tableTextPaint);
        winPaint.setColor(Color.YELLOW);
        winPaint.setTextSize(100);
        winPaint.setShadowLayer(15, 0, 0, Color.BLACK);

        backgroundDrawable = context.getResources().getDrawable(R.drawable.pitch, null);
        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);
        goalkeeperDrawable = context.getResources().getDrawable(R.drawable.keeper, null);

        SharedPreferences sp = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String ballName = sp.getString("selectedBallName", "ball");
        int resID = context.getResources().getIdentifier(ballName, "drawable", context.getPackageName());
        ballDrawable = context.getResources().getDrawable(resID != 0 ? resID : R.drawable.ball, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        backgroundDrawable.draw(canvas);
        goalDrawable.setBounds(goalLeft, goalTop, goalRight, goalBottom);
        goalDrawable.draw(canvas);

        int cY = keeperY - keeperJumpHeight;
        goalkeeperDrawable.setBounds(keeperX, cY, keeperX + keeperWidth, cY + keeperHeight);
        goalkeeperDrawable.draw(canvas);

        if (gameOverMessage.isEmpty()) {
            ballDrawable.setBounds(ballX - ballRadius, ballY - ballRadius, ballX + ballRadius, ballY + ballRadius);
            ballDrawable.draw(canvas);
        }

        drawScoreTable(canvas);

        if (!gameOverMessage.isEmpty()) {
            canvas.drawText(gameOverMessage, getWidth() / 2, getHeight() / 2, winPaint);
        }
    }

    private void drawScoreTable(Canvas canvas) {
        int rectW = 600, rectH = 180;
        int rectX = (getWidth() - rectW) / 2;
        int rectY = getHeight() - 450; // מיקום מורם מעל האצבעות

        // ציור רקע הטבלה
        canvas.drawRoundRect(new RectF(rectX, rectY, rectX + rectW, rectY + rectH), 30, 30, tableBgPaint);

        // כותרות ותוצאות
        tableTextPaint.setTextSize(35);
        tableTextPaint.setColor(Color.CYAN);
        canvas.drawText("PLAYER 1", rectX + rectW/4, rectY + 60, tableTextPaint);
        tableTextPaint.setColor(Color.MAGENTA);
        canvas.drawText("PLAYER 2", rectX + 3*rectW/4, rectY + 60, tableTextPaint);

        tableTextPaint.setTextSize(65);
        tableTextPaint.setColor(Color.WHITE);
        canvas.drawText(String.valueOf(scoreP1), rectX + rectW/4, rectY + 140, tableTextPaint);
        canvas.drawText(String.valueOf(scoreP2), rectX + 3*rectW/4, rectY + 140, tableTextPaint);

        // חיווי תור
        tableTextPaint.setTextSize(30);
        tableTextPaint.setColor(Color.YELLOW);
        String turn = isPlayer1Turn ? "P1 TURN" : "P2 TURN";
        canvas.drawText("● " + turn, getWidth()/2, rectY + rectH + 40, tableTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!gameOverMessage.isEmpty()) return false;

        // הגבלה: רק השחקן שתורו יכול להזיז את הכדור
        boolean myTurn = (isPlayer1Turn && myRole == 1) || (!isPlayer1Turn && myRole == 2);
        if (!myTurn) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!isShot) { startX = event.getX(); startY = event.getY(); isDragging = true; }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isDragging) { ballX = (int) event.getX(); ballY = (int) event.getY(); invalidate(); }
                break;
            case MotionEvent.ACTION_UP:
                if (isDragging) {
                    isDragging = false; isShot = true;
                    float calcDx = (startX - event.getX()) / 10;
                    float calcDy = (startY - event.getY()) / 10;

                    // שליחה לענן
                    fbManager.sendShot(calcDx, calcDy);

                    this.dx = calcDx; this.dy = calcDy;
                    if (isPlayer1Turn) shotsP1++; else shotsP2++;
                }
                break;
        }
        return true;
    }

    @Override
    public void onOpponentMove(float dx, float dy) {
        // פונקציה זו נקראת כשה-Firebase מזהה שהיריב בעט
        if (!isShot) {
            this.dx = dx;
            this.dy = dy;
            this.isShot = true;
            if (isPlayer1Turn) shotsP1++; else shotsP2++;
            invalidate();
        }
    }

    private void checkGameStatus() {
        int rem1 = TOTAL_SHOTS - shotsP1, rem2 = TOTAL_SHOTS - shotsP2;

        if (scoreP1 > scoreP2 + rem2) gameOverMessage = "P1 WINS!";
        else if (scoreP2 > scoreP1 + rem1) gameOverMessage = "P2 WINS!";
        else if (shotsP1 == 5 && shotsP2 == 5) {
            if (scoreP1 == scoreP2) gameOverMessage = "DRAW!";
            else gameOverMessage = (scoreP1 > scoreP2) ? "P1 WINS!" : "P2 WINS!";
        } else {
            isPlayer1Turn = !isPlayer1Turn;
            resetBallToStart();
            return;
        }
        invalidate();
    }

    private void resetBallToStart() {
        ballX = getWidth() / 2;
        ballY = (int)(getHeight() * 0.55);
        dx = 0; dy = 0; isShot = false; isGoalChecked = false;
        keeperJumping = false; keeperJumpHeight = 0;
        invalidate();
    }

    private class ThreadGame extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                try { sleep(35); } catch (Exception e) {}
                handler.post(() -> {
                    if (!gameOverMessage.isEmpty()) return;

                    keeperX += keeperDirection * 12;
                    if (keeperX <= goalLeft || keeperX + keeperWidth >= goalRight) keeperDirection *= -1;

                    if (isShot) {
                        ballX += dx; ballY += dy;
                        dx *= 0.99; dy *= 0.99;

                        if (!isGoalChecked && ballY <= goalBottom) {
                            isGoalChecked = true;
                            boolean inside = ballX > goalLeft && ballX < goalRight;
                            boolean saved = ballX + ballRadius > keeperX && ballX - ballRadius < keeperX + keeperWidth && ballY - ballRadius < keeperY + keeperHeight;

                            if (saved) keeperJumping = true;
                            else if (inside) {
                                if (isPlayer1Turn) scoreP1++; else scoreP2++;
                            }
                            handler.postDelayed(() -> checkGameStatus(), 1200);
                        }
                    }
                    if (keeperJumping) {
                        if (keeperJumpHeight < 150) keeperJumpHeight += 25;
                        else { keeperJumpHeight -= 25; if (keeperJumpHeight <= 0) { keeperJumpHeight = 0; keeperJumping = false; } }
                    }
                    invalidate();
                });
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        isRunning = false;
    }
}