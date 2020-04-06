package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Chats extends AppCompatActivity implements View.OnClickListener {
    String p,a;
    DatabaseReference db;
    FirebaseAuth auth;
    TableLayout t;
    Button b;
    EditText text;
    ScrollView scr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Intent intent=getIntent();
        p=intent.getExtras().getString("person");
        auth=FirebaseAuth.getInstance();
        t=findViewById(R.id.table);
        a=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        chatbase(a,p);
        b=findViewById(R.id.button2);
        b.setOnClickListener(this);
        text=findViewById(R.id.editText3);
        scr=findViewById(R.id.scrollView2);
        text.setOnClickListener(this);
    }

    public void chatbase(String a1,String p1)
    {
        int i=0;String n="";
        while(i<a1.length() && i<p1.length())
        {
            if((int)(a1.charAt(i))<(int)(p1.charAt(i)))
            {
                n=""+(a1+"^"+p1);
                break;
            }
            else if((int)(a1.charAt(i))>(int)(p1.charAt(i)))
            {
                n=""+(p1+"^"+a1);
                break;
            }
            i++;
        }
        db=FirebaseDatabase.getInstance().getReference().child("ChatBox").child(n);
        viewChat();

    }

    public void viewChat()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.removeAllViews();
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    try {
                        User u = ds1.getValue(User.class);
                        final TableRow tr=new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.MATCH_PARENT));
                        tr.setGravity(Gravity.CENTER);
                        TextView tv=new TextView(getApplicationContext());
                        if(u.email.contains(a+"\\")){
                            String z=(u.email.substring(u.email.indexOf('\\')+1));
                            tv.setText(z);
                            tv.setGravity(Gravity.END);
                        }
                        if(u.email.contains(p+"\\")){
                            String z=(u.email.substring(u.email.indexOf('\\')+1));
                            tv.setText(z);
                        }
                        tr.addView(tv);
                        t.addView(tr);

                    } catch (NullPointerException npe) {
                    }
                }
                scr.post(new Runnable() {
                    @Override
                    public void run() {
                        scr.fullScroll(View.FOCUS_DOWN);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addChat(String n)
    {
        User u=new User(n);
        db.push().setValue(u);
        viewChat();
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(),ChatBox.class));
    }

    @Override
    public void onClick(View v) {
        if(v==b)
        {
            String n=text.getText().toString();
            if(n.length()==0)
            {
                Toast.makeText(getApplicationContext(),"Message cannot be blank",Toast.LENGTH_SHORT).show();
                return;
            }
            text.setText("");
            text.setHint("Type your message");
            chatbase(a,p);
            addChat(a+"\\"+n);
        }
        if(v==text)
        {

        }
    }
}