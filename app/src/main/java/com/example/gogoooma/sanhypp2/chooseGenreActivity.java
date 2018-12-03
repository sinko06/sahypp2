package com.example.gogoooma.sanhypp2;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class chooseGenreActivity extends Fragment {
    View v;
    Thread thread;
    AppCompatDialog progressDialog = null;
    final String musicFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Music";
    List<String> musicList;
    List<String> genreList;
    List<String> titleList;

    List<String> sadGenre;
    List<String> positiveGenre;
    List<String> energeticGenre;
    List<String> loudGenre;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_choose_genre, container, false);
        init();

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                init2();
            }
        });
        thread.start();
        startProgress();

        return v;
    }

    public void init2() {
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
    private void startProgress() {
        progressON((MusicActivity) getActivity(), "음악 분석 중입니다...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread.join();
                } catch (Exception e) {
                }
                progressOFF();
            }
        }).start();

    }

    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }


        if (progressDialog != null && progressDialog.isShowing()) {
            progressSET(message);
        } else {

            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.fragment_loading);
            progressDialog.show();

        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        img_loading_frame.setBackground(ContextCompat.getDrawable(v.getContext(), R.drawable.music_loading));
        final AnimationDrawable frameAnimation = (AnimationDrawable) img_loading_frame.getBackground();
        img_loading_frame.post(new Runnable() {
            @Override
            public void run() {
                frameAnimation.start();
            }
        });

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }

    public void progressSET(String message) {

        if (progressDialog == null || !progressDialog.isShowing()) {
            return;
        }

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }

    }

    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public void init(){
        ImageButton b1 = (ImageButton)v.findViewById(R.id.sadButton);
        ImageButton b2 = (ImageButton)v.findViewById(R.id.positiveButton);
        ImageButton b3 = (ImageButton)v.findViewById(R.id.energeticButton);
        ImageButton b4 = (ImageButton)v.findViewById(R.id.loudButton);
        final Intent i = new Intent(v.getContext(), CellMusic.class);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "sad");
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "positive");
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "energetic");
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "loud");
                startActivity(i);
            }
        });
    }
}