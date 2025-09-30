package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.first_project.R;
import com.example.first_project.adapter.MessageAdapter;
import com.example.first_project.model.Message;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Start";

    private RecyclerView recyclerView;
    private MessageAdapter adapter;
    private EditText editMessage;
    private List<Message> messages = new ArrayList<>();

    // работа с Firestore(Firebase)
    private FirebaseFirestore db;
    private CollectionReference messageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);



        Log.d(TAG, "onCreate: Activityyy created");

//        Button button = findViewById(R.id.btn_Back);
//        Button btnThrid = findViewById(R.id.btn_thirdActivity);
        recyclerView = findViewById(R.id.recyclerMessages);
        editMessage = findViewById(R.id.editTextMessage);
        ImageButton btnSend = findViewById(R.id.btnSend);
        ImageButton btnBack = findViewById(R.id.btnBack);


        adapter = new MessageAdapter(messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messages.add(new Message("Friend", "I`m gaaaaaaaaaa"));
        adapter.notifyDataSetChanged();

//        Intent intentToSecond = new Intent(MainActivity.this,SecondActivity.class );
//        Intent intentToThird = new Intent(MainActivity.this,ThirdActivity.class );


        //intentToSecond.putExtra("user_name", "Asror");

        db = FirebaseFirestore.getInstance();
        messageRef = db.collection("messages");

        messageRef.orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener((snapshot,  error ) -> {
                    if (error != null) {
                        Log.w(TAG, "Ошибка Firestore", error);
                        return;
                    }

                    messages.clear();
                    for (var document : snapshot) {
                        Message msg = document.toObject(Message.class);
                        messages.add(msg);
                    }

                    adapter.notifyDataSetChanged();
                    if (!messages.isEmpty()) {
                        recyclerView.scrollToPosition(messages.size() -1);
                    }

                });


        btnSend.setOnClickListener(v -> {
            String text = editMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                Message msg = new Message("You", text);
                messageRef.add(msg);
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
            try {
                startActivity(new Intent(this, ChatListActivity.class));
                finish();
            } catch (Exception e) {
                finish();
            }
        });
//
//        btnThrid.setOnClickListener(v ->{
//            startActivity(intentToThird);
//        });


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