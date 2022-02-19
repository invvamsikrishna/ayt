package com.inv.ayt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.UUID;

public class ViewEcgActivity extends AppCompatActivity {


    Runnable commRunnable;
    public int control_a = 0;
    public XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    public int flag_stop = 0;
    public LinearLayout layout;
    public GraphicalView mChart;
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    public int ready_bt;
    public int size_AxisTitleTextSize = 20;
    public int size_ChartTitleTextSize = 20;
    public int size_LabelsTextSize = 20;
    public int size_LineWidth = 5;
    public XYSeries emptyBuffer = new XYSeries("X Series");

    public Uri uri_file;
    public double[] vector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_ecg);

        layout = (LinearLayout) findViewById(R.id.viewGraph);

        String filepath = getIntent().getStringExtra("filepath");
        uri_file = Uri.fromFile(new File(filepath));
        vector = readFromFile();

        for (int i = 0; i < vector.length; i++) {
            emptyBuffer.add(i, vector[i]);
        }

        ready_bt = 1;
    }

    private double[] readFromFile() {
        ArrayList<Double> ret = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri_file);
            if (inputStream != null) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                new StringBuilder();
                while (true) {
                    String readLine = bufferedReader.readLine();
                    String receiveString = readLine;
                    if (readLine == null) {
                        break;
                    }
                    ret.add(Double.parseDouble(receiveString));
                }
                inputStream.close();
            } else {
                Toast.makeText(getApplication(), "Error opening file!", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e("file input_read", e.getMessage());
            Toast.makeText(getApplication(), "Error opening file!", Toast.LENGTH_SHORT).show();
        }
        if (ret.size() > 0) {
            double[] array = new double[ret.size() - 1];
            for (int i = 0; i < (ret.size() - 1); i++) {
                array[i] = ret.get(i);
            }
            return array;
        } else {
            double[] array = new double[0];
            return array;
        }
    }


    public ViewEcgActivity() {

        this.commRunnable = new Runnable() {
            public void run() {
                XYSeriesRenderer Xrenderer1 = new XYSeriesRenderer();
                Xrenderer1.setColor(-16776961);
                Xrenderer1.setLineWidth((float) ViewEcgActivity.this.size_LineWidth);
                Xrenderer1.setDisplayChartValues(false);

                ViewEcgActivity.this.mRenderer.setMarginsColor(-1);
                ViewEcgActivity.this.mRenderer.setXLabelsColor(-16777216);
                ViewEcgActivity.this.mRenderer.setYLabelsColor(0, -16777216);
                ViewEcgActivity.this.mRenderer.setLabelsColor(-16777216);
                ViewEcgActivity.this.mRenderer.setXTitle("Time [s] ");
                ViewEcgActivity.this.mRenderer.setYTitle("Level");
                ViewEcgActivity.this.mRenderer.setZoomButtonsVisible(false);
                ViewEcgActivity.this.mRenderer.setZoomEnabled(false, false);
                ViewEcgActivity.this.mRenderer.setPanEnabled(true, false);
                ViewEcgActivity.this.mRenderer.setXLabels(0);

                ViewEcgActivity.this.mRenderer.addSeriesRenderer(Xrenderer1);
                ViewEcgActivity.this.mRenderer.setShowLegend(false);
                ViewEcgActivity.this.mRenderer.setShowGrid(false);

                ViewEcgActivity.this.mRenderer.setLabelsTextSize((float) ViewEcgActivity.this.size_LabelsTextSize);
                ViewEcgActivity.this.mRenderer.setAxisTitleTextSize((float) ViewEcgActivity.this.size_AxisTitleTextSize);
                ViewEcgActivity.this.mRenderer.setChartTitleTextSize((float) ViewEcgActivity.this.size_ChartTitleTextSize);
                ViewEcgActivity.this.mRenderer.setShowCustomTextGrid(true);
                ViewEcgActivity.this.mRenderer.setYLabels(6);
                ViewEcgActivity.this.mRenderer.setShowCustomTextGrid(true);
                ViewEcgActivity.this.mRenderer.setGridColor(-16777216);
                ViewEcgActivity.this.mRenderer.setYLabelsAngle(270.0f);
                ViewEcgActivity.this.mRenderer.setYAxisMin(0);
                ViewEcgActivity.this.mRenderer.setYAxisMax(5);
                ViewEcgActivity.this.mRenderer.setXAxisMax(2400);

                int count1 = 0;
                while (true) {
                    if (ViewEcgActivity.this.ready_bt == 1) {
                        ViewEcgActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (ViewEcgActivity.this.flag_stop == 0 && ViewEcgActivity.this.control_a == 0) {
                                    ViewEcgActivity.this.dataset.addSeries(ViewEcgActivity.this.emptyBuffer);
                                    ViewEcgActivity.this.mChart = ChartFactory.getLineChartView(ViewEcgActivity.this.getBaseContext(), ViewEcgActivity.this.dataset, ViewEcgActivity.this.mRenderer);
                                    ViewEcgActivity.this.layout.addView(ViewEcgActivity.this.mChart);
                                    ViewEcgActivity.this.control_a = 1;
                                    System.out.println("8");
                                }
                                if (ViewEcgActivity.this.flag_stop == 0 && ViewEcgActivity.this.control_a == 1) {
                                    ViewEcgActivity.this.dataset.clear();
                                    ViewEcgActivity.this.dataset.addSeries(ViewEcgActivity.this.emptyBuffer);
                                    ViewEcgActivity.this.mChart.repaint();
                                    System.out.println("9");
                                }
                                ViewEcgActivity.this.ready_bt = 0;
                            }
                        });
                    }
                }
            }
        };
        new Thread(commRunnable).start();
    }
}
