package com.example.noamfinalproj;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FBManager {
    private DatabaseReference gameRef;
    private OnMoveReceivedListener listener;
    private long lastTimestamp = 0;

    public interface OnMoveReceivedListener {
        void onOpponentMove(float dx, float dy);
    }

    public FBManager(String roomId, OnMoveReceivedListener listener) {
        this.listener = listener;
        this.gameRef = FirebaseDatabase.getInstance().getReference("Games").child(roomId);
        listenForMoves();
    }

    public void sendShot(float dx, float dy) {
        long time = System.currentTimeMillis();
        lastTimestamp = time;
        gameRef.child("lastShot").child("dx").setValue(dx);
        gameRef.child("lastShot").child("dy").setValue(dy);
        gameRef.child("lastShot").child("timestamp").setValue(time);
    }

    private void listenForMoves() {
        gameRef.child("lastShot").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.hasChild("timestamp")) {
                    long time = snapshot.child("timestamp").getValue(Long.class);
                    if (time > lastTimestamp) {
                        lastTimestamp = time;
                        float dx = snapshot.child("dx").getValue(Float.class);
                        float dy = snapshot.child("dy").getValue(Float.class);
                        if (listener != null) {
                            listener.onOpponentMove(dx, dy);
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }
}