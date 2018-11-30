package com.example.gogoooma.sanhypp2;

import android.os.Bundle;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class ChartActivity extends AppCompatActivity {
    ViewPager vp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        vp = (ViewPager)findViewById(R.id.viewPager);
        vp.setAdapter(new pagerAdapter(getSupportFragmentManager()));
    }

    private class pagerAdapter extends FragmentStatePagerAdapter
    {
        public pagerAdapter(android.support.v4.app.FragmentManager fm)
        {
            super(fm);
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position)
        {
            switch(position)
            {
                case 0:
                    return new PieChartActivity();
                case 1:
                    return new LineChartActivity1();
            }
            return null;
        }
        @Override
        public int getCount()
        {
            return 2;
        }
    }
}
