package com.example.gogoooma.sanhypp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class chooseGenreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_genre);
        init();
    }

    public void init(){
        Button b1 = (Button)findViewById(R.id.sadButton);
        Button b2 = (Button)findViewById(R.id.positiveButton);
        Button b3 = (Button)findViewById(R.id.energeticButton);
        Button b4 = (Button)findViewById(R.id.loudButton);
        final Intent i = new Intent(this, CellMusic.class);

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