package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.ChatsAdapter;
import com.example.first_project.model.ChatItem;

import java.util.ArrayList;
import java.util.List;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerChats;
    private ChatsAdapter adapter;
    private final List<ChatItem> chats = new ArrayList<>();

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);

        recyclerChats = findViewById(R.id.recyclerChats);
        recyclerChats.setLayoutManager(new LinearLayoutManager(this));


        chats.add(new ChatItem("Gleb", "Active now"));
        chats.add(new ChatItem("Семья", "Online"));
        chats.add(new ChatItem("Bro", "What`s up bro?"));

        adapter = new ChatsAdapter(chats, item -> {
            Intent openChat = new Intent(this, MainActivity.class);
            openChat.putExtra("chat_name", item.getName());
            startActivity(openChat);
        });

        recyclerChats.setAdapter(adapter);
    }
}

