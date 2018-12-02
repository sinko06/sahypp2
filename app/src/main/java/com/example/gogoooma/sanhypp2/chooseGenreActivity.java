package com.example.gogoooma.sanhypp2;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class chooseGenreActivity extends Fragment {
    View v;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_choose_genre, container, false);

        return v;
    }

    public void init(){
        ImageButton b1 = (ImageButton)v.findViewById(R.id.sadButton);
        ImageButton b2 = (ImageButton)v.findViewById(R.id.positiveButton);
        ImageButton b3 = (ImageButton)v.findViewById(R.id.energeticButton);
        ImageButton b4 = (ImageButton)v.findViewById(R.id.loudButton);
        final Intent i = new Intent(v.getContext(), CellMusic.class);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "sad");
                startActivity(i);
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "positive");
                startActivity(i);
            }
        });
        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "energetic");
                startActivity(i);
            }
        });
        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i.putExtra("str", "loud");
                startActivity(i);
            }
        });
    }
}