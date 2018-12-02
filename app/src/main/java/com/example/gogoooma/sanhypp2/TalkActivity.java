package com.example.gogoooma.sanhypp2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TalkActivity extends AppCompatActivity {
    TextView talkContent;

    String name = null;
    Long calDate;
    // 단어 저장할 변수
    HashMap<String, Integer> wordList;
    // 단어 잠깐 저장해둔 거
    AppCompatDialog progressDialog = null;
    Thread thread, thread2;

    // line 읽어오면서 라인 하나하나 다 저장
    ArrayList<String> txtList = new ArrayList<>();
    ArrayList<String> wordArr = new ArrayList<>();
    ArrayList<String> removeWord = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        wordList = new HashMap<>();

        Intent intent = getIntent();
        final String folderPath = intent.getStringExtra("folderPath");
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.analyzeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countWordFreq();
                RecordScore rs = new RecordScore();
                rs.fileName = name + ".txt";
                rs.contents = "hi";
                rs.WriteTextFile();
            }
        });
        talkContent = (TextView) findViewById(R.id.musicText);
        talkContent.setMovementMethod(new ScrollingMovementMethod());
        talkContent.setText("");

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Scanner scanner = new Scanner(getResources().openRawResource(R.raw.word));
                while (scanner.hasNextLine()) {
                    String wordListWord = scanner.nextLine();
                    int wordListScore = Integer.parseInt(scanner.nextLine());
                    if (wordListWord.length() > 1) {
                        if (wordListWord.substring(wordListWord.length() - 1, wordListWord.length()).equals("다"))
                            wordListWord = wordListWord.substring(0, wordListWord.length() - 1);
                        if (wordListWord.length() > 2)
                            if (wordListWord.substring(wordListWord.length() - 2, wordListWord.length()).equals("하고"))
                                wordListWord = wordListWord.substring(0, wordListWord.length() - 2);
                        wordList.put(wordListWord, wordListScore);
                        wordArr.add(wordListWord);
                    }
                }
                scanner.close();

                Scanner scanner2 = new Scanner(getResources().openRawResource(R.raw.removeword));
                while(scanner2.hasNextLine()){
                    String word = scanner2.nextLine();
                    removeWord.add(word);
                }

                scanner2.close();
                ReadTextFile(folderPath, "KakaoTalkChats.txt");
                searchBinary();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        talkContent.setText("대충 있어보이는 내용으로 채울 예정 ex) 오늘 한 말 수나 가장 긍정적인 말(점수max)이 들어간 대화 등 통계 대충 넣기");
                    }
                });
            }
        });
        thread.start();
        startProgress();
        talkContent.setText("");
    }

    public void ReadTextFile(String foldername, String filename) {
        try {
            File dir = new File(foldername);
            //디렉토리 폴더가 없으면 생성함
            if (!dir.exists()) {
                dir.mkdir();
            }
            FileInputStream fis = new FileInputStream(foldername + "/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            if (line != null) {
                name = line.substring(0, line.indexOf(" 님과"));
            }
            while ((line = br.readLine()) != null) {
                if (line.length() > 2 && txtList.size() > 2) {
                    if (!line.substring(0, 2).equals("20")) {
                        int idx = txtList.size() - 1;
                        Log.d(line, idx + "");
                        txtList.set(idx, txtList.get(idx) + " " + line);
                        continue;
                    }
                }
                if (line.length() >= 0 && line.length() <= 2  && txtList.size() > 2) {
                    int idx = txtList.size() - 1;
                    Log.d(line, idx + "");
                    txtList.set(idx, txtList.get(idx) + " " + line);
                    continue;
                }
                txtList.add(line);
            }
            br.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void searchBinary() {
        try {
            int startdatenum = -30;
            int count = 0;
            GlobalVariable.dayAllList.clear();
            GlobalVariable.dayAllScore.clear();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newsimpleDateFormat = new SimpleDateFormat("yyyy년 M월 d일");

            Calendar cal = Calendar.getInstance();
            cal.add(cal.DATE, startdatenum);

            Date startdayago = cal.getTime();
            String startstring = simpleDateFormat.format(startdayago);
            Date startdatetemp = simpleDateFormat.parse(startstring);
            String startdate = newsimpleDateFormat.format(startdatetemp);
            int high = 0;

            for (int i = 4; i < txtList.size(); i++) {
                if (txtList.get(i).indexOf("일") != -1 && txtList.get(i).indexOf("일") < 30) {
                    Date firstdate = newsimpleDateFormat.parse(txtList.get(i).substring(0, txtList.get(i).indexOf("일") + 1));
                    String strnewdate = simpleDateFormat.format(firstdate);
                    firstdate = simpleDateFormat.parse(strnewdate);

                    calDate = firstdate.getTime() - startdatetemp.getTime();
                    if (calDate >= 0) {
                        high = i;
                        break;
                    }
                }
            }

            GlobalVariable.dayAllList.add(startdate);

            boolean flag = false;
            int yourScore = 0;
            for (int i = high; i < txtList.size(); i++) {
                if (txtList.get(i).indexOf("일") == -1)
                    continue;
                if (txtList.get(i).contains(startdate)) {
                    flag = true;
                    for (int j = 0; j < wordArr.size(); j++) {
                        if (txtList.get(i).contains(wordArr.get(j))) {
                            int x = txtList.get(i).indexOf("회원님");
                            if (x > 20 && x < 30) {

                            } else {
                                yourScore += wordList.get(wordArr.get(i));
                            }

                        }
                    }
                } else {
                    if(!flag)
                        i--;
                    else
                        flag = false;
                        GlobalVariable.dayAllScore.add(yourScore);
                        yourScore = 0;
                        startdatenum++;
                        Calendar cal1= Calendar.getInstance();
                        cal1.add(cal1.DATE, startdatenum);
                        Date dayago = cal1.getTime();
                        String string = simpleDateFormat.format(dayago);
                        Date datetemp = simpleDateFormat.parse(string);
                        String date = newsimpleDateFormat.format(datetemp);
                        Log.d(date,"날짜");
                        Log.d(txtList.get(i),"내용");
                        Log.d(String.valueOf(startdatenum),"num");
                        //Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
                        startdate = date;
                        GlobalVariable.dayAllList.add(startdate);
                        cal1.clear();

                }

            }
            GlobalVariable.dayAllScore.add(yourScore);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void countWordFreq() {
        GlobalVariable.wordIUse.clear();
        GlobalVariable.wordYouUse.clear();
        for (int i = 0; i < txtList.size(); i++) {
            int idx = txtList.get(i).indexOf(" : ");
            if (idx > 0) {
                String str = txtList.get(i).substring(idx + 3);
                String[] words = str.split(" ");
                for (int j = 0; j < words.length; j++) {
                    if (txtList.get(i).substring(0, idx).contains("회원님")) {
                        if (GlobalVariable.wordIUse.get(words[j]) != null)
                            GlobalVariable.wordIUse.put(words[j], GlobalVariable.wordIUse.get(words[j]) + 1);
                        else
                            GlobalVariable.wordIUse.put(words[j], 1);
                    } else {
                        if (GlobalVariable.wordYouUse.get(words[j]) != null)
                            GlobalVariable.wordYouUse.put(words[j], GlobalVariable.wordYouUse.get(words[j]) + 1);
                        else
                            GlobalVariable.wordYouUse.put(words[j], 1);
                    }
                }
            }
        }
        GlobalVariable.myList = sortByValue(GlobalVariable.wordIUse);
        GlobalVariable.yourList = sortByValue(GlobalVariable.wordYouUse);

        for(int i=0;i<GlobalVariable.myList.size();i++){
            int check=0;
            for(int j=0;j<removeWord.size();j++){
                if(GlobalVariable.myList.get(i).equals(removeWord.get(j))){
                    check=1;
                }
            }
            if(check==1){
                GlobalVariable.myList.remove(i);
                i--;
            }
        }

        for(int i=0;i<GlobalVariable.yourList.size();i++){
            int check=0;
            for(int j=0;j<removeWord.size();j++){
                if(GlobalVariable.yourList.get(i).equals(removeWord.get(j))){
                    check=1;
                }
            }
            if(check==1){
                GlobalVariable.yourList.remove(i);
                i--;
            }
        }



        Intent intent = new Intent(getApplicationContext(), ChartActivity.class);
        startActivity(intent);


    }

    public static List sortByValue(final Map map) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list, new Comparator() {
            public int compare(Object o1, Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v2).compareTo(v1);
            }
        });

        return list;
    }

    private void startProgress() {
        progressON(this, "카카오톡 분석 중입니다...");

        thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    thread.join();
                } catch (Exception e) {
                }
                progressOFF();
            }
        });
        thread2.start();

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
}