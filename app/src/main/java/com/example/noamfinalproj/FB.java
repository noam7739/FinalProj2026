package com.example.noamfinalproj;

import androidx.annotation.NonNull;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FB {
    private static FB instance;
    private final FirebaseDatabase database;
    private final DatabaseReference ballRef, scoreRef, keeperRef;

    private FB() {
        database = FirebaseDatabase.getInstance();
        ballRef = database.getReference("ballPosition");
        scoreRef = database.getReference("gameScore");
        keeperRef = database.getReference("keeperX");
    }

    public static synchronized FB getInstance() {
        if (instance == null) instance = new FB();
        return instance;
    }

    public void setBallPosition(int x, int y) { ballRef.setValue(new FbBall(x, y)); }
    public void updateScore(int p1, int p2) {
        scoreRef.child("scoreP1").setValue(p1);
        scoreRef.child("scoreP2").setValue(p2);
    }
    public void setKeeperPosition(int x) { keeperRef.setValue(x); }

    public void listenToGame(BoardGame boardGame) {
        ballRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                FbBall b = s.getValue(FbBall.class);
                if (b != null) boardGame.setBallPosFromFB(b);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        scoreRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                if (s.exists()) {
                    Integer p1 = s.child("scoreP1").getValue(Integer.class);
                    Integer p2 = s.child("scoreP2").getValue(Integer.class);
                    if (p1 != null && p2 != null) boardGame.updateScoreFromFB(p1, p2);
                }
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });

        keeperRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot s) {
                Integer kX = s.getValue(Integer.class);
                if (kX != null) boardGame.updateKeeperFromFB(kX);
            }
            @Override public void onCancelled(@NonNull DatabaseError e) {}
        });
    }
}