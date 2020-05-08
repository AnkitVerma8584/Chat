package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static android.graphics.Color.BLUE;

public class Registration extends AppCompatActivity implements View.OnClickListener {
    private EditText e,p;
    private TextView t;
    private Button b;
    String username,password;
    private ProgressDialog pd;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Registration Page");
        if(savedInstanceState!=null){
            onRestoreInstanceState(savedInstanceState);
        }
        auth=FirebaseAuth.getInstance();
        e=findViewById(R.id.editText);
        p=findViewById(R.id.editText2);
        b=findViewById(R.id.button);
        t=findViewById(R.id.textView3);
        pd=new ProgressDialog(this);
        b.setOnClickListener(this);
        t.setOnClickListener(this);
    }
    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
    }
    @Override
    public void onClick(View v) {
        if(v==b)
        {
            username=e.getText().toString();
            password=p.getText().toString();
            if(username.length()==0)
            {
                Toast.makeText(getApplicationContext(),"Email id cannot be blank",Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.length()==0)
            {
                Toast.makeText(getApplicationContext(),"Password cannot be blank",Toast.LENGTH_SHORT).show();
                return;
            }
            pd.setMessage("Registering User");
            pd.show();
            auth.createUserWithEmailAndPassword(username,password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();
                            if(task.isSuccessful()){
                                auth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "New Account Created. Please Verify your email.", Toast.LENGTH_SHORT).show();
                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    finish();
                                                    startActivity(new Intent(getApplicationContext(),Login.class));
                                                }
                                            }, 500);
                                        }
                                        else
                                        {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

        }
        if(v==t)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),Login.class));
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(),Home.class));
    }
}
