package com.inv.ayt;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LeadActivity extends AppCompatActivity {

    LinearLayout btn1, btn2, btn3, btn4, btn5, btn6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        btn5 = findViewById(R.id.btn5);
        btn6 = findViewById(R.id.btn6);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Lead I", "Place device as shown in figure", R.drawable.lead);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Lead II", "Place device as shown in figure", R.drawable.lead);
            }
        });

        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Lead III", "Place device as shown in figure", R.drawable.lead);
            }
        });

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Augmented Vector Right (aVR)", "5th Intercostal space at the midclavicular line", R.drawable.lead);
            }
        });

        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Augmented Vector Left (aVL)", "Anterior axillary line at the same level as V4", R.drawable.lead);
            }
        });

        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails("Augmented vector foot (aVF)", "Midaxillary line at the same level as V4 and V5", R.drawable.lead);
            }
        });
    }

    public void showDetails(String title, String des, int image) {
        Dialog dialog = new Dialog(LeadActivity.this);
        dialog.setContentView(R.layout.lead_dialog);
        TextView textView = dialog.findViewById(R.id.title);
        textView.setText(title);
        TextView textView1 = dialog.findViewById(R.id.text);
        textView1.setText(des);
        ImageView imageView = dialog.findViewById(R.id.imageView);
//        imageView.setBackgroundResource(image);
        textView1.setText(des);
        dialog.findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LeadActivity.this, EcgActivity.class);
                startActivity(intent);
            }
        });
        dialog.show();
    }
}