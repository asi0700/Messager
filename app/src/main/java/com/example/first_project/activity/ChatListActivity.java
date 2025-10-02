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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerChats;
    private ChatsAdapter adapter;
    private final List<ChatItem> chats = new ArrayList<>();
    private DatabaseReference userChatsRef;
    private DatabaseReference chatsRef;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);

        recyclerChats = findViewById(R.id.recyclerChats);
        recyclerChats.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatsAdapter(chats, item -> {
            Intent openChat = new Intent(this, ChatActivity.class);
            openChat.putExtra("chat_id", item.getChatId());
            openChat.putExtra("user_name", item.getName());
            startActivity(openChat);
        });
        recyclerChats.setAdapter(adapter);

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        FirebaseDatabase db = FirebaseDatabase.getInstance(databaseUrl);
        userChatsRef = db.getReference("user_chats").child(currentUserId);
        chatsRef = db.getReference("chats");

        loadChats();
    }

    private void loadChats() {
        if (userChatsRef == null || chatsRef == null) return;

        userChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                chats.clear();
                for (DataSnapshot chatIdSnap : snapshot.getChildren()) {
                    String chatId = chatIdSnap.getKey();
                    if (chatId == null) continue;

                    chatsRef.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot chatSnap) {

                            String lastMessage = chatSnap.child("lastMessage").getValue(String.class);
                            Map<String, String> participantNames = (Map<String, String>) chatSnap.child("participantNames").getValue();
                            String otherName = extractOtherName(participantNames);

                            ChatItem item = new ChatItem(chatId, otherName != null ? otherName : "Чат", lastMessage != null ? lastMessage : "");
                            chats.add(item);
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) { }
                    });
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }

    private String extractOtherName(Map<String, String> participantNames) {
        if (participantNames == null || participantNames.isEmpty()) return null;
        String myId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        for (Map.Entry<String, String> e : participantNames.entrySet()) {
            if (myId == null || !e.getKey().equals(myId)) {
                return e.getValue();
            }
        }
        return null;
    }
}

