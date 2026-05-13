package com.example.noamfinalproj;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class handles all communication with Firebase Realtime Database.
 * It is written in simple Java to make it easy to follow.
 */
public class FB {
    // This variable holds the single instance of this class (Singleton)
    private static FB instance;
    
    // Firebase database objects
    private FirebaseDatabase database;
    private DatabaseReference ballRef;
    private DatabaseReference scoreRef;
    private DatabaseReference keeperRef;

    // The constructor is private so only this class can create it
    private FB() {
        database = FirebaseDatabase.getInstance();
        
        // Point to specific locations in the database
        ballRef = database.getReference("ballPosition");
        scoreRef = database.getReference("gameScore");
        keeperRef = database.getReference("keeperX");
    }

    // Static method to get the instance of this class
    public static synchronized FB getInstance() {
        if (instance == null) {
            instance = new FB();
        }
        return instance;
    }

    // Sends the ball's X and Y coordinates to the database
    public void setBallPosition(int x, int y) {
        FbBall ballData = new FbBall(x, y);
        ballRef.setValue(ballData);
    }

    // Updates the scores for both players in the database
    public void updateScore(int p1, int p2) {
        scoreRef.child("scoreP1").setValue(p1);
        scoreRef.child("scoreP2").setValue(p2);
    }

    // Sends the keeper's X position to the database
    public void setKeeperPosition(int x) {
        keeperRef.setValue(x);
    }

    /**
     * This method tells Firebase to notify the BoardGame object 
     * whenever data in the database changes.
     */
    public void listenToGame(final BoardGame boardGame) {
        
        // 1. Listen for Ball position changes
        ballRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FbBall ballFromDb = snapshot.getValue(FbBall.class);
                if (ballFromDb != null) {
                    boardGame.setBallPosFromFB(ballFromDb);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        // 2. Listen for Score changes
        scoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Integer p1Score = snapshot.child("scoreP1").getValue(Integer.class);
                    Integer p2Score = snapshot.child("scoreP2").getValue(Integer.class);
                    
                    if (p1Score != null && p2Score != null) {
                        boardGame.updateScoreFromFB(p1Score, p2Score);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });

        // 3. Listen for Keeper position changes
        keeperRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer keeperX = snapshot.getValue(Integer.class);
                if (keeperX != null) {
                    boardGame.updateKeeperFromFB(keeperX);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error if needed
            }
        });
    }
}
