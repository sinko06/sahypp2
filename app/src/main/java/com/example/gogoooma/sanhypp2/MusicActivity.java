package com.example.gogoooma.sanhypp2;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
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
    final String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
    List<String> musicList;
    List<String> genreList;
    List<String> titleList;

    List<String> sadGenre;
    List<String> positiveGenre;
    List<String> energeticGenre;
    List<String> loudGenre;
    private Boolean isFabOpen = false;
    TextView tv1, tv2, tv3;
    ImageButton b1, b2, b3;

    private android.support.design.widget.FloatingActionButton fab, fab1, fab2, fab3;
    private android.view.animation.Animation fab_open, fab_close, rotate_forward, rotate_backward;
    int score;

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
                Intent intent = new Intent(getApplicationContext(), Youtube.class);
                startActivity(intent);
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickFab();
                manager.beginTransaction().replace(R.id.content_music, new AnalyzeMusicFragment()).commit();
            }
        });


        init();
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

    public void init() {
        readMusic();
        analyzeMusic();

    }

    // 폴더 내 음악파일명 읽어옴
    public void readMusic() {
        File dir = new File(musicFolder);
        File[] files = dir.listFiles();

        musicList = new ArrayList<>();
        genreList = new ArrayList<>();
        titleList = new ArrayList<>();

        for (int i = 0; i < files.length; i++) {
            musicList.add(files[i].toString().substring(26));

            try {
                MP3File mp3 = (MP3File) AudioFileIO.read(files[i]);
                Tag tag = mp3.getTag();
                String g = tag.getFirst(FieldKey.GENRE);
                String t = tag.getFirst(FieldKey.TITLE);
                genreList.add(g);
                titleList.add(t);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 음악 분석
    public void analyzeMusic() {
        sadGenre = new ArrayList<>();
        loudGenre = new ArrayList<>();
        energeticGenre = new ArrayList<>();
        positiveGenre = new ArrayList<>();

        Scanner s1 = new Scanner(getResources().openRawResource(R.raw.sad));
        while (s1.hasNextLine()) {
            sadGenre.add(s1.nextLine());
        }
        Scanner s2 = new Scanner(getResources().openRawResource(R.raw.loud));
        while (s2.hasNextLine()) {
            loudGenre.add(s2.nextLine());
        }
        Scanner s3 = new Scanner(getResources().openRawResource(R.raw.energetic));
        while (s3.hasNextLine()) {
            energeticGenre.add(s3.nextLine());
        }
        Scanner s4 = new Scanner(getResources().openRawResource(R.raw.positive));
        while (s4.hasNextLine()) {
            positiveGenre.add(s4.nextLine());
        }

        for (int i = 0; i < (int) musicList.size() / 10; i++) {
            for (int j = 0; j < sadGenre.size(); j++) {
                if (sadGenre.get(j).equals(genreList.get(i))) {
                    GlobalVariable.music.add(new MyMusic(musicList.get(i), titleList.get(i), "sad"));
                    break;
                }
            }
            for (int j = 0; j < loudGenre.size(); j++) {
                if (loudGenre.get(j).equals(genreList.get(i))) {
                    GlobalVariable.music.add(new MyMusic(musicList.get(i), titleList.get(i), "loud"));
                    break;
                }
            }
            for (int j = 0; j < positiveGenre.size(); j++) {
                if (positiveGenre.get(j).equals(genreList.get(i))) {
                    GlobalVariable.music.add(new MyMusic(musicList.get(i), titleList.get(i), "positive"));
                    break;
                }
            }
            for (int j = 0; j < energeticGenre.size(); j++) {
                if (energeticGenre.get(j).equals(genreList.get(i))) {
                    GlobalVariable.music.add(new MyMusic(musicList.get(i), titleList.get(i), "energetic"));
                    break;
                }
            }
        }
    }
}