package com.example.gogoooma.sanhypp2;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    static final String folderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/KakaoTalk/Chats";

    ListView listView;
    ArrayList<String> filePathList;
    ArrayList<String> talkNameList;
    ArrayAdapter<String> adapter;

    View v;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) v.findViewById(R.id.mainListView);
        filePathList = new ArrayList<>();
        talkNameList = new ArrayList<>();
        subDirList(folderName);
        adapter = new ArrayAdapter<String>(v.getContext(),
                android.R.layout.simple_list_item_1, talkNameList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(v.getContext(), TalkActivity.class);
                intent.putExtra("folderPath", filePathList.get(position));
                startActivity(intent);
            }
        });

        return v;
    }
    public void subDirList(String source){
        File dir = new File(source);
        File[] fileList = dir.listFiles();
        try{
            for(int i = 0 ; i < fileList.length ; i++){
                File file = fileList[i];
                if(file.isDirectory()) {
                    String line = ReadFirstLine(file.getCanonicalPath(), "KakaoTalkChats.txt");
                    if(line != null) {
                        if(talkNameList.contains(line)){
                            for(int t=0; t<talkNameList.size(); t++){
                                if(talkNameList.get(t).equals(line)) {
                                    talkNameList.set(t, line);
                                    filePathList.set(t, file.getCanonicalPath());
                                }
                            }
                        }
                        else {
                            talkNameList.add(line);
                            filePathList.add(file.getCanonicalPath());
                        }
                    }
                }
            }
        }catch(Exception e){

        }
    }

    public String ReadFirstLine(String foldername, String filename){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            FileInputStream fis = new FileInputStream(foldername + "/" + filename);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();

            br.close();
            fis.close();
            return line;
        }catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
