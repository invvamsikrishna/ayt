package com.inv.ayt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Dialog;
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
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ReportActivity extends AppCompatActivity {

    private static final int DIALOG_LOAD_FILE = 1000;
    private static final String FTYPE = ".txt";
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 1;
    public int Fs = 600;
    /* access modifiers changed from: private */
    public int HR = 0;
    public AlertDialog.Builder alertDialogBuilder;
    BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();
    public byte[] buffer3 = new byte[3];
    public byte[] buffer7 = new byte[13];
    public int[] buffer_bt;
    public int[] buffer_value_read = new int[8];
    public Button buttonOpen;
    public Button buttonRestart;
    public Button buttonSet;
    public ToggleButton buttonStart;
    public int check_byte = 0;
    public String chosen_file;
    Runnable commRunnable;
    public int control_a = 0;
    public int control_location = 1;
    public int control_read_0;
    public int control_read_1;
    public int control_read_2;
    public int[] copy_buffer_bt;
    public XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
    public int delta_plot = 2;
    public EditText edit_name;
    public String external_file;
    /* access modifiers changed from: private */
    public FileOutputStream fOut;
    public String files_folder = "cache";
    public int flag_start = 0;
    public int flag_stop = 0;
    public int gain_coeff = 1100;
    public int high_pass_filter_id;
    /* access modifiers changed from: private */
    /* access modifiers changed from: private */
    public double hw_high_pass_band = 0.5d;
    public double hw_low_pass_band = 40.0d;
    public int ind_bt;
    public int index_name_file;
    public String input_file_complete_Path;
    /* access modifiers changed from: private */
    public LinearLayout layout;
    public int lead = 1;
    public int low_pass_filter_id;
    public GraphicalView mChart;
    /* access modifiers changed from: private */
    public String mChosenFile;
    public String[] mFileList;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    public XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
    BroadcastReceiver myDiscoverer = new myOwnBroadcastReceiver();
    BroadcastReceiver checkIsConnected = new myOwnBroadcastReceiver();
    /* access modifiers changed from: private */
    public File myFile;
    /* access modifiers changed from: private */
    public OutputStreamWriter myOutWriter;
    public String name_temp;
    public int notch50or60 = 50;
    public int pixel_density;
    public String plot_title_1;
    public String plot_title_2;
    public String plot_title_NoFilter;
    public int ready_bt;
    public int saveDataFiltered = 1;
    public BluetoothSocket scSocket;
    public double screenInches;
    public int size_AxisTitleTextSize = 20;
    public int size_ChartTitleTextSize = 20;
    public int size_LabelsTextSize = 20;
    public int size_LineWidth = 5;
    public String string_name_file_temp;
    public String text_lead;
    public String text_notch50or60;
    public Uri uri_input_file;
    public int value_read = 0;
    public XYSeries xSeries = new XYSeries("X Series");
    public ConnectToBluetooth connectBT;
    public SendReceiveBytes bluetoothStream;
    public Button connectBtn, rescanBtn, nextBtn, finishBtn;
    public String fileName = "";
    public boolean emptyFlag = true, recordFlag = true;
    public TextView placeText;
    public int graph_start = 0;
    public int lead_flag = 0;
    public int data_size = 0;
    public int min_threshold = 300;
    public int max_threshold = 550;
    public RelativeLayout timerLayout;
    public ProgressBar progressBar, timerBar;
    public TextView progressText, timerText;
    public CountDownTimer cTimer = null;
    public ImageView lead1Img, v1Img, v2Img, v3Img, v4Img, v5Img, v6Img, lead2Img;
    public ArrayList<String> hrValues = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_report);

        placeText = findViewById(R.id.place);
        layout = (LinearLayout) findViewById(R.id.Chart_layout);
        connectBtn = findViewById(R.id.connectBtn);
        rescanBtn = findViewById(R.id.rescanBtn);
        nextBtn = findViewById(R.id.nextBtn);
        finishBtn = findViewById(R.id.finishBtn);
        progressBar = findViewById(R.id.progressBar);
        progressText = findViewById(R.id.progressText);
        timerLayout = findViewById(R.id.timerLayout);
        timerBar = findViewById(R.id.timerBar);
        timerText = findViewById(R.id.timerText);

        lead1Img = findViewById(R.id.lead1Img);
        v1Img = findViewById(R.id.v1Img);
        v2Img = findViewById(R.id.v2Img);
        v3Img = findViewById(R.id.v3Img);
        v4Img = findViewById(R.id.v4Img);
        v5Img = findViewById(R.id.v5Img);
        v6Img = findViewById(R.id.v6Img);
        lead2Img = findViewById(R.id.lead2Img);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDiscovery();
                connectBtn.setEnabled(false);
            }
        });
        rescanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
                checkLead(false);
                nextBtn.setEnabled(false);
                finishBtn.setEnabled(false);
                System.out.println("save file");

            }
        });
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextLead();
                nextBtn.setEnabled(false);
                finishBtn.setEnabled(false);
            }
        });
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishReport();
                nextBtn.setEnabled(false);
                finishBtn.setEnabled(false);
            }
        });
    }

    public void showDetails(String title, String des, int image) {
        Dialog dialog = new Dialog(ReportActivity.this);
        dialog.setContentView(R.layout.lead_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        TextView textView = dialog.findViewById(R.id.title);
        textView.setText(title);
        TextView textView1 = dialog.findViewById(R.id.text);
        textView1.setText("Please hold the device as shown in figure");
        ImageView imageView = dialog.findViewById(R.id.image);
        imageView.setImageResource(image);
        dialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLead(true);
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.skipBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lead_flag++;
                nextLead();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void nextLead() {
        if (lead_flag == 0) {
            showDetails("Lead 1", "", R.drawable.lead1);
            min_threshold = 0;
            max_threshold = 800;
        } else if (lead_flag == 1) {
            showDetails("V1", "", R.drawable.v1);
            min_threshold = 0;
            max_threshold = 800;
            lead1Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 2) {
            showDetails("V2", "", R.drawable.v2);
            min_threshold = 0;
            max_threshold = 800;
            v1Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 3) {
            showDetails("V3", "", R.drawable.v3);
            min_threshold = 0;
            max_threshold = 800;
            v2Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 4) {
            showDetails("V4", "", R.drawable.v4);
            min_threshold = 0;
            max_threshold = 800;
            v3Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 5) {
            showDetails("V5", "", R.drawable.v5);
            min_threshold = 0;
            max_threshold = 800;
            v4Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 6) {
            showDetails("V6", "", R.drawable.v6);
            min_threshold = 0;
            max_threshold = 800;
            v5Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 7) {
            showDetails("Lead 2", "", R.drawable.lead2);
            min_threshold = 0;
            max_threshold = 800;
            v6Img.setImageResource(R.drawable.check);
        } else if (lead_flag == 8) {
            finishReport();
        }
    }

    public void finishReport(){
        Intent intent = new Intent(ReportActivity.this, ViewLeadActivity.class);
        startActivity(intent);
        finish();
    }

    public void checkLead(boolean flag) {
        if (flag)
            lead_flag++;
        graph_start = 1;
        data_size = 0;
        createFile(false);
        setProgressValue();
        startTimer();
    }

    public void setProgressValue() {
        progressBar.setProgress(data_size);
        progressText.setText(data_size + "%");
    }

    public void startTimer() {
        cTimer = new CountDownTimer(15000, 1000) {
            public void onTick(long minutes) {
                int seconds = (int) (minutes / 1000);
                int percent = (int) (seconds * 100) / 15;
                timerLayout.setVisibility(View.VISIBLE);
                timerBar.setProgress(percent);
                timerText.setText(seconds + "");
            }

            public void onFinish() {
                cTimer.start();
            }
        };
        cTimer.start();
        rescanBtn.setEnabled(false);
    }

    public void stopTimer() {
        if (cTimer != null)
            cTimer.cancel();
        cTimer = null;
        timerLayout.setVisibility(View.GONE);
        timerBar.setProgress(100);
        timerText.setText("15");
        rescanBtn.setEnabled(true);
    }

    public void createFile(boolean flag) {
        try {
            fileName = flag ? "values.txt" : lead_flag + ".txt";
            File dir = new File(getExternalFilesDir((String) null).getAbsolutePath(), "/" + this.files_folder + "/");
            if (!dir.mkdirs()) dir.mkdirs();
            FileOutputStream file = new FileOutputStream(new File(dir, fileName));
            myOutWriter = new OutputStreamWriter(file);
            flag_start = 1;
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void saveFile() {
        try {
            myOutWriter.close();
            flag_start = 0;
            System.out.println("saved");
        } catch (IOException e) {
            e.getStackTrace();
        }
        if (lead_flag == 1) saveHRValues();
    }

    public void saveHRValues() {
        createFile(true);
        for (int i = 0; i < hrValues.size(); i++) {
            try {
                ReportActivity.this.myOutWriter.append(hrValues.get(i));
                ReportActivity.this.myOutWriter.append(System.getProperty("line.separator"));
            } catch (IOException e) {
                Log.e("HrValues", "Error with record");
            }
        }
        try {
            myOutWriter.close();
            flag_start = 0;
            System.out.println("hr saved");
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    public void startDiscovery() {
        ActivityCompat.requestPermissions(ReportActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure, Do you want to exit ?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (flag_start == 1) {
                            saveFile();
                        }
                        if (scSocket != null) {
                            bluetoothStream.write("0".getBytes(Charset.defaultCharset()));
                            connectBT.cancel();
                        } else {
                            bluetooth.cancelDiscovery();
                        }
                        finish();
                    }
                }).create().show();
    }

    public ReportActivity() {
        int i = this.Fs;
        this.buffer_bt = new int[(i * 3)];
        this.copy_buffer_bt = new int[(i * 3)];
        this.ind_bt = 0;
        this.ready_bt = 0;
        this.low_pass_filter_id = 25;
        this.high_pass_filter_id = 0;
        this.plot_title_1 = " ";
        this.plot_title_2 = "  LowPass = 40 Hz";
        this.text_lead = "LI";
        this.text_notch50or60 = " ; 50 Hz notch";
        this.plot_title_NoFilter = " ";
        this.mHandler = new Handler() {
            public void handleMessage(Message msg) {
                Message message = msg;
                if (message.what == 2 && graph_start == 1) {
                    byte[] readBuf = (byte[]) message.obj;
                    for (int u = 0; u < message.arg1; u++) {
                        ReportActivity.this.buffer7[12] = ReportActivity.this.buffer7[11];
                        ReportActivity.this.buffer7[11] = ReportActivity.this.buffer7[10];
                        ReportActivity.this.buffer7[10] = ReportActivity.this.buffer7[9];
                        ReportActivity.this.buffer7[9] = ReportActivity.this.buffer7[8];
                        ReportActivity.this.buffer7[8] = ReportActivity.this.buffer7[7];
                        ReportActivity.this.buffer7[7] = ReportActivity.this.buffer7[6];
                        ReportActivity.this.buffer7[6] = ReportActivity.this.buffer7[5];
                        ReportActivity.this.buffer7[5] = ReportActivity.this.buffer7[4];
                        ReportActivity.this.buffer7[4] = ReportActivity.this.buffer7[3];
                        ReportActivity.this.buffer7[3] = ReportActivity.this.buffer7[2];
                        ReportActivity.this.buffer7[2] = ReportActivity.this.buffer7[1];
                        ReportActivity.this.buffer7[1] = ReportActivity.this.buffer7[0];
                        ReportActivity.this.buffer7[0] = readBuf[u];
                        ReportActivity ReportActivity = ReportActivity.this;
                        ReportActivity.control_read_0 = ReportActivity.buffer7[12] & 255;
                        ReportActivity ReportActivity2 = ReportActivity.this;
                        ReportActivity2.control_read_1 = ReportActivity2.buffer7[6] & 255;
                        ReportActivity ReportActivity3 = ReportActivity.this;
                        ReportActivity3.control_read_2 = ReportActivity3.buffer7[0] & 255;
                        if (ReportActivity.this.control_read_1 == 255 && ReportActivity.this.control_read_2 == 255 && ReportActivity.this.control_read_0 == 255) {
                            if (ReportActivity.this.check_byte == 0) {
                                ReportActivity.this.buffer_value_read[0] = ((ReportActivity.this.buffer7[10] & 192) >>> 6) + ((ReportActivity.this.buffer7[11] & 255) << 2);
                                ReportActivity.this.buffer_value_read[1] = ((ReportActivity.this.buffer7[9] & 240) >>> 4) + ((ReportActivity.this.buffer7[10] & 63) << 4);
                                ReportActivity.this.buffer_value_read[2] = ((ReportActivity.this.buffer7[8] & 252) >>> 2) + ((ReportActivity.this.buffer7[9] & 15) << 6);
                                ReportActivity.this.buffer_value_read[3] = (ReportActivity.this.buffer7[7] & 255) + ((ReportActivity.this.buffer7[8] & 3) << 8);
                                ReportActivity.this.buffer_value_read[4] = ((ReportActivity.this.buffer7[4] & 192) >>> 6) + ((ReportActivity.this.buffer7[5] & 255) << 2);
                                ReportActivity.this.buffer_value_read[5] = ((ReportActivity.this.buffer7[3] & 240) >>> 4) + ((ReportActivity.this.buffer7[4] & 63) << 4);
                                ReportActivity.this.buffer_value_read[6] = ((ReportActivity.this.buffer7[2] & 252) >>> 2) + ((ReportActivity.this.buffer7[3] & 15) << 6);
                                ReportActivity.this.buffer_value_read[7] = (ReportActivity.this.buffer7[1] & 255) + ((ReportActivity.this.buffer7[2] & 3) << 8);
                                if (ReportActivity.this.ind_bt >= ReportActivity.this.Fs * 3) {
                                    ReportActivity.this.ind_bt = 0;
                                    for (int uu = 0; uu < ReportActivity.this.Fs * 3; uu++) {
                                        ReportActivity.this.copy_buffer_bt[uu] = ReportActivity.this.buffer_bt[uu];
                                    }
                                    ReportActivity.this.ready_bt = 1;
                                }
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt] = ReportActivity.this.buffer_value_read[0];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 1] = ReportActivity.this.buffer_value_read[1];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 2] = ReportActivity.this.buffer_value_read[2];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 3] = ReportActivity.this.buffer_value_read[3];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 4] = ReportActivity.this.buffer_value_read[4];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 5] = ReportActivity.this.buffer_value_read[5];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 6] = ReportActivity.this.buffer_value_read[6];
                                ReportActivity.this.buffer_bt[ReportActivity.this.ind_bt + 7] = ReportActivity.this.buffer_value_read[7];
                                ReportActivity.this.ind_bt += 8;
                                ReportActivity.this.check_byte = 1;
                            } else {
                                ReportActivity.this.check_byte = 0;
                            }
                        }
                    }
                }
            }
        };
        this.commRunnable = new Runnable() {
            public void run() {
                double G2_low;
                double G1_low;
                double G2_high;
                double G1_high;
                double[] filter_lB1;
                XYSeriesRenderer Xrenderer;
                int int_label_6;
                int int_label_62;
                int int_label_4;
                double[] filter_lA1;
                double[] buffer_plot;
//                AnonymousClass10 r1 = this;
                double[] buffer_plot2 = new double[(ReportActivity.this.Fs * 3)];
                int integer_hr_window = (ReportActivity.this.Fs * 2) / 3;
                int count2 = 0;
                int ind = 0;
                double[] filter_hA1 = new double[3];
                double[] filter_lB12 = new double[3];
                double[] filter_lA12 = new double[3];
                double[] filter_lB2 = new double[3];
                double[] buffer_hr = new double[integer_hr_window];
                double[] buffer_hr2 = new double[3];
                double[] buffer_fhB1 = {0.0d, 0.0d, 0.0d};
                double[] buffer_flB1 = {0.0d, 0.0d, 0.0d};
                double[] buffer_fhA1 = {0.0d, 0.0d, 0.0d};
                double[] buffer_flA1 = {0.0d, 0.0d, 0.0d};
                double[] buffer_fhB2 = {0.0d, 0.0d, 0.0d};
                double[] buffer_flB2 = {0.0d, 0.0d, 0.0d};
                double[] buffer_fhA2 = {0.0d, 0.0d, 0.0d};
                double[] buffer_flA2 = {0.0d, 0.0d, 0.0d};
                double output_low1 = 0.0d;
                double output_low2 = 0.0d;
                double output_high1 = 0.0d;
                double output_high2 = 0.0d;
                double output2_50_60 = 0.0d;
                double output1_50_60 = 0.0d;
                double output3_50_60 = 0.0d;
                double output4_50_60 = 0.0d;
                double[] buffer0_50 = {0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d};
                double[] buffer0_60 = {0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d, 0.0d};
                double[] num_filter_50 = {1.0d, -1.73168804769603d, 0.999790560489761d};
                double[] den_filter_50 = {1.0d, -1.695774820284193d, 0.958550747036629d};
                double[] num_filter_100 = {1.0d, -0.99979056048976d, 0.999790560489761d};
                double[] den_filter_100 = {1.0d, -0.979056048976068d, 0.958550747036629d};
                double[] num_filter_60 = {1.0d, -1.617695108503741d, 0.999790560489761d};
                double[] den_filter_60 = {1.0d, -1.58414596413446d, 0.958550747036629d};
                double[] num_filter_120 = {1.0d, -0.61790454801398d, 0.999790560489761d};
                double[] den_filter_120 = {1.0d, -0.605089915158392d, 0.958550747036629d};
                double[] buffer_num_filter_50 = {0.0d, 0.0d, 0.0d};
                double[] buffer_den_filter_50 = {0.0d, 0.0d, 0.0d};
                double[] buffer_num_filter_60 = {0.0d, 0.0d, 0.0d};
                double[] buffer_den_filter_60 = {0.0d, 0.0d, 0.0d};
                double[] buffer_num_filter_100 = {0.0d, 0.0d, 0.0d};
                double[] buffer_den_filter_100 = {0.0d, 0.0d, 0.0d};
                double[] buffer_num_filter_120 = {0.0d, 0.0d, 0.0d};
                double[] buffer_den_filter_120 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_num_filter_50 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_den_filter_50 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_num_filter_60 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_den_filter_60 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_num_filter_100 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_den_filter_100 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_num_filter_120 = {0.0d, 0.0d, 0.0d};
                double[] buffer2_den_filter_120 = {0.0d, 0.0d, 0.0d};
                double[] A1_low25 = {1.0d, -1.757753609482727d, 0.819760442927314d};
                double[] A2_low25 = {1.0d, -1.559054301141692d, 0.614051781937882d};
                double[] B1_low25 = {1.0d, 2.0d, 1.0d};
                double[] B2_low25 = {1.0d, 2.0d, 1.0d};
                double[] A1_low35 = {1.0d, -1.641977617376592d, 0.758796104516994d};
                double[] A2_low35 = {1.0d, -1.402731984362635d, 0.502529342464571d};
                double[] B1_low35 = {1.0d, 2.0d, 1.0d};
                double[] B2_low35 = {1.0d, 2.0d, 1.0d};
                double[] A1_low40 = {1.0d, -1.581005271466872d, 0.730625726656938d};
                double[] A2_low40 = {1.0d, -1.328044221785862d, 0.453725384627135d};
                double[] B1_low40 = {1.0d, 2.0d, 1.0d};
                double[] B2_low40 = {1.0d, 2.0d, 1.0d};
                double[] A1_low100 = {1.0d, -0.75108142165694d, 0.50216284331388d};
                double[] A2_low100 = {1.0d, -0.555523722444109d, 0.111047444888218d};
                double[] B1_low100 = {1.0d, 2.0d, 1.0d};
                double[] B2_low100 = {1.0d, 2.0d, 1.0d};
                double[] A1_low150 = {1.0d, -0.16058961851103d, 0.44646269217169d};
                double[] A2_low150 = {1.0d, -0.115415025303165d, 0.03956612989658d};
                double[] B1_low150 = {1.0d, 2.0d, 1.0d};
                double[] B2_low150 = {1.0d, 2.0d, 1.0d};
                double[] A1_high1010 = {1.0d, -1.999197734721228d, 0.999198830904549d};
                double[] A2_high1010 = {1.0d, -1.998065806264844d, 0.998066901827515d};
                double[] B1_high1010 = {1.0d, -2.0d, 1.0d};
                double[] B2_high1010 = {1.0d, -2.0d, 1.0d};
                double[] A1_high1015 = {1.0d, -1.998796021366643d, 0.99879848728493d};
                double[] A2_high1015 = {1.0d, -1.997099290226235d, 0.997101754051262d};
                double[] B1_high1015 = {1.0d, -2.0d, 1.0d};
                double[] B2_high1015 = {1.0d, -2.0d, 1.0d};
                double[] A1_high1025 = {1.0d, -1.997991434939058d, 0.997998281967313d};
                double[] A2_high1025 = {1.0d, -1.995167418322513d, 0.995174255672988d};
                double[] B1_high1025 = {1.0d, -2.0d, 1.0d};
                double[] B2_high1025 = {1.0d, -2.0d, 1.0d};
                double[] A1_high1050 = {1.0d, -1.995973219799735d, 0.996000580481828d};
                double[] A2_high1050 = {1.0d, -1.990344492411856d, 0.990371775935689d};
                double[] B1_high1050 = {1.0d, -2.0d, 1.0d};
                double[] B2_high1050 = {1.0d, -2.0d, 1.0d};
                double[] A1_high1100 = {1.0d, -1.991908009819602d, 0.99201723338842d};
                double[] A2_high1100 = {1.0d, -1.980727460109226d, 0.980836070607794d};
                double[] B1_high1100 = {1.0d, -2.0d, 1.0d};
                int integer_hr_window2 = integer_hr_window;
                XYSeriesRenderer Xrenderer2 = new XYSeriesRenderer();
                Xrenderer2.setColor(-16776961);
                Xrenderer2.setLineWidth((float) ReportActivity.this.size_LineWidth);
                double[] buffer_d2 = new double[5];
                ReportActivity.this.mRenderer.setMargins(new int[]{0, 0, 0, 0});
                ReportActivity.this.mRenderer.setMarginsColor(-1);
                ReportActivity.this.mRenderer.setXLabelsColor(-16777216);
                double[] D = {-2.0d, -1.0d, 0.0d, 1.0d, 2.0d};
                ReportActivity.this.mRenderer.setYLabelsColor(0, -16777216);
                ReportActivity.this.mRenderer.setLabelsColor(-16777216);
                ReportActivity.this.mRenderer.setXTitle("");
                ReportActivity.this.mRenderer.setYTitle("");
                ReportActivity.this.mRenderer.setZoomButtonsVisible(false);
                ReportActivity.this.mRenderer.setXLabels(0);
                ReportActivity.this.mRenderer.setPanEnabled(false);
                ReportActivity.this.mRenderer.setClickEnabled(false);
                ReportActivity.this.mRenderer.addSeriesRenderer(Xrenderer2);
                ReportActivity.this.mRenderer.setShowLegend(false);
                ReportActivity.this.mRenderer.setShowGrid(false);
                int int_label_2 = ((ReportActivity.this.Fs / ReportActivity.this.delta_plot) * 3) / 6;
                int int_label_3 = (((ReportActivity.this.Fs / ReportActivity.this.delta_plot) * 3) * 2) / 6;
                double[] buffer_d1 = new double[5];
                int int_label_42 = (((ReportActivity.this.Fs / ReportActivity.this.delta_plot) * 3) * 3) / 6;
                double[] buffer_plot3 = buffer_plot2;
                int int_label_5 = (((ReportActivity.this.Fs / ReportActivity.this.delta_plot) * 3) * 4) / 6;
                double[] filter_hA2 = new double[3];
                int int_label_63 = (((ReportActivity.this.Fs / ReportActivity.this.delta_plot) * 3) * 5) / 6;
                double[] filter_hB2 = new double[3];
                double[] filter_hB1 = new double[3];
                double[] B2_high1100 = {1.0d, -2.0d, 1.0d};
                Xrenderer2.setDisplayChartValues(false);
                ReportActivity.this.mRenderer.setLabelsTextSize(0);
                ReportActivity.this.mRenderer.setAxisTitleTextSize(0);
                ReportActivity.this.mRenderer.setChartTitleTextSize(0);
                ReportActivity.this.mRenderer.setShowCustomTextGrid(false);
                ReportActivity.this.mRenderer.setYLabels(0);
                ReportActivity.this.mRenderer.setGridColor(-16777216);
                ReportActivity.this.mRenderer.setYLabelsAngle(270.0f);
                ReportActivity.this.mRenderer.setYAxisMin(0);
                ReportActivity.this.mRenderer.setYAxisMax(800);
                int count1 = 0;
                while (true) {
                    if (ReportActivity.this.ready_bt == 1) {
                        int vv = 0;
                        int count12 = count1;
                        while (vv < ReportActivity.this.Fs * 3) {
                            int read_value = ReportActivity.this.copy_buffer_bt[vv];
                            int int_label_52 = int_label_5;

                            //lowpass25
                            filter_lB12[0] = B1_low25[0];
                            filter_lB12[1] = B1_low25[1];
                            filter_lB12[2] = B1_low25[2];
                            filter_lA12[0] = A1_low25[0];
                            filter_lA12[1] = A1_low25[1];
                            filter_lA12[2] = A1_low25[2];
                            filter_lB2[0] = B2_low25[0];
                            filter_lB2[1] = B2_low25[1];
                            filter_lB2[2] = B2_low25[2];
                            buffer_hr2[0] = A2_low25[0];
                            buffer_hr2[1] = A2_low25[1];
                            buffer_hr2[2] = A2_low25[2];
                            G1_low = 0.015501708361147d;
                            G2_low = 0.013749370199048d;


                            //highpass 0
                            filter_hB1[0] = B1_high1100[0];
                            filter_hB1[1] = B1_high1100[1];
                            filter_hB1[2] = B1_high1100[2];
                            filter_hA1[0] = A1_high1100[0];
                            filter_hA1[1] = A1_high1100[1];
                            filter_hA1[2] = A1_high1100[2];
                            filter_hB2[0] = B2_high1100[0];
                            filter_hB2[1] = B2_high1100[1];
                            filter_hB2[2] = B2_high1100[2];
                            filter_hA2[0] = A2_high1100[0];
                            filter_hA2[1] = A2_high1100[1];
                            filter_hA2[2] = A2_high1100[2];
                            G1_high = 0.995981310802006d;
                            G2_high = 0.990390882679255d;

                            //notch50
                            filter_lB1 = filter_lB12;

                            buffer0_50[11] = buffer0_50[10];
                            buffer0_50[10] = buffer0_50[9];
                            buffer0_50[9] = buffer0_50[8];
                            buffer0_50[8] = buffer0_50[7];
                            buffer0_50[7] = buffer0_50[6];
                            buffer0_50[6] = buffer0_50[5];
                            buffer0_50[5] = buffer0_50[4];
                            buffer0_50[4] = buffer0_50[3];
                            buffer0_50[3] = buffer0_50[2];
                            buffer0_50[2] = buffer0_50[1];
                            buffer0_50[1] = buffer0_50[0];
                            buffer0_50[0] = (double) read_value;
                            output4_50_60 = (((((((((((buffer0_50[0] + buffer0_50[1]) + buffer0_50[2]) + buffer0_50[3]) + buffer0_50[4]) + buffer0_50[5]) + buffer0_50[6]) + buffer0_50[7]) + buffer0_50[8]) + buffer0_50[9]) + buffer0_50[10]) + buffer0_50[11]) / 12.0d;
                            Xrenderer = Xrenderer2;

                            //lowpass!=0
                            buffer_flB1[2] = buffer_flB1[1];
                            buffer_flB1[1] = buffer_flB1[0];
                            buffer_flB1[0] = output4_50_60;
                            buffer_flA1[2] = buffer_flA1[1];
                            buffer_flA1[1] = output_low1;
                            output_low1 = (((((buffer_flB1[0] * filter_lB1[0]) + (buffer_flB1[1] * filter_lB1[1])) + (buffer_flB1[2] * filter_lB1[2])) * G1_low) - (buffer_flA1[1] * filter_lA12[1])) - (buffer_flA1[2] * filter_lA12[2]);
                            buffer_flB2[2] = buffer_flB2[1];
                            buffer_flB2[1] = buffer_flB2[0];
                            buffer_flB2[0] = output_low1;
                            buffer_flA2[2] = buffer_flA2[1];
                            buffer_flA2[1] = output_low2;
                            output_low2 = ((G2_low * (((buffer_flB2[0] * filter_lB2[0]) + (buffer_flB2[1] * filter_lB2[1])) + (buffer_flB2[2] * filter_lB2[2]))) - (buffer_flA2[1] * buffer_hr2[1])) - (buffer_flA2[2] * buffer_hr2[2]);

                            //highpass==0
                            output_high2 = output_low2;

                            double output1 = output_high2;

                            buffer_plot3[ind] = output1;
                            buffer_d1[4] = buffer_d1[3];
                            buffer_d1[3] = buffer_d1[2];
                            buffer_d1[2] = buffer_d1[1];
                            buffer_d1[1] = buffer_d1[0];
                            buffer_d1[0] = output1;
                            double outputd1 = (buffer_d1[0] * D[0]) + (buffer_d1[1] * D[1]) + (buffer_d1[2] * D[2]) + (buffer_d1[3] * D[3]) + (buffer_d1[4] * D[4]);
                            buffer_d2[4] = buffer_d2[3];
                            buffer_d2[3] = buffer_d2[2];
                            buffer_d2[2] = buffer_d2[1];
                            buffer_d2[1] = buffer_d2[0];
                            buffer_d2[0] = outputd1;
                            double outputd2 = (buffer_d2[0] * D[0]) + (buffer_d2[1] * D[1]) + (buffer_d2[2] * D[2]) + (buffer_d2[3] * D[3]) + (buffer_d2[4] * D[4]);
                            count12++;
                            count2++;
                            int integer_hr_window_minus1 = integer_hr_window2 - 1;
                            for (int y = 0; y < integer_hr_window_minus1; y++) {
                                buffer_hr[integer_hr_window_minus1 - y] = buffer_hr[(integer_hr_window_minus1 - 1) - y];
                            }
                            buffer_hr[0] = outputd2;
                            double summ = 0.0d;
                            for (int y2 = 0; y2 < integer_hr_window_minus1; y2++) {
                                summ += Math.abs(buffer_hr[y2]);
                            }
                            int integer_hr_window3 = integer_hr_window2;
                            double mean1 = (summ * 2.0d) / ((double) integer_hr_window3);
                            int hr_window_half = integer_hr_window3 / 2;
                            int integer_hr_window4 = integer_hr_window3;
                            double[] filter_hA12 = filter_hA1;
                            int hr_40ms = (int) (((double) ReportActivity.this.Fs) * 0.04d);
                            if ((-buffer_hr[hr_window_half]) > mean1 * 2.0d && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 10] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 9] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 8] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 7] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 6] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 5] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 4] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 3] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 2] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half - 1] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 1] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 2] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 3] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 4] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 5] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 6] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 7] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 8] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 9] && buffer_hr[hr_window_half] < buffer_hr[hr_window_half + 10]) {
                                count12 = 0;
                            }
                            if (buffer_hr[hr_window_half] > mean1 && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 10] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 9] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 8] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 7] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 6] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 5] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 4] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 3] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 2] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half - 1] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 1] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 2] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 3] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 4] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 5] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 6] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 7] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 8] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 9] && buffer_hr[hr_window_half] > buffer_hr[hr_window_half + 10] && count12 < hr_40ms) {
                                ReportActivity ReportActivity = ReportActivity.this;
                                int unused = ReportActivity.HR = (ReportActivity.Fs * 60) / count2;
                                count2 = 0;
                            }
                            int ind2 = ind + 1;
                            if (ind2 == ReportActivity.this.Fs * 3) {
                                ind2 = 0;
                                ReportActivity.this.xSeries.clear();
                                int ind_plot = 1;
                                int ind_buffer_plot = 1;
                                while (true) {
                                    int integer_hr_window_minus12 = integer_hr_window_minus1;
                                    buffer_plot = buffer_plot3;
                                    if (ind_buffer_plot >= buffer_plot.length) {
                                        break;
                                    }
//                                    if (buffer_plot[ind_buffer_plot] >= 100 && buffer_plot[ind_buffer_plot] <= 700) {
                                    ReportActivity.this.xSeries.add((double) ind_plot, buffer_plot[ind_buffer_plot]);
                                    if (buffer_plot[ind_buffer_plot] < min_threshold || buffer_plot[ind_buffer_plot] > max_threshold) {
                                        recordFlag = false;
                                    }
//                                    } else {
//                                        ReportActivity.this.xSeries.add((double) ind_plot, 400);
//                                        emptyFlag = false;
//                                    }
                                    ind_plot++;
                                    ind_buffer_plot += ReportActivity.this.delta_plot;
                                    integer_hr_window_minus1 = integer_hr_window_minus12;
                                    hr_window_half = hr_window_half;
                                    hr_40ms = hr_40ms;
                                    filter_lA12 = filter_lA12;
                                    int_label_42 = int_label_42;
                                    buffer_plot3 = buffer_plot;
                                }
                                int i4 = hr_window_half;
                                int_label_4 = int_label_42;
                                filter_lA1 = filter_lA12;
                                System.out.println(buffer_plot.length);

                                int ind_plot1 = 1;
                                if (ReportActivity.this.flag_start == 1) {
                                    while (true) {
                                        if (ind_plot1 >= buffer_plot.length) {
                                            break;
                                        }
                                        if (recordFlag && HR >= 60 && HR <= 160) {
                                            double value_to_write = (double) buffer_plot[ind_plot1];
                                            try {
                                                ReportActivity.this.myOutWriter.append(String.valueOf((((5.0d * value_to_write) / 1023.0d) * 1000.0d) / ((double) ReportActivity.this.gain_coeff)));
                                                ReportActivity.this.myOutWriter.append(System.getProperty("line.separator"));
                                            } catch (IOException e) {
                                            }
                                        }
                                        ind_plot1++;
                                    }
                                }
                                ReportActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        if (ReportActivity.this.flag_stop == 0 && ReportActivity.this.control_a == 0) {
                                            XYMultipleSeriesRenderer xYMultipleSeriesRenderer = ReportActivity.this.mRenderer;
                                            // xYMultipleSeriesRenderer.setChartTitle(ReportActivity.this.plot_title_NoFilter + ReportActivity.this.plot_title_1 + ReportActivity.this.plot_title_2 + "  (" + ReportActivity.this.text_lead + ")" + ReportActivity.this.text_notch50or60);
                                            ReportActivity.this.dataset.addSeries(ReportActivity.this.xSeries);
                                            ReportActivity.this.mChart = ChartFactory.getLineChartView(ReportActivity.this.getBaseContext(), ReportActivity.this.dataset, ReportActivity.this.mRenderer);
                                            ReportActivity.this.layout.addView(ReportActivity.this.mChart);
                                            ReportActivity.this.control_a = 1;
                                        }
                                        if (ReportActivity.this.flag_stop == 0 && ReportActivity.this.control_a == 1) {
                                            ReportActivity.this.dataset.clear();
                                            XYMultipleSeriesRenderer xYMultipleSeriesRenderer2 = ReportActivity.this.mRenderer;
                                            // xYMultipleSeriesRenderer2.setChartTitle(ReportActivity.this.plot_title_NoFilter + ReportActivity.this.plot_title_1 + ReportActivity.this.plot_title_2 + "  (" + ReportActivity.this.text_lead + ")" + ReportActivity.this.text_notch50or60);
                                            if (HR >= 60 && HR <= 160) {
                                                if (data_size == 0) {
                                                    stopTimer();
                                                }
                                                placeText.setText("");
                                                if (recordFlag) {
                                                    data_size = data_size + 10;
                                                    setProgressValue();
                                                    hrValues.add(String.valueOf(HR));
                                                    System.out.println("recorded");
                                                    if (data_size >= 100) {
                                                        graph_start = 0;
                                                        System.out.println("save file");
                                                        saveFile();
                                                        nextBtn.setEnabled(true);
                                                        finishBtn.setEnabled(true);
                                                    }
                                                }
                                            } else {
                                                placeText.setText("Please hold the device properly");
                                            }
                                            if (data_size != 0 && data_size != 100) {
                                                ReportActivity.this.dataset.addSeries(ReportActivity.this.xSeries);
                                            }
                                            ReportActivity.this.mChart.repaint();
                                            System.out.println("9");
                                        }
                                        String s_bpm = String.valueOf(ReportActivity.this.HR);
                                        ReportActivity.this.emptyFlag = true;
                                        ReportActivity.this.recordFlag = true;
//                                        ReportActivity.this.hr_value_TextView.setText("only for Lead I");
//                                        ReportActivity.this.hr_bpm_TextView.setText(BuildConfig.FLAVOR);
                                    }
                                });
                            } else {
                                int i5 = hr_40ms;
                                int_label_4 = int_label_42;
                                filter_lA1 = filter_lA12;
                                buffer_plot = buffer_plot3;
                                int i6 = hr_window_half;
                            }
                            ind = ind2;
                            vv++;
                            int i7 = read_value;
                            buffer_plot3 = buffer_plot;
                            int_label_5 = int_label_52;
                            Xrenderer2 = Xrenderer;
                            filter_hA1 = filter_hA12;
                            integer_hr_window2 = integer_hr_window4;
                            filter_lB12 = filter_lB1;
                            filter_lA12 = filter_lA1;
                            int_label_42 = int_label_4;
                        }
                        XYSeriesRenderer xYSeriesRenderer = Xrenderer2;
                        double[] dArr = filter_lB12;
                        int i9 = int_label_42;
                        double[] dArr2 = filter_lA12;
                        int integer_hr_window5 = integer_hr_window2;
                        double[] dArr3 = buffer_plot3;
                        double[] dArr4 = filter_hA1;
                        ReportActivity.this.ready_bt = 0;
                        count1 = count12;
                        integer_hr_window2 = integer_hr_window5;
                    } else {
                        XYSeriesRenderer xYSeriesRenderer2 = Xrenderer2;
                        double[] dArr5 = filter_lB12;
                        int i11 = int_label_42;
                        double[] dArr6 = filter_lA12;
                        int integer_hr_window6 = integer_hr_window2;
                        double[] dArr7 = buffer_plot3;
                        double[] dArr8 = filter_hA1;
                        integer_hr_window2 = integer_hr_window6;
                    }
                    buffer_plot3 = buffer_plot3;
                }
            }
        };
        new Thread(commRunnable).start();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == -1) {
                if (this.bluetooth.isEnabled()) {
                    IntentFilter filter = new IntentFilter();
                    filter.addAction(BluetoothDevice.ACTION_FOUND);
                    filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
                    registerReceiver(this.myDiscoverer, filter);
                    registerReceiver(this.checkIsConnected, new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED));
                    if (!this.bluetooth.isDiscovering()) {
                        this.bluetooth.startDiscovery();
                        System.out.println("Start Discovery");
                    }
                }
            } else {
                ToastMaster("You need to turn Bluetooth ON !!!");
            }
        }
        if (requestCode == 101) {
            this.lead = data.getIntExtra("lead", this.lead);
            this.gain_coeff = data.getIntExtra("gain_coeff", this.gain_coeff);
            this.notch50or60 = data.getIntExtra("notch50or60", this.notch50or60);
            this.saveDataFiltered = data.getIntExtra("saveDataFiltered", this.saveDataFiltered);
            int i = this.lead;
            if (i == 1) {
                this.text_lead = "LI";
            } else if (i == 2) {
                this.text_lead = "LII";
            } else if (i == 3) {
                this.text_lead = "LIII";
            }
            int i2 = this.notch50or60;
            if (i2 == 50) {
                this.text_notch50or60 = " ; 50 Hz notch";
            } else if (i2 == 60) {
                this.text_notch50or60 = " ; 60 Hz notch";
            } else if (i2 == 0) {
                this.text_notch50or60 = " ; no notch";
            }
        }
    }

    public class myOwnBroadcastReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                String discoveredDeviceName = intent.getStringExtra("android.bluetooth.device.extra.NAME");
                ReportActivity ReportActivity = ReportActivity.this;
                ReportActivity.ToastMaster("Discovered: " + discoveredDeviceName);
                BluetoothDevice discoveredDevice = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                switch (discoveredDevice.getBondState()) {
                    case 10:
                        break;
                    case 11:
                        break;
                    case 12:
                        break;
                }
                if (discoveredDeviceName != null && (discoveredDeviceName.equals("AYT-22"))) {
                    ReportActivity ReportActivity2 = ReportActivity.this;
                    ReportActivity2.unregisterReceiver(ReportActivity2.myDiscoverer);
                    ReportActivity2.connectBT = new ConnectToBluetooth(discoveredDevice);
                    new Thread(ReportActivity2.connectBT).start();
                }
            }
            if ("android.bluetooth.device.action.ACL_CONNECTED".equals(action)) {
                do {
                } while (ReportActivity.this.scSocket == null);
                ReportActivity.this.ToastMaster("CONNECTED");
                connectBtn.setVisibility(View.INVISIBLE);
                rescanBtn.setVisibility(View.VISIBLE);
                nextBtn.setVisibility(View.VISIBLE);
                finishBtn.setVisibility(View.VISIBLE);
                if (ReportActivity.this.scSocket != null) {
                    ReportActivity ReportActivity3 = ReportActivity.this;
                    ReportActivity3.unregisterReceiver(ReportActivity3.checkIsConnected);
                    ReportActivity3.bluetoothStream = new SendReceiveBytes(ReportActivity3.scSocket);
                    new Thread(ReportActivity3.bluetoothStream).start();
                    ReportActivity3.bluetoothStream.write("5".getBytes(Charset.defaultCharset()));
                    nextLead();
                }
            }
            if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                Snackbar snackbar = Snackbar.make(findViewById(R.id.layout), "Device not found", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("RETRY", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startDiscovery();
                    }
                });
                snackbar.show();
            }
        }
    }

    public class ConnectToBluetooth implements Runnable {
        private BluetoothDevice btShield;
        private BluetoothSocket mySocket = null;
        private UUID uuid;

        public ConnectToBluetooth(BluetoothDevice bluetoothShield) {
            UUID fromString = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            this.uuid = fromString;
            this.btShield = bluetoothShield;
            try {
                this.mySocket = bluetoothShield.createRfcommSocketToServiceRecord(fromString);
            } catch (IOException e) {
                Log.e("ConnectToBluetooth", "Error with Socket");
            }
            ReportActivity.this.bluetooth.cancelDiscovery();
            System.out.println("Stop Discovery");
        }

        public void run() {
            try {
                this.mySocket.connect();
                ReportActivity.this.scSocket = this.mySocket;
            } catch (IOException e) {
                Log.e("ConnectToBluetooth", "Error with Socket Connection");
                try {
                    this.mySocket.close();
                } catch (IOException e2) {
                }
            }
        }

        public void cancel() {
            try {
                this.mySocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class SendReceiveBytes implements Runnable {
        String TAG = "SendReceiveBytes";
        private InputStream btInputStream = null;
        private OutputStream btOutputStream = null;
        private BluetoothSocket btSocket;

        public SendReceiveBytes(BluetoothSocket socket) {
            this.btSocket = socket;
            try {
                this.btInputStream = socket.getInputStream();
                this.btOutputStream = this.btSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(this.TAG, "Error when getting input or output Stream");
            }
        }

        public void run() {
            while (true) {
                byte[] buffer = new byte[200];
                try {
                    ReportActivity.this.mHandler.obtainMessage(2, this.btInputStream.read(buffer), -1, buffer).sendToTarget();
                } catch (IOException e) {
                    Log.e(this.TAG, "Error reading from btInputStream");
                    return;
                }
            }
        }

        public void write(byte[] bytes) {
            try {
                this.btOutputStream.write(bytes);
            } catch (IOException e) {
                Log.e(this.TAG, "Error when writing to btOutputStream");
            }
        }

        public void cancel() {
            try {
                this.btSocket.close();
            } catch (IOException e) {
                Log.e(this.TAG, "Error when closing the btSocket");
            }
        }
    }

    public void ToastMaster(String textToDisplay) {
        Toast myMessage = Toast.makeText(getApplicationContext(), textToDisplay, Toast.LENGTH_SHORT);
        myMessage.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
        try {
            unregisterReceiver(myDiscoverer);
            unregisterReceiver(checkIsConnected);
        } catch (Exception e) {
            Log.e("Ondestroy", "Receiver not registered");
        }
    }
}