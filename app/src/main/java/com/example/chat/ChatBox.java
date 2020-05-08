package com.example.chat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBox extends AppCompatActivity implements NewChat.NewChatListener, View.OnClickListener {
    FirebaseAuth auth;
    DatabaseReference db,df,dl;
    private TableLayout t;
    String u;
    int c=100;
    Display display;
    String allname="",allnm="";
    LoadingDialog loadingDialog=new LoadingDialog(ChatBox.this);
    ScrollView scr;
    int fl=0,unseen_message=0,first_time=0;
    FloatingActionButton flab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        getSupportActionBar().setTitle("Chatable: Your Chats");
        if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            Toast.makeText(getApplicationContext(), "Please verify your email ID and login again.", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(), Home.class));
        }
        auth = FirebaseAuth.getInstance();
        t = findViewById(R.id.table);
        db = FirebaseDatabase.getInstance().getReference().child("ChatBox");
        df = FirebaseDatabase.getInstance().getReference().child("Users");
        dl = FirebaseDatabase.getInstance().getReference().child("Last");
        display = getWindowManager().getDefaultDisplay();
        scr = findViewById(R.id.full);
        flab=findViewById(R.id.fab);
        flab.setOnClickListener(this);
        //start();
        begin();
    }
    public void begin(){
        fl=0;
        flab.setVisibility(View.VISIBLE);
        try {
            loadingDialog.startLoadingDialog();
            dl.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    t.removeAllViews();
                    scr.setBackgroundColor(Color.WHITE);
                    fl=0;
                    c=100;
                    for(DataSnapshot d1 : dataSnapshot.getChildren())  {
                        User all=d1.getValue(User.class);
                        u=all.email;
                        //if(!allname.contains(u))
                        //{
                            String zz = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                            if (zz.contains("."))
                                zz = zz.replace('.', '!');
                            if (u.contains(zz)) {
                                final String t1 = u.substring(0, u.indexOf('^')), t2 = u.substring(u.indexOf('^') + 1);
                                /*if (t1.equals(zz)) {
                                    allnm=allnm+t2+"$";
                                } else if (t2.equals(zz)) {
                                    allnm=allnm+t1+"$";
                                }*/
                                allnm=allnm+u+"$";
                            }
                        //}

                    }
                    loadingDialog.dismissDialog();
                    sort(allnm);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    public void sort(String n)
    {
        int p=0;
        for(int i=0;i<n.length();i++)
            if(n.charAt(i)=='$')
                p++;
        String name[]=new String[p];
        String t="";int nc=p-1;
        for(int i=0;i<n.length();i++)
        {
            if(n.charAt(i)=='$')
            {
                name[nc--]=t;
                t="";
            }
            else
                t=t+n.charAt(i);
        }
        display(name);
    }
    public void display(String name[]){
        fl=0;
        allname="";
        try {
            loadingDialog.startLoadingDialog();
            t.removeAllViews();
            scr.setBackgroundColor(Color.WHITE);
            fl=0;
            c=100;
            for(int i=0;i<name.length;i++){
                u=name[i];
                unseen_message = 0;
                //Toast.makeText(getApplicationContext(),u,Toast.LENGTH_SHORT).show();
                if(!allname.contains(u))
                {
                    if (first_time == 0) {
                        change(u);
                    }
                    final TableRow tr = new TableRow(getApplicationContext());
                    tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                    final Button b = new Button(getApplicationContext());
                    CircleImageView civ = new CircleImageView(getApplicationContext());
                    String zz = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                    if (zz.contains("."))
                        zz = zz.replace('.', '!');
                    if (u.contains(zz)) {
                        final String t1 = u.substring(0, u.indexOf('^')), t2 = u.substring(u.indexOf('^') + 1);
                        if (t1.equals(zz)) {
                            db.child(u).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    unseen_message = 0;
                                    for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                        User ch = ds1.getValue(User.class);
                                        if (ch.email.contains(t2 + "\\") && !ch.email.contains("*%SEEN%*")) {
                                            unseen_message++;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                            getname(t2, c);
                            Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                            b.setBackground(bac);
                            b.setPadding(15, 5, 25000, 10);
                            b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            b.setId(c);
                            civ.setId(100 * c);
                            c++;
                            tr.addView(civ);
                            tr.addView(b);
                        } else if (t2.equals(zz)) {
                            db.child(u).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                    unseen_message = 0;
                                    for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                        User ch = ds1.getValue(User.class);
                                        if (ch.email.contains(t1 + "\\") && !ch.email.contains("*%SEEN%*")) {
                                            unseen_message++;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                        }
                                    });
                            getname(t1, c);
                            Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                            b.setBackground(bac);
                            b.setPadding(15, 5, 25000, 10);
                            b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            b.setId(c);
                            civ.setId(100 * c);
                            c++;
                            tr.addView(civ);
                            tr.addView(b);
                        }
                    }
                    t.addView(tr);
                    allname=allname+u;
                }
            }
            if(first_time==0)
                first_time=1;
            loadingDialog.dismissDialog();
            try {
                checkClick();
                checkImClick();
            } catch (Exception e) {
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }
    /*public void start(){
        fl=0;
        try {
            loadingDialog.startLoadingDialog();
            dl.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    t.removeAllViews();
                    scr.setBackgroundColor(Color.WHITE);
                    fl=0;
                    c=100;
                    for(DataSnapshot d1 : dataSnapshot.getChildren())  {
                        User all=d1.getValue(User.class);
                        u=all.email;
                        if(!allname.contains(u))
                        {
                            if (first_time == 0) {
                                change(u);
                            }
                            final TableRow tr = new TableRow(getApplicationContext());
                            tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                            final Button b = new Button(getApplicationContext());
                            CircleImageView civ = new CircleImageView(getApplicationContext());
                            String zz = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                            if (zz.contains("."))
                                zz = zz.replace('.', '!');
                            if (u.contains(zz)) {
                                final String t1 = u.substring(0, u.indexOf('^')), t2 = u.substring(u.indexOf('^') + 1);
                                if (t1.equals(zz)) {
                                    db.child(u).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            unseen_message = 0;
                                            for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                                User ch = ds1.getValue(User.class);
                                                if (ch.email.contains(t2 + "\\") && !ch.email.contains("*%SEEN%*")) {
                                                    unseen_message++;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    getname(t2, c);
                                    Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                                    b.setBackground(bac);
                                    b.setPadding(15, 5, 25000, 10);
                                    b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                    b.setId(c);
                                    civ.setId(100 * c);
                                    c++;
                                    tr.addView(civ);
                                    tr.addView(b);
                                } else if (t2.equals(zz)) {
                                    db.child(u).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                            unseen_message = 0;
                                            for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                                User ch = ds1.getValue(User.class);
                                                if (ch.email.contains(t1 + "\\") && !ch.email.contains("*%SEEN%*")) {
                                                    unseen_message++;
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                    getname(t1, c);
                                    Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                                    b.setBackground(bac);
                                    b.setPadding(15, 5, 25000, 10);
                                    b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                    b.setId(c);
                                    civ.setId(100 * c);
                                    c++;
                                    tr.addView(civ);
                                    tr.addView(b);
                                }
                            }
                            t.addView(tr);
                            allname=allname+u;
                        }

                    }
                    if(first_time==0)
                        first_time=1;
                    loadingDialog.dismissDialog();
                    try {
                        checkClick();
                        checkImClick();
                    } catch (Exception e) {
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }*/
    public void change(String uu)
    {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        User ue=new User(uu+"$"+currentDate+"#"+currentTime);
        db.child(uu).child("BLANK").setValue(ue);
    }
    public void getname(final String n,final int t)
    {
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    Details u=d1.getValue(Details.class);
                    if(n.equals(d1.getKey()))
                    {
                        CircleImageView civ=findViewById(100*t);
                        try {
                            Glide.with(getApplicationContext()).load(u.l).into(civ);
                        } catch (Exception e) {
                            begin();
                        }
                        Point size=new Point();
                        display.getSize(size);
                        civ.getLayoutParams().height=size.x/7;
                        civ.getLayoutParams().width=size.x/7;
                        civ.requestLayout();
                        civ.setLeft(5);
                        civ.setPadding(8,8,8,8);
                        Button b=findViewById(t);
                        b.setTextColor(Color.BLACK);
                        b.setTransformationMethod(null);
                        String s1=u.n+"\t"+" (%"+unseen_message+"&"+d1.getKey()+u.d+") "+"\n"+u.s;
                        SpannableString spannableString=new SpannableString(s1);
                        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE),s1.indexOf('('),s1.indexOf(')')+1,0);
                        if(unseen_message>0) {
                            MediaPlayer ring= MediaPlayer.create(getApplicationContext(),R.raw.sent);
                            ring.start();
                            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), s1.indexOf('%') + 1, s1.indexOf('&'), 0);
                            spannableString.setSpan(new BackgroundColorSpan(Color.GREEN), s1.indexOf('%') + 1, s1.indexOf('&'), 0);
                        }
                        unseen_message=0;
                        b.setText(spannableString);
                        b.setLeft(10);
                        b.setTextSize(18);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void getEmail(final String n)
    {
        Intent Int=new Intent(getApplicationContext(),Chats.class);
        Int.putExtra("person",""+n.substring(n.indexOf('&')+1,n.indexOf(')'))+"&"+n.substring(0,n.indexOf('(')-1));
        startActivity(Int);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf=getMenuInflater();
        inf.inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item1:
                openDialog();
                return true;
            case R.id.item3:
                AlertDialog.Builder alt=new AlertDialog.Builder(this);
                alt.setTitle("Warning!")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to log out of this app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(),Home.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog a=alt.create();
                a.show();
                return true;
            case R.id.item2:
                finish();
                startActivity(new Intent(getApplicationContext(),Edit.class));
                return true;
            case R.id.about:
                finish();
                startActivity(new Intent(getApplicationContext(),About.class));
            default:return super.onOptionsItemSelected(item);
        }

    }

    public void checkClick()
    {
        for(int i=100;i<c;i++)
        {
            final Button b=scr.findViewById(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getEmail(""+b.getText());

                }
            });
        }
    }

    public void checkImClick()
    {
        for(int i=100;i<c;i++)
        {
            final Button b=findViewById(i);
            final CircleImageView civ=findViewById(100*i);
            try {
                civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flab.setVisibility(View.INVISIBLE);
                        LayoutInflater inflater=getLayoutInflater();
                        View view=inflater.inflate(R.layout.pic,null);
                        TextView txt=view.findViewById(R.id.txt);
                        final ImageView imv=view.findViewById(R.id.img);
                        String n=b.getText().toString();
                        scr.setBackgroundColor(Color.BLACK);
                        fl=1;
                        final String file=n.substring(n.indexOf('&')+1,n.indexOf('@'));
                        txt.setText(n.substring(0,n.indexOf('(')-1));
                        df.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for(DataSnapshot ds: dataSnapshot.getChildren()){
                                    Details u1=ds.getValue(Details.class);
                                    if(file.equals(ds.getKey()))
                                        Glide.with(getApplicationContext()).load(u1.l).into(imv);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        t.removeAllViews();
                        t.addView(view);
                        txt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //start();
                                begin();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Picture changed",Toast.LENGTH_SHORT).show();
                //start();
                begin();
            }

        }
    }

    public void openDialog(){
        NewChat nc=new NewChat();
        nc.show(getSupportFragmentManager(),"New Chat");
    }

    @Override
    public void applyTexts(final String n) {
        final Intent Int=new Intent(getApplicationContext(),Chats.class);

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    Details u=d1.getValue(Details.class);
                    if(n.equals(d1.getKey()))
                    {
                        Int.putExtra("person",n+u.d+"&"+u.n);
                        //finish();
                        startActivity(Int);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(fl==0){
            finishAffinity();
        }
        else if(fl==1){
            //start();
            begin();
        }

    }

    @Override
    public void onClick(View v) {
        if(v==flab)
        {
            //finish();
            startActivity(new Intent(getApplicationContext(),MorePeople.class));
        }
    }
}