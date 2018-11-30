package com.example.gogoooma.sanhypp2;


import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyzeFragment extends Fragment {
    View v = null;
    TextView textView = null;
    static String folderName = MainFragment.folderName;
    int score = 0;
    String name = null;
    AppCompatDialog progressDialog = null;

    // 단어 저장할 변수
    HashMap<String, Integer> wordList = null;
    // 단어 잠깐 저장해둔 거

    // line 읽어오면서 라인 하나하나 다 저장
    ArrayList<String> txtList = new ArrayList<>();
    ArrayList<String> wordArr = new ArrayList<>();
    Thread thread;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_analyze, container, false);

        textView = (TextView) v.findViewById(R.id.analyzeText);
        wordList = new HashMap<>();

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

                subDirList(folderName);
                //Toast.makeText(v.getContext(), "score = "+ score, Toast.LENGTH_SHORT).show();
            }
        });
        thread.start();
        startProgress();

        return v;
    }

    public void subDirList(String source) {
        File dir = new File(source);
        File[] fileList = dir.listFiles();
        try {
            for (int idx = 0; idx < fileList.length; idx++) {
                File file = fileList[idx];
                if (file.isDirectory()) {
                    ReadTextFile(file.getCanonicalPath(), "KakaoTalkChats.txt");
                    searchBinary();
                }
            }
        } catch (Exception e) {

        }
    }

    public void ReadTextFile(String foldername, String filename) {
        try {
            txtList.clear();
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
            int twoScore = 0;
            int oneScore = 0;
            int toScore = 0;
            for (int i = high; i < txtList.size(); i++) {
                //talkContent.append(txtList.get(i) + '\n');
                // name 이 안 들어가 있으면 pass
                //if(txtList.get(i).indexOf(", ") != -1){

                // word 안에 있는 단어가 들어가는지 하나하나 확인, 한 단어 두 번 들어가면 못 셈(수정필요)
                for (int j = 0; j < wordArr.size(); j++) {
                    // 단어가 들어가면 그 단어 가중치만큼 더함 ( 나중에는 가중치 직접 부여 )
                    if (txtList.get(i).contains(wordArr.get(j))) {
                        int x = txtList.get(i).indexOf("회원님");
                        if (txtList.get(i).contains(twonewdate)) {
                            if (x > 20 && x < 30) {
                                score += wordList.get(wordArr.get(j));
                                twoScore += wordList.get(wordArr.get(j));
                            }
                        } else if (txtList.get(i).contains(onenewdate)) {
                            if (x > 20 && x < 30) {
                                score += wordList.get(wordArr.get(j)) * 1.5;
                                oneScore += wordList.get(wordArr.get(j));
                            }
                        } else if (txtList.get(i).contains(tonewdate)) {
                            if (x > 20 && x < 30) {
                                score += wordList.get(wordArr.get(j)) * 2;
                                toScore += wordList.get(wordArr.get(j));
                            }
                        }
                    }
                }

            }
            GlobalVariable.dayScore.add(twoScore);
            GlobalVariable.dayScore.add(oneScore);
            GlobalVariable.dayScore.add(toScore);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void startProgress() {
        progressON((MainActivity) getActivity(), "카카오톡 분석 중입니다...");

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
