package com.example.gogoooma.sanhypp2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
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

    TextView cg;
    TextView pe;
    TextView yt;
    int score;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        init();
    }

    public void init() {
        readMusic();
        analyzeMusic();

        cg = (TextView)findViewById(R.id.choosegenre);
        pe = (TextView)findViewById(R.id.proposeEmotion);
        yt = (TextView)findViewById(R.id.youtube);

        cg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),chooseGenreActivity.class);
                startActivity(intent);
            }
        });

//        pe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), /*교희가 넣을 공간*/);
//                startActivity(intent);
//            }
//        });

        yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Youtube.class);
                startActivity(intent);
            }
        });
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

        for (int i = 0; i < (int)musicList.size()/10; i++) {
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