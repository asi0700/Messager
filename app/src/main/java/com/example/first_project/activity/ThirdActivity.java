package com.example.first_project.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.R;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceStata){
        super.onCreate(savedInstanceStata);
        setContentView(R.layout.activity_third);
        findViewById(R.id.btn_BackMain).setOnClickListener(v -> finish());
    }



}
