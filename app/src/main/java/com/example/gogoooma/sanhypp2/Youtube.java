package com.example.gogoooma.sanhypp2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class Youtube extends AppCompatActivity {

    ArrayList<String> playlists;
    String urlWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube);
        playlists = new ArrayList<String>(Arrays.asList("우울할 때 듣는 노래","기쁠 때 듣는 노래"));
        try {
            urlWord = URLEncoder.encode(playlists.get(0),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        WebView webView = (WebView)findViewById(R.id.webView);
        String url = "https://www.youtube.com/results?search_query=" + urlWord;
        webView.loadUrl(url);
    }
}
