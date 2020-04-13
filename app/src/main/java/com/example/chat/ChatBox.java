package com.example.chat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class ChatBox extends AppCompatActivity implements  NewChat.NewChatListener{
    FirebaseAuth auth;
    DatabaseReference db,df;
    private TableLayout t;
    String u;
    int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        auth=FirebaseAuth.getInstance();
        t=findViewById(R.id.table);
        db= FirebaseDatabase.getInstance().getReference().child("ChatBox");
        df=FirebaseDatabase.getInstance().getReference().child("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1 : dataSnapshot.getChildren())  {
                    u=d1.getKey();
                    final TableRow tr=new TableRow(getApplicationContext());
                    tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.WRAP_CONTENT));
                    Button b=new Button(getApplicationContext());

                    b.setTextSize(30);
                    if(u.contains(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'))))
                    {
                        String t1=u.substring(0,u.indexOf('^')),t2=u.substring(u.indexOf('^')+1);
                        if(t1.equals(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'))))
                        {
                            getname(t2,c);
                            Drawable bac=getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                            b.setBackground(bac);
                            Drawable left=getApplicationContext().getResources().getDrawable(R.drawable.ic_person_black_24dp);
                            left.setBounds(0,0,60,60);
                            b.setCompoundDrawables(left,null,null,null);
                            b.setCompoundDrawablePadding(20);
                            b.setPadding(20,20,20,20);
                            b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            b.setId(c);
                            c++;
                            tr.addView(b);
                        }
                        else if(t2.equals(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'))))
                        {
                            getname(t1,c);
                            Drawable bac=getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                            b.setBackground(bac);
                            Drawable left=getApplicationContext().getResources().getDrawable(R.drawable.ic_person_black_24dp);
                            left.setBounds(0,0,60,60);
                            b.setCompoundDrawables(left,null,null,null);
                            b.setCompoundDrawablePadding(20);
                            b.setPadding(20,20,20,20);
                            b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                            b.setId(c);
                            c++;
                            tr.addView(b);
                        }

                    }
                    t.addView(tr);
                }
                checkClick();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
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
                        Button b=findViewById(t);
                        b.setText(u.n);
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
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1: dataSnapshot.getChildren()){
                    Details u=d1.getValue(Details.class);
                    if(n.equals(u.n))
                    {
                        Intent Int=new Intent(getApplicationContext(),Chats.class);
                        Int.putExtra("person",""+d1.getKey()+"&"+u.n);
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
            case R.id.item2:
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
            default:return super.onOptionsItemSelected(item);
        }

    }

    public void checkClick()
    {
        for(int i=0;i<c;i++)
        {
            final Button b=findViewById(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getEmail(""+b.getText());

                }
            });
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
                    User u=d1.getValue(User.class);
                    if(n.equals(d1.getKey()))
                    {
                        Int.putExtra("person",n+"&"+u.email);
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
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Warning!")
                .setCancelable(false)
                .setMessage("Are you sure you want to exit?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
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
    }
}