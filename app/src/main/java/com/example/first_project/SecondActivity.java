package com.example.first_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.adapter.MessageAdapter;
import com.example.first_project.model.Message;

import java.util.ArrayList;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter adapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Message> messages = new ArrayList<>();
        messages.add(new Message("Asi", "Hello hddfdkjbfh bghbdg df dfgdfg gfd hgdfg dghdg dg fdjhgbfdgdnfg gdufghdfgbd gdghuidg h  sjfsfj jbfuishfsf jbuyhe78rtge re gh vfffuidfhguidHello hddfdkjbfh bghbdg df dfgdfg gfd hgdfg dghdg dg fdjhgbfdgdnfg gdufghdfgbd gdghuidg h  sjfsfj jbfuishfsf jbuyhe78rtge re gh vfffuidfhguidHello hddfdkjbfh bghbdg df dfgdfg gfd hgdfg dghdg dg fdjhgbfdgdnfg gdufghdfgbd gdghuidg h  sjfsfj jbfuishfsf jbuyhe78rtge re gh vfffuidfhguid"));
        messages.add(new Message("Gleb", "Hi!"));
        messages.add(new Message("aaaa", "ertad!"));

        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);

        Button button = findViewById(R.id.btnBack_SecondActivity);
        button.setOnClickListener(v -> finish());

    };

}
