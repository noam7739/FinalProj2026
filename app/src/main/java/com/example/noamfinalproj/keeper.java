package com.example.noamfinalproj;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class keeper extends GameObject {
    private int direction = 1;
    private int jumpHeight = 0;
    private boolean isJumping = false;
    private int speed = 15;

    public keeper(float x, float y, int width, int height, Drawable image) {
        super(x, y, width, height, image);
    }

    public void update(int leftLimit, int rightLimit) {
        x += direction * speed;
        if (x <= leftLimit + width/2 || x >= rightLimit - width/2) direction *= -1;

        if (isJumping) {
            jumpHeight += 25;
            if (jumpHeight > 120) {
                isJumping = false;
                jumpHeight = 0;
            }
        }
    }

    public void jump() { isJumping = true; }

    @Override
    public void draw(Canvas canvas) {
        float originalY = y;
        y -= jumpHeight;
        super.draw(canvas);
        y = originalY;
    }
}
