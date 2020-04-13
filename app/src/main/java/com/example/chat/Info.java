package com.example.chat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Info extends AppCompatActivity implements View.OnClickListener {
    EditText n,s;
    FirebaseAuth auth;
    Button bt;
    DatabaseReference db;
    String name,status;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        auth=FirebaseAuth.getInstance();
        n=findViewById(R.id.name);
        s=findViewById(R.id.status);
        db= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@')));
        bt=findViewById(R.id.save);
        bt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==bt)
        {
            name=n.getText().toString();
            status=s.getText().toString();
            if(name.equals(' '))
            {
                Toast.makeText(getApplicationContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if(status.equals(' '))
            {
                Toast.makeText(getApplicationContext(), "Status cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            Details d=new Details(name,status);
            db.setValue(d);
            Toast.makeText(getApplicationContext(),"Details Saved",Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alt=new AlertDialog.Builder(this);
        alt.setTitle("Warning!")
                .setCancelable(false)
                .setMessage("Please fill in the details and save to proceed.")
                .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog a=alt.create();
        a.show();
    }
}
