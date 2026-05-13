package com.example.noamfinalproj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import java.util.Random;

public class BoardGame extends View {
    private boolean isRunning = true;

    private Paint scorePaint, infoPaint, winPaint, goalAnnouncePaint, buttonPaint, buttonTextPaint, uiBoxPaint, linePaint;
    private Drawable backgroundDrawable, goalDrawable;
    private Player player;
    private Ball ball;
    private Keeper keeper;

    private float startX, startY;
    private boolean isDragging = false;
    private boolean isShot = false;
    private boolean isGoalChecked = false;
    private int goalLeft, goalRight, goalTop, goalBottom;
    private int scoreP1 = 0;
    private int scoreP2 = 0;
    private int shotsP1 = 0;
    private int shotsP2 = 0;
    private final int TOTAL_SHOTS_PER_PLAYER = 5;
    private boolean isPlayer1Turn = true;
    private int myRole;
    private String gameOverMessage = "";
    private String feedbackText = "";
    private int shakeIntensity = 0;
    private final Random random = new Random();
    private RectF restartBtn, homeBtn;

    public BoardGame(Context context, int role) {
        super(context);
        this.myRole = role;

        // איפוס תוצאות בבסיס הנתונים בתחילת המשחק (רק על ידי שחקן 1)
        if (myRole == 1) {
            FB.getInstance().updateScore(0, 0);
        }

        initPaints();
        loadAssets(context);
        FB.getInstance().listenToGame(this);
        
        // יצירת תהליך נפרד ללולאת המשחק
        Thread gameThread = new Thread(new Runnable() {
            @Override
            public void run() {
                gameLoop();
            }
        });
        gameThread.start();
    }

    private void initPaints() {
        scorePaint = new Paint();
        scorePaint.setTextSize(45);
        scorePaint.setColor(Color.WHITE);
        scorePaint.setFakeBoldText(true);

        uiBoxPaint = new Paint();
        uiBoxPaint.setColor(Color.argb(150, 0, 0, 0));

        infoPaint = new Paint(scorePaint);
        infoPaint.setTextAlign(Paint.Align.CENTER);
        infoPaint.setTextSize(55);

        winPaint = new Paint();
        winPaint.setColor(Color.YELLOW);
        winPaint.setTextSize(100);
        winPaint.setFakeBoldText(true);
        winPaint.setTextAlign(Paint.Align.CENTER);

        goalAnnouncePaint = new Paint(winPaint);
        goalAnnouncePaint.setColor(Color.GREEN);

        buttonPaint = new Paint();
        buttonPaint.setColor(Color.parseColor("#2E7D32"));

        buttonTextPaint = new Paint(scorePaint);
        buttonTextPaint.setTextAlign(Paint.Align.CENTER);

        linePaint = new Paint();
        linePaint.setColor(Color.WHITE);
        linePaint.setStrokeWidth(8);
        linePaint.setStyle(Paint.Style.STROKE);
        float[] intervals = new float[]{20, 20};
        linePaint.setPathEffect(new DashPathEffect(intervals, 0));
    }

    private void loadAssets(Context context) {
        backgroundDrawable = context.getResources().getDrawable(R.drawable.pitch, null);
        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);
        player = new Player(context);
        ball = new Ball(0, 0, context.getResources().getDrawable(R.drawable.ball, null));
        keeper = new Keeper(0, 150, 220, 220, context.getResources().getDrawable(R.drawable.keeper, null));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        backgroundDrawable.setBounds(0, 0, w, h);
        
        goalLeft = (w / 2) - 350;
        goalRight = (w / 2) + 350;
        goalTop = 120;
        goalBottom = 340;
        
        keeper.setPosition(w / 2f, 250);
        
        restartBtn = new RectF(w / 2f - 250, h / 2f + 50, w / 2f + 250, h / 2f + 170);
        homeBtn = new RectF(w / 2f - 250, h / 2f + 200, w / 2f + 250, h / 2f + 320);
        
        resetBallToStart();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // אפקט רעידת מסך
        if (shakeIntensity > 0) {
            float dx = random.nextInt(shakeIntensity) - (shakeIntensity / 2f);
            float dy = random.nextInt(shakeIntensity) - (shakeIntensity / 2f);
            canvas.translate(dx, dy);
            shakeIntensity = shakeIntensity - 2;
        }
        
        super.onDraw(canvas);
        
