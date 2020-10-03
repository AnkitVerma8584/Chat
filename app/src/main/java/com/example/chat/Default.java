package com.example.chat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Timer;
import java.util.TimerTask;

public class Default extends AppCompatActivity {

    Timer timer;
    FirebaseAuth auth;
    DatabaseReference db;
    String a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
        getSupportActionBar().hide();
        try {
            auth = FirebaseAuth.getInstance();
            a = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
            if (a.contains("."))
                a = a.replace('.', '!');
            db = FirebaseDatabase.getInstance().getReference().child("Users").child(a);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(0xFF212D94);
            }
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Details d = dataSnapshot.getValue(Details.class);
                    if (d.soundEffect == 1) {
                        MediaPlayer ring = MediaPlayer.create(getApplicationContext(), R.raw.home);
                        ring.start();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Intent i = new Intent(getApplicationContext(), Home.class);
                startActivity(i);
                Default.this.finish();
            }
        }, 2000);
    }
}
