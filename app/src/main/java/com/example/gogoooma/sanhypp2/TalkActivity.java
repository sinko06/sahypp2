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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TalkActivity extends AppCompatActivity {
    TextView talkContent;
    String newtext;
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
        talkContent.setText(newtext);
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
            StringBuffer buffer = new StringBuffer();
            while((line != null)){
                buffer.append(line + "\n");
                line = br.readLine();
            }

            br.close();
            fis.close();

            String alltext = buffer.toString();
            alltext = alltext.substring(40,alltext.length());
            br.close();
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

                int twostartloc = alltext.indexOf(twonewdate);
                int onestartloc = alltext.indexOf(onenewdate);
                int tostartloc = alltext.indexOf(tonewdate);
                if(twostartloc != -1){
                    newtext = alltext.substring(twostartloc,alltext.length());
                }else{
                    onestartloc = alltext.indexOf(onenewdate);
                    if(onestartloc != -1){
                        newtext = alltext.substring(onestartloc,alltext.length());
                    }else{
                        tostartloc = alltext.indexOf(tonewdate);
                        if(tostartloc != -1){
                            newtext = alltext.substring(tostartloc,alltext.length());
                        }else{
                            Toast.makeText(getApplicationContext(),"분석할 텍스트가 없습니다.",Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
