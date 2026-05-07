package com.example.noamfinalproj;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class RoleSelectionActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        Button btnP1 = findViewById(R.id.btnSelectP1);
        Button btnP2 = findViewById(R.id.btnSelectP2);

        btnP1.setOnClickListener(v -> startGame(1));
        btnP2.setOnClickListener(v -> startGame(2));
    }

    private void startGame(int playerNum) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("PLAYER_ROLE", playerNum);
        startActivity(intent);
        finish(); // סוגר את מסך הבחירה כדי שלא יחזרו אליו
    }
}