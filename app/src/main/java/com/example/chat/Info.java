package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Info extends AppCompatActivity implements View.OnClickListener {
    EditText n,s;
    FirebaseAuth auth;
    Button bt;
    DatabaseReference db;
    String name,status;
    private ImageView profile;
    private StorageReference mStorageRef;
    public static final int PICK_IMAGE=1;
    private Uri imageuri;
    String domain;
    ProgressDialog progress;
    String z1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        auth=FirebaseAuth.getInstance();
        domain=auth.getCurrentUser().getEmail().substring(auth.getCurrentUser().getEmail().indexOf('@'));
        n=findViewById(R.id.name);
        s=findViewById(R.id.status);
        z1=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        if(z1.contains("."))
        {
            z1=z1.replace('.','!');
        }
        db= FirebaseDatabase.getInstance().getReference().child("Users")
                .child(z1);
        bt=findViewById(R.id.save);
        bt.setOnClickListener(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        profile=findViewById(R.id.profilepic);
        imageuri=null;
        progress=new ProgressDialog(this);
        Glide.with(getApplicationContext()).load("https://firebasestorage.googleapis.com/v0/b/chat-87dc0.appspot.com/o/default_pic.png?alt=media&token=0319c156-031c-4b07-9a0c-27ebecdd34d1").into(profile);
        profile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v==bt)
        {
            name=n.getText().toString();
            status=s.getText().toString();
            if(name.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if(status.equals(""))
            {
                Toast.makeText(getApplicationContext(), "Status cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if(name.contains("!") || name.contains("@") || name.contains("#") || name.contains("$") || name.contains("%") || name.contains("^") || name.contains("&") || name.contains("(") || name.contains(")") || name.contains("*"))
            {
                Toast.makeText(getApplicationContext(), "Name cannot contain special characters '!','@','#','%','^','&','*','(' and ')'", Toast.LENGTH_SHORT).show();
                return;
            }
            storeimage();
        }
        if(v==profile){
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent,"Select a picture"),PICK_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode == RESULT_OK){
            try {
                imageuri=data.getData();
                profile.setImageURI(imageuri);
            } catch (Exception e) {}
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
    void storeimage() {     //To store the pic
        try {
            if (imageuri != null) {
                String z2=auth.getCurrentUser().getEmail()
                        .substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
                if(z2.contains("."))
                    z2=z2.replace('.','!');
                final StorageReference user_profile = mStorageRef.child(z2+".jpg");
                progress.setTitle("Uploading...");
                progress.show();
                progress.setCancelable(false);
                user_profile.putFile(imageuri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                user_profile.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {


                                    @Override
                                    public void onSuccess(Uri uri) {

                                        String img_url = uri.toString();// to store url of the image
                                        Details d=new Details(name,status,img_url,domain);
                                        db.setValue(d);
                                        Toast.makeText(getApplicationContext(),"Details Saved",Toast.LENGTH_SHORT).show();
                                        progress.dismiss();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(),Login.class));

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                progress.setMessage((int)(100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount())+ " % Completed");
                            }
                        });
            } else {
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Note!")
                        .setCancelable(false)
                        .setMessage("You have not selected any profile image.Do you want to proceed with a default image?")
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                String img_url="https://firebasestorage.googleapis.com/v0/b/chat-87dc0.appspot.com/o/default_pic.png?alt=media&token=0319c156-031c-4b07-9a0c-27ebecdd34d1";
                                Details d=new Details(name,status,img_url,domain);
                                db.setValue(d);
                                Toast.makeText(getApplicationContext(),"Details Saved",Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(getApplicationContext(),Login.class));
                            }
                        });

                AlertDialog a1 = alt.create();
                a1.show();

            }
        }catch (Exception e)
        {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
