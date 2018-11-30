package com.example.gogoooma.sanhypp2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class Choose extends AppCompatActivity {
    TextView cellmusic;
    TextView youtube;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        cellmusic = (TextView)findViewById(R.id.cellmusic);
        youtube = (TextView)findViewById(R.id.youtube);

        cellmusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CellMusic.class);
                startActivity(intent);
            }
        });

        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),Youtube.class);
                startActivity(intent);
            }
        });
    }
}
