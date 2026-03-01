package com.example.noamfinalproj;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class player extends GameObject {
    private Drawable idleDrawable, kickDrawable;
    private boolean isKicking = false;
    private float rotationAngle = 0;
    private long kickStartTime = 0;
    private final long KICK_DURATION = 400;

    public player(Context context) {
        // אתחול עם ערכי 0, ה-BoardGame יעדכן מיקום בכל פריים
        super(0, 0, 350, 450, null);
        idleDrawable = context.getResources().getDrawable(R.drawable.player_idle, null);
        kickDrawable = context.getResources().getDrawable(R.drawable.player_kick, null);
        this.image = idleDrawable;
    }

    // עדכון מיקום השחקן שיהיה תמיד משמאל לכדור
    public void updateState(float ballX, float ballY, float dragX) {
        this.x = ballX - 80; // 80 פיקסלים משמאל למרכז הכדור
        this.y = ballY + 100; // מעט מתחת לכדור כדי שהרגל תפגע

        // חישוב זווית סיבוב לפי המשיכה
        rotationAngle = Math.max(-15, Math.min(15, -dragX / 40));
    }

    public void startKick() {
        isKicking = true;
        kickStartTime = System.currentTimeMillis();
    }

    @Override
    public void draw(Canvas canvas) {
        // בדיקה אם זמן האנימציה של הבעיטה נגמר
        if (isKicking && (System.currentTimeMillis() - kickStartTime > KICK_DURATION)) {
            isKicking = false;
            rotationAngle = 0;
        }

        // בחירת התמונה הנכונה (עומד או בועט)
        image = isKicking ? kickDrawable : idleDrawable;

        canvas.save();
        canvas.rotate(rotationAngle, x, y);
        super.draw(canvas); // קורא לציור הבסיסי ב-GameObject
        canvas.restore();
    }
}