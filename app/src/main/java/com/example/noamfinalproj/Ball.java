package com.example.noamfinalproj;

import android.graphics.drawable.Drawable;

public class Ball extends GameObject {
    private float dx = 0, dy = 0;
    private final float ballRadius = 55;

    public Ball(float x, float y, Drawable image) {
        super(x, y, 110, 110, image);
    }

    public void setVelocity(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public void update() {
        x += dx;
        y += dy;
        dx *= 0.985f; // חיכוך להאטה
        dy *= 0.985f;
    }

    public boolean isStopped() {
        // אם המהירות נמוכה מאוד, הכדור נחשב כעצור
        return Math.abs(dx) < 0.2 && Math.abs(dy) < 0.2;
    }

    public float getRadius() {
        return ballRadius;
    }
}