package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

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

    private String chatId;
    private String otherUserName;
    private String currentUserId;

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

            if (chatId != null) {
                messageRef = database.getReference("chats").child(chatId).child("messages");

            } else {
                messageRef = database.getReference("messages");
            }
        } catch (Exception e) {
            Log.e(TAG, "Database " + e.getMessage());
            return;
        }

        loadMessages();

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
                        messages.add(msg);
                        Log.d(TAG, "Заргузка сообщений: " + msg.getText() );
                    }
                }
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
        msg.setTimestamp(System.currentTimeMillis());

        if (messageId != null) {
            messageRef.child(messageId).setValue(msg)
                    .addOnSuccessListener(aVoid -> {
                        updateLastMessage(text);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "ОШибка при отправке сообщений " + e.getMessage());
                    });
        }

    }

    private void updateLastMessage(String lastMessage) {
        if (chatId != null) {
            try {
                DatabaseReference chatRef = database.getReference("chats").child(chatId);
                chatRef.child("lastMessage").setValue(lastMessage);
                chatRef.child("lastMessagetime").setValue(System.currentTimeMillis());
            } catch (Exception e) {
                Log.e(TAG, "Ошибка  обновлений последних сообщений " + e.getMessage());
            }
        }

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