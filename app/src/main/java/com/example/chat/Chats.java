package com.example.chat;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
public class Chats extends AppCompatActivity implements View.OnClickListener {
    String p,a;
    DatabaseReference db,ud,dbr;
    FirebaseAuth auth;
    TableLayout t;
    Button b;
    EditText text;
    ScrollView scr;
    int c=0,fl=1,bl=0,f=0;
    int bot=0;
    String fname,lastChat;
    int activityOnline;
    String unread="";
    List<String> un=new ArrayList<>();
    String autotext="";
    String fblock="blockList";
    List<String> blocks=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Intent intent=getIntent();
        p=intent.getExtras().getString("person");
        String t1=p.substring(p.indexOf('&')+1);
        p=p.substring(0,p.indexOf('@'));
        auth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle(t1);
        t=findViewById(R.id.table);
        try {
            FileInputStream fis = openFileInput(fblock);
            String string;
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((string = br.readLine()) != null) {
                blocks.add(string);
                //Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
            }
        }catch(Exception e){}
        a=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        if(a.contains("."))
            a=a.replace('.','!');
        activityOnline=1;
        dbr=FirebaseDatabase.getInstance().getReference().child("Users").child(a);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Details dd=dataSnapshot.getValue(Details.class);
                bot=dd.chatbot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        b=findViewById(R.id.button2);
        b.setOnClickListener(this);
        text=findViewById(R.id.editText3);
        scr=findViewById(R.id.scrollView2);
        ud=FirebaseDatabase.getInstance().getReference().child("Last");

