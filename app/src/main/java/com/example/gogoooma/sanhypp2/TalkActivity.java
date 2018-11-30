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

    // line 읽어오면서 라인 하나하나 다 저장
    ArrayList<String> txtList = new ArrayList<>();
    ArrayList<String> wordArr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);
        wordList = new HashMap<>();
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
        talkContent = (TextView) findViewById(R.id.talkContent);
        talkContent.setMovementMethod(new ScrollingMovementMethod());
        talkContent.setText("");
        ReadTextFile(folderPath, "KakaoTalkChats.txt");
        talkContent.setText("");
        //Toast.makeText(TalkActivity.this, "Please Waiting", Toast.LENGTH_SHORT).show();

        searchBinary();
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
                if(line.length() > 2 && txtList.size() > 2){
                    if(!line.substring(0,2).equals("20")) {
                        int idx = txtList.size() - 1;
                        txtList.set(idx, txtList.get(idx) + " " + line);
                        continue;
                    }
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
            int startdatenum = -60;
            int count = 0;
            GlobalVariable.dayList.clear();
            GlobalVariable.dayScore.clear();
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
                    Calendar cal1 = Calendar.getInstance();
                    cal1.add(cal1.DATE, startdatenum);
                    Date dayago = cal1.getTime();
                    String string = simpleDateFormat.format(dayago);
                    Date datetemp = simpleDateFormat.parse(string);
                    String date = newsimpleDateFormat.format(datetemp);

                    //Toast.makeText(getApplicationContext(),string,Toast.LENGTH_SHORT).show();
                    startdate = date;
                    GlobalVariable.dayAllList.add(startdate);
                }

            }
            GlobalVariable.dayAllScore.add(yourScore);
            Toast.makeText(this, GlobalVariable.dayAllList.size() + "", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void countWordFreq() {
        for(int i=0;i<GlobalVariable.dayAllScore.size();i++){
            if(!GlobalVariable.dayAllScore.get(i).equals(0))
            Toast.makeText(getApplicationContext(),String.valueOf(GlobalVariable.dayAllScore.get(i)),Toast.LENGTH_SHORT).show();
        }

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
}