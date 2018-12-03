package com.example.gogoooma.sanhypp2;


import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class AnalyzeMusicFragment extends Fragment {
    View v;
    int mPeakPos;
    Thread thread;
    TextView textView;
    AppCompatDialog progressDialog = null;
    final String root = Environment.getExternalStorageDirectory().getAbsolutePath();
    ArrayList<File> fileList = new ArrayList<>();
    HashMap<String, Double> musicScoreMap = new HashMap();
    private ListView audiolistView; // 리스트뷰
    private ArrayList<Song_Item> songsList = null; // 데이터 리스트
    private ListViewAdapter listViewAdapter = null; // 리스트뷰에 사용되는 ListViewAdapter
    ArrayList<String> arr1 = new ArrayList<>();
    ArrayList<Double> arr2 = new ArrayList<>();
    ArrayList<String> musicFile = new ArrayList<>();
    MediaPlayer mp = new MediaPlayer();
    FloatingActionButton fabbutton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_cell_music, container, false);
        final File rootFile = new File(root);
        fabbutton = (FloatingActionButton) v.findViewById(R.id.analyzeFab1);
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ReadTextFile();
                findSubFiles(rootFile, fileList);
                try {
                    for (int i = 0; i < fileList.size(); i++)
                        musicScoreMap.put(fileList.get(i).getCanonicalPath(), calcMusicScore(fileList.get(i)));
                }catch (Exception e){

                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Iterator iterator = sortByValue(musicScoreMap).iterator();
                        while(iterator.hasNext()) {
                            String temp = (String) iterator.next();
                            arr1.add(temp);
                            arr2.add(musicScoreMap.get(temp));
                        }

                        // Adapter에 추가 데이터를 저장하기 위한 ArrayList
                        songsList = new ArrayList<Song_Item>(); // ArrayList 생성
                        audiolistView = (ListView) v.findViewById(R.id.my_listView);
                        listViewAdapter = new ListViewAdapter(v.getContext()); // Adapter 생성
                        audiolistView.setAdapter(listViewAdapter); // 어댑터를 리스트뷰에 세팅
                        audiolistView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                        audiolistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    if(mp.isPlaying()) {
                                        mp.stop();
                                    }
                                    mp = new MediaPlayer();
                                    mp.setDataSource(musicFile.get(position));
                                    fabbutton.setImageResource(android.R.drawable.ic_media_pause);
                                    mp.prepare();
                                    mp.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        fabbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(mp.isPlaying()) {
                                    fabbutton.setImageResource(android.R.drawable.ic_media_play);
                                    mp.pause();
                                }else{
                                    fabbutton.setImageResource(android.R.drawable.ic_media_pause);
                                    mp.start();
                                }

                            }
                        });

                        for(int i=0; i<arr2.size(); i++) {
                            double score = (arr2.get(i)-30)/5 - 60;
                            int glovalScore = GlobalVariable.score;
                            if(glovalScore - 10 <= score && score <= glovalScore + 10) {
                                try {
                                    getMusicMetaInfo(arr1.get(i));
                                }catch (Exception e){}
                            }
                        }
                    }

                });
            }
        });

        thread.start();
        startProgress();
        return v;
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

    public void findSubFiles(File parentFile, ArrayList<File> subFiles) {
        if (parentFile.isFile()) {
            String fileName = parentFile.getName();
            try {
                if (fileName.substring(fileName.lastIndexOf('.') + 1).equals("mp3")) {
                    if (!musicScoreMap.containsKey(parentFile.getCanonicalPath())) {
                        fileList.add(parentFile);
                    }
                }
            } catch (Exception e) {
            }
        } else if (parentFile.isDirectory()) {
            File[] childFiles = parentFile.listFiles();
            for (File childFile : childFiles) {
                findSubFiles(childFile, subFiles);
            }
        }
    }

    public double calcMusicScore(File file) {
        double sum = 0;
        try {
            //String file = Environment.getExternalStorageDirectory() + "/TodayOneMusic/a11.mp3";
            byte[] soundBytes;

            InputStream inputStream =
                    v.getContext().getContentResolver().openInputStream(Uri.fromFile(file));

            soundBytes = new byte[inputStream.available()];
            soundBytes = toByteArray(inputStream);

            inputStream.close();
            //File file2 = new File(Environment.getExternalStorageDirectory() + "/TodayOneMusic/music.txt");
            //FileWriter os = new FileWriter(file2, true);
            //os.write(sum + "\r\n");
            //os.close();
            double[] buffer = calculateFFT(soundBytes); // --> HERE ^__^
            for (int i = 0; i < buffer.length; i++) {
                double number = buffer[i];
                if (sum < number)
                    sum = number;
            }

        } catch (Exception e) {
        }
        try {
            String contents = file.getAbsolutePath() + "\r\n" + sum + "\r\n";
            new RecordScore().writeThisFile("music_analysis.txt", contents, true);
        } catch (Exception e5) {
        }
        return sum;
    }

    byte[] buffer = new byte[1024];
    public byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int read = 0;
        while (read != -1) {
            read = in.read(buffer);
            if (read != -1)
                out.write(buffer, 0, read);
        }
        out.close();
        return out.toByteArray();
    }

    public double[] calculateFFT(byte[] signal) {
        final int mNumberOfFFTPoints = 1024;
        double mMaxFFTSample;

        double temp;
        Complex[] y;
        Complex[] complexSignal = new Complex[mNumberOfFFTPoints];
        double[] absSignal = new double[mNumberOfFFTPoints / 2];

        for (int i = 0; i < mNumberOfFFTPoints; i++) {
            temp = (double) ((signal[2 * i] & 0xFF) | (signal[2 * i + 1] << 8)) / 32768.0F;
            complexSignal[i] = new Complex(temp, 0.0);
        }

        y = FFT.fft(complexSignal); // --> Here I use FFT class

        mMaxFFTSample = 0.0;
        mPeakPos = 0;
        for (int i = 0; i < (mNumberOfFFTPoints / 2); i++) {
            absSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
            if (absSignal[i] > mMaxFFTSample) {
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
                if (flag) {
                    file = line;
                    flag = false;
                    continue;
                } else {
                    flag = true;
                    score = Double.parseDouble(line);
                    musicScoreMap.put(file, score);
                }
            }
            br.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startProgress() {
        progressON((MusicActivity) getActivity(), "음악 분석 중입니다...");
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

    private void getMusicMetaInfo (String fileStr)
    {
        Uri fileUri = Uri.parse(fileStr);
        String filePath = fileUri.getPath();
        Cursor c = v.getContext().getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,"_data ='" + filePath + "'",null,null);
        c.moveToNext();
        if(c.getCount()>0) {
            int id = c.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,id);
            Log.d("g", fileStr + "'s uri : " + uri.toString());
            Cursor cur = v.getContext().getContentResolver().query(uri, null, null, null, null);
            if(cur.moveToFirst()) {
                //   int artistColumn = cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST);
                long track_id = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID));
                String title =  cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST));
                String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
                long mDuration = cur.getLong(cur.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                long albumId = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String datapath = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));

                listViewAdapter.addItem(track_id, albumId, title, artist, album, mDuration, datapath);
                musicFile.add(fileStr);
            }
        }
        c.close();
    }

    class ViewHolder {
        public ImageView mImgAlbumArt;
        public TextView mTitle;
        public TextView mSubTitle;
        public TextView mDuration;
    }

    private class ListViewAdapter extends BaseAdapter {
        Context context;

        public ListViewAdapter(Context context) {
            this.context = context;
        }

        // 음악 데이터 추가를 위한 메소드
        public void addItem(long mId, long AlbumId, String Title, String Artist, String Album, long Duration, String DataPath) {
            Song_Item item = new Song_Item();
            item.setmId(mId);
            item.setAlbumId(AlbumId);
            item.setTitle(Title);
            item.setArtist(Artist);
            item.setAlbum(Album);
            item.setDuration(Duration);
            item.setDataPath(DataPath);
            songsList.add(item);
        }


        @Override
        public int getCount() {
            return songsList.size(); // 데이터 개수 리턴
        }

        @Override
        public Object getItem(int position) {
            return songsList.get(position);
        }

        // 지정한 위치(position)에 있는 데이터 리턴
        @Override
        public long getItemId(int position) {
            return position;
        }

        // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            final Context context = parent.getContext();
            final Integer index = Integer.valueOf(position);

            // 화면에 표시될 View
            if (convertView == null) {
                viewHolder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.song_item, parent, false);

                convertView.setBackgroundColor(0x00FFFFFF);
                convertView.invalidate();

                // 화면에 표시될 View 로부터 위젯에 대한 참조 획득
                viewHolder.mImgAlbumArt = (ImageView) convertView.findViewById(R.id.album_Image);
                viewHolder.mTitle = (TextView) convertView.findViewById(R.id.txt_title);
                viewHolder.mSubTitle = (TextView) convertView.findViewById(R.id.txt_subtitle);
                viewHolder.mDuration = (TextView) convertView.findViewById(R.id.txt_duration);

                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            // PersonData 에서 position 에 위치한 데이터 참조 획득
            final Song_Item songItem = songsList.get(position);

            // 아이템 내 각 위젯에 데이터 반영
            Bitmap albumArt = getArtworkQuick(context, (int) songItem.getAlbumId(), 100, 100);
            if (albumArt != null) {
                viewHolder.mImgAlbumArt.setImageBitmap(albumArt);
            }
            viewHolder.mTitle.setText(songItem.getTitle());
            viewHolder.mSubTitle.setText(songItem.getArtist());

            int dur = (int) songItem.getDuration();
            int hrs = (dur / 3600000);
            int mns = (dur / 60000) % 60000;
            int scs = dur % 60000 / 1000;
            String songTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
            if (hrs == 0) {
                songTime = String.format("%02d:%02d", mns, scs);
            }
            viewHolder.mDuration.setText(songTime);


            return convertView;
        }

    }

    /* Album ID로 부터 Bitmap 이미지를 생성해 리턴해 주는 메소드 */
    public static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    public static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    // Get album art for specified album. This method will not try to
    // fall back to getting artwork directly from the file, nor will it attempt to repair the database.
    public static Bitmap getArtworkQuick(Context context, int album_id, int w, int h) {
        w -= 2;
        h -= 2;
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            ParcelFileDescriptor fd = null;
            try {
                fd = res.openFileDescriptor(uri, "r");
                int sampleSize = 1;

                sBitmapOptionsCache.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);
                int nextWidth = sBitmapOptionsCache.outWidth >> 1;
                int nextHeight = sBitmapOptionsCache.outHeight >> 1;
                while (nextWidth > w && nextHeight > h) {
                    sampleSize <<= 1;
                    nextWidth >>= 1;
                    nextHeight >>= 1;
                }

                sBitmapOptionsCache.inSampleSize = sampleSize;
                sBitmapOptionsCache.inJustDecodeBounds = false;
                Bitmap b = BitmapFactory.decodeFileDescriptor(
                        fd.getFileDescriptor(), null, sBitmapOptionsCache);

                if (b != null) {
                    // finally rescale to exactly the size we need
                    if (sBitmapOptionsCache.outWidth != w || sBitmapOptionsCache.outHeight != h) {
                        Bitmap tmp = Bitmap.createScaledBitmap(b, w, h, true);
                        b.recycle();
                        b = tmp;
                    }
                }

                return b;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fd != null)
                        fd.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}