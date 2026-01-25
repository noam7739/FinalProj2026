package com.example.noamfinalproj;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Ball {
    private int x,y;

    public Ball(int y, int x) {
        this.y = y;
        this.x = x;
    }

    public int getX() {
        return x;
    }
    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setXY(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move() {
        x = x+20;
        y=y+20;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawCircle(x,y,60,new Paint());
    }
}

