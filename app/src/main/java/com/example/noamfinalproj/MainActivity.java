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




        btnInstructions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
            startActivity(intent);
        });

        btnInstructions.setOnClickListener(new View.OnClickListener() {
                                               @Override
                                               public void onClick(View v) {
                                                   Intent intent = new Intent(MainActivity.this, InstructionActivity.class);
                                                   startActivity(intent);
                                               }
                                           });
        btnStart.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GameActivity.class);
            startActivity(intent);
        });
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });


        btnLocker.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, LockerActivity.class);
            startActivity(intent);


            btnLocker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, LockerActivity.class);
                    startActivity(intent);
                }
            });
              });
}
    }

