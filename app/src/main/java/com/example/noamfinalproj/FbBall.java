package com.example.noamfinalproj;

public class FbBall {
    private int x;
    private int y;

    public FbBall() {
    }

    public FbBall(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "fbBall{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
