package com.example.noamfinalproj;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;

public class BoardGame extends View {

    private int ballX, ballY;
    private float startX, startY;
    private float dx = 0, dy = 0;

    private boolean isDragging = false;
    private boolean isRunning = true;
    private boolean isShot = false;       // האם הכדור בעיטה
    private boolean isGoalChecked = false;

    private Drawable ballDrawable;
    private Drawable goalDrawable;
    private Drawable goalkeeperDrawable;

    // שוער
    private int keeperX;
    private final int keeperY = 120;    // גובה הבסיס של השוער
    private final int keeperWidth = 200;
    private final int keeperHeight = 200;
    private int keeperDirection = 1; // 1 ימינה, -1 שמאלה
    private boolean keeperJumping = false; // האם השוער בקפיצה
    private int keeperJumpHeight = 0;      // גובה הקפיצה

    private int score = 0;

    private ThreadGame threadGame;
    private Handler handler = new Handler(Looper.getMainLooper());

    public BoardGame(Context context) {
        super(context);

        // מיקום התחלתי של הכדור
        ballX = 500;
        ballY = 500;

        // טעינת כדור
        SharedPreferences sp = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);
        String ballName = sp.getString("selectedBallName", "ball");
        int resID = context.getResources().getIdentifier(ballName, "drawable", context.getPackageName());
        if (resID == 0) resID = R.drawable.ball;
        ballDrawable = context.getResources().getDrawable(resID, null);

        // טעינת שער
        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);

        // טעינת שוער
        goalkeeperDrawable = context.getResources().getDrawable(R.drawable.keeper, null);

        // מיקום התחלתי של השוער
        keeperX = 300;

        threadGame = new ThreadGame();
        threadGame.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // ציור שער
        goalDrawable.setBounds(300, 50, 700, 200);
        goalDrawable.draw(canvas);

        // ציור שוער עם קפיצה
        int currentKeeperY = keeperY - keeperJumpHeight;
        goalkeeperDrawable.setBounds(
                keeperX,
                currentKeeperY,
                keeperX + keeperWidth,
                currentKeeperY + keeperHeight
        );
        goalkeeperDrawable.draw(canvas);

        // ציור כדור
        ballDrawable.setBounds(ballX - 50, ballY - 50,
                ballX + 50, ballY + 50);
        ballDrawable.draw(canvas);

        // ציור ניקוד
        Paint paint = new Paint();
        paint.setTextSize(70);
        canvas.drawText("Score: " + score, 50, 80, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (!isShot) {
                    startX = event.getX();
                    startY = event.getY();
                    isDragging = true;
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (isDragging && !isShot) {
                    ballX = (int) event.getX();
                    ballY = (int) event.getY();
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (isDragging && !isShot) {
                    isDragging = false;
                    dx = (startX - event.getX()) / 15;
                    dy = (startY - event.getY()) / 15;
                    isShot = true;
                }
                return true;
        }
        return true;
    }

    // ================= RESET BALL =================
    private void resetBallToStart() {
        ballX = getWidth() / 2;
        ballY = getHeight() / 2; // עכשיו במרכז המסך
        dx = 0;
        dy = 0;
        isShot = false;
        isGoalChecked = false;
        keeperJumping = false;
        keeperJumpHeight = 0;
    }

    // ================= THREAD =================
    private class ThreadGame extends Thread {
        @Override
        public void run() {
            while (isRunning) {
                try {
                    sleep(40); // ~25 FPS
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                handler.post(() -> {

                    // ===== תזוזת שוער =====
                    keeperX += keeperDirection * 10;
                    if (keeperX <= 300 || keeperX + keeperWidth >= 700) {
                        keeperDirection *= -1;
                    }



                    // ===== תנועת הכדור =====
                    if (isShot) {
                        ballX += dx;
                        ballY += dy;

                        dx *= 0.98;
                        dy *= 0.98;

                        // בדיקת גול
                        if (!isGoalChecked && ballY <= 200) {
                            isGoalChecked = true;

                            boolean insideGoal = ballX > 300 && ballX < 700;

                            boolean saved = ballX + 50 > keeperX &&
                                    ballX - 50 < keeperX + keeperWidth &&
                                    ballY - 50 < keeperY + keeperHeight;

                            // אם השוער צריך לקפוץ
                            if (saved) keeperJumping = true;

                            // אם הכדור נכנס ולא נעצר → ניקוד
                            if (insideGoal && !saved) score++;

                            // השהייה של שנייה לפני ריסט – התיקון כאן:
                            handler.postDelayed(() -> BoardGame.this.resetBallToStart(), 1000);
                        }

                        // אם הכדור יצא מהמסך
                        if (ballY <= 0 || ballX <= 0 || ballX >= getWidth()) {
                            resetBallToStart();
                        }
                    }

                    // ===== אנימציית קפיצה לשוער =====
                    if (keeperJumping) {
                        if (keeperJumpHeight < 150) keeperJumpHeight += 20;
                        else {
                            keeperJumpHeight -= 20;
                            if (keeperJumpHeight <= 0) {
                                keeperJumpHeight = 0;
                                keeperJumping = false;
                            }
                        }
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
