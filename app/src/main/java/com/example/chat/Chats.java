package com.example.chat;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.ClipData;
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
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    int c=0,fl=1,bl=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Intent intent=getIntent();
        p=intent.getExtras().getString("person");
        String t1=p.substring(p.indexOf('&')+1);
        p=p.substring(0,p.indexOf('@'));
        auth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle(t1);
        t=findViewById(R.id.table);
        a=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        chatbase(a,p);
        b=findViewById(R.id.button2);
        b.setOnClickListener(this);
        text=findViewById(R.id.editText3);
        scr=findViewById(R.id.scrollView2);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item=menu.findItem(R.id.item6);
        if(bl==1)
            item.setTitle("Unblock");
        else if(bl==0)
            item.setTitle("Block");
        return true;
    }

    public void chatbase(String a1, String p1)
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
                fl=1;
                c=0;
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    try {
                        User u = ds1.getValue(User.class);
                        final TableRow tr=new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tr.setGravity(Gravity.CENTER);
                        if(!ds1.getKey().equals(a+"\\"+"BLOCK") && !ds1.getKey().equals(p+"\\"+"BLOCK")) {
                            if (u.email.contains(a + "\\")) {
                                String z = (u.email.substring(u.email.indexOf('\\') + 1));
                                LayoutInflater inflater = getLayoutInflater();
                                View v = inflater.inflate(R.layout.right_chat, null);
                                TextView tvt = v.findViewById(R.id.r_message);
                                if (z.indexOf('$') > -1)
                                    tvt.setText(z.substring(0, z.indexOf('$')));
                                else
                                    tvt.setText(z);
                                c++;
                                t.addView(v);
                            }
                            if (u.email.contains(p + "\\")) {
                                String z = (u.email.substring(u.email.indexOf('\\') + 1));
                                LayoutInflater inflater = getLayoutInflater();
                                View v = inflater.inflate(R.layout.left_chat, null);
                                TextView tvt = v.findViewById(R.id.l_message);
                                if (z.indexOf('$') > -1)
                                    tvt.setText(z.substring(0, z.indexOf('$')));
                                else
                                    tvt.setText(z);
                                c++;
                                t.addView(v);
                            }
                        }
                        else if(u.email.equals("Block"))
                        {
                            fl=fl*0;
                            if(ds1.getKey().equals(a+"\\"+"BLOCK"))
                                bl=1;
                        }
                        else if(u.email.equals("Unblock")){
                            fl=fl*1;
                            if(ds1.getKey().equals(a+"\\"+"BLOCK"))
                                bl=0;
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
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addChat(String n) {
        if(c==0)
        {
            User b=new User("Unblock");
            db.child(a+"\\"+"BLOCK").setValue(b);
            db.child(p+"\\"+"BLOCK").setValue(b);
        }
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(n.contains("^BLOCK^")) {
            User b = new User("Block");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else if(n.contains("^UNBLOCK^")){
            User b= new User("Unblock");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else {
            n = n + "$" + currentDate + "#" + currentTime;
            User u = new User(n);
            db.push().setValue(u);
            viewChat();
        }
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
            if(fl==1) {
                String n = text.getText().toString();
                if (n.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Message cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                text.setText("");
                text.setHint("Type your message");
                chatbase(a, p);
                addChat(a + "\\" + n);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Chat is Blocked",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item4:
                Intent intent=new Intent(getApplicationContext(),ProfilePerson.class);
                intent.putExtra("Email",p);
                finish();
                startActivity(intent);
                return true;
            case R.id.item5:
                return true;
            case R.id.item6:
                if(item.getTitle().equals("Block")) {
                    chatbase(a, p);
                    addChat(a + "\\" + "^BLOCK^");
                    Toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
                    item.setTitle("Unblock");
                }
                else if(item.getTitle().equals("Unblock")) {
                    chatbase(a, p);
                    addChat(a + "\\" + "^UNBLOCK^");
                    Toast.makeText(getApplicationContext(), "Unblocked", Toast.LENGTH_SHORT).show();
                    item.setTitle("Block");
                }
                if(bl==1)
                    item.setTitle("Unblock");
                else if(bl==0)
                    item.setTitle("Block");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}