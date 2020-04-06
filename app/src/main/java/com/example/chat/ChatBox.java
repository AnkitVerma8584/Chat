package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    DatabaseReference db;
    private TableLayout t;
    String u;
    int c=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        auth=FirebaseAuth.getInstance();
        t=findViewById(R.id.table);
        db= FirebaseDatabase.getInstance().getReference().child("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d1 : dataSnapshot.getChildren())  {
                    u=d1.getKey();
                    final TableRow tr=new TableRow(getApplicationContext());
                    tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT,TableLayout.LayoutParams.FILL_PARENT));
                    Button b=new Button(getApplicationContext());
                    b.setText(u);
                    b.setTextSize(30);
                    if(!u.equals(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'))))
                    {
                        b.setId(c);
                        c++;
                        tr.addView(b);
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
                auth.signOut();
                finish();
                startActivity(new Intent(getApplicationContext(),Login.class));
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
                    Intent Int=new Intent(getApplicationContext(),Chats.class);
                    Int.putExtra("person",""+b.getText());
                    finish();
                    startActivity(Int);
                }
            });
        }
    }



    public void openDialog(){
        NewChat nc=new NewChat();
        nc.show(getSupportFragmentManager(),"New Chat");
    }

    @Override
    public void applyTexts(String n) {
        Intent Int=new Intent(getApplicationContext(),Chats.class);
        Int.putExtra("person",n);
        finish();
        startActivity(Int);
    }
}
