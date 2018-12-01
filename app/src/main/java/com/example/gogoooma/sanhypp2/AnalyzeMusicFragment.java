package com.example.gogoooma.sanhypp2;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyzeMusicFragment extends Fragment {
    View v;
    int mPeakPos;
    Thread thread;
    AppCompatDialog progressDialog = null;
    final String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    ArrayList<File> fileList = new ArrayList<>();
    HashMap<File, Double> musicScoreMap = new HashMap();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_analyze_music, container, false);
        final File rootFile = new File(root);

        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ReadTextFile();
                findSubFiles(rootFile, fileList);
                for (int i = 0; i < fileList.size(); i++)
                    musicScoreMap.put(fileList.get(i), calcMusicScore(fileList.get(i)));
            }
        });

        thread.start();
        startProgress();

//        Toast.makeText(v.getContext(), musicScoreList.size()+"", Toast.LENGTH_SHORT).show();
        //startProgress();
        return v;
    }
    public void findSubFiles(File parentFile, ArrayList<File> subFiles) {
        if(parentFile.isFile()){
            String fileName = parentFile.getName();
            try {
                if (fileName.substring(fileName.lastIndexOf('.') + 1).equals("mp3")) {
                    if (!musicScoreMap.containsKey(parentFile)) {
                        fileList.add(parentFile);
                    }
                }
            }catch (Exception e){}
        } else if(parentFile.isDirectory()) {
            File[] childFiles = parentFile.listFiles();
            for (File childFile : childFiles) {
                findSubFiles(childFile, subFiles);
            }
        }
    }

    public double calcMusicScore(File file){
        double sum = 0;
        try {
            byte[] soundBytes;

            InputStream inputStream =
                    v.getContext().getContentResolver().openInputStream(Uri.fromFile(file));

            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);

            inputStream.close();
            double[] buffer = calculateFFT(soundBytes); // --> HERE ^__^
            for (int i = 0; i < buffer.length; i++) {
                double number = buffer[i];
                if (sum < number)
                    sum = number;
            }

        } catch (Exception e) {
        }
        String contents = file.getAbsolutePath() +"\r\n" + sum + "\r\n";
        new RecordScore().writeThisFile("music_analysis.txt", contents, true);
        return sum;
    }
    byte[] buffers = new byte[1024];
    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;

        while (read != -1) {
            read = in.read(buffers);
            if (read != -1)
                out.write(buffers, 0, read);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }
    public double[] calculateFFT(byte[] signal)
    {
        final int mNumberOfFFTPoints =1024;
        double mMaxFFTSample;

        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        double[] absSignal = new double[mNumberOfFFTPoints/2];

        for(int i = 0; i < mNumberOfFFTPoints; i++){
            temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp,0.0);
        }

        y = FFT.fft(complexSignal); // --> Here I use FFT class

        mMaxFFTSample = 0.0;
        mPeakPos = 0;
        for(int i = 0; i < (mNumberOfFFTPoints/2); i++)
        {
            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
            if(absSignal[i] > mMaxFFTSample)
            {
                mMaxFFTSample = absSignal[i];
                mPeakPos = i;
            }
        }

        return absSignal;

    }

    public void ReadTextFile() {
        try {
            String foldername = root + "/TodayOneMusic";
            File dir = new File(foldername);
            //디렉토리 폴더가 없으면 생성함
            if (!dir.exists()) {
                dir.mkdir();
            }
            FileInputStream fis = new FileInputStream(foldername + "/" + "music_analysis.txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, "UTF-8"));
            boolean flag = true;
            String line = "";
            String file = "";
            double score;
            while ((line = br.readLine()) != null) {
                if(flag) {
                    file = line;
                    flag = false;
                    continue;
                }
                else {
                    flag = true;
                    score = Double.parseDouble(line);
                    musicScoreMap.put(new File(file), score);
                }
            }
            br.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void startProgress() {
        progressON((MainActivity)getActivity(), "음악 분석 중입니다...");

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

}
