package com.inv.ayt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUriExposedException;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class ViewLeadActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 200;
    public String files_folder = "cache";
    Runnable commRunnable;
    public int size_LineWidth = 3;
    public int control_a = 0;
    public int flag_stop = 0;
    public int ready_bt;
    public GraphicalView mChart, mChart1, mChart2;
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer1 = new XYMultipleSeriesRenderer();
    public XYMultipleSeriesRenderer mRenderer2 = new XYMultipleSeriesRenderer();
    public XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    public XYMultipleSeriesDataset dataset1 = new XYMultipleSeriesDataset();
    public XYMultipleSeriesDataset dataset2 = new XYMultipleSeriesDataset();
    public XYSeries buffer1 = new XYSeries("X Series");
    public XYSeries buffer2 = new XYSeries("X Series");
    public XYSeries buffer3 = new XYSeries("X Series");
    public XYSeries buffer4 = new XYSeries("X Series");
    public XYSeries buffer5 = new XYSeries("X Series");
    public XYSeries buffer6 = new XYSeries("X Series");
    public XYSeries buffer7 = new XYSeries("X Series");
    public XYSeries buffer8 = new XYSeries("X Series");
    public XYSeries buffer9 = new XYSeries("X Series");
    public XYSeries buffer10 = new XYSeries("X Series");
    public XYSeries buffer11 = new XYSeries("X Series");
    public XYSeries buffer12 = new XYSeries("X Series");
    public XYSeries emptyBuffer = new XYSeries("X Series");
    public XYSeries emptyBuffer1 = new XYSeries("X Series");
    public XYSeries emptyBuffer2 = new XYSeries("X Series");
    public XYSeries emptyBuffer3 = new XYSeries("X Series");
    public XYSeries emptyBuffer4 = new XYSeries("X Series");
    public XYSeries emptyBuffer5 = new XYSeries("X Series");
    public XYSeries emptyBuffer6 = new XYSeries("X Series");
    public XYSeries emptyBuffer7 = new XYSeries("X Series");
    public XYSeries emptyBuffer8 = new XYSeries("X Series");
    public XYSeries emptyBuffer9 = new XYSeries("X Series");
    public XYSeries emptyBuffer10 = new XYSeries("X Series");
    public XYSeries emptyBuffer11 = new XYSeries("X Series");
    public double gain_x = 3.0d;

    public ArrayList<Double> vector1, vector2, vector3, vector4;
    public ArrayList<Double> vector5, vector6, vector7, vector8, hrValues, vitalValues;
    public ArrayList<Double> lead1 = new ArrayList<Double>();
    public ArrayList<Double> lead2 = new ArrayList<Double>();

    public LinearLayout layout0, layout1, layout2, report0, report1, report2, generateLayout;
    public TextInputEditText nameEdit;
    public TextView dateView, nameView, ageView, genderView, user1, user2;
    public AutoCompleteTextView ageEdit, genderEdit;
    public TextView qrsView, qtView, qtcView, prView, interpretation;
    public TextView hrView, bpView, spo2View, tempView;
    public Button saveBtn, generateBtn;

    public double qrs = 0, qt = 0, qtc = 0, pr = 0;
    public String name = "", age = "20", gender = "", reportId = "";
    public int minIndex = 2000;
    public int smallFlag = 900;

    String[] ageItems = new String[103];
    String[] genderItems = {"Male", "Female"};
    ArrayAdapter<String> ageAdapter, genderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_view_lead);

        ageItems[0] = "New Born";
        ageItems[1] = "0-5 Months";
        ageItems[2] = "6-12 Months";
        for (int i = 0; i < 100; i++) {
            ageItems[i + 3] = String.valueOf(i + 1);
        }

        layout0 = (LinearLayout) findViewById(R.id.viewGraph0);
        layout1 = (LinearLayout) findViewById(R.id.viewGraph1);
        layout2 = (LinearLayout) findViewById(R.id.viewGraph2);
        report0 = (LinearLayout) findViewById(R.id.report0);
        report1 = (LinearLayout) findViewById(R.id.report1);
        report2 = (LinearLayout) findViewById(R.id.report2);
        generateLayout = findViewById(R.id.generateLayout);
        saveBtn = findViewById(R.id.saveBtn);
        generateBtn = findViewById(R.id.generateBtn);

        nameEdit = findViewById(R.id.name);
        ageEdit = findViewById(R.id.age);
        genderEdit = findViewById(R.id.gender);

        ageAdapter = new ArrayAdapter<String>(this, R.layout.list_items, ageItems);
        genderAdapter = new ArrayAdapter<String>(this, R.layout.list_items, genderItems);
        ageEdit.setAdapter(ageAdapter);
        genderEdit.setAdapter(genderAdapter);

        dateView = findViewById(R.id.dateView);
        nameView = findViewById(R.id.nameView);
        ageView = findViewById(R.id.ageView);
        genderView = findViewById(R.id.genderView);
        user1 = findViewById(R.id.user1);
        user2 = findViewById(R.id.user2);

        hrView = findViewById(R.id.hrView);
        bpView = findViewById(R.id.bpView);
        spo2View = findViewById(R.id.spo2View);
        tempView = findViewById(R.id.tempView);

        qrsView = findViewById(R.id.qrsView);
        qtView = findViewById(R.id.qtView);
        qtcView = findViewById(R.id.qtcView);
        prView = findViewById(R.id.prView);
        interpretation = findViewById(R.id.interpretation);

        ageEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                age = parent.getItemAtPosition(position).toString();
            }
        });

        genderEdit.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                gender = parent.getItemAtPosition(position).toString();
            }
        });

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = nameEdit.getText().toString();
                if (name.isEmpty()) {
                    nameEdit.setError("Required");
                } else if (age.isEmpty()) {
                    ageEdit.setError("Required");
                } else if (gender.isEmpty()) {
                    genderEdit.setError("Required");
                } else {
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyyMMddhhssmm", Locale.US);
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                    Date date = new Date();
                    reportId = dateFormat1.format(date);
                    dateView.setText(dateFormat2.format(date));
                    nameView.setText(name);
                    ageView.setText(age);
                    genderView.setText(gender);
                    user1.setText(name + " / " + age + " yrs / " + gender);
                    user2.setText(name + " / " + age + " yrs / " + gender);
                    generateGraph();
                    generateLayout.setVisibility(View.GONE);
                    report0.setVisibility(View.VISIBLE);
                    report1.setVisibility(View.VISIBLE);
                    report2.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("hai");
                GeneratePdf();
            }
        });

