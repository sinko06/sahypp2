package com.example.gogoooma.sanhypp2;

public class MyMusic {
    String m_filename;
    String m_title;
    String m_tag;

    public MyMusic(String filename, String title, String tag){
        m_filename = filename;
        m_title = title;
        m_tag = tag;
    }

    public String getFilename() { return m_filename; }
    public String getTitle() { return m_title; }
    public String getTag() { return m_tag; }
}
