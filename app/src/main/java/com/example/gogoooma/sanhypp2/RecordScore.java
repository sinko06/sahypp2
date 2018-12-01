package com.example.gogoooma.sanhypp2;

import android.os.Environment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class RecordScore {
    String name;
    String fileName;
    String contents;
    boolean overwrite;

    public void writeThisFile(String fileName, String contents, boolean overwrite){
        this.fileName = fileName;
        this.contents = contents;
        this.overwrite = overwrite;
        WriteTextFile();
    }

    public void WriteTextFile(){
        try{
            String foldername = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TodayOneMusic";
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            FileOutputStream fos = new FileOutputStream(foldername+"/"+fileName, overwrite);
            //파일쓰기
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}