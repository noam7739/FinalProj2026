package com.example.noamfinalproj;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnStart = findViewById(R.id.btnStart);
        Button btnInstructions = findViewById(R.id.btnInstructions);
        Button btnLocker = findViewById(R.id.btnLocker);

        // מעבר למסך בחירת תפקיד לפני המשחק
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, RoleSelectionActivity.class));
        });

        btnInstructions.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, InstructionActivity.class));
        });

        btnLocker.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LockerActivity.class));
        });
    }
}