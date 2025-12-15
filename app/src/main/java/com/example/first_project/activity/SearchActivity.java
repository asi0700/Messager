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
import com.example.first_project.model.User;
import com.example.first_project.network.ApiClient;
import com.example.first_project.network.ApiService;
import com.example.first_project.network.SessionManager;
import com.example.first_project.network.dto.ChatStartRequest;
import com.example.first_project.network.dto.ChatStartResponse;
import com.example.first_project.network.dto.UserDto;
import com.example.first_project.network.dto.UsersResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerUsers;
    private List<User> users = new ArrayList<>();
    private UsersAdapter adapter;
    private ApiService api;
    private SessionManager session;
    private String currentUserId;

    @Override
    protected  void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        api = ApiClient.get(this);
        session = new SessionManager(this);
        currentUserId = session.getUserId();
        if (currentUserId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

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
        api.searchUsers(username).enqueue(new retrofit2.Callback<UsersResponse>() {
            @Override
            public void onResponse(retrofit2.Call<UsersResponse> call, retrofit2.Response<UsersResponse> response) {
                users.clear();
                if (response.isSuccessful() && response.body() != null && response.body().users != null) {
                    for (UserDto dto : response.body().users) {
                        if (dto.id != null && !dto.id.equals(currentUserId)) {
                            User u = new User(dto.id, dto.email, dto.username);
                            u.setStatus(dto.status);
                            u.setDisplayName(dto.displayName);
                            u.setProfileImage(dto.profileImageUrl);
                            users.add(u);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(retrofit2.Call<UsersResponse> call, Throwable t) {
                Toast.makeText(SearchActivity.this, "Ошибка поиска: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onUserClick(User user) {
        createChatWithUser(user);
    }

    private void createChatWithUser(User otherUser) {
        api.startChat(new ChatStartRequest(otherUser.getUserId()))
                .enqueue(new retrofit2.Callback<ChatStartResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<ChatStartResponse> call, retrofit2.Response<ChatStartResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            String chatId = response.body().chatId;
                            Intent intent = new Intent(SearchActivity.this, ChatActivity.class);
                            intent.putExtra("chat_id", chatId);
                            intent.putExtra("user_name", otherUser.getUsername());
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(SearchActivity.this, "Не удалось создать чат", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ChatStartResponse> call, Throwable t) {
                        Toast.makeText(SearchActivity.this, "Ошибка: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
