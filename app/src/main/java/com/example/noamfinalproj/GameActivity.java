package com.example.noamfinalproj;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    BoardGame boardGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // קבלת התפקיד (ברירת מחדל 1 למקרה של תקלה)
        int playerRole = getIntent().getIntExtra("PLAYER_ROLE", 1);

        boardGame = new BoardGame(this, playerRole);
        setContentView(boardGame);
    }
}