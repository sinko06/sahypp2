package com.example.gogoooma.sanhypp2;

public class Song_Item {
    private long mId; // 오디오 고유 ID
    private long AlbumId; // 오디오 앨범아트 ID
    private String Title; // 타이틀 정보
    private String Artist; // 아티스트 정보
    private String Album; // 앨범 정보
    private long Duration; // 재생시간
    private String DataPath; // 실제 데이터 위치

    public Song_Item() {
    }

    public Song_Item(long mId, long albumId, String title, String artist, String album, long duration, String dataPath, boolean checkBoxState) {
        this.mId = mId;
        AlbumId = albumId;
        Title = title;
        Artist = artist;
        Album = album;
        Duration = duration;
        DataPath = dataPath;

    }

    public long getmId() {
        return mId;
    }

    public void setmId(long mId) {
        this.mId = mId;
    }

    public long getAlbumId() {
        return AlbumId;
    }

    public void setAlbumId(long albumId) {
        AlbumId = albumId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public String getAlbum() {
        return Album;
    }

    public void setAlbum(String album) {
        Album = album;
    }

    public long getDuration() {
        return Duration;
    }

    public void setDuration(long duration) {
        Duration = duration;
    }

    public String getDataPath() {
        return DataPath;
    }

    public void setDataPath(String dataPath) {
        DataPath = dataPath;
    }

}

