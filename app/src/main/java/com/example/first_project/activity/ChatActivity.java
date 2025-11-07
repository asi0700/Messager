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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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

    // Работа с Reatime Database
    private DatabaseReference messageRef;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private String chatId;
    private String otherUserName;
    private String currentUserId;

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
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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

        try {
            String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
            database = FirebaseDatabase.getInstance(databaseUrl);
            storage = FirebaseStorage.getInstance();

            if (chatId != null) {
                messageRef = database.getReference("chats").child(chatId).child("messages");
                storageRef = storage.getReference().child("chat_images").child(chatId);
            } else {
                messageRef = database.getReference("messages");
                storageRef = storage.getReference().child("chat_images");
            }
        } catch (Exception e) {
            Log.e(TAG, "Database " + e.getMessage());
            return;
        }

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
        if (messageRef == null) {
            Log.e(TAG, "messageRef is null, невозможно загрузить сообщение ");
            return;
        }

        messageRef.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messages.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Message msg = dataSnapshot.getValue(Message.class);
                    if (msg != null ) {
                        // Обработка timestamp - если он 0 или null, устанавливаем текущее время
                        // ServerValue.TIMESTAMP автоматически конвертируется в long при чтении
                        if (msg.getTimestamp() == 0) {
                            Object timestampObj = dataSnapshot.child("timestamp").getValue();
                            if (timestampObj instanceof Long) {
                                msg.setTimestamp((Long) timestampObj);
                            } else if (timestampObj instanceof Map) {
                                // Если это ServerValue.TIMESTAMP (Map), используем текущее время
                                msg.setTimestamp(System.currentTimeMillis());
                            } else {
                                msg.setTimestamp(System.currentTimeMillis());
                            }
                        }
                        messages.add(msg);
                        Log.d(TAG, "Заргузка сообщений: " + msg.getText() + ", timestamp: " + msg.getTimestamp());
                    }
                }
                // Явная сортировка по timestamp для гарантии правильного порядка
                messages.sort((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()));
                adapter.notifyDataSetChanged();
                if (!messages.isEmpty()) {
                    recyclerView.scrollToPosition(messages.size() -1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG,  "ОШИбка загрузки сообщений: " + error.getMessage());
            }
        });
    }


    private void sendMessage(String text) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = "  ";
        }

        String messageId = messageRef.push().getKey();

        Message msg = new Message(currentUserId, currentUserName, text, chatId);
        // Используем ServerValue.TIMESTAMP для гарантии правильного порядка
        Map<String, Object> timestampMap = new HashMap<>();
        timestampMap.put("timestamp", ServerValue.TIMESTAMP);

        if (messageId != null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("senderId", msg.getSenderId());
            messageMap.put("senderName", msg.getSenderName());
            messageMap.put("text", msg.getText());
            messageMap.put("chatId", msg.getChatId());
            messageMap.put("type", msg.getType());
            messageMap.put("imageUrl", msg.getImageUrl());
            messageMap.put("timestamp", ServerValue.TIMESTAMP);

            messageRef.child(messageId).setValue(messageMap)
                    .addOnSuccessListener(aVoid -> {
                        updateLastMessage(text);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "ОШибка при отправке сообщений " + e.getMessage());
                    });
        }
    }

    private void sendImageMessage(String imageUrl, String caption) {
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentUserName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        if (currentUserName == null || currentUserName.isEmpty()) {
            currentUserName = "  ";
        }

        String messageId = messageRef.push().getKey();

        if (messageId != null) {
            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("senderId", currentUserId);
            messageMap.put("senderName", currentUserName);
            messageMap.put("text", caption != null ? caption : "");
            messageMap.put("chatId", chatId);
            messageMap.put("type", "image");
            messageMap.put("imageUrl", imageUrl);
            messageMap.put("timestamp", ServerValue.TIMESTAMP);

            messageRef.child(messageId).setValue(messageMap)
                    .addOnSuccessListener(aVoid -> {
                        updateLastMessage("Фото");
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Ошибка при отправке изображения " + e.getMessage());
                        Toast.makeText(this, "Ошибка при отправке изображения", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void updateLastMessage(String lastMessage) {
        if (chatId != null) {
            try {
                DatabaseReference chatRef = database.getReference("chats").child(chatId);
                chatRef.child("lastMessage").setValue(lastMessage);
                chatRef.child("lastMessagetime").setValue(ServerValue.TIMESTAMP);
            } catch (Exception e) {
                Log.e(TAG, "Ошибка  обновлений последних сообщений " + e.getMessage());
            }
        }
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
        if (storageRef == null) {
            Log.e(TAG, "storageRef is null");
            return;
        }

        // Создаем уникальное имя файла
        String fileName = "image_" + System.currentTimeMillis() + ".jpg";
        StorageReference imageRef = storageRef.child(fileName);

        // Показываем индикатор загрузки
        Toast.makeText(this, "Загрузка изображения...", Toast.LENGTH_SHORT).show();

        // Загружаем изображение
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> {
            // Получаем URL загруженного изображения
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrl = uri.toString();
                sendImageMessage(imageUrl, null);
                Toast.makeText(this, "Изображение отправлено", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Ошибка получения URL изображения: " + e.getMessage());
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Ошибка загрузки изображения: " + e.getMessage());
            Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            // Можно добавить ProgressBar для отображения прогресса
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Log.d(TAG, "Прогресс загрузки: " + progress + "%");
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