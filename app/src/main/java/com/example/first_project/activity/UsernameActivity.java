package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.R;
import com.example.first_project.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UsernameActivity extends AppCompatActivity {
    private TextInputEditText editTextUsername;
    private MaterialButton buttonContinue;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;

    private static final String TAG = "UsernameActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);

        Log.d(TAG, "=== НАЧАЛО ЛОГОВ ===");


        mAuth = FirebaseAuth.getInstance();

        try {

            String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
            FirebaseDatabase database = FirebaseDatabase.getInstance(databaseUrl);
            usersRef = database.getReference("users");
            Log.d(TAG, "Database initialized successfully: " + databaseUrl);
        } catch (Exception e) {
            Log.e(TAG, "Database init failed: " + e.getMessage());
            Toast.makeText(this, "Ошибка инициализации базы данных", Toast.LENGTH_LONG).show();
            return;
        }

        editTextUsername = findViewById(R.id.editTextUsername);
        buttonContinue = findViewById(R.id.buttonContinue);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User: " + currentUser.getEmail() + ", UID: " + currentUser.getUid());
        } else {
            Log.e(TAG, "No user found!");
            Toast.makeText(this, "Ошибка: пользователь не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        buttonContinue.setOnClickListener(v -> {
            Log.d(TAG, "BUTTON CLICKED - FIXED VERSION");

            String username = editTextUsername.getText().toString().trim();

            if (username.isEmpty()) {
                editTextUsername.setError("Введите никнейм");
                return;
            }

            if (username.length() < 3) {
                editTextUsername.setError("Никнейм должен быть не короче 3 символов");
                return;
            }

            Log.d(TAG, "Сохранение: " + username);
            saveUserDirectly(username);
        });
    }

    private void saveUserDirectly(String username) {

        if (usersRef == null) {
            Log.e(TAG, "usersRef is NULL!");
            Toast.makeText(this, "Ошибка: база данных не инициализирована", Toast.LENGTH_LONG).show();
            return;
        }

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Log.e(TAG, "No user found in save method!");
            return;
        }

        String userId = firebaseUser.getUid();
        String email = firebaseUser.getEmail();

        Log.d(TAG, "Attempting to save - UID: " + userId + ", Username: " + username);

        User user = new User(userId, email, username);

        // Запись в базу данных
        usersRef.child(userId).setValue(user)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "=== SUCCESS: User saved to database! ===");
                    Toast.makeText(this, "Успех! Добро пожаловать, " + username + "!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "=== FAILED: " + e.getMessage() + " ===");
                    Toast.makeText(this, "Ошибка сохранения: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}