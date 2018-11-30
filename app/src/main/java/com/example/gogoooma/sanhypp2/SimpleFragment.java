package com.example.gogoooma.sanhypp2;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {
    private Typeface tf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        tf = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected BarData generateBarData() {


        ArrayList<IBarDataSet> sets = new ArrayList<IBarDataSet>();

        for(int i = 0; i < GlobalVariable.dayAllList.size(); i++) {
            ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
            entries.add(new BarEntry(i,(float) GlobalVariable.dayAllScore.get(i)));
            BarDataSet ds = new BarDataSet(entries, GlobalVariable.dayAllList.get(i));
            switch(i%60)
            {
                case 0:ds.setColor(Color.rgb(200,20,20));break;
                case 1:ds.setColor(Color.rgb(20 ,200,20));break;
                case 2:ds.setColor(Color.rgb(20,20,200));break;

            }
            sets.add(ds);
        }

        BarData d = new BarData(sets);
        d.setValueTypeface(tf);
        return d;
    }



    /**
     * generates less data (1 DataSet, 4 values)
     * @return
     */
    protected PieData generatePieData() {

        int sum =0;

        for(int i=0;i<7;i++){
            sum = sum + GlobalVariable.wordIUse.get(GlobalVariable.myList.get(i));
        }
        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();


        for(int i = 0; i < 7; i++) {
            entries1.add(new PieEntry((float)GlobalVariable.wordIUse.get(GlobalVariable.myList.get(i))/sum, GlobalVariable.myList.get(i)));
        }

        PieDataSet ds1 = new PieDataSet(entries1,  "나의 최빈단어");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);
        d.setValueTypeface(tf);

        return d;
    }

    protected PieData generatePieData2() {

        int sum =0;

        for(int i=0;i<7;i++){
            sum = sum + GlobalVariable.wordYouUse.get(GlobalVariable.yourList.get(i));
        }
        ArrayList<PieEntry> entries1 = new ArrayList<PieEntry>();


        for(int i = 0; i < 7; i++) {
            entries1.add(new PieEntry((float)GlobalVariable.wordYouUse.get(GlobalVariable.yourList.get(i))/sum, GlobalVariable.yourList.get(i)));
        }

        PieDataSet ds1 = new PieDataSet(entries1,  "상대방의 최빈단어");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextColor(Color.WHITE);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(ds1);
        d.setValueTypeface(tf);

        return d;
    }
}



