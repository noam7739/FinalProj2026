package com.example.noamfinalproj;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button btnStart = findViewById(R.id.btnStart);
        Button btnInstructions = findViewById(R.id.btnInstructions);
        Button btnLocker = findViewById(R.id.btnLocker);


        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "Game Starting...", Toast.LENGTH_SHORT).show();


            }
        });


        btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "How to Play: Swipe to Kick!", Toast.LENGTH_LONG).show();
            }
        });


        btnLocker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Opening Locker...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}