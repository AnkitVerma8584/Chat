package com.example.chat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatBox extends AppCompatActivity implements NewChat.NewChatListener, View.OnClickListener {
    FirebaseAuth auth;
    DatabaseReference db, df, dl;
    private TableLayout t;
    int c = 100;
    Display display;
    String allname = "";
    String hh = "";
    //LoadingDialog loadingDialog=new LoadingDialog(ChatBox.this);
    ScrollView scr;
    int fl = 0, unseen_message = 0, first_time = 0;
    FloatingActionButton flab;
    int flag = 0;
    String[] name;
    List<String> last = new ArrayList<>();
    String lastChat = "";
    String a = "";
    String fblock = "blockList";
    List<String> blocks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        auth = FirebaseAuth.getInstance();

        if (!Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).isEmailVerified()) {
            auth.signOut();
            flag = 1;
            finish();
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
        try {
            FileInputStream fis = openFileInput(fblock);
            String string;
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            while ((string = br.readLine()) != null) {
                blocks.add(string);
                //Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        t = findViewById(R.id.table);
        db = FirebaseDatabase.getInstance().getReference().child("ChatBox");
        df = FirebaseDatabase.getInstance().getReference().child("Users");
        dl = FirebaseDatabase.getInstance().getReference().child("Last");
        display = getWindowManager().getDefaultDisplay();
        scr = findViewById(R.id.full);
        flab = findViewById(R.id.fab);
        flab.setOnClickListener(this);
        first_time = 0;
        a = Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()).substring(0, Objects.requireNonNull(auth.getCurrentUser().getEmail()).indexOf('@'));
        if (flag == 0) {
            load();
        }

    }

    public void load() {
        lastChat = "_" + Objects.requireNonNull(Objects.requireNonNull(auth.getCurrentUser()).getEmail()).substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
        last.clear();
        hh = "";
        FileInputStream fis2 = null;
        try {
            fis2 = openFileInput(lastChat);
            InputStreamReader isr = new InputStreamReader(fis2);
            BufferedReader br = new BufferedReader(isr);
            int gl = 0;
            String p1 = "";
            while ((p1 = br.readLine()) != null) {
                gl = 0;
                for (String s4 : last) {
                    if (p1.equals(s4)) {
                        gl = 1;
                        break;
                    }
                }
                if (gl == 0)
                    last.add(p1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dd : dataSnapshot.getChildren()) {
                    String zz = dd.getKey();
                    String t1 = zz.substring(0, zz.indexOf('^')), t2 = zz.substring(zz.indexOf('^') + 1);

                    if (t1.equals(a) || t2.equals(a)) {
                        int gl = 0;
                        for (String s4 : last) {
                            if (dd.getKey().equals(s4)) {
                                gl = 1;
                                break;
                            }
                        }
                        if (gl == 0) {
                            last.add(0, dd.getKey());
                            hh = hh + dd.getKey();
                        }
                    }
                }

                display();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void display() {
        try {
            t.removeAllViews();
            flab.setVisibility(View.VISIBLE);
            scr.setBackgroundColor(Color.WHITE);
            fl = 0;
            c = 100;
            int i = 0;
            for (String u : last) {
                unseen_message = 0;
                final TableRow tr = new TableRow(getApplicationContext());
                tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                final Button b = new Button(getApplicationContext());
                CircleImageView civ = new CircleImageView(getApplicationContext());
                String zz = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                if (zz.contains("."))
                    zz = zz.replace('.', '!');
                int gl = 0;
                for (String bl : blocks) {
                    if (bl.contains(u)) {
                        gl = 1;
                        break;
                    }
                }
                final int gg = gl;
                if (u.contains(zz)) {
                    final String t1 = u.substring(0, u.indexOf('^')), t2 = u.substring(u.indexOf('^') + 1);
                    if (t1.equals(zz)) {
                        db.child(u).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                unseen_message = 0;
                                for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                    User uu = ds1.getValue(User.class);
                                    assert uu != null;
                                    if (uu.email.contains(t2 + "\\"))
                                        unseen_message++;
                                }
                                if (gg == 1)
                                    unseen_message = 0;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        getname(t2, c);
                        Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                        b.setBackground(bac);
                        b.setPadding(15, 5, 25000, 10);
                        b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        b.setId(c);
                        civ.setId(100 * c);
                        c++;
                        tr.addView(civ);
                        tr.addView(b);
                    } else if (t2.equals(zz)) {
                        db.child(u).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot2) {
                                unseen_message = 0;
                                for (DataSnapshot ds1 : dataSnapshot2.getChildren()) {
                                    User uu = ds1.getValue(User.class);
                                    assert uu != null;
                                    if (uu.email.contains(t1 + "\\"))
                                        unseen_message++;
                                }
                                if (gg == 1)
                                    unseen_message = 0;
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                        getname(t1, c);
                        Drawable bac = getApplicationContext().getResources().getDrawable(R.drawable.chatbox);
                        b.setBackground(bac);
                        b.setPadding(15, 5, 25000, 10);
                        b.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                        b.setId(c);
                        civ.setId(100 * c);
                        c++;
                        tr.addView(civ);
                        tr.addView(b);
                    }
                }
                t.addView(tr);
                allname = allname + u;
            }
            /*if(i==name.length && first_time==0)
            {
                change();
                first_time=1;
                //begin();
            }*/
            try {
                checkClick();
                checkImClick();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getname(final String n, final int t) {
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                    Details u = d1.getValue(Details.class);
                    if (n.equals(d1.getKey())) {
                        CircleImageView civ = findViewById(100 * t);
                        try {
                            Glide.with(getApplicationContext()).load(u.l).into(civ);
                        } catch (Exception e) {
                            load();
                        }
                        Point size = new Point();
                        display.getSize(size);
                        civ.getLayoutParams().height = size.x / 7;
                        civ.getLayoutParams().width = size.x / 7;
                        civ.requestLayout();
                        civ.setLeft(5);
                        civ.setPadding(8, 8, 8, 8);
                        Button b = findViewById(t);
                        b.setTextColor(Color.BLACK);
                        b.setTransformationMethod(null);
                        assert u != null;
                        String s1 = u.n + "\t" + " (%" + unseen_message + "&" + d1.getKey() + u.d + ") " + "\n" + u.s;
                        SpannableString spannableString = new SpannableString(s1);
                        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), s1.indexOf('('), s1.lastIndexOf(')') + 1, 0);
                        if (unseen_message > 0) {
                            spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), s1.indexOf('%') + 1, s1.indexOf('&'), 0);
                            spannableString.setSpan(new BackgroundColorSpan(Color.GREEN), s1.indexOf('%') + 1, s1.indexOf('&'), 0);
                            unseen_message = 0;
                        }
                        b.setText(spannableString);
                        b.setLeft(10);
                        b.setTextSize(18);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void getEmail(final String n) {
        Intent Int = new Intent(getApplicationContext(), Chats.class);
        Int.putExtra("person", "" + n.substring(n.indexOf('&') + 1, n.lastIndexOf(')')) + "&" + n.substring(0, n.indexOf('(') - 1));
        finish();
        startActivity(Int);
    }

    public void checkClick() {
        for (int i = 100; i < c; i++) {
            final Button b = scr.findViewById(i);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getEmail("" + b.getText());

                }
            });
            b.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    longClick(b);
                    return false;
                }
            });
        }
    }

    public void longClick(final Button b) {
        AlertDialog.Builder alt = new AlertDialog.Builder(this);
        alt.setTitle("Warning!")
                .setCancelable(false)
                .setMessage("Are you sure you want to delete this chat?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            String n = b.getText().toString();
                            n = n.substring(n.indexOf('&') + 1, n.lastIndexOf('@'));
                            n = common(a, n);
                            String fname = n;
                            FileOutputStream fos = openFileOutput(fname, MODE_PRIVATE);
                            fos.write("".getBytes());

                            FileInputStream fis = openFileInput(lastChat);
                            InputStreamReader isr = new InputStreamReader(fis);
                            BufferedReader br = new BufferedReader(isr);
                            String p1 = "";
                            List<String> refresh = new ArrayList<>();
                            while ((p1 = br.readLine()) != null) {
                                if (!p1.equals(fname))
                                    refresh.add(p1);
                            }
                            p1 = "";
                            fos = openFileOutput(lastChat, MODE_PRIVATE);
                            for (String g : refresh) {
                                p1 = p1 + g + "\n";
                            }
                            fos.write(p1.getBytes());
                            Toast.makeText(getApplicationContext(), "Chat deleted", Toast.LENGTH_SHORT).show();
                            db.child(fname).removeValue();
                            load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog aaa = alt.create();
        aaa.show();
    }

    public String common(String a1, String p1) {
        int i = 0;
        String n = "";
        while (i < a1.length() && i < p1.length()) {
            if ((int) (a1.charAt(i)) < (int) (p1.charAt(i))) {
                n = "" + (a1 + "^" + p1);
                break;
            } else if ((int) (a1.charAt(i)) > (int) (p1.charAt(i))) {
                n = "" + (p1 + "^" + a1);
                break;
            } else {
                if (a1.length() < p1.length())
                    n = "" + (a1 + "^" + p1);
                else
                    n = "" + (p1 + "^" + a1);
            }
            i++;
        }
        return n;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inf = getMenuInflater();
        inf.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                openDialog();
                return true;
            case R.id.item3:
                AlertDialog.Builder alt = new AlertDialog.Builder(this);
                alt.setTitle("Warning!")
                        .setCancelable(false)
                        .setMessage("Are you sure you want to log out of this app?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                auth.signOut();
                                finish();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog a = alt.create();
                a.show();
                return true;
            case R.id.item2:
                finish();
                startActivity(new Intent(getApplicationContext(), Edit.class));
                return true;
            case R.id.about:
                finish();
                startActivity(new Intent(getApplicationContext(), About.class));
                return true;
            case R.id.settings:
                finish();
                startActivity(new Intent(getApplicationContext(), ChatableSettings.class));
                return true;
            case R.id.sync:
                last.clear();
                load();
            default:
                return super.onOptionsItemSelected(item);
        }

    }


    public void checkImClick() {
        for (int i = 100; i < c; i++) {
            final Button b = findViewById(i);
            final CircleImageView civ = findViewById(100 * i);
            try {
                civ.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flab.setVisibility(View.INVISIBLE);
                        LayoutInflater inflater = getLayoutInflater();
                        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.pic, null);
                        TextView txt = view.findViewById(R.id.txt);
                        final ImageView imv = view.findViewById(R.id.img);
                        String n = b.getText().toString();
                        scr.setBackgroundColor(Color.BLACK);
                        fl = 1;
                        final String file = n.substring(n.indexOf('&') + 1, n.indexOf('@'));
                        txt.setText(n.substring(0, n.indexOf('(') - 1));
                        df.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    Details u1 = ds.getValue(Details.class);
                                    if (file.equals(ds.getKey()))
                                        Glide.with(getApplicationContext()).load(u1.l).into(imv);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        t.removeAllViews();
                        t.addView(view);
                        txt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                load();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Picture changed", Toast.LENGTH_SHORT).show();
                load();
            }

        }
    }

    public void openDialog() {
        NewChat nc = new NewChat();
        nc.show(getSupportFragmentManager(), "New Chat");
    }

    @Override
    public void applyTexts(final String n) {
        final Intent Int = new Intent(getApplicationContext(), Chats.class);

        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                    Details u = d1.getValue(Details.class);
                    if (n.equals(d1.getKey())) {
                        Int.putExtra("person", n + u.d + "&" + u.n);
                        //finish();
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
        if (fl == 0) {
            finishAffinity();
        } else if (fl == 1) {
            load();
        }

    }

    @Override
    public void onClick(View v) {
        if (v == flab) {
            //finish();
            startActivity(new Intent(getApplicationContext(), MorePeople.class));
        }
    }
}