package com.example.first_project.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.MessageAdapter;
import com.example.first_project.model.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private String currentUseerId;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        currentUseerId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        List<Message> messages = new ArrayList<>();


        adapter = new MessageAdapter(messages, currentUseerId);
        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.btnBack_SecondActivity);
        button.setOnClickListener(v -> finish());

    };

}
