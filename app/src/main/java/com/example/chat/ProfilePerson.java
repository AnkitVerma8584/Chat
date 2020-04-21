package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfilePerson extends AppCompatActivity {
    DatabaseReference db;
    ImageView imv;
    TextView n,e,s;
    String person;
    String send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_person);
        getSupportActionBar().setTitle("Profile");
        imv=findViewById(R.id.imageView);
        n=findViewById(R.id.name);
        e=findViewById(R.id.email);
        s=findViewById(R.id.status);
        Intent intent=getIntent();
        person=intent.getStringExtra("Email");
        db= FirebaseDatabase.getInstance().getReference().child("Users").child(person);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Details d1=dataSnapshot.getValue(Details.class);
                n.setText(d1.n);
                String z=person;
                if(z.contains("!"))
                    z=z.replace('!','.');
                e.setText(z+""+d1.d);
                s.setText(d1.s);
                Glide.with(getApplicationContext()).load(d1.l).into(imv);
                send=person+""+d1.d+"&"+d1.n;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent i=new Intent(getApplicationContext(),Chats.class);
        i.putExtra("person",send);
        finish();
        startActivity(i);
    }
}