        backgroundDrawable.draw(canvas);
        goalDrawable.setBounds(goalLeft, goalTop, goalRight, goalBottom);
        goalDrawable.draw(canvas);
        
        keeper.draw(canvas);

        if (gameOverMessage.equals("")) {
            if (isDragging == true) {
                float targetX = ball.getX() + (startX - ball.getX()) * 2;
                float targetY = ball.getY() + (startY - ball.getY()) * 2;
                canvas.drawLine(ball.getX(), ball.getY(), targetX, targetY, linePaint);
            }
            player.draw(canvas);
            ball.draw(canvas);
        }
        
        drawUI(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // בדיקה האם זה התור של השחקן הנוכחי
        boolean isMyTurn;
        if (isPlayer1Turn == true && myRole == 1) {
            isMyTurn = true;
        } else if (isPlayer1Turn == false && myRole == 2) {
            isMyTurn = true;
        } else {
            isMyTurn = false;
        }

        // טיפול במסך סיום משחק
        if (gameOverMessage.equals("") == false) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (restartBtn.contains(event.getX(), event.getY())) {
                    resetGame();
                } else if (homeBtn.contains(event.getX(), event.getY())) {
                    if (getContext() instanceof android.app.Activity) {
                        ((android.app.Activity) getContext()).finish();
                    }
                }
            }
            return true;
        }

        // אם זה לא התור שלי, אני לא יכול לגעת בכדור
        if (isMyTurn == false) {
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            if (isShot == false && isGoalChecked == false) {
                startX = event.getX();
                startY = event.getY();
                isDragging = true;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (isDragging == true) {
                ball.setPosition(event.getX(), event.getY());
                float playerOffset = event.getX() - startX;
                player.updateState(event.getX(), event.getY(), playerOffset);
                invalidate();
            }
        } else if (action == MotionEvent.ACTION_UP) {
            if (isDragging == true) {
                isDragging = false;
                isShot = true;
                player.startKick();
                
                float velocityX = (startX - event.getX()) / 7.0f;
                float velocityY = (startY - event.getY()) / 7.0f;
                ball.setVelocity(velocityX, velocityY);
                
                if (isPlayer1Turn == true) {
                    shotsP1 = shotsP1 + 1;
                } else {
                    shotsP2 = shotsP2 + 1;
                }
            }
        }
        return true;
    }

    private void gameLoop() {
        while (isRunning == true) {
            try {
                Thread.sleep(16);
            } catch (Exception e) {
                // התעלמות משגיאות בהמתנה
            }
            
            // עדכון מיקום השוער בשרת (רק על ידי שחקן 1)
            if (myRole == 1) {
                keeper.update(goalLeft, goalRight);
                FB.getInstance().setKeeperPosition((int) keeper.getX());
            }
            
            // עדכון תנועת הכדור
            if (isShot == true) {
                ball.update();
                FB.getInstance().setBallPosition((int) ball.getX(), (int) ball.getY());
                
                // בדיקת שער
                if (isGoalChecked == false) {
                    if (ball.getY() <= goalBottom) {
                        checkGoal();
                    }
                }
                
                // בדיקה אם הכדור יצא מהמסך או עצר
                if (ball.getY() < -100 || ball.isStopped() == true) {
                    isShot = false;
                    // מעבר לתור הבא אחרי שנייה
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            nextTurn();
                        }
                    }, 1000);
                }
            }
            postInvalidate();
        }
    }

    public void updateKeeperFromFB(int x) {
        if (myRole == 2) {
            keeper.setPosition(x, keeper.getY());
        }
    }

    public void updateScoreFromFB(int p1, int p2) {
        this.scoreP1 = p1;
        this.scoreP2 = p2;
    }

    public void setBallPosFromFB(FbBall fb) {
        if (isShot == false && isDragging == false) {
            ball.setPosition(fb.getX(), fb.getY());
        }
    }

    private void checkGoal() {
        isGoalChecked = true;
        
        boolean inGoal;
        if (ball.getX() > goalLeft && ball.getX() < goalRight) {
            inGoal = true;
        } else {
            inGoal = false;
        }
        
        boolean saved;
        float distanceToKeeper = Math.abs(ball.getX() - keeper.getX());
        if (distanceToKeeper < 110 && ball.getY() < keeper.getY() + 110) {
            saved = true;
        } else {
            saved = false;
        }

        if (saved == true) {
            keeper.jump();
            feedbackText = "SAVED!";
            goalAnnouncePaint.setColor(Color.RED);
        } else if (inGoal == true) {
            feedbackText = "GOAL!!!";
            goalAnnouncePaint.setColor(Color.GREEN);
            if (isPlayer1Turn == true) {
                scoreP1 = scoreP1 + 1;
            } else {
                scoreP2 = scoreP2 + 1;
            }
            FB.getInstance().updateScore(scoreP1, scoreP2);
        } else {
            feedbackText = "MISS!";
        }
    }

    private void nextTurn() {
        feedbackText = ""; 
        if (isPlayer1Turn == true) {
            isPlayer1Turn = false;
        } else {
            isPlayer1Turn = true;
        }
        isGoalChecked = false;
        resetBallToStart();
        checkGameOver();
    }

    private void checkGameOver() {
        if (shotsP1 == 5 && shotsP2 == 5) {
            if (scoreP1 > scoreP2) {
                gameOverMessage = "P1 WINS!";
            } else if (scoreP2 > scoreP1) {
                gameOverMessage = "P2 WINS!";
            } else {
                gameOverMessage = "DRAW!";
            }
        }
    }

    private void resetGame() {
        scoreP1 = 0;
        scoreP2 = 0;
        shotsP1 = 0;
        shotsP2 = 0;
        FB.getInstance().updateScore(0, 0);
        isPlayer1Turn = true;
        gameOverMessage = "";
        feedbackText = "";
        resetBallToStart();
    }

    private void resetBallToStart() {
        float startXPos = getWidth() / 2f;
        float startYPos = getHeight() * 0.55f;
        ball.setPosition(startXPos, startYPos);
        ball.setVelocity(0, 0);
        player.updateState(ball.getX(), ball.getY(), 0);
        isShot = false;
    }

    private void drawUI(Canvas canvas) {
        int h = getHeight();
        int w = getWidth();
        
        // ציור תיבות המידע
        canvas.drawRoundRect(20, h - 180, w / 2f - 10, h - 40, 20, 20, uiBoxPaint);
        canvas.drawRoundRect(w / 2f + 10, h - 180, w - 20, h - 40, 20, 20, uiBoxPaint);
        
        scorePaint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("P1: " + scoreP1, w / 4f, h - 120, scorePaint);
        canvas.drawText("Shots: " + shotsP1 + "/5", w / 4f, h - 60, scorePaint);
        
        canvas.drawText("P2: " + scoreP2, (w / 4f) * 3, h - 120, scorePaint);
        canvas.drawText("Shots: " + shotsP2 + "/5", (w / 4f) * 3, h - 60, scorePaint);

        if (gameOverMessage.equals("") == false) {
            // מסך סיום משחק
            canvas.drawARGB(200, 0, 0, 0);
            canvas.drawText(gameOverMessage, w / 2f, h / 2f - 100, winPaint);
            
            canvas.drawRoundRect(restartBtn, 30, 30, buttonPaint);
            canvas.drawText("RESTART", restartBtn.centerX(), restartBtn.centerY() + 20, buttonTextPaint);
            
            Paint redBtn = new Paint(buttonPaint);
            redBtn.setColor(Color.RED);
            canvas.drawRoundRect(homeBtn, 30, 30, redBtn);
            canvas.drawText("EXIT", homeBtn.centerX(), homeBtn.centerY() + 20, buttonTextPaint);
            
        } else if (feedbackText.equals("") == false) {
            // הצגת הודעת גול או החמצה
            canvas.drawText(feedbackText, w / 2f, h / 2f, goalAnnouncePaint);
        } else {
            // הצגת תור השחקן
            String turnText;
            if (isPlayer1Turn == true) {
                turnText = "PLAYER 1 TURN";
            } else {
                turnText = "PLAYER 2 TURN";
            }
            
            boolean isMyTurn;
            if (isPlayer1Turn == true && myRole == 1) {
                isMyTurn = true;
            } else if (isPlayer1Turn == false && myRole == 2) {
                isMyTurn = true;
            } else {
                isMyTurn = false;
            }
            
            String youText = "";
            if (isMyTurn == true) {
                youText = " (YOU)";
            }
            canvas.drawText(turnText + youText, w / 2f, 80, infoPaint);
        }
    }
}