package com.inv.ayt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    public String cache_folder = "cache";
    public final String fileFolder = "data";
    Button btn1, btn2;
    FloatingActionButton floatBtn;
    ArrayList<String> fileNames = new ArrayList<String>();
    ArrayList<String> filePaths = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        floatBtn = findViewById(R.id.floatBtn);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, EcgActivity.class);
                startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePrevFiles();
                Intent intent = new Intent(HomeActivity.this, ReportActivity.class);
                startActivity(intent);
            }
        });

        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFileList();
                String[] itemNames = new String[fileNames.size()];
                for (int i = 0; i < fileNames.size(); i++) {
                    itemNames[i] = fileNames.get(i);
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("Choose file");
                builder.setItems(itemNames, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HomeActivity.this, ViewEcgActivity.class);
                        intent.putExtra("filepath",filePaths.get(which));
                        startActivity(intent);
                    }
                });
                builder.create().show();
            }
        });
    }

    public void loadFileList() {
        File mPath = new File(getExternalFilesDir((String) null).getAbsolutePath() + "/" + fileFolder);
        System.out.println(mPath.getAbsolutePath());
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
            return;
        }
        File[] files_dir = mPath.listFiles();
//        file_names[0] = "Search on your device...";
        fileNames.clear();
        filePaths.clear();
        for (int index_i = 0; index_i < files_dir.length; index_i++) {
            fileNames.add(files_dir[index_i].getName());
            filePaths.add(files_dir[index_i].getPath());
        }
        return;
    }

    public void deletePrevFiles(){
        File mPath = new File(getExternalFilesDir((String) null).getAbsolutePath() + "/" + cache_folder);
        System.out.println(mPath.getPath());
        try {
            mPath.mkdirs();
        } catch (SecurityException e) {
            System.out.println(e.getMessage());
            return;
        }
        File[] files = mPath.listFiles();
        for (int index = 0; index < files.length; index++) {
            String filename = files[index].getName();
            System.out.println(filename);
            if(!filename.equals("vitals.txt")){
                files[index].delete();
                System.out.println("Deleted");
            }
        }
    }
}