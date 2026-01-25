
package com.example.noamfinalproj;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;

public class BoardGame extends View {
    private float ballX = 500; // מיקום הכדור בהתחלה
    private float ballY = 1000;
    private Drawable ballDrawable;
    private Drawable goalDrawable;
    private float dx = 0, dy = 0;

    public BoardGame(Context context) {
        super(context);
        // אתחול האייקונים (כדור ושער)
        ballDrawable = context.getResources().getDrawable(R.drawable.ball, null);
        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // ציור השער
        goalDrawable.setBounds(300, 50, 700, 200); // מיקום השער
        goalDrawable.draw(canvas);

        // ציור הכדור
        ballDrawable.setBounds((int)ballX - 50, (int)ballY - 50, (int)ballX + 50, (int)ballY + 50);
        ballDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // זיהוי של כל סוגי המגע
        int action = event.getAction();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                // הכדור מתחיל לזוז כשהשחקן לוחץ על המסך
                dx = event.getX();
                dy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                // נעדכן את מיקום הכדור לפי מיקום המגע
                ballX = event.getX();
                ballY = event.getY();
                invalidate(); // מאתחל את הציור מחדש
                break;
            case MotionEvent.ACTION_UP:
                // אפשר לבצע פעולה כאשר השחקן משחרר את המגע (כמו זריקת הכדור)
                break;
            default:
                return false;
        }
        return true;
    }
}
