package com.example.gogoooma.sanhypp2;


import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyzeMusicFragment extends Fragment {
    View v;
    int mPeakPos;
    Thread thread;
    AppCompatDialog progressDialog = null;
    final String root = MainFragment.folderName;
    ArrayList<File> fileList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_analyze_music, container, false);
        File rootFile = new File(root);
        findSubFiles(rootFile, fileList);
        for (File file : fileList) {
            Toast.makeText(v.getContext(), file.getName(), Toast.LENGTH_SHORT).show();
        }
        return v;
    }
    public void findSubFiles(File parentFile, ArrayList<File> subFiles) {
        if(parentFile.isFile()){
            String fileName = parentFile.getName();
            if(fileName.substring(fileName.lastIndexOf('.')+1).equals("mp3"))
                subFiles.add(parentFile);
        } else if(parentFile.isDirectory()) {
            File[] childFiles = parentFile.listFiles();
            for (File childFile : childFiles) {
                findSubFiles(childFile, subFiles);
            }
        }
    }

    public double calcMusicScore(String file){
        double sum = 0;
        try {
            //String file = Environment.getExternalStorageDirectory() + "/TodayOneMusic/a11.mp3";
            byte[] soundBytes;

            InputStream inputStream =
                    v.getContext().getContentResolver().openInputStream(Uri.fromFile(new File(file)));

            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);

            File file2 = new File(Environment.getExternalStorageDirectory() + "/TodayOneMusic/music.txt");
            FileWriter os = new FileWriter(file2, true);

            os.write(sum + "\r\n");

            os.close();
            double[] buffer = calculateFFT(soundBytes); // --> HERE ^__^
            for (int i = 0; i < buffer.length; i++) {
                double number = buffer[i];
                if (sum < number)
                    sum = number;
            }

        } catch (Exception e) {
        }
        return sum;
    }

    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        byte[] buffer = new byte[1024];
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer, 0, read);
        }
        out.close();
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