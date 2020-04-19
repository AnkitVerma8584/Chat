package com.example.chat;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.io.File;
import java.io.FileOutputStream;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.YELLOW;

public class Chats extends AppCompatActivity implements View.OnClickListener {
    String p,a;
    DatabaseReference db;
    FirebaseAuth auth;
    TableLayout t;
    Button b;
    EditText text;
    ScrollView scr;
    String fname;
    FileOutputStream fos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Intent intent=getIntent();
        p=intent.getExtras().getString("person");
        String t1=p.substring(p.indexOf('&')+1);
        p=p.substring(0,p.indexOf('&'));
        auth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle(t1);
        t=findViewById(R.id.table);
        a=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        chatbase(a,p);
        b=findViewById(R.id.button2);
        b.setOnClickListener(this);
        text=findViewById(R.id.editText3);
        scr=findViewById(R.id.scrollView2);
        File dir = getApplicationContext().getDir("Chat_App", Context.MODE_PRIVATE);
        if (!dir.exists())
        {
            dir.mkdirs();
        }
        fname="Chat_App/"+a+"^"+p;
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
            }else{
                if(a1.length()<p1.length())
                    n=""+(a1+"^"+p1);
                else
                    n=""+(p1+"^"+a1);
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
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tr.setGravity(Gravity.CENTER);
                        if(u.email.contains(a+"\\")){
                            String z=(u.email.substring(u.email.indexOf('\\')+1));
                            LayoutInflater inflater=getLayoutInflater();
                            View v=inflater.inflate(R.layout.right_chat,null);
                            TextView tvt=v.findViewById(R.id.r_message);
                            tvt.setText(z);
                            t.addView(v);
                        }
                        if(u.email.contains(p+"\\")){
                            String z=(u.email.substring(u.email.indexOf('\\')+1));
                            LayoutInflater inflater=getLayoutInflater();
                            View v=inflater.inflate(R.layout.left_chat,null);
                            TextView tvt=v.findViewById(R.id.l_message);
                            tvt.setText(z);
                            t.addView(v);
                        }
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
    }

}