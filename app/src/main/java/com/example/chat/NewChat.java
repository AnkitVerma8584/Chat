package com.example.chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.firebase.auth.FirebaseAuth;


public class NewChat extends AppCompatDialogFragment {

    private EditText txt;
    private NewChatListener listener;
    FirebaseAuth auth;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View view=inflater.inflate(R.layout.new_chat,null);
        auth=FirebaseAuth.getInstance();
        builder.setView(view)
                .setTitle("New Chat")
                .setPositiveButton("Start Chat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name=txt.getText().toString().substring(0,txt.getText().toString().indexOf('@'));
                        if(name.equals(auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'))))
                        {
                            Toast.makeText(getContext(),"You cannot message yourself",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        else {
                            listener.applyTexts(name);
                        }
                    }
                });
        txt= view.findViewById(R.id.people);

        return builder.create();
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