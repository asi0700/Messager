package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.ChatsAdapter;
import com.example.first_project.model.ChatItem;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerChats;
    private ChatsAdapter adapter;
    private EditText editTextSearch;
    private FloatingActionButton fabNewChat;

    private final List<ChatItem> allChats = new ArrayList<>();
    private final List<ChatItem> filteredChats = new ArrayList<>();
    private DatabaseReference userChatRef;

    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);

        initViews();
        setupRecyclerView();
        setupSearch();
        setupToolbar();
        loadUserChats();

        fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });

    }



    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_profile) {
            openProfile();
            return true;
        } else if (id == R.id.menu_logout) {
            showLogoutConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openProfile() {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }


    private void showLogoutConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Выход из аккаунта")
                .setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Выйти", (dialog, which) -> logoutUser())
                .setNegativeButton("Отмена", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();


        Intent intent = new Intent(this, RegistrationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

        Toast.makeText(this, "Вы вышли из аккаунта", Toast.LENGTH_SHORT).show();
    }

    private void initViews(){
        recyclerChats = findViewById(R.id.recyclerChats);
        editTextSearch = findViewById(R.id.editTextSearch);
        fabNewChat = findViewById(R.id.fabNewChat);
    }

    private  void setupRecyclerView() {
        recyclerChats.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ChatsAdapter(filteredChats, item -> {
            openChat(item);
        });
        recyclerChats.setAdapter(adapter);
    }

    private void setupSearch() {
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterChats(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private  void filterChats(String query) {
        filteredChats.clear();

        if (query.isEmpty()) {
            filteredChats.addAll(allChats);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (ChatItem chat : allChats){
                if (chat.getName().toLowerCase().contains(lowerCaseQuery)){
                    filteredChats.add(chat);
                }
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void loadUserChats() {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        userChatRef = FirebaseDatabase.getInstance(databaseUrl).getReference("user_chats").child(currentUserId);

        userChatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allChats.clear();
                filteredChats.clear();

                if (snapshot.exists()) {

                    for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                        String chatId = chatSnapshot.getKey();
                        loadChatDetails(chatId);
                    }
                } else {
                    showTestChats();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showTestChats();
            }
        });
    }

    private  void loadChatDetails(String chatId) {
        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        DatabaseReference chatRef  = FirebaseDatabase.getInstance(databaseUrl).getReference("chats").child(chatId);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    String chatName = snapshot.child("participantNames").child(getOtherUserId(chatId)).getValue(String.class);
                    String lastMessage = snapshot.child("lastMessage").getValue(String.class);

                    if (chatName != null) {
                        ChatItem chatItem = new ChatItem(chatId, chatName, lastMessage != null ? lastMessage : "Нет сообщений");
                        allChats.add(chatItem);
                        filteredChats.add(chatItem);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private String getOtherUserId(String  chatId) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String[] users = chatId.split("_");
        return users[0].equals(currentUserId) ? users[1] : users[0];
    }

    private void showTestChats() {
        allChats.clear();
        filteredChats.clear();

        allChats.add(new ChatItem("chat1", "Gleb", "Active now"));
        allChats.add(new ChatItem("chat2", "Семья", "Online"));
        allChats.add(new ChatItem("chat3", "Bro", "What`s up bro?"));

        filteredChats.addAll(allChats);
        adapter.notifyDataSetChanged();
    }

    private void openChat(ChatItem chatItem) {
        Intent openChat = new Intent(this, ChatActivity.class);
        openChat.putExtra("chat_id", chatItem.getChatId());
        openChat.putExtra("user_name", chatItem.getName());
        startActivity(openChat);
    }

}
