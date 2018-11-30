package com.example.gogoooma.sanhypp2;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;

public class PieChartActivity extends SimpleFragment {
    public static Fragment newInstance() {
        return new PieChartActivity();
    }
    private PieChart mChart;
    private PieChart mChart1;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_pie_chart, container, false);


        mChart = v.findViewById(R.id.pieChart1);
        mChart.getDescription().setEnabled(false);
        mChart1 = v.findViewById(R.id.pieChart2);
        mChart1.getDescription().setEnabled(false);

        //Typeface tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

//        mChart.setCenterTextTypeface(tf);
        mChart.setCenterText(generateCenterText());
        mChart.setCenterTextSize(10f);
        mChart1.setCenterText(generateCenterText1());
        mChart1.setCenterTextSize(10f);

//        mChart.setCenterTextTypeface(tf);

        // radius of the center hole in percent of maximum radius
        mChart.setHoleRadius(45f);
        mChart.setTransparentCircleRadius(50f);
        mChart1.setHoleRadius(45f);
        mChart1.setTransparentCircleRadius(50f);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);

        Legend k = mChart1.getLegend();
        k.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        k.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        k.setOrientation(Legend.LegendOrientation.VERTICAL);
        k.setDrawInside(false);

        mChart.setData(generatePieData());
        mChart1.setData(generatePieData2());

        return v;
    }

    private SpannableString generateCenterText() {
        String title = "나의 최빈단어";
        SpannableString s = new SpannableString(title);
        s.setSpan(new RelativeSizeSpan(2f), 0, title.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), title.length(), s.length(), 0);
        return s;
    }

    private SpannableString generateCenterText1() {
        String title = "상대방의 최빈단어";
        SpannableString s = new SpannableString(title);
        s.setSpan(new RelativeSizeSpan(2f), 0, title.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), title.length(), s.length(), 0);
        return s;
    }
}