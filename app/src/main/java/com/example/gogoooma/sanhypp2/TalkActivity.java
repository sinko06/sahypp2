package com.example.gogoooma.sanhypp2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class TalkActivity extends AppCompatActivity {
    TextView talkContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        Intent intent = getIntent();
        String folderPath = intent.getStringExtra("folderPath");
        talkContent = (TextView)findViewById(R.id.talkContent);
        talkContent.setMovementMethod(new ScrollingMovementMethod());
        talkContent.setText("");
        ReadTextFile(folderPath, "KakaoTalkChats.txt");
    }

    public void ReadTextFile(String foldername, String filename){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            FileInputStream fis = new FileInputStream(foldername + "/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = "";
            while((line = br.readLine()) != null){
                talkContent.append('\n' + line);
            }
            br.close();
            fis.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
