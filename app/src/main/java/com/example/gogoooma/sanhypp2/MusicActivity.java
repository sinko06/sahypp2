package com.example.gogoooma.sanhypp2;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MusicActivity extends AppCompatActivity {

    private Boolean isFabOpen = false;
    TextView tv1, tv2, tv3;
    ImageButton b1, b2, b3;

    private android.support.design.widget.FloatingActionButton fab, fab1, fab2, fab3;
    private android.view.animation.Animation fab_open, fab_close, rotate_forward, rotate_backward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);
        fab1 = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab3);
        tv1 = (TextView)findViewById(R.id.musicText);
        tv2 = (TextView)findViewById(R.id.musicText2);
        tv3 = (TextView)findViewById(R.id.musicText3);
        b1 = (ImageButton) findViewById(R.id.musicButton);
        b2 = (ImageButton) findViewById(R.id.musicButton2);
        b3 = (ImageButton) findViewById(R.id.musicButton3);

        fab_open = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fab_open);
        fab_close = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.fab_close);
        rotate_forward = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_forward);
        rotate_backward = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.rotate_backward);
        final FragmentManager manager = getFragmentManager();

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new AnalyzeMusicFragment()).commit();
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new Youtube()).commit();
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new chooseGenreActivity()).commit();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFAB();
            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new chooseGenreActivity()).commit();

            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new Youtube()).commit();

            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new AnalyzeMusicFragment()).commit();
            }
        });
    }

    public void clickFab(){
        tv1.setVisibility(View.GONE);
        tv2.setVisibility(View.GONE);
        tv3.setVisibility(View.GONE);
        b1.setVisibility(View.GONE);
        b2.setVisibility(View.GONE);
        b3.setVisibility(View.GONE);
    }


    public void animateFAB() {

        if (isFabOpen) {

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);

            isFabOpen = false;
            android.util.Log.d("Raj", "close");

        } else {
            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            isFabOpen = true;
            android.util.Log.d("Raj", "open");

        }
    }
}