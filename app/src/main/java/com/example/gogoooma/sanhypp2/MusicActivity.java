package com.example.gogoooma.sanhypp2;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends AppCompatActivity {
    final String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
    List<String> musicList;
    List<MyMusic> music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        init();
    }

    public void init(){
        music = new ArrayList<>();
        analyzeMusic();
    }

    // 폴더 내 음악파일명 읽어옴
    public void readMusic(){
        File dir = new File(musicFolder);
        File[] files = dir.listFiles();

        musicList = new ArrayList<>();

        for(int i=0;i<files.length; i++){
            musicList.add(files[i].getName());
        }
    }

    // 음악 분석
    public void analyzeMusic(){

    }
}
