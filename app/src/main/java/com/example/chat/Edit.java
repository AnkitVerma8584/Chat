package com.example.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Edit extends AppCompatActivity implements View.OnClickListener {
    TextView t;
    EditText u, s;
    DatabaseReference db;
    FirebaseAuth auth;
    Button b;
    private StorageReference mStorageRef;
    public static final int PICK_IMAGE = 1;
    private Uri imageuri;
    ImageView profile;
    String durl;
    ProgressDialog progress;
    String name, status, domain;
    TextView res;
    int sound, chatbot;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        t = findViewById(R.id.textView4);
        u = findViewById(R.id.name);
        s = findViewById(R.id.status);
        b = findViewById(R.id.save);
        res = findViewById(R.id.resetPass);
        res.setVisibility(View.VISIBLE);
        res.setOnClickListener(this);
        b.setOnClickListener(this);
        profile = findViewById(R.id.profilepic);
        progress = new ProgressDialog(this);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        t.setText("Edit Your Details");
        imageuri = null;
        auth = FirebaseAuth.getInstance();
        domain = auth.getCurrentUser().getEmail().substring(auth.getCurrentUser().getEmail().indexOf('@'));
        db = FirebaseDatabase.getInstance().getReference().child("Users");
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                progress.setMessage("Loading...");
                progress.show();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Details det = d.getValue(Details.class);
                    String z1 = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                    if (z1.contains(".")) {
                        z1 = z1.replace('.', '!');
                    }
                    if (d.getKey().equals(z1)) {
                        u.setText(det.n);
                        s.setText(det.s);
                        sound = det.soundEffect;
                        chatbot = det.chatbot;
                        Glide.with(getApplicationContext()).load(det.l).into(profile);
                        progress.dismiss();
                        durl = det.l;
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        profile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == b) {
            name = u.getText().toString();
            status = s.getText().toString();
            if (name.equals("")) {
                Toast.makeText(getApplicationContext(), "Name cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (status.equals("")) {
                Toast.makeText(getApplicationContext(), "Status cannot be blank", Toast.LENGTH_SHORT).show();
                return;
            }
            if (name.contains("!") || name.contains("@") || name.contains("#") || name.contains("$") || name.contains("%") || name.contains("^") || name.contains("&") || name.contains("(") || name.contains(")") || name.contains("*")) {
                Toast.makeText(getApplicationContext(), "Name cannot contain special characters '!','@','#','%','^','&','*','(' and ')'", Toast.LENGTH_SHORT).show();
                return;
            }
            storeimage();
        }
        if (v == profile) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a picture"), PICK_IMAGE);
        }
        if (v == res) {
            res.setVisibility(View.INVISIBLE);
            String forgetPasswordEmail = auth.getCurrentUser().getEmail().toString();
            auth.sendPasswordResetEmail(forgetPasswordEmail)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Reset Password link sent to your email.", Toast.LENGTH_SHORT).show();
                            } else {
                                res.setVisibility(View.VISIBLE);
                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    public void storeimage() {
        try {
            if (imageuri != null) {
                String z1 = auth.getCurrentUser().getEmail()
                        .substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                if (z1.contains(".")) {
                    z1 = z1.replace('.', '!');
                }
                final StorageReference user_profile = mStorageRef.child(z1 + ".jpg");
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
                                        Details d = new Details(name, status, img_url, domain, sound, chatbot);
                                        String z2 = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                                        if (z2.contains("."))
                                            z2 = z2.replace('.', '!');
                                        db.child(z2).setValue(d);
                                        Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
                                        progress.dismiss();
                                        finish();
                                        startActivity(new Intent(getApplicationContext(), Login.class));

                                    }
                                });
                            }
                        })
                        .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                                progress.setMessage((int) (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount()) + " % Completed");
                            }
                        });
            } else {
                Details d = new Details(name, status, durl, domain, sound, chatbot);
                String z2 = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                if (z2.contains("."))
                    z2 = z2.replace('.', '!');
                db.child(z2).setValue(d);
                Toast.makeText(getApplicationContext(), "Details Saved", Toast.LENGTH_SHORT).show();
                finish();
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                imageuri = data.getData();
                profile.setImageURI(imageuri);
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), ChatBox.class));
    }
}
