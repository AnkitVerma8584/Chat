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
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBox extends AppCompatActivity implements  NewChat.NewChatListener{
    FirebaseAuth auth;
    DatabaseReference db,df;
    private TableLayout t;
    String u;
    int c=100;
    Display display;
    LoadingDialog loadingDialog=new LoadingDialog(ChatBox.this);
    ScrollView scr;
    int fl=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        auth=FirebaseAuth.getInstance();
        t=findViewById(R.id.table);
        db= FirebaseDatabase.getInstance().getReference().child("ChatBox");
        df=FirebaseDatabase.getInstance().getReference().child("Users");
        display=getWindowManager().getDefaultDisplay();
        scr=findViewById(R.id.full);
        start();
    }
    public void start(){
        fl=0;
        try {
            loadingDialog.startLoadingDialog();
            db.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    t.removeAllViews();
                    scr.setBackgroundColor(Color.WHITE);
                    fl=0;
                    c=100;
                    for(DataSnapshot d1 : dataSnapshot.getChildren())  {
                        u=d1.getKey();
                        User ch=d1.getValue(User.class);
                        final TableRow tr=new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        Button b=new Button(getApplicationContext());
                        CircleImageView civ=new CircleImageView(getApplicationContext());
                        String zz=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
                        if(zz.contains("."))
                            zz=zz.replace('.','!');
                        if(u.contains(zz))
                        {
                            String t1=u.substring(0,u.indexOf('^')),t2=u.substring(u.indexOf('^')+1);
                            if(t1.equals(zz))
                            {
                                getname(t2,c);
                                Drawable bac=getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                                b.setBackground(bac);
                                b.setPadding(15,5,25000,10);
                                b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                b.setId(c);
                                civ.setId(100*c);
                                c++;
                                tr.addView(civ);
                                try {
                                    if(ch.email.contains(t2+"\\") && !ch.email.contains("*%SEEN%*"))
                                    {
                                        /*Drawable mm=getApplicationContext().getResources().getDrawable(R.drawable.ic_message_black_24dp);
                                        b.setCompoundDrawables(null,null,mm,null);
                                        b.setCompoundDrawablePadding(5);*/
                                    }
                                } catch (Exception e) {
                                }
                                tr.addView(b);
                            }
                            else if(t2.equals(zz))
                            {
                                getname(t1,c);
                                Drawable bac=getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                                b.setBackground(bac);
                                b.setPadding(15,5,25000,10);
                                b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                                b.setId(c);
                                civ.setId(100*c);
                                c++;
                                tr.addView(civ);
                                try {
                                    if(ch.email.contains(t1+"\\") && !ch.email.contains("*%SEEN%*"))
                                    {
                                        /*Drawable mm=getApplicationContext().getResources().getDrawable(R.drawable.ic_message_black_24dp);
                                        b.setCompoundDrawables(null,null,mm,null);
                                        b.setCompoundDrawablePadding(5);*/

                                    }
                                } catch (Exception e) {
                                }
                                tr.addView(b);
                            }
                        }
                        t.addView(tr);
                    }
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
                            finish();
                            startActivity(new Intent(getApplicationContext(),ChatBox.class));
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
                        String s1=u.n+"\t"+" ("+d1.getKey()+u.d+") "+"\n"+u.s;
                        SpannableString spannableString=new SpannableString(s1);
                        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE),s1.indexOf('('),s1.indexOf(')')+1,0);
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
        Int.putExtra("person",""+n.substring(n.indexOf('(')+1,n.indexOf(')'))+"&"+n.substring(0,n.indexOf('(')-1));
        finish();
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
                        LayoutInflater inflater=getLayoutInflater();
                        View view=inflater.inflate(R.layout.pic,null);
                        TextView txt=view.findViewById(R.id.txt);
                        final ImageView imv=view.findViewById(R.id.img);
                        String n=b.getText().toString();
                        scr.setBackgroundColor(Color.BLACK);
                        fl=1;
                        final String file=n.substring(n.indexOf('(')+1,n.indexOf('@'));
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
                                finish();
                                startActivity(new Intent(getApplicationContext(),ChatBox.class));
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Picture changed",Toast.LENGTH_SHORT).show();
                start();
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
                        finish();
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
            start();
        }

    }
}