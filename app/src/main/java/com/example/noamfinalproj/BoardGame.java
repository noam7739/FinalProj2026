
package com.example.noamfinalproj;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

public class BoardGame extends View {
    private int ballX = 500; // מיקום הכדור בהתחלה
    private int ballY = 1000;
    private Drawable ballDrawable;
    private Drawable goalDrawable;

    private float x = 0, y = 0;
    private Ball ball;

    public BoardGame(Context context) {
        super(context);
        // 1. גישה לקובץ ההגדרות (חייב להיות אותו שם כמו בלוקר)
        SharedPreferences sp = context.getSharedPreferences("GamePrefs", Context.MODE_PRIVATE);

        // 2. שליפת שם הכדור. אם המשתמש לא בחר, "ball" יהיה ברירת המחדל
        String ballName = sp.getString("selectedBallName", "ball");

        // 3. מציאת ה-ID של התמונה לפי השם שלה (בתיקיית drawable)
        int resID = context.getResources().getIdentifier(ballName, "drawable", context.getPackageName());

        // 4. בדיקת הגנה: אם ה-ID לא נמצא (resID == 0), נשתמש בכדור ברירת המחדל
        if (resID == 0) {
            resID = R.drawable.ball; // וודא שיש לך קובץ בשם ball בתיקיית ה-drawable
        }

        // 5. השורה החשובה: יצירת ה-Drawable והשמתו במשתנה של המחלקה
        // הערה: בגרסאות אנדרואיד חדשות משתמשים ב-null עבור ה-Theme
        this.ballDrawable = context.getResources().getDrawable(resID, null);



        goalDrawable = context.getResources().getDrawable(R.drawable.goal, null);
        ball = new Ball(100,100);
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
                x = event.getX();
                y = event.getY();

                break;
            case MotionEvent.ACTION_MOVE:
                // נעדכן את מיקום הכדור לפי מיקום המגע
                ballX = (int) event.getX();
                ballY = (int) event.getY();
                ball.setXY(ballX,ballY);
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