        try {
            chatbase(a,p);
        } catch (IOException e) {
        }
    }

    public void chatbase(String a1, String p1) throws IOException {
        int i=0;String n="";
        while(i<a1.length() && i<p1.length())
        {
            if((int)(a1.charAt(i))<(int)(p1.charAt(i)))
            {
                n=""+(a1+"^"+p1);
                break;
            }
            else if((int)(a1.charAt(i))>(int)(p1.charAt(i)))
            {
                n=""+(p1+"^"+a1);
                break;
            }else{
                if(a1.length()<p1.length())
                    n=""+(a1+"^"+p1);
                else
                    n=""+(p1+"^"+a1);
            }
            i++;
        }
        db=FirebaseDatabase.getInstance().getReference().child("ChatBox").child(n);
        fname=n;

        lastChat="_"+a;
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                unread="";
                for(DataSnapshot ds2:dataSnapshot.getChildren()){
                    User u2=ds2.getValue(User.class);
                    if(u2.email.contains(a+"\\"))
                    {

                        unread=unread+u2.email;
                        //un.add(u2.email);
                    }
                    /*if (u2.email.contains(p + "\\")) {
                        String z = (u2.email.substring(u2.email.indexOf('\\') + 1));
                        LayoutInflater inflater = getLayoutInflater();
                        View v = inflater.inflate(R.layout.left_chat, null);
                        TextView tvt = v.findViewById(R.id.l_message);
                        if (z.indexOf('$') > -1) {
                            String p;
                            p = z.substring(0, z.indexOf('$')) + "    " + z.substring(z.indexOf('#') + 1);
                            SpannableString spannableString = new SpannableString(p);
                            spannableString.setSpan(new RelativeSizeSpan(0.6f), p.indexOf(':') - 2, p.length(), 0);
                            spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':') - 2, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                            tvt.setText(spannableString);
                        } else {
                            tvt.setText(z);
                        }
                        c++;
                        t.addView(v);
                    }*/
                }

                FileInputStream fis=null;
                try {
                    fis=openFileInput(fname);
                    t.removeAllViews();
                    InputStreamReader isr=new InputStreamReader(fis);
                    BufferedReader br=new BufferedReader(isr);
                    String text;
                    String g=unread;
                    while ((text = br.readLine()) != null) {
                        //Toast.makeText(getApplicationContext(),g,Toast.LENGTH_SHORT).show();
                        if (text.contains(a + "\\")) {
                            String z = (text.substring(text.indexOf('\\') + 1));
                            LayoutInflater inflater = getLayoutInflater();

                            if (!g.contains(text)) {
                                View v = inflater.inflate(R.layout.seen, null);
                                TextView tvt = v.findViewById(R.id.r_message);
                                if (z.indexOf('$') > -1) {
                                    String p;
                                    p = z.substring(0, z.indexOf('$')) + "    " + z.substring(z.indexOf('#') + 1);
                                    SpannableString spannableString = new SpannableString(p);
                                    spannableString.setSpan(new RelativeSizeSpan(0.6f), p.indexOf(':') - 2, p.length(), 0);
                                    spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':') - 2, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvt.setText(spannableString);
                                } else
                                    tvt.setText(z);
                                t.addView(v);
                            } else {
                                //Toast.makeText(getApplicationContext(), unread, Toast.LENGTH_SHORT).show();
                                View v = inflater.inflate(R.layout.right_chat, null);
                                TextView tvt = v.findViewById(R.id.r_message);
                                if (z.indexOf('$') > -1) {
                                    String p;
                                    p = z.substring(0, z.indexOf('$')) + "    " + z.substring(z.indexOf('#') + 1);
                                    SpannableString spannableString = new SpannableString(p);
                                    spannableString.setSpan(new RelativeSizeSpan(0.6f), p.indexOf(':') - 2, p.length(), 0);
                                    spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':') - 2, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvt.setText(spannableString);
                                } else {
                                    tvt.setText(z);
                                }
                                t.addView(v);
                            }
                            c++;

                        } else if (text.contains(p + "\\")) {
                            String z = (text.substring(text.indexOf('\\') + 1));
                            LayoutInflater inflater = getLayoutInflater();
                            View v = inflater.inflate(R.layout.left_chat, null);
                            TextView tvt = v.findViewById(R.id.l_message);
                            if (z.indexOf('$') > -1) {
                                String p;
                                p = z.substring(0, z.indexOf('$')) + "    " + z.substring(z.indexOf('#') + 1);
                                SpannableString spannableString = new SpannableString(p);
                                spannableString.setSpan(new RelativeSizeSpan(0.6f), p.indexOf(':') - 2, p.length(), 0);
                                spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':') - 2, p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                tvt.setText(spannableString);
                            } else {
                                tvt.setText(z);
                            }
                            c++;
                            t.addView(v);

                        }
                        unread = "";
                    }
                    scr.post(new Runnable() {
                        @Override
                        public void run() {
                            scr.fullScroll(View.FOCUS_DOWN);
                        }
                    });

                }
                catch(Exception e){
                    //Toast.makeText(getApplicationContext(),"File Doesnt exist",Toast.LENGTH_SHORT).show();
                }
                finally{
                    if(!blocks.contains(fname))
                        newChats();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //show();
        //viewChat();
    }

    public void newChats(){
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dss:dataSnapshot.getChildren()){
                    User uu=dss.getValue(User.class);
                    final String ss=(uu.email);
                    if(ss.contains(p+"\\") && ss.contains(":"))
                    {
                        try {
                            final FileOutputStream fos=openFileOutput(fname,MODE_APPEND);
                            if(!check(ss) && activityOnline==1)
                            {
                                fos.write((ss + "\n").getBytes());
                                autotext=ss;
                            }
                            if(activityOnline==1)
                            {
                                db.child(dss.getKey()).removeValue();
                            }
                        } catch (IOException e) {

                        }

                    }
                    scr.post(new Runnable() {
                        @Override
                        public void run() {
                            scr.fullScroll(View.FOCUS_DOWN);
                        }
                    });
                }
                String time = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
                int t=Integer.parseInt(time);
                if(!autotext.contains(" (Crypton)") && (autotext.contains("good morning") || autotext.contains("Good morning") || autotext.contains("Good Morning") || autotext.contains("GOOD MORNING"))&& bot==1 && (t>=4 && t<12) ) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Good Morning (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(!autotext.contains(" (Crypton)") && (autotext.contains("good afternoon") || autotext.contains("Good Afternoon") || autotext.contains("Good afternoon") || autotext.contains("GOOD AFTERNOON"))&& bot==1 && (t>=12 && t<16)) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Good Afternoon (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(!autotext.contains(" (Crypton)") && (autotext.contains("good evening") || autotext.contains("Good Evening") || autotext.contains("Good evening") || autotext.contains("GOOD EVENING"))&& bot==1 && (t>=16 && t<20)) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Good Evening (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(!autotext.contains(" (Crypton)") && (autotext.contains("good night") || autotext.contains("Good night") || autotext.contains("Good Night") || autotext.contains("GOOD NIGHT"))&& bot==1 && (t>=20 || t<3)) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Good Night (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(!autotext.contains(" (Crypton)") && (autotext.contains("hello") || autotext.contains("Hello") || autotext.contains("HELLO"))&& bot==1 ) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Hello (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(!autotext.contains(" (Crypton)") && (autotext.contains("thank you") || autotext.contains("thanks") || autotext.contains("Thank You") || autotext.contains("Thank you") || autotext.contains("Thanks") || autotext.contains("THANK YOU") || autotext.contains("THANKS"))&& bot==1 && (t>=20 || t<3)) {

                    autotext = "";
                    try {
                        addChat(a+"\\"+"Welcome (Crypton)");
                    } catch (IOException e) {
                        e.printStackTrace();
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
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item=menu.findItem(R.id.item6);
        if(blocks.contains(fname))
        {
            item.setTitle("Unblock");
        }
        else {
            item.setTitle("Block");
        }
        return true;
    }

    public boolean check(String t) throws IOException {
        FileInputStream fis=null;
        fis=openFileInput(fname);
        InputStreamReader isr=new InputStreamReader(fis);
        BufferedReader br=new BufferedReader(isr);
        String text;
        while((text=br.readLine())!=null){
            if(t.equals(text))
                return true;
        }
        return false;
    }

    public void addChat(String n) throws IOException {
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
        //String currentTime2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(n.contains("^BLOCK^")) {
            User b = new User("Block");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else if(n.contains("^UNBLOCK^")){
            User b= new User("Unblock");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else {
            n = n + "$" + currentDate + "#" + currentTime;
            User u = new User(n);
            db.push().setValue(u);
            dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Details detail=dataSnapshot.getValue(Details.class);
                    try {
                        if(detail.soundEffect==1){
                            MediaPlayer ring= MediaPlayer.create(getApplicationContext(),R.raw.sent);
                            ring.start();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            /*User u2=new User(""+currentDate+"%"+currentTime);
            db.child("LAST").setValue(u2);
            u=new User(commonChatBoxName(a,p));
            ud.child(""+currentDate + "*" + currentTime2).setValue(u);*/
            final FileOutputStream fos=openFileOutput(fname,MODE_APPEND);
            fos.write((n+"\n").getBytes());

            String z = (n.substring(n.indexOf('\\') + 1));
            LayoutInflater inflater = getLayoutInflater();

            View v = inflater.inflate(R.layout.right_chat, null);
            TextView tvt = v.findViewById(R.id.r_message);
            if (z.indexOf('$') > -1)
            {
                String p;
                p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1);
                SpannableString spannableString=new SpannableString(p);
                spannableString.setSpan(new RelativeSizeSpan(0.6f),p.indexOf(':')-2,p.length(),0);
                spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':')-2,p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvt.setText(spannableString);
            }
            else
                tvt.setText(z);
            t.addView(v);
            //Updating the last person messaged list
            List<String> last=new ArrayList<>();
            FileInputStream fis2=null;
            try{
                fis2=openFileInput(lastChat);
                InputStreamReader isr=new InputStreamReader(fis2);
                BufferedReader br=new BufferedReader(isr);
                String p1="";
                while((p1=br.readLine())!=null){
                    last.add(p1);
                }
            }catch(Exception e){}
            FileOutputStream fos2=openFileOutput(lastChat,MODE_PRIVATE);
            String p1=fname;
            for(String name:last){
                if(!p1.contains(name))
                    p1=p1+"\n"+name;
            }
            fos2.write(p1.getBytes());

            //chatbase(a,p);
        }
    }

    public String commonChatBoxName(String a1,String p1){
        int i=0;String n="";
        while(i<a1.length() && i<p1.length())
        {
            if((int)(a1.charAt(i))<(int)(p1.charAt(i)))
            {
                n=""+(a1+"^"+p1);
                break;
            }
            else if((int)(a1.charAt(i))>(int)(p1.charAt(i)))
            {
                n=""+(p1+"^"+a1);
                break;
            }else{
                if(a1.length()<p1.length())
                    n=""+(a1+"^"+p1);
                else
                    n=""+(p1+"^"+a1);
            }
            i++;
        }
        return n;
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        finishAndRemoveTask();
        f=1;
        activityOnline=0;
        startActivity(new Intent(getApplicationContext(),ChatBox.class));
    }

    @Override
    public void onClick(View v) {
        if(v==b)
        {
            if(blocks.contains(fname)){
                Toast.makeText(getApplicationContext(), "Please unblock at first", Toast.LENGTH_SHORT).show();
                return;
            }
            if(fl==1) {
                String n = text.getText().toString();
                if (n.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Message cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                text.setText("");
                text.setHint("Type your message");
                try {
                    chatbase(a, p);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    addChat(a + "\\" + n);
                } catch (IOException e) {

                }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Chat is Blocked",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item4:
                Intent intent=new Intent(getApplicationContext(),ProfilePerson.class);
                intent.putExtra("Email",p);
                finish();
                startActivity(intent);
                return true;
            case R.id.item5:
                /*Snackbar.make(findViewById(R.id.scrollView2), "Delete Chats will be available in next Update", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                AlertDialog.Builder alt=new AlertDialog.Builder(this);
                alt.setTitle("Warning!")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to clear the chats from your phone? If you want to delete your unread chats for everyone,cancel this, go to home page and long press on the chat.")
                        .setPositiveButton("Clear Chats", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    final FileOutputStream fos=openFileOutput(fname,MODE_PRIVATE);
                                    fos.write("".getBytes());
                                    t.removeAllViews();
                                    Toast.makeText(getApplicationContext(),"Chats deleted",Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {

                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog aaa=alt.create();
                aaa.show();

                try {
                    chatbase(a,p);
                } catch (IOException e) {

                }
                return true;
            case R.id.item6:
                /*if(item.getTitle().equals("Block")) {
                    try {
                        chatbase(a, p);
                    } catch (IOException e) {

                    }
                    try {
                        Toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
                        item.setTitle("Unblock");
                        addChat(a + "\\" + "^BLOCK^");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                else if(item.getTitle().equals("Unblock")) {
                    try {
                        chatbase(a, p);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Toast.makeText(getApplicationContext(), "Unblocked", Toast.LENGTH_SHORT).show();
                        item.setTitle("Block");
                        addChat(a + "\\" + "^UNBLOCK^");
                    } catch (IOException e) {

                    }

                }
                if(bl==1)
                    item.setTitle("Unblock");
                else if(bl==0)
                    item.setTitle("Block");

                 */
                if(item.getTitle().equals("Block")){
                    blocks.add(fname);
                    String gg="";
                    try {
                        FileOutputStream fos=openFileOutput(fblock,MODE_PRIVATE);
                        for(String blocklist:blocks){
                            gg=gg+blocklist+"\n";
                        }
                        fos.write(gg.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Blocked",Toast.LENGTH_SHORT).show();
                    activityOnline=0;
                    finish();
                    startActivity(new Intent(getApplicationContext(),ChatBox.class));

                }
                else if(item.getTitle().equals("Unblock")){
                    blocks.remove(fname);
                    String gg="";
                    try {
                        FileOutputStream fos=openFileOutput(fblock,MODE_PRIVATE);
                        for(String blocklist:blocks){
                            gg=gg+blocklist+"\n";
                        }
                        fos.write(gg.getBytes());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Unblocked",Toast.LENGTH_SHORT).show();
                    activityOnline=0;
                    finish();
                    startActivity(new Intent(getApplicationContext(),ChatBox.class));

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}