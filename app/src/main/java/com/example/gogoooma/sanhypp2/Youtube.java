package com.example.gogoooma.sanhypp2;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Youtube extends Fragment {

    ArrayList<String> playlists;
    String urlWord;
    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_youtube, container, false);
        playlists = new ArrayList<String>(Arrays.asList("화날 때 듣는 노래", "우울할 때 듣는 노래", "잔잔한 노래", "기쁠 때 듣는 노래"));
        int score = 0;
        int globalScore = GlobalVariable.score;
        if(globalScore < -3) score = 0;
        else if(-3 <= globalScore && globalScore <= -1) score = 1;
        else if(0 < globalScore && globalScore <= 2) score = 2;
        else score = 3;
        try {
            urlWord = URLEncoder.encode(playlists.get(score),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebView webView = (WebView)v.findViewById(R.id.webView);
        String url = "https://www.youtube.com/results?search_query=" + urlWord;
        webView.loadUrl(url);

        return v;
    }
}
