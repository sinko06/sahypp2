package com.example.gogoooma.sanhypp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;

public class TalkActivity extends AppCompatActivity {
    TextView talkContent;
    // 단어 저장할 변수
    HashMap<String, Integer> wordList;
    // 단어 잠깐 저장해둔 거
    String[] wordArr = new String[1000];
    // 단어 개수 비교하려고 임시 인덱스
    int tempIdx = 0;
    // line 읽어오면서 라인 하나하나 다 저장

    ArrayList<String> txtList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        wordList = new HashMap<>();
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.word));
        while (scanner.hasNextLine()) {
            String str = scanner.nextLine();

            wordList.put(str, (int)(Math.random() * 4) + 1);
            wordArr[tempIdx] = str;
            tempIdx++;
            Toast.makeText(getApplicationContext(),String.valueOf(tempIdx),Toast.LENGTH_SHORT).show();

        }

        scanner.close();

        Intent intent = getIntent();
        final String folderPath = intent.getStringExtra("folderPath");
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.analyzeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(TalkActivity.this, "Please Waiting", Toast.LENGTH_SHORT).show();
                String name = "이동준";
                int score = 0;
                for(int i=0; i<txtList.size(); i++){
                    // name 이 안 들어가 있으면 pass
                    if(txtList.get(i).indexOf(name + " : ") != -1){
                        // word 안에 있는 단어가 들어가는지 하나하나 확인, 한 단어 두 번 들어가면 못 셈(수정필요)
                        for(int j=0; j<tempIdx; j++){
                            // 단어가 들어가면 그 단어 가중치만큼 더함 ( 나중에는 가중치 직접 부여 )
                            if(txtList.get(i).indexOf(wordArr[j]) != -1)
                                score += wordList.get(wordArr[j]);
                        }
                    }
                }
                Toast.makeText(TalkActivity.this, score+"", Toast.LENGTH_SHORT).show();
            }
        });
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat newsimpleDateFormat = new SimpleDateFormat("yyyy년 M월 d일");

                try {
                    Calendar cal1 = Calendar.getInstance();
                    cal1.add(cal1.DATE,-2);
                    Date twodayago = cal1.getTime();
                    String twostring = simpleDateFormat.format(twodayago);
                    Date twodate = simpleDateFormat.parse(twostring);
                    String twonewdate = newsimpleDateFormat.format(twodate);

                    Calendar cal2 = Calendar.getInstance();
                    cal2.add(cal2.DATE,-1);
                    Date onedayago = cal2.getTime();
                    String onestring = simpleDateFormat.format(onedayago);
                    Date onedate = simpleDateFormat.parse(onestring);
                    String onenewdate = newsimpleDateFormat.format(onedate);

                    Calendar cal3 = Calendar.getInstance();
                    cal3.add(cal3.DATE,0);
                    Date today = cal3.getTime();
                    String tostring = simpleDateFormat.format(today);
                    Date todate = simpleDateFormat.parse(tostring);
                    String tonewdate = newsimpleDateFormat.format(todate);

                    if(line.contains(twonewdate) || line.contains(onenewdate) || line.contains(tonewdate)){
                        talkContent.append('\n' + line);
                        txtList.add(line);
                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            br.close();
            fis.close();

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}