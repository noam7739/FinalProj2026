package com.example.noamfinalproj;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LockerActivity extends AppCompatActivity {

    private ImageView imgBallPreview;
    private String currentSelectedBall = "ball_blue"; // שם ברירת המחדל של ה-drawable
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker);


        ImageButton btnBlue = findViewById(R.id.btnSelectBlue);
        ImageButton btnRed = findViewById(R.id.btnSelectRed);
        ImageButton btnYellow = findViewById(R.id.btnSelectYellow);
        Button btnSave = findViewById(R.id.btnSaveLocker);

        sp = getSharedPreferences("GamePrefs", MODE_PRIVATE);

        // טעינת הבחירה הקודמת מהזיכרון
        currentSelectedBall = sp.getString("selectedBallName", "ball_blue");
        updatePreview(currentSelectedBall);

        // לחיצה על כדורים להחלפת תצוגה מקדימה
        btnBlue.setOnClickListener(v -> updatePreview("ball_blue"));
        btnRed.setOnClickListener(v -> updatePreview("ball_red"));
        btnYellow.setOnClickListener(v -> updatePreview("ball_yellow"));

        // שמירה ויציאה
        btnSave.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("selectedBallName", currentSelectedBall);
            editor.apply();

            Toast.makeText(this, "הכדור נבחר בהצלחה!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updatePreview(String imageName) {
        currentSelectedBall = imageName;
        // הפיכת מחרוזת (String) למזהה תמונה (Resource ID)
        int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
        imgBallPreview.setImageResource(resID);
    }
}
