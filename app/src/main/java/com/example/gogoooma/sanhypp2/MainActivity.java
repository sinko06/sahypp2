package com.example.gogoooma.sanhypp2;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    final String folderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/KakaoTalk/Chats";
    final String folderFormat = "/KakaoTalk_Chats_";

    ListView listView;
    ArrayList<String> filePathList;
    ArrayAdapter<String> adapter;

    String newtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        listView = (ListView)findViewById(R.id.mainListView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String folder = folderName + folderFormat + filePathList.get(position);
                Intent intent = new Intent(getApplicationContext(), TalkActivity.class);
                intent.putExtra("folderPath", folder);
                startActivity(intent);
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filePathList = new ArrayList<>();
                subDirList(folderName);
                adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, filePathList);
                listView.setAdapter(adapter);
                //                Toast.makeText(MainActivity.this, folderName, Toast.LENGTH_SHORT).show();
//                String fileName = "talk.txt";
//                String content = "nooooob\n";
//                WriteTextFile(folderName, fileName, content);
                // folder Name : /KakaoTalk/Chats/
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getPermission();
    }

    public void subDirList(String source){
        File dir = new File(source);
        File[] fileList = dir.listFiles();
        try{
            for(int i = 0 ; i < fileList.length ; i++){
                File file = fileList[i];
                if(file.isDirectory()) {
                    filePathList.add(file.getName().substring(16));
                }
            }
        }catch(Exception e){

        }
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


    public void WriteTextFile(String foldername, String filename, String contents){
        try{
            File dir = new File (foldername);
            //디렉토리 폴더가 없으면 생성함
            if(!dir.exists()){
                dir.mkdir();
            }
            //파일 output stream 생성
            Toast.makeText(this, foldername+"/"+filename, Toast.LENGTH_SHORT).show();
            FileOutputStream fos = new FileOutputStream(foldername+"/"+filename, true);
            //파일쓰기
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(fos));
            writer.write(contents);
            writer.flush();

            writer.close();
            fos.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.READ_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }
}
