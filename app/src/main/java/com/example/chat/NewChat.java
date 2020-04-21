package com.example.chat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class NewChat extends AppCompatDialogFragment {

    private EditText txt;
    private NewChatListener listener;
    FirebaseAuth auth;
    DatabaseReference db;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.new_chat,null);
        auth=FirebaseAuth.getInstance();
        db= FirebaseDatabase.getInstance().getReference().child("Users");
        builder.setView(view)
                .setTitle("New Chat")
                .setPositiveButton("Start Chat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String name = txt.getText().toString().substring(0, txt.getText().toString().indexOf('@'));
                            if(name.contains("."))
                                name=name.replace('.','!');
                            String z=auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                            if(z.contains("."))
                                z=z.replace('.','!');
                            if (name.equals(z)) {
                                Toast.makeText(getContext(), "You cannot message yourself", Toast.LENGTH_SHORT).show();
                                return;
                            } else {
                                check(name);
                            }
                        }
                        catch(Exception e){
                            Toast.makeText(getContext(),"Enter a valid email ID.",Toast.LENGTH_SHORT).show();
                        }
                    }

                });
        txt= view.findViewById(R.id.people);

        return builder.create();
    }

    int fl=0;
    void check(final String n)
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds1: dataSnapshot.getChildren())
                {
                    if(ds1.getKey().equals(n))
                    {
                        listener.applyTexts(n);
                        fl=1;
                        break;
                    }
                }
                if(fl==0)
                    try{Toast.makeText(getContext(),"The email id has still not been registered for this app.",Toast.LENGTH_SHORT).show();}
                catch(Exception e){Toast.makeText(getContext(),"The email id has still not been registered for this app.",Toast.LENGTH_SHORT).show();}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener=(NewChatListener)context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()+"Must implement NewChatDialog");
        }

    }

    public interface NewChatListener{
        void applyTexts(String name);
    }
}