package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import static android.graphics.Color.BLUE;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth auth;
    TextView reg;
    Button b;
    EditText e,p;
    private String email,pass;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Login Page");
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),ChatBox.class));
        }
        reg=findViewById(R.id.textView3);
        b=findViewById(R.id.button);
        e=findViewById(R.id.editText);
        p=findViewById(R.id.editText2);
        pd=new ProgressDialog(this);
        b.setOnClickListener(this);
        reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==b)
        {
            email=e.getText().toString();
            pass=p.getText().toString();
            if(email.length()==0 || pass.length()==0)
                Toast.makeText(getApplicationContext(),"One or More Fields are Empty. Please fill them.",Toast.LENGTH_SHORT).show();
            else {
                pd.setMessage("Logging IN");
                pd.show();
                auth.signInWithEmailAndPassword(email, pass)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                pd.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Logging Successful", Toast.LENGTH_SHORT).show();
                                    finish();
                                    startActivity(new Intent(getApplicationContext(), ChatBox.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
        if(v==reg)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),Registration.class));
        }
    }
}
