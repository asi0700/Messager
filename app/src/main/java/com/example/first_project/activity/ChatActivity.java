package com.example.first_project.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.MessageAdapter;
import com.example.first_project.model.Message;
import com.example.first_project.model.User;
import com.example.first_project.network.ApiClient;
import com.example.first_project.network.ApiService;
import com.example.first_project.network.SessionManager;
import com.example.first_project.network.dto.ChatMessageRequest;
import com.example.first_project.network.dto.MessageDto;
import com.example.first_project.network.dto.MessageResponse;
import com.example.first_project.network.dto.MessagesResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private static final String TAG = "ChatActivity";
    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText editMessage;
    private List<Message> messages = new ArrayList<>();

//    // работа с Firestore(Firebase)
//    private FirebaseFirestore db;
//    private CollectionReference messageRef;

    private String chatId;
    private String otherUserName;
    private String currentUserId;
    private ApiService api;
    private SessionManager session;

    // ActivityResultLauncher для выбора изображения
    private ActivityResultLauncher<String> imagePickerLauncher;
    private ActivityResultLauncher<String> permissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        chatId = intent.getStringExtra("chat_id");
        otherUserName = intent.getStringExtra("user_name");
        session = new SessionManager(this);
        api = ApiClient.get(this);
        currentUserId = session.getUserId();
        if (currentUserId == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        Log.d(TAG, "onCreate: Activityyy created " + "Chat ID" + chatId + " c" +" User " + otherUserName  );

//        Button button = findViewById(R.id.btn_Back);
//        Button btnThrid = findViewById(R.id.btn_thirdActivity);
        recyclerView = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editTextMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnBack = findViewById(R.id.btnBack);


        
        try {
            androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
            }

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle(otherUserName);
            }

            android.widget.TextView titleView = findViewById(R.id.textTitle);
            if (titleView != null && otherUserName != null) {
                titleView.setText(otherUserName);
            }
        } catch (Exception ignore) {

        }

        adapter = new MessageAdapter(messages, currentUserId);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

//        messages.add(new Message("Friend", "I`m gaaaaaaaaaa"));
//        adapter.notifyDataSetChanged();

//        Intent intentToSecond = new Intent(MainActivity.this,SecondActivity.class );
//        Intent intentToThird = new Intent(MainActivity.this,ThirdActivity.class );


        //intentToSecond.putExtra("user_name", "Asror");

        // Инициализация ActivityResultLauncher для выбора изображения
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                this::handleImageSelection
        );

        // Инициализация ActivityResultLauncher для запроса разрешений
        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openImagePicker();
                    } else {
                        Toast.makeText(this, "Разрешение необходимо для выбора изображений", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        loadMessages();

        ImageButton btnAttach = findViewById(R.id.btnAttach);
        if (btnAttach != null) {
            btnAttach.setOnClickListener(v -> {
                if (checkImagePermission()) {
                    openImagePicker();
                } else {
                    requestImagePermission();
                }
            });
        }

        btnSend.setOnClickListener(v -> {
            String text = editMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendMessage(text);
                editMessage.setText("");
//                recyclerView.scrollToPosition(adapter.getItemCount() -1);
            }
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                if (text.equals("Привет")){
//                    Message reply = new Message("Friend", "Привет!");
//                    adapter.addMessage(reply);
//                    recyclerView.scrollToPosition(adapter.getItemCount() -1);
//                } else {
//                    Message reply = new Message("Friend", "Давай, всё иди ");
//                    adapter.addMessage(reply);
//                    recyclerView.scrollToPosition(adapter.getItemCount()-1);
//                }
//            }, 2000 );
        });



        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
           finish();
        });
//
//        btnThrid.setOnClickListener(v ->{
//            startActivity(intentToThird);
//        });

    }

    private void loadMessages() {
        api.getMessages(chatId, 200).enqueue(new retrofit2.Callback<MessagesResponse>() {
            @Override
            public void onResponse(retrofit2.Call<MessagesResponse> call, retrofit2.Response<MessagesResponse> response) {
                messages.clear();
                if (response.isSuccessful() && response.body() != null && response.body().messages != null) {
                    for (MessageDto dto : response.body().messages) {
                        Message msg = new Message(dto.senderId, "", dto.text, dto.chatId);
                        msg.setMessageId(dto.id);
                        msg.setTimestamp(dto.timestamp);
                        msg.setImageUrl(dto.imageUrl);
                        msg.setType(dto.imageUrl != null ? "image" : "text");
                        messages.add(msg);
                    }
                    messages.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                    adapter.notifyDataSetChanged();
                    if (!messages.isEmpty()) {
                        recyclerView.scrollToPosition(messages.size() -1);
                    }
                }
            }

            @Override
            public void onFailure(retrofit2.Call<MessagesResponse> call, Throwable t) {
                Log.e(TAG, "Ошибка загрузки сообщений " + t.getMessage());
            }
        });
    }


    private void sendMessage(String text) {
        api.sendTextMessage(chatId, new ChatMessageRequest(text))
                .enqueue(new retrofit2.Callback<MessageResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<MessageResponse> call, retrofit2.Response<MessageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MessageDto dto = response.body().message;
                            Message msg = new Message(dto.senderId, "", dto.text, dto.chatId);
                            msg.setMessageId(dto.id);
                            msg.setTimestamp(dto.timestamp);
                            msg.setImageUrl(dto.imageUrl);
                            msg.setType(dto.imageUrl != null ? "image" : "text");
                            messages.add(msg);
                            messages.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messages.size() -1);
                        } else {
                            Toast.makeText(ChatActivity.this, "Ошибка отправки", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Сервер недоступен: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendImageMessage(String imageUrl, String caption) {
        // handled by server endpoint when we upload via multipart in uploadImageToServer
    }

    private boolean checkImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestImagePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void openImagePicker() {
        imagePickerLauncher.launch("image/*");
    }

    private void handleImageSelection(Uri imageUri) {
        if (imageUri != null) {
            uploadImageToFirebase(imageUri);
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        Toast.makeText(this, "Загрузка изображения...", Toast.LENGTH_SHORT).show();
        java.io.File file;
        try {
            file = com.example.first_project.util.FileUtils.copyUriToCache(this, imageUri);
        } catch (Exception e) {
            Toast.makeText(this, "Не удалось прочитать файл", Toast.LENGTH_SHORT).show();
            return;
        }

        okhttp3.RequestBody reqFile = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
        okhttp3.MultipartBody.Part body = okhttp3.MultipartBody.Part.createFormData("file", file.getName(), reqFile);

        api.sendImageMessage(chatId, body, "")
                .enqueue(new retrofit2.Callback<MessageResponse>() {
                    @Override
                    public void onResponse(retrofit2.Call<MessageResponse> call, retrofit2.Response<MessageResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            MessageDto dto = response.body().message;
                            Message msg = new Message(dto.senderId, "", dto.text, dto.chatId);
                            msg.setMessageId(dto.id);
                            msg.setTimestamp(dto.timestamp);
                            msg.setImageUrl(dto.imageUrl);
                            msg.setType("image");
                            messages.add(msg);
                            messages.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                            adapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(messages.size() -1);
                            Toast.makeText(ChatActivity.this, "Изображение отправлено", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ChatActivity.this, "Ошибка отправки изображения", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(ChatActivity.this, "Сервер недоступен: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart: Activity created");
    }

    @Override
    protected  void onResume(){
        super.onResume();
        Log.d(TAG, "onResume: Activity created");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Activity created");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: Activity created");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Activity created");
    }


}