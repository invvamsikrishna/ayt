package com.inv.ayt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    LineChart lineChart;
    LineData lineData;
    List<Entry> entryList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        lineChart = findViewById(R.id.linechart);
        entryList.add(new Entry(10,20));
        entryList.add(new Entry(5,10));
        entryList.add(new Entry(7,31));
        entryList.add(new Entry(3,14));

        LineDataSet lineDataSet = new LineDataSet(entryList,"country");
        lineDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        lineDataSet.setFillAlpha(110);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(100f);
        xAxis.setGridColor(getResources().getColor(R.color.black));

        YAxis yAxisRight = lineChart.getAxisRight();
        yAxisRight.setDrawAxisLine(true);
        yAxisRight.setDrawGridLines(true);
        yAxisRight.setGranularityEnabled(true);
        yAxisRight.setGridColor(getResources().getColor(R.color.black));

        YAxis yAxisLeft = lineChart.getAxisLeft();
        yAxisLeft.setDrawGridLines(true);
        yAxisLeft.setGridColor(getResources().getColor(R.color.black));


        lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
    }
}