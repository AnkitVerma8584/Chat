package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChatableSettings extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference db;
    Switch s1,s2;
    String d,l,n,s;
    int i;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatable_settings);
        s1=findViewById(R.id.switch1);
        s2=findViewById(R.id.switch2);
        auth=FirebaseAuth.getInstance();
        String authemail=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        if(authemail.contains("."))
            authemail=authemail.replace('.','!');
        db= FirebaseDatabase.getInstance().getReference().child("Users").child(authemail);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Details d1=dataSnapshot.getValue(Details.class);
                n=d1.n;
                d=d1.d;
                s=d1.s;
                l=d1.l;
                i=d1.chatbot;
                if(d1.soundEffect==1)
                    s1.setChecked(true);
                else if(d1.soundEffect==0)
                    s1.setChecked(false);
                if(d1.chatbot==1)
                    s2.setChecked(true);
                else if(d1.chatbot==0)
                    s2.setChecked(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_settings,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save:
                Details d2;int a,b;
                if(s1.isChecked())
                    a=1;
                else
                    a=0;
                if(s2.isChecked())
                    b=1;
                else b=0;
                d2=new Details(n,s,l,d,a,b);
                db.setValue(d2);
                Snackbar.make(findViewById(R.id.setting), "Changes Saved", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();        }
        return super.onOptionsItemSelected(item);
    }
}
