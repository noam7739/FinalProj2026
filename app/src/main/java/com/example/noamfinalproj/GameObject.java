package com.example.noamfinalproj;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public abstract class GameObject {
    protected float x, y;
    protected int width, height;
    protected Drawable image;

    public GameObject(float x, float y, int width, int height, Drawable image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public void draw(Canvas canvas) {
        if (image != null) {
            // הציור מתבצע סביב מרכז האובייקט
            image.setBounds((int)x - width/2, (int)y - height/2, (int)x + width/2, (int)y + height/2);
            image.draw(canvas);
        }
    }

    public float getX() { return x; }
    public float getY() { return y; }
    public void setPosition(float x, float y) { this.x = x; this.y = y; }
}