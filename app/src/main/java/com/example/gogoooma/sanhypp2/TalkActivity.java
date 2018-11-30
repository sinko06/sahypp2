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
    HashMap<String, Integer> wordIUse = new HashMap<>();
    HashMap<String, Integer> wordYouUse = new HashMap<>();
    String name = null;

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
            String wordListWord = scanner.nextLine();
            int wordListScore = Integer.parseInt(scanner.nextLine());
            wordList.put(wordListWord, wordListScore);
            wordArr[tempIdx] = wordListWord;
            tempIdx++;
        }
        scanner.close();

        Intent intent = getIntent();
        final String folderPath = intent.getStringExtra("folderPath");
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.analyzeFab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countWordFreq();
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
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat newsimpleDateFormat = new SimpleDateFormat("yyyy년 M월 d일");

            Calendar cal1 = Calendar.getInstance();
            cal1.add(cal1.DATE, -2);
            Date twodayago = cal1.getTime();
            String twostring = simpleDateFormat.format(twodayago);
            Date twodate = simpleDateFormat.parse(twostring);
            String twonewdate = newsimpleDateFormat.format(twodate);

            Calendar cal2 = Calendar.getInstance();
            cal2.add(cal2.DATE, -1);
            Date onedayago = cal2.getTime();
            String onestring = simpleDateFormat.format(onedayago);
            Date onedate = simpleDateFormat.parse(onestring);
            String onenewdate = newsimpleDateFormat.format(onedate);

            Calendar cal3 = Calendar.getInstance();
            cal3.add(cal3.DATE, 0);
            Date today = cal3.getTime();
            String tostring = simpleDateFormat.format(today);
            Date todate = simpleDateFormat.parse(tostring);
            String tonewdate = newsimpleDateFormat.format(todate);

            GlobalVariable.dayList.add(twonewdate);
            GlobalVariable.dayList.add(onenewdate);
            GlobalVariable.dayList.add(tonewdate);

            int high = txtList.size(), low = 0, mid;
            while (true) {
                mid = (high + low) / 2;
                if (txtList.get(mid).contains(twonewdate) || txtList.get(mid).contains(onenewdate)
                        || txtList.get(mid).contains(tonewdate)) {
                    high = mid;
                } else
                    low = mid;
                if (high - low <= 1)
                    break;
            }
            int myScore = 0;
            int yourScore = 0;
            int twoScore = 0;
            int oneScore = 0;
            int toScore = 0;
            for (int i = high; i < txtList.size(); i++) {
                talkContent.append(txtList.get(i) + '\n');
                // name 이 안 들어가 있으면 pass
                //if(txtList.get(i).indexOf(", ") != -1){

                // word 안에 있는 단어가 들어가는지 하나하나 확인, 한 단어 두 번 들어가면 못 셈(수정필요)
                for (int j = 0; j < tempIdx; j++) {
                    // 단어가 들어가면 그 단어 가중치만큼 더함 ( 나중에는 가중치 직접 부여 )
                    if (txtList.get(i).contains(wordArr[j])) {
                        int x = txtList.get(i).indexOf("회원님");
                        if (txtList.get(i).contains(twonewdate)) {
                            if (x>20 && x < 30) {
                                myScore += wordList.get(wordArr[j]);
                                twoScore += wordList.get(wordArr[j]);
                            }else
                                yourScore += wordList.get(wordArr[j]);
                        } else if (txtList.get(i).contains(onenewdate)) {
                            if (x>20 && x < 30){
                                myScore += wordList.get(wordArr[j]) * 1.5;
                                oneScore += wordList.get(wordArr[j]);
                            }else
                                yourScore += wordList.get(wordArr[j]) * 1.5;
                        } else if (txtList.get(i).contains(tonewdate)) {
                            if (x>20 && x < 30){
                                myScore += wordList.get(wordArr[j]) * 2;
                                toScore += wordList.get(wordArr[j]) * 2;
                            }else
                                yourScore += wordList.get(wordArr[j]) * 2;
                        }
                    }
                }
                GlobalVariable.dayScore.add(twoScore);
                GlobalVariable.dayScore.add(oneScore);
                GlobalVariable.dayScore.add(toScore);
            }
            Toast.makeText(TalkActivity.this, myScore + " : " + yourScore, Toast.LENGTH_SHORT).show();

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void countWordFreq(){
        for(int i=0; i<txtList.size(); i++){
            int idx = txtList.get(i).indexOf(" : ");
            if(idx > 0){
                String str = txtList.get(i).substring(idx + 3);
                String[] words = str.split(" ");
                for(int j=0; j<words.length; j++) {
                    if (txtList.get(i).substring(0, idx).contains("회원님")) {
                        if (wordIUse.get(words[j]) != null)
                            wordIUse.put(words[j], wordIUse.get(words[j]) + 1);
                        else
                            wordIUse.put(words[j], 1);
                    } else {
                        if (wordYouUse.get(words[j]) != null)
                            wordYouUse.put(words[j], wordYouUse.get(words[j]) + 1);
                        else
                            wordYouUse.put(words[j], 1);
                    }
                }
            }
        }
        List<String> myList = sortByValue(wordIUse);
        List<String> yourList = sortByValue(wordYouUse);
        talkContent.setText("");
        for(int i=0; i<15; i++) {
            talkContent.append("내가 "+(i+1)+"번째로 많이 한 말 : "+myList.get(i) + " = " + wordIUse.get(myList.get(i))+"번" + '\n');
        }
        talkContent.append("\n\n\n");
        for(int i=0; i<15; i++) {
            talkContent.append(name+"이(가) "+(i+1)+"번째로 많이 한 말 : "+yourList.get(i) + " = " + wordYouUse.get(yourList.get(i))+"번" + '\n');
        }
    }
    public static List sortByValue(final Map map) {
        List<String> list = new ArrayList();
        list.addAll(map.keySet());

        Collections.sort(list,new Comparator() {
            public int compare(Object o1,Object o2) {
                Object v1 = map.get(o1);
                Object v2 = map.get(o2);

                return ((Comparable) v2).compareTo(v1);
            }
        });

        return list;
    }
}