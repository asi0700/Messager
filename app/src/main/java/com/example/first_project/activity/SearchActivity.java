package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.UsersAdapter;
import com.example.first_project.model.Chat;
import com.example.first_project.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerUsers;
    private DatabaseReference usersRef;
    private List<User> users = new ArrayList<>();
    private UsersAdapter adapter;

    @Override
    protected  void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Используем тот же URL Realtime Database, что и в UsernameActivity
        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        usersRef = FirebaseDatabase.getInstance(databaseUrl).getReference("users");

        initView();
        setupRecyclerView();
        setupSearch();

    }

    private void initView() {
        searchView = findViewById(R.id.searchView);
        recyclerUsers = findViewById(R.id.recyclerUsers);
    }

    private  void setupRecyclerView() {
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UsersAdapter(users, this::onUserClick);
        recyclerUsers.setAdapter(adapter);
    }

    private  void setupSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public  boolean onQueryTextChange(String newText){
                if (newText.isEmpty()) {
                    users.clear();
                    adapter.notifyDataSetChanged();
                } else {
                    searchUsers(newText);
                }
                return false;
            }
        });
    }

    private void searchUsers(String username) {
        usersRef.orderByChild("username")
                .startAt(username)
                .endAt(username + "\uf8ff")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        users.clear();
                        String currentUserId = getCurrentUserId();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            User user = dataSnapshot.getValue(User.class);
                            if (user != null && !user.getUserId().equals(currentUserId)){
                                users.add(user);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SearchActivity.this, "ОШибка поиска", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getCurrentUserId(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return  user != null ? user.getUid() : "";
    }

    private void onUserClick(User user) {
        createChatWithUser(user);
    }

    private void createChatWithUser(User otherUser) {
        String currentUserId = getCurrentUserId();
        String chatId = generateChatId(currentUserId, otherUser.getUserId());

        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        DatabaseReference chatsRef = FirebaseDatabase.getInstance(databaseUrl).getReference("chats");
        DatabaseReference userChatsRef = FirebaseDatabase.getInstance(databaseUrl).getReference("user_chats");

        // Создаем новый объект чата (потом можно ремлизовать провеку на сущест чат с username )
        Map<String, String> participantNames = new HashMap<>();
        participantNames.put(currentUserId, getCurrentUserName());
        participantNames.put(otherUser.getUserId(), otherUser.getUsername());

        List<String> participants = Arrays.asList(currentUserId, otherUser.getUserId());

        Chat chat = new Chat(chatId, participants, participantNames);

        chatsRef.child(chatId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    chatsRef.child(chatId).setValue(chat);
                } else {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("chatId", chatId);
                    meta.put("participants", participants);
                    meta.put("participantNames", participantNames);
                    chatsRef.child(chatId).updateChildren(meta);
                }

                userChatsRef.child(currentUserId).child(chatId).setValue(true);
                userChatsRef.child(otherUser.getUserId()).child(chatId).setValue(true);

                Toast.makeText(SearchActivity.this, "Чат создан с " + otherUser.getUsername(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                intent.putExtra("chat_id", chatId);
                intent.putExtra("user_name", otherUser.getUsername());
                startActivity(intent);
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SearchActivity.this, "Ошибка создания чата: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private  String getCurrentUserName() {
        return "You"; // временно потом из файрбаса получу реальный ник
    }

    private  String generateChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 ? user1 +"_" + user2 : user2 + "_" + user1;
    }

}
