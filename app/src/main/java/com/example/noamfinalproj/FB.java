package com.example.noamfinalproj;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// google explanations
// https://firebase.google.com/docs/database/android/lists-of-data#java_1


public class FB {
    private static FB instance;

    FirebaseDatabase database;
    private static Context context;

    private FB() {
        //database = FirebaseDatabase.getInstance("https://fbrecordssingletone-default-rtdb.firebaseio.com/");
        database = FirebaseDatabase.getInstance();

        DatabaseReference myRef = database.getReference("ballPosition"); // push adds new node with unique value


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange( DataSnapshot snapshot) {
                //records.clear();  // clear the array list
                FbBall fbBall = snapshot.getValue(FbBall.class);
                ((GameActivity)context).boardGame.setBallPosFromFB(fbBall);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    public static FB getInstance(Context context1) {
        if (null == instance) {
            context = context1;
            instance = new FB();
        }
        return instance;
    }

    public void setBallPosition(int x, int y)
    {
        // Write a message to the database
        DatabaseReference myRef = database.getReference("ballPosition"); // push adds new node with unique value

        //DatabaseReference myRef = database.getReference("
        // /" + FirebaseAuth.getInstance().getUid());

        FbBall fbBall = new FbBall(x,y);
        myRef.setValue(fbBall);
    }
}
