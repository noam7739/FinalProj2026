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
    private String currentSelectedBall = "ball_blue";
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
        currentSelectedBall = sp.getString("selectedBallName", "ball_blue");
        updatePreview(currentSelectedBall);

        btnBlue.setOnClickListener(v -> updatePreview("ball_blue"));
        btnRed.setOnClickListener(v -> updatePreview("ball_red"));
        btnYellow.setOnClickListener(v -> updatePreview("ball_yellow"));

        btnSave.setOnClickListener(v -> {
            sp.edit().putString("selectedBallName", currentSelectedBall).apply();
            Toast.makeText(this, "הכדור נבחר בהצלחה!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void updatePreview(String imageName) {
        currentSelectedBall = imageName;
        int resID = getResources().getIdentifier(imageName, "drawable", getPackageName());
        if (imgBallPreview != null && resID != 0) {
            imgBallPreview.setImageResource(resID);
        }
    }
}