//        generateGraph();
//        generateLayout.setVisibility(View.GONE);
//        report0.setVisibility(View.VISIBLE);
//        report1.setVisibility(View.VISIBLE);
//        report2.setVisibility(View.VISIBLE);
//        saveBtn.setVisibility(View.VISIBLE);
    }

    public void generateGraph() {
        File mPath = new File(getExternalFilesDir((String) null).getAbsolutePath() + "/" + files_folder);
        if (!mPath.mkdirs()) mPath.mkdirs();
        vector1 = readFromFile(Uri.fromFile(new File(mPath + "/1.txt")));
        vector2 = readFromFile(Uri.fromFile(new File(mPath + "/2.txt")));
        vector3 = readFromFile(Uri.fromFile(new File(mPath + "/3.txt")));
        vector4 = readFromFile(Uri.fromFile(new File(mPath + "/4.txt")));
        vector5 = readFromFile(Uri.fromFile(new File(mPath + "/5.txt")));
        vector6 = readFromFile(Uri.fromFile(new File(mPath + "/6.txt")));
        vector7 = readFromFile(Uri.fromFile(new File(mPath + "/7.txt")));
        vector8 = readFromFile(Uri.fromFile(new File(mPath + "/8.txt")));
        hrValues = readFromFile(Uri.fromFile(new File(mPath + "/values.txt")));
        vitalValues = readFromFile(Uri.fromFile(new File(mPath + "/vitals.txt")));


        List<Double> sortedVector1 = new ArrayList<Double>();
        List<Integer> topVector1 = new ArrayList<Integer>();
        List<Integer> indexVector1 = new ArrayList<Integer>();

        List<Double> sortedVector2 = new ArrayList<Double>();
        List<Integer> topVector2 = new ArrayList<Integer>();
        List<Integer> indexVector2 = new ArrayList<Integer>();

        int flagVector3 = 0;

        for (int i = 0; i < vector1.size(); i++) {
            if (i >= minIndex && i <= minIndex * 2) {
                sortedVector1.add(vector1.get(i));
            }
        }
        for (int i = 0; i < vector8.size(); i++) {
            if (i >= minIndex && i <= minIndex * 2) {
                sortedVector2.add(vector8.get(i));
            }
        }
        Collections.sort(sortedVector1, Collections.reverseOrder());
        Collections.sort(sortedVector2, Collections.reverseOrder());

        for (int i = 0; i < vector1.size(); i++) {
            emptyBuffer.add(i, (vector1.get(i) * gain_x) + 48);
            if (i < 20) {
                int index = vector1.indexOf(sortedVector1.get(i));
                if (index >= minIndex && index <= minIndex * 2) {
                    topVector1.add(index);
                }
            }
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer1.add((i + 100), (vector1.get(i) * gain_x) + 27);
            }
        }
        Collections.sort(topVector1);
        System.out.println(topVector1);
        List<Integer> filteredIndex1 = new ArrayList<Integer>();
        for (int i = 0; i < topVector1.size(); i++) {
            if (filteredIndex1.size() == 0 || (topVector1.get(i) - filteredIndex1.get(filteredIndex1.size() - 1)) < 50) {
                filteredIndex1.add(topVector1.get(i));
            }
            int diff = topVector1.get(i) - filteredIndex1.get(filteredIndex1.size() - 1);
            if (diff > 50 || (topVector1.size() - 1) == i) {
                if (diff < 20) {
                    filteredIndex1.add(topVector1.get(i));
                }
                int index = 0;
                double value = 0;
                for (int j = 0; j < filteredIndex1.size(); j++) {
                    if (vector1.get(filteredIndex1.get(j)) > value) {
                        index = filteredIndex1.get(j);
                        value = vector1.get(filteredIndex1.get(j));
                    }
                }
                indexVector1.add(index);
                if ((topVector1.size() - 1) == i && diff > 50) {
                    indexVector1.add(topVector1.get(i));
                }
                filteredIndex1.clear();
                filteredIndex1.add(topVector1.get(i));
            }
        }
        System.out.println(indexVector1);
        if (indexVector1.size() > 0) {
            int drawIndex1 = indexVector1.get(0);
            for (int i = drawIndex1; i < indexVector1.get(indexVector1.size() - 1); i++) {
                lead1.add(vector1.get(i));
            }
        }

        for (int i = 0; i < vector8.size(); i++) {
            emptyBuffer1.add(i, (vector8.get(i) * gain_x) + 38);
            if (i < 20) {
                int index = vector8.indexOf(sortedVector2.get(i));
                if (index >= minIndex && index <= minIndex * 2) {
                    topVector2.add(index);
                }
            }
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer2.add((i + 100), (vector8.get(i) * gain_x) + 15);
            }
        }
        Collections.sort(topVector2);
        System.out.println(topVector2);
        List<Integer> filteredIndex2 = new ArrayList<Integer>();
        for (int i = 0; i < topVector2.size(); i++) {
            if (filteredIndex2.size() == 0 || (topVector2.get(i) - filteredIndex2.get(filteredIndex2.size() - 1)) < 50) {
                filteredIndex2.add(topVector2.get(i));
            }
            int diff = topVector2.get(i) - filteredIndex2.get(filteredIndex2.size() - 1);
            if (diff > 50 || (topVector2.size() - 1) == i) {
                if (diff < 50) {
                    filteredIndex2.add(topVector2.get(i));
                }
                int index = 0;
                double value = 0;
                for (int j = 0; j < filteredIndex2.size(); j++) {
                    if (vector8.get(filteredIndex2.get(j)) > value) {
                        index = filteredIndex2.get(j);
                        value = vector8.get(filteredIndex2.get(j));
                    }
                }
                indexVector2.add(index);
                if ((topVector2.size() - 1) == i && diff > 50) {
                    indexVector2.add(topVector2.get(i));
                }
                filteredIndex2.clear();
                filteredIndex2.add(topVector2.get(i));
            }
        }
        System.out.println(indexVector2);
        if (indexVector2.size() > 0) {
            int drawIndex2 = indexVector2.get(0);
            for (int i = 1; i < indexVector2.get(indexVector2.size() - 1); i++) {
                if (indexVector1.size() > i && indexVector2.size() > i) {
                    int value1 = indexVector1.get(i) - indexVector1.get(i - 1);
                    int value2 = indexVector2.get(i) - indexVector2.get(i - 1);
                    int diff = value1 - value2;
                    if (diff > 0) {
                        for (int j = 0; j < diff; j++) {
                            vector8.add((indexVector2.get(i) - 50) + j, vector8.get(indexVector2.get(i) - 50));
                        }
                    }
                }
            }
            for (int i = drawIndex2; i < indexVector2.get(indexVector2.size() - 1); i++) {
                lead2.add(vector8.get(i));
            }
            if (indexVector1.size() > 0) generateTestResults(indexVector1.get(0));
        }


        for (int i = minIndex; i < (minIndex * 3); i++) {
            if (lead1.size() == 0 || lead2.size() == 0)
                break;
            if (lead1.size() <= flagVector3 || lead2.size() <= flagVector3) {
                flagVector3 = 0;
            }
            double value1 = lead2.get(flagVector3) - lead1.get(flagVector3);
            double value2 = (lead2.get(flagVector3) + lead1.get(flagVector3)) / 2;
            double value3 = (lead2.get(flagVector3) / 2) - lead1.get(flagVector3);
            double value4 = lead2.get(flagVector3) - (lead1.get(flagVector3) / 2);
            emptyBuffer2.add(i, (value1 * gain_x) + 34);
            emptyBuffer3.add(i, (-value2 * gain_x) + 30);
            emptyBuffer4.add(i, (value3 * gain_x) + 18);
            emptyBuffer5.add(i, (value4 * gain_x) + 3);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer3.add(i + 100, (value1 * gain_x) + 7);
                buffer4.add((i + (smallFlag + 200)), (-value2 * gain_x) + 37);
                buffer5.add((i + (smallFlag + 200)), (value3 * gain_x) + 22);
                buffer6.add((i + (smallFlag + 200)), (value4 * gain_x) + 5);
            }
            flagVector3++;
        }

        for (int i = 0; i < vector2.size(); i++) {
            emptyBuffer6.add(i, (vector2.get(i) * gain_x) + 48);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer7.add((i + ((smallFlag * 2) + 300)), (vector2.get(i) * gain_x) + 27);
            }
        }
        for (int i = 0; i < vector3.size(); i++) {
            emptyBuffer7.add(i, (vector3.get(i) * gain_x) + 38);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer8.add((i + ((smallFlag * 2) + 300)), (vector3.get(i) * gain_x) + 15);
            }
        }
        for (int i = 0; i < vector4.size(); i++) {
            emptyBuffer8.add(i, (vector4.get(i) * gain_x) + 28);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer9.add((i + ((smallFlag * 2) + 300)), (vector4.get(i) * gain_x) + 2);
            }
        }
        for (int i = 0; i < vector5.size(); i++) {
            emptyBuffer9.add(i, (vector5.get(i) * gain_x) + 18);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer10.add((i + ((smallFlag * 3) + 400)), (vector5.get(i) * gain_x) + 27);
            }
        }
        for (int i = 0; i < vector6.size(); i++) {
            emptyBuffer10.add(i, (vector6.get(i) * gain_x) + 8);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer11.add((i + ((smallFlag * 3) + 400)), (vector6.get(i) * gain_x) + 15);
            }
        }
        for (int i = 0; i < vector7.size(); i++) {
            emptyBuffer11.add(i, (vector7.get(i) * gain_x) + 0);
            if (i > minIndex && i < minIndex + smallFlag) {
                buffer12.add((i + ((smallFlag * 3) + 400)), (vector7.get(i) * gain_x) + 2);
            }
        }
        generateHR();
        ready_bt = 1;
    }

    public void generateTestResults(int min) {
        int p1 = 0, p2 = 0, p3 = 0, p4 = 0, p5 = 0, q1 = 0, q2 = 0;
        boolean p1flag = false, p2flag = false, p3flag = true, p4flag = false, p5flag = false, q1flag = false, q2flag = false;
        for (int i = min + 1; i < min + 520; i++) {
//            buffer12.add(i, (vector1.get(i) * gain_x) - 3);
            double temp = vector1.get(i - 1);
            double now = vector1.get(i);
            double temp1 = vector1.get(i + 1);
            if ((now > temp) && p3flag) {
                p3flag = false;
                p4flag = true;
                p3 = i;
            }
            if ((now < temp) && p4flag) {
                p4flag = false;
                p5flag = true;
                p4 = i;
            }
            if (p5flag) {
                boolean flag = true;
                for (int j = 0; j < 50; j++) {
                    double first = vector1.get(i + j);
                    double last = vector1.get(i - j);
                    if (last > now) {
                        flag = false;
                        break;
                    }
                    if (first > now) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    p5flag = false;
                    q2flag = true;
                    p5 = i;
                }
            }
            if ((now < temp1) && q2flag) {
                q2flag = false;
                p2flag = true;
                q2 = i;
                break;
            }
        }
        for (int i = min; i > min - 200; i--) {
            double temp = vector1.get(i - 1);
            double now = vector1.get(i);
//            buffer12.add(i, (vector1.get(i) * gain_x) - 3);
            if ((now < temp) && p2flag) {
                p2flag = false;
                p1flag = true;
                p2 = i;
            }
            if (p1flag) {
                boolean flag = true;
                for (int j = 0; j < 20; j++) {
                    double last = vector1.get(i + j);
                    if (last > now) {
                        flag = false;
                        break;
                    }
                }
                if ((now > temp) && flag) {
                    p1flag = false;
                    q1flag = true;
                    p1 = i;
                }
            }
            if ((now < temp) && q1flag) {
                p2flag = false;
                p1flag = true;
                q1 = i;
                break;
            }
        }
        if (q1 != 0 && p1 != 0 && p2 != 0 && p3 != 0 && p4 != 0 && p5 != 0 && q2 != 0) {
            qrs = ((p3 - p2) / 21.0) * 40;
            qt = ((q2 - p2) / 21.0) * 40;
            pr = ((p2 - q1) / 21.0) * 40;
            if (pr < 120) pr = 120;
            qrsView.setText((int) qrs + " ms");
            qtView.setText((int) qt + " ms");
            prView.setText((int) pr + " ms");
        }
    }

    public void generateHR() {
        int avg = 0;
        String sympotoms = " - ";
        for (int i = 0; i < vitalValues.size(); i++) {
            if (vitalValues.get(i) <= 0) continue;
            if (i == 0) {
                avg = vitalValues.get(i).intValue();
                hrView.setText(avg + " bpm");
            } else if (i == 2) {
                int value = vitalValues.get(i).intValue();
                int value1 = vitalValues.get(i - 1).intValue();
                bpView.setText(value1 + " / " + value);
            } else if (i == 3) {
                int value = vitalValues.get(i).intValue();
                spo2View.setText(value + "");
            } else if (i == 4) {
                tempView.setText(vitalValues.get(i).toString());
            }
        }
        if (avg > 60) {
            sympotoms = "Sinus Rhythm";
            if (qt > 0) {
                qtc = qt + (int) ((avg - 60) * 1.75);
                qtcView.setText((int) qtc + " ms");
            }
        }
        if (avg > 0) {
            if (age == "New Born") {
                if (avg < 100) sympotoms = "Bradycardia";
                if (avg > 160) sympotoms = "tachycardia";
            } else if (age == "0-5 Months") {
                if (avg < 90) sympotoms = "Bradycardia";
                if (avg > 150) sympotoms = "tachycardia";
            } else if (age == "6-12 Months") {
                if (avg < 80) sympotoms = "Bradycardia";
                if (avg > 140) sympotoms = "tachycardia";
            } else {
                int agee = Integer.parseInt(age);
                if (agee >= 1 && agee <= 3) {
                    if (avg < 80) sympotoms = "Bradycardia";
                    if (avg > 130) sympotoms = "tachycardia";
                } else if (agee >= 4 && agee <= 5) {
                    if (avg < 80) sympotoms = "Bradycardia";
                    if (avg > 120) sympotoms = "tachycardia";
                } else if (agee >= 6 && agee <= 10) {
                    if (avg < 70) sympotoms = "Bradycardia";
                    if (avg > 110) sympotoms = "tachycardia";
                } else if (agee >= 11 && agee <= 14) {
                    if (avg < 60) sympotoms = "Bradycardia";
                    if (avg > 105) sympotoms = "tachycardia";
                } else if (agee >= 15) {
                    if (avg > 100 && avg <= 180) sympotoms = "Sinus Tachycardia";
                    if (avg >= 180 && avg <= 250) sympotoms = "Sinus Tachycardia";
                    if (avg >= 250 && avg <= 350) sympotoms = "Atrial Flutter";
                    if (avg >= 400 && avg <= 600) sympotoms = "Atrial Fibrillation";
                }
            }
        }
        interpretation.setText(sympotoms);
    }

    private ArrayList<Double> readFromFile(Uri file) {
        ArrayList<Double> ret = new ArrayList<>();
        try {
            InputStream inputStream = getContentResolver().openInputStream(file);
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
                Log.e("Error opening file!", "error");
            }
        } catch (Exception e) {
            Log.e("Error opening file!", e.getMessage());
        }
        return ret;
    }


    public ViewLeadActivity() {
        this.commRunnable = new Runnable() {
            public void run() {
                XYSeriesRenderer Xrendererr1 = new XYSeriesRenderer();
                Xrendererr1.setColor(R.color.black);
                Xrendererr1.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr1.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr2 = new XYSeriesRenderer();
                Xrendererr2.setColor(R.color.black);
                Xrendererr2.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr2.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr3 = new XYSeriesRenderer();
                Xrendererr3.setColor(R.color.black);
                Xrendererr3.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr3.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr4 = new XYSeriesRenderer();
                Xrendererr4.setColor(R.color.black);
                Xrendererr4.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr4.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr5 = new XYSeriesRenderer();
                Xrendererr5.setColor(R.color.black);
                Xrendererr5.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr5.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr6 = new XYSeriesRenderer();
                Xrendererr6.setColor(R.color.black);
                Xrendererr6.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr6.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr7 = new XYSeriesRenderer();
                Xrendererr7.setColor(R.color.black);
                Xrendererr7.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr7.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr8 = new XYSeriesRenderer();
                Xrendererr8.setColor(R.color.black);
                Xrendererr8.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr8.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr9 = new XYSeriesRenderer();
                Xrendererr9.setColor(R.color.black);
                Xrendererr9.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr9.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr10 = new XYSeriesRenderer();
                Xrendererr10.setColor(R.color.black);
                Xrendererr10.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr10.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr11 = new XYSeriesRenderer();
                Xrendererr11.setColor(R.color.black);
                Xrendererr11.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr11.setDisplayChartValues(false);

                XYSeriesRenderer Xrendererr12 = new XYSeriesRenderer();
                Xrendererr12.setColor(R.color.black);
                Xrendererr12.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrendererr12.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer1 = new XYSeriesRenderer();
                Xrenderer1.setColor(R.color.black);
                Xrenderer1.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer1.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer2 = new XYSeriesRenderer();
                Xrenderer2.setColor(R.color.black);
                Xrenderer2.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer2.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer3 = new XYSeriesRenderer();
                Xrenderer3.setColor(R.color.black);
                Xrenderer3.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer3.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer4 = new XYSeriesRenderer();
                Xrenderer4.setColor(R.color.black);
                Xrenderer4.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer4.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer5 = new XYSeriesRenderer();
                Xrenderer5.setColor(R.color.black);
                Xrenderer5.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer5.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer6 = new XYSeriesRenderer();
                Xrenderer6.setColor(R.color.black);
                Xrenderer6.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer6.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer7 = new XYSeriesRenderer();
                Xrenderer7.setColor(R.color.black);
                Xrenderer7.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer7.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer8 = new XYSeriesRenderer();
                Xrenderer8.setColor(R.color.black);
                Xrenderer8.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer8.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer9 = new XYSeriesRenderer();
                Xrenderer9.setColor(R.color.black);
                Xrenderer9.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer9.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer10 = new XYSeriesRenderer();
                Xrenderer10.setColor(R.color.black);
                Xrenderer10.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer10.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer11 = new XYSeriesRenderer();
                Xrenderer11.setColor(R.color.black);
                Xrenderer11.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer11.setDisplayChartValues(false);

                XYSeriesRenderer Xrenderer12 = new XYSeriesRenderer();
                Xrenderer12.setColor(R.color.black);
                Xrenderer12.setLineWidth((float) ViewLeadActivity.this.size_LineWidth);
                Xrenderer12.setDisplayChartValues(false);

                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr1);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr2);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr3);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr4);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr5);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr6);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr7);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr8);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr9);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr10);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr11);
                ViewLeadActivity.this.mRenderer.addSeriesRenderer(Xrendererr12);

                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer1);
                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer2);
                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer3);
                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer4);
                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer5);
                ViewLeadActivity.this.mRenderer1.addSeriesRenderer(Xrenderer6);

                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer7);
                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer8);
                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer9);
                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer10);
                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer11);
                ViewLeadActivity.this.mRenderer2.addSeriesRenderer(Xrenderer12);

                ViewLeadActivity.this.mRenderer.setMargins(new int[]{0, 0, 0, 0});
                ViewLeadActivity.this.mRenderer.setMarginsColor(-1);
                ViewLeadActivity.this.mRenderer.setXLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer.setYLabelsColor(0, -16777216);
                ViewLeadActivity.this.mRenderer.setLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer.setZoomButtonsVisible(false);
                ViewLeadActivity.this.mRenderer.setZoomEnabled(false, false);
                ViewLeadActivity.this.mRenderer.setPanEnabled(false, false);

                ViewLeadActivity.this.mRenderer.setXLabels(0);
                ViewLeadActivity.this.mRenderer.setYLabels(0);

                ViewLeadActivity.this.mRenderer.setShowLegend(false);
                ViewLeadActivity.this.mRenderer.setShowGrid(false);

                ViewLeadActivity.this.mRenderer.setLabelsTextSize(0);
                ViewLeadActivity.this.mRenderer.setAxisTitleTextSize(0);
                ViewLeadActivity.this.mRenderer.setChartTitleTextSize(0);

                ViewLeadActivity.this.mRenderer.setYAxisMin(0);
                ViewLeadActivity.this.mRenderer.setYAxisMax(40);
                ViewLeadActivity.this.mRenderer.setXAxisMin(minIndex);
                ViewLeadActivity.this.mRenderer.setXAxisMax((minIndex * 3) + 200);

                ViewLeadActivity.this.mRenderer1.setMargins(new int[]{0, 0, 0, 0});
                ViewLeadActivity.this.mRenderer1.setMarginsColor(-1);
                ViewLeadActivity.this.mRenderer1.setXLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer1.setYLabelsColor(0, -16777216);
                ViewLeadActivity.this.mRenderer1.setLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer1.setZoomButtonsVisible(false);
                ViewLeadActivity.this.mRenderer1.setZoomEnabled(false, false);
                ViewLeadActivity.this.mRenderer1.setPanEnabled(false, false);

                ViewLeadActivity.this.mRenderer1.setXLabels(0);
                ViewLeadActivity.this.mRenderer1.setYLabels(0);

                ViewLeadActivity.this.mRenderer1.setShowLegend(false);
                ViewLeadActivity.this.mRenderer1.setShowGrid(false);

                ViewLeadActivity.this.mRenderer1.setLabelsTextSize(0);
                ViewLeadActivity.this.mRenderer1.setAxisTitleTextSize(0);
                ViewLeadActivity.this.mRenderer1.setChartTitleTextSize(0);

                ViewLeadActivity.this.mRenderer1.setYAxisMin(0);
                ViewLeadActivity.this.mRenderer1.setYAxisMax(60);
                ViewLeadActivity.this.mRenderer1.setXAxisMin(minIndex);
                ViewLeadActivity.this.mRenderer1.setXAxisMax(minIndex * 3);

                ViewLeadActivity.this.mRenderer2.setMargins(new int[]{0, 0, 0, 0});
                ViewLeadActivity.this.mRenderer2.setMarginsColor(-1);
                ViewLeadActivity.this.mRenderer2.setXLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer2.setYLabelsColor(0, -16777216);
                ViewLeadActivity.this.mRenderer2.setLabelsColor(-16777216);
                ViewLeadActivity.this.mRenderer2.setZoomButtonsVisible(false);
                ViewLeadActivity.this.mRenderer2.setZoomEnabled(false, false);
                ViewLeadActivity.this.mRenderer2.setPanEnabled(false, false);

                ViewLeadActivity.this.mRenderer2.setXLabels(0);
                ViewLeadActivity.this.mRenderer2.setYLabels(0);

                ViewLeadActivity.this.mRenderer2.setShowLegend(false);
                ViewLeadActivity.this.mRenderer2.setShowGrid(false);

                ViewLeadActivity.this.mRenderer2.setLabelsTextSize(0);
                ViewLeadActivity.this.mRenderer2.setAxisTitleTextSize(0);
                ViewLeadActivity.this.mRenderer2.setChartTitleTextSize(0);

                ViewLeadActivity.this.mRenderer2.setYAxisMin(0);
                ViewLeadActivity.this.mRenderer2.setYAxisMax(60);
                ViewLeadActivity.this.mRenderer2.setXAxisMin(minIndex);
                ViewLeadActivity.this.mRenderer2.setXAxisMax(minIndex * 3);

                int count1 = 0;
                while (true) {
                    if (ViewLeadActivity.this.ready_bt == 1) {
                        ViewLeadActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                if (ViewLeadActivity.this.flag_stop == 0 && ViewLeadActivity.this.control_a == 0) {
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer1);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer2);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer3);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer4);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer5);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer6);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer7);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer8);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer9);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer10);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer11);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer12);
                                    ViewLeadActivity.this.mChart = ChartFactory.getLineChartView(ViewLeadActivity.this.getBaseContext(), ViewLeadActivity.this.dataset, ViewLeadActivity.this.mRenderer);
                                    ViewLeadActivity.this.layout0.addView(ViewLeadActivity.this.mChart);

                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer1);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer2);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer3);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer4);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer5);
                                    ViewLeadActivity.this.mChart1 = ChartFactory.getLineChartView(ViewLeadActivity.this.getBaseContext(), ViewLeadActivity.this.dataset1, ViewLeadActivity.this.mRenderer1);
                                    ViewLeadActivity.this.layout1.addView(ViewLeadActivity.this.mChart1);

                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer6);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer7);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer8);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer9);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer10);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer11);
                                    ViewLeadActivity.this.mChart2 = ChartFactory.getLineChartView(ViewLeadActivity.this.getBaseContext(), ViewLeadActivity.this.dataset2, ViewLeadActivity.this.mRenderer2);
                                    ViewLeadActivity.this.layout2.addView(ViewLeadActivity.this.mChart2);

                                    ViewLeadActivity.this.control_a = 1;
                                }
                                if (ViewLeadActivity.this.flag_stop == 0 && ViewLeadActivity.this.control_a == 1) {
                                    ViewLeadActivity.this.dataset.clear();
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer1);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer2);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer3);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer4);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer5);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer6);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer7);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer8);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer9);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer10);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer11);
                                    ViewLeadActivity.this.dataset.addSeries(ViewLeadActivity.this.buffer12);
                                    ViewLeadActivity.this.mChart.repaint();

                                    ViewLeadActivity.this.dataset1.clear();
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer1);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer2);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer3);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer4);
                                    ViewLeadActivity.this.dataset1.addSeries(ViewLeadActivity.this.emptyBuffer5);
                                    ViewLeadActivity.this.mChart1.repaint();

                                    ViewLeadActivity.this.dataset2.clear();
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer6);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer7);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer8);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer9);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer10);
                                    ViewLeadActivity.this.dataset2.addSeries(ViewLeadActivity.this.emptyBuffer11);
                                    ViewLeadActivity.this.mChart2.repaint();
                                }

                                ViewLeadActivity.this.ready_bt = 0;
                            }
                        });
                    }
                }
            }
        };
        new Thread(commRunnable).start();
    }

    public void GeneratePdf() {
        saveBtn.setEnabled(false);

        report0.setDrawingCacheEnabled(true);
        report0.buildDrawingCache();
        Bitmap bm = Bitmap.createBitmap(report0.getWidth(), report0.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        report0.draw(c);

        report1.setDrawingCacheEnabled(true);
        report1.buildDrawingCache();
        Bitmap bm1 = Bitmap.createBitmap(report1.getWidth(), report1.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c1 = new Canvas(bm1);
        report1.draw(c1);

        report2.setDrawingCacheEnabled(true);
        report2.buildDrawingCache();
        Bitmap bm2 = Bitmap.createBitmap(report2.getWidth(), report2.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c2 = new Canvas(bm2);
        report2.draw(c2);

        System.out.println(report0.getHeight());
        System.out.println(report1.getHeight());
        System.out.println(report2.getHeight());

        Paint drawPaint = new Paint(Paint.FILTER_BITMAP_FLAG);

        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(report0.getWidth(), report0.getHeight() + 40, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);
        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(bm, 0, 20, drawPaint);
        pdfDocument.finishPage(myPage);

        PdfDocument.PageInfo mypageInfo1 = new PdfDocument.PageInfo.Builder(report1.getWidth(), report1.getHeight() + 40, 2).create();
        PdfDocument.Page myPage1 = pdfDocument.startPage(mypageInfo1);
        Canvas canvas1 = myPage1.getCanvas();
        canvas1.drawBitmap(bm1, 0, 20, drawPaint);
        pdfDocument.finishPage(myPage1);

        PdfDocument.PageInfo mypageInfo2 = new PdfDocument.PageInfo.Builder(report2.getWidth(), report2.getHeight() + 40, 3).create();
        PdfDocument.Page myPage2 = pdfDocument.startPage(mypageInfo2);
        Canvas canvas2 = myPage2.getCanvas();
        canvas2.drawBitmap(bm2, 0, 20, drawPaint);
        pdfDocument.finishPage(myPage2);

        if (checkPermission()) {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath(), "/");
            if (!dir.mkdirs()) dir.mkdirs();
            try {
                String fileName = name + "_AYT_5V-" + reportId + ".pdf";
                File file = new File(dir, fileName);
                pdfDocument.writeTo(new FileOutputStream(file));
                System.out.println(file.getAbsolutePath());
                saveBtn.setEnabled(true);
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layout), "Report saved", Snackbar.LENGTH_LONG);
                snackbar.setAction("OPEN", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        Uri apkURI = FileProvider.getUriForFile(
                                getApplicationContext(),
                                getApplicationContext().getPackageName() + ".provider", file);
                        intent.setDataAndType(apkURI, "application/pdf");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    }
                });
                snackbar.show();
            } catch (IOException e) {
                e.printStackTrace();
                saveBtn.setEnabled(true);
            }
        } else {
            requestPermission();
            saveBtn.setEnabled(true);
        }
    }

    private boolean checkPermission() {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    System.out.println("granted");
                } else {
                    System.out.println("denied");
                    finish();
                }
            }
        }
    }
}
