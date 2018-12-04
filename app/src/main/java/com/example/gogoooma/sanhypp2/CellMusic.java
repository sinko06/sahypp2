package com.example.gogoooma.sanhypp2;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CellMusic extends AppCompatActivity {

    private ListView audiolistView; // 리스트뷰
    private ArrayList<Song_Item> songsList = null; // 데이터 리스트
    private ListViewAdapter listViewAdapter = null; // 리스트뷰에 사용되는 ListViewAdapter
    ArrayList<String> musicList = new ArrayList<>();
    ArrayList<String> musics = new ArrayList<>();
    AppCompatDialog progressDialog = null;
    MediaPlayer mp = new MediaPlayer();


    Context context;
    Thread thread;
    String gen;
    FloatingActionButton fabbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cell_music);
        context = this.getBaseContext();
        Intent i = getIntent();
        gen = i.getStringExtra("str");
        fabbutton = (FloatingActionButton) findViewById(R.id.analyzeFab1);

        songsList = new ArrayList<Song_Item>(); // ArrayList 생성
        audiolistView = (ListView) findViewById(R.id.my_listView);
        listViewAdapter = new ListViewAdapter(getApplicationContext()); // Adapter 생성
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                findSubFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath()), new ArrayList<File>());
                for (int idx = 0; idx < musicList.size(); idx++) {
                    try {
                        loadAudio(musicList.get(idx));
                    }catch (Exception e){}
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
                                    mp.setDataSource(musics.get(position));
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
                    }
                });
            }
        });
        thread.start();
        startProgress();
        // Adapter에 추가 데이터를 저장하기 위한 ArrayList

    }

    private void loadAudio(String fileStr) {
        Uri fileUri = Uri.parse(fileStr);
        String filePath = fileUri.getPath();
        Cursor c = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, "_data ='" + filePath + "'", null, null);
        c.moveToNext();
        if (c.getCount() > 0) {
            int id = c.getInt(0);
            Uri uri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id);
            Log.d("g", fileStr + "'s uri : " + uri.toString());
            Cursor cur = getContentResolver().query(uri, null, null, null, null);
            if (cur.moveToFirst()) {
                //   int artistColumn = cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST);
                long track_id = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media._ID));
                String title = cur.getString(cur.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE));
                String artist = cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ARTIST));
                String album = cur.getString(cur.getColumnIndex(MediaStore.Audio.AlbumColumns.ALBUM));
                long mDuration = cur.getLong(cur.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION));
                long albumId = cur.getLong(cur.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
                String datapath = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.DATA));
                try {
                    ArrayList<String> genre = new ArrayList<>();

                    if (gen.equals("sad")) {
                        Scanner s1 = new Scanner(getResources().openRawResource(R.raw.sad));
                        while (s1.hasNextLine()) {
                            genre.add(s1.nextLine());
                        }
                    } else if (gen.equals("loud")) {
                        Scanner s2 = new Scanner(getResources().openRawResource(R.raw.loud));
                        while (s2.hasNextLine()) {
                            genre.add(s2.nextLine());
                        }
                    } else if (gen.equals("energetic")) {
                        Scanner s3 = new Scanner(getResources().openRawResource(R.raw.energetic));
                        while (s3.hasNextLine()) {
                            genre.add(s3.nextLine());
                        }
                    } else if (gen.equals("positive")) {
                        Scanner s4 = new Scanner(getResources().openRawResource(R.raw.positive));
                        while (s4.hasNextLine()) {
                            genre.add(s4.nextLine());
                        }
                    }
                    MP3File mp3 = (MP3File) AudioFileIO.read(new File(fileStr));
                    Tag tag = mp3.getTag();
                    String g = tag.getFirst(FieldKey.GENRE);
                    if (genre.contains(g)) {
                        listViewAdapter.addItem(track_id, albumId, title, artist, album, mDuration, datapath);
                        musics.add(fileStr);
                    }
                } catch (Exception e2) {
                }
            }
        }
        c.close();
    }

    public void findSubFiles(File parentFile, ArrayList<File> subFiles) {
        if (parentFile.isFile()) {
            String fileName = parentFile.getName();
            try {
                if (fileName.substring(fileName.lastIndexOf('.') + 1).equals("mp3")) {
                    musicList.add(parentFile.getCanonicalPath());
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
            Bitmap albumArt = CellMusic.getArtworkQuick(context, (int) songItem.getAlbumId(), 100, 100);
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


    private void startProgress() {
        progressON(this, "음악 분석 중입니다...");
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
        img_loading_frame.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.music_loading));
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