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
import com.example.first_project.network.ApiClient;
import com.example.first_project.network.ApiService;
import com.example.first_project.network.SessionManager;
import com.example.first_project.network.dto.ChatDto;
import com.example.first_project.network.dto.ChatsResponse;
import com.example.first_project.network.dto.MessageResponse;
import com.example.first_project.network.dto.StatusRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    private String currentUserId;
    private ApiService api;
    private SessionManager session;
    
    @Override
    protected  void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);

        session = new SessionManager(this);
        api = ApiClient.get(this);
        currentUserId = session.getUserId();
        if (currentUserId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
        setupSearch();
        setupToolbar();
        loadUserChats();

        fabNewChat.setOnClickListener(v -> {
            startActivity(new Intent(this, SearchActivity.class));
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        updateStatus("Online");
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        updateStatus("Offline");
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
        api.getUserChats(currentUserId).enqueue(new retrofit2.Callback<ChatsResponse>() {
            @Override
            public void onResponse(retrofit2.Call<ChatsResponse> call, retrofit2.Response<ChatsResponse> response) {
                allChats.clear();
                filteredChats.clear();
                if (response.isSuccessful() && response.body() != null && response.body().chats != null) {
                    for (ChatDto dto : response.body().chats) {
                        ChatItem chatItem = new ChatItem(
                                dto.chatId,
                                dto.name != null ? dto.name : "Пользователь",
                                dto.lastMessage != null ? dto.lastMessage : "Нет сообщений",
                                dto.lastMessageTime
                        );
                        chatItem.setOtherUserId(dto.otherUserId);
                        allChats.add(chatItem);
                    }
                    sortChatsByTime();
                } else {
                    showTestChats();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ChatsResponse> call, Throwable t) {
                showTestChats();
            }
        });
    }
    
    private void sortChatsByTime() {
        // Sort by timestamp (newest first)
        allChats.sort((chat1, chat2) -> Long.compare(chat2.getTimestamp(), chat1.getTimestamp()));
        
        // Clear and re-add all items to filtered list to maintain the sort order
        filteredChats.clear();
        filteredChats.addAll(allChats);
        
        adapter.notifyDataSetChanged();
    }

    private String getOtherUserId(String  chatId) {
        String[] users = chatId.split("_");
        return users[0].equals(currentUserId) ? users[1] : users[0];
    }

    private void showTestChats() {
        allChats.clear();
        filteredChats.clear();

        long currentTime = System.currentTimeMillis();
        
        // Create test chats with different timestamps
        ChatItem chat1 = new ChatItem("chat1", "Gleb", "Active now", currentTime);
        chat1.setOnline(true); // Set as online
        
        ChatItem chat2 = new ChatItem("chat2", "Семья", "Online", currentTime - 3600000); // 1 hour ago
        chat2.setOnline(true); // Set as online
        
        ChatItem chat3 = new ChatItem("chat3", "Bro", "What`s up bro?", currentTime - 7200000); // 2 hours ago
        chat3.setOnline(false); // Set as offline
        
        allChats.add(chat1);
        allChats.add(chat2);
        allChats.add(chat3);

        // Sort by timestamp
        sortChatsByTime();
    }

    private void openChat(ChatItem chatItem) {
        Intent openChat = new Intent(this, ChatActivity.class);
        openChat.putExtra("chat_id", chatItem.getChatId());
        openChat.putExtra("user_name", chatItem.getName());
        startActivity(openChat);
    }

    private void updateStatus(String status) {
        if (currentUserId == null) return;
        api.updateStatus(currentUserId, new StatusRequest(status))
                .enqueue(new retrofit2.Callback<MessageResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<MessageResponse> call, retrofit2.Response<MessageResponse> response) { }

                    @Override
                    public void onFailure(retrofit2.Call<MessageResponse> call, Throwable t) { }
                });
    }

}
