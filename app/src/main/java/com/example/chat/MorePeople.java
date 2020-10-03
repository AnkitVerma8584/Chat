package com.example.chat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class MorePeople extends AppCompatActivity {

    FirebaseAuth auth;
    DatabaseReference db, df, dl;
    private TableLayout t;
    String u;
    int c = 100;
    Display display;
    LoadingDialog loadingDialog = new LoadingDialog(MorePeople.this);
    ScrollView scr;
    int fl = 0;
    Details details;
    FloatingActionButton flab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_box);
        getSupportActionBar().setTitle("Global");
        auth = FirebaseAuth.getInstance();
        t = findViewById(R.id.table);
        db = FirebaseDatabase.getInstance().getReference().child("ChatBox");
        df = FirebaseDatabase.getInstance().getReference().child("Users");
        dl = FirebaseDatabase.getInstance().getReference().child("Last");
        display = getWindowManager().getDefaultDisplay();
        scr = findViewById(R.id.full);
        flab = findViewById(R.id.fab);
        flab.setVisibility(View.INVISIBLE);
        start();
    }

    public void start() {
        fl = 0;
        try {
            loadingDialog.startLoadingDialog();
            df.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    t.removeAllViews();
                    scr.setBackgroundColor(Color.WHITE);
                    fl = 0;
                    c = 100;
                    for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                        u = d1.getKey();
                        details = d1.getValue(Details.class);
                        final TableRow tr = new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));
                        final Button b = new Button(getApplicationContext());
                        CircleImageView civ = new CircleImageView(getApplicationContext());
                        String zz = auth.getCurrentUser().getEmail().substring(0, auth.getCurrentUser().getEmail().indexOf('@'));
                        if (zz.contains("."))
                            zz = zz.replace('.', '!');
                        if (!u.equals(zz)) {
                            getname(u, c);
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
                        t.addView(tr);

                    }
                    loadingDialog.dismissDialog();
                    try {
                        checkClick();
                        checkImClick();
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void getname(final String n, final int t1) {
        df.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot d1 : dataSnapshot.getChildren()) {
                    Details u = d1.getValue(Details.class);
                    if (n.equals(d1.getKey())) {
                        CircleImageView civ = findViewById(100 * t1);
                        try {
                            Glide.with(getApplicationContext()).load(u.l).into(civ);
                        } catch (Exception e) {
                            start();
                        }
                        Point size = new Point();
                        display.getSize(size);
                        civ.getLayoutParams().height = size.x / 7;
                        civ.getLayoutParams().width = size.x / 7;
                        civ.requestLayout();
                        civ.setLeft(5);
                        civ.setPadding(8, 8, 8, 8);
                        Button b = findViewById(t1);
                        b.setTextColor(Color.BLACK);
                        b.setTransformationMethod(null);
                        String n2;
                        if (n.contains("!"))
                            n2 = n.replace('!', '.');
                        else
                            n2 = n;
                        String s1 = u.n + "\t" + " (%" + "&" + "\n" + n2 + u.d + ") " + "\n" + u.s;
                        SpannableString spannableString = new SpannableString(s1);
                        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), s1.indexOf('('), s1.indexOf('&') + 1, 0);
                        spannableString.setSpan(new ForegroundColorSpan(Color.WHITE), s1.indexOf(')'), s1.indexOf(')') + 1, 0);
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
        String p = n.substring(n.indexOf('\n') + 1, n.indexOf(')'));
        if (p.contains("."))
            p = p.replace('.', '!');
        Int.putExtra("person", "" + p + "&" + n.substring(0, n.indexOf('(') - 1));
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
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.pic, null);
                        TextView txt = view.findViewById(R.id.txt);
                        final ImageView imv = view.findViewById(R.id.img);
                        String n = b.getText().toString();
                        scr.setBackgroundColor(Color.BLACK);
                        fl = 1;
                        String file1 = n.substring(n.indexOf('\n') + 1, n.indexOf('@'));
                        if (file1.contains("."))
                            file1 = file1.replace('.', '!');
                        final String file = file1;
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
                                start();
                            }
                        });
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Picture changed", Toast.LENGTH_SHORT).show();
                start();
            }

        }
    }

    @Override
    public void onBackPressed() {
        if (fl == 0) {
            finish();
            startActivity(new Intent(getApplicationContext(), ChatBox.class));
        } else if (fl == 1) {
            start();
        }
    }
}
