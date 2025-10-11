package com.example.first_project.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.R;
import com.example.first_project.model.User;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {


    private static final String TAG = "ProfileActivity";

    private TextView textUsername, textEmail, textUserId, textRegistrationDate, textChatsCount;
    private MaterialButton buttonEditProfile;
    private ImageView imageAvatar;
    private TextView textUsernameTitle;

    private DatabaseReference usersRef;
    private DatabaseReference userChatsRef;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.activity_profile);

        setupToolbar();
        initViews();
        loadUserData();

        imageAvatar.setOnClickListener(v -> {
            openImagePicker();
        });

        buttonEditProfile.setOnClickListener(v -> {
            Toast.makeText(this, "Редактирование профиля будет добавлено позже", Toast.LENGTH_SHORT).show();
        });

    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Мой профиль");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private void initViews() {
        imageAvatar = findViewById(R.id.imageAvatar);
        textUsernameTitle = findViewById(R.id.textUsernameTitle);
        textUsername = findViewById(R.id.textUsername);
        textEmail = findViewById(R.id.textEmail);
        textUserId = findViewById(R.id.textUserId);
        textRegistrationDate = findViewById(R.id.textRegistrationDate);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
    }


    private void loadUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Пользователь не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Log.d(TAG, "Загрузка данных пользователя: " + currentUser.getUid());


        textEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Не указан");
        textUserId.setText(currentUser.getUid());


        if (currentUser.getMetadata() != null) {
            long creationTimestamp = currentUser.getMetadata().getCreationTimestamp();
            String registrationDate = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm", Locale.getDefault())
                    .format(new Date(creationTimestamp));
            textRegistrationDate.setText(registrationDate);
        }

        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        usersRef = FirebaseDatabase.getInstance(databaseUrl).getReference("users").child(currentUser.getUid());

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null && user.getUsername() != null) {
                        textUsername.setText(user.getUsername());
                        textUsernameTitle.setText(user.getUsername());
                        Log.d(TAG, "Ник пользователя загружен: " + user.getUsername());
                    } else {
                        textUsername.setText("Не установлен");
                        textUsernameTitle.setText("Мой профиль");
                    }
                } else {
                    textUsername.setText("Не установлен");
                    Log.d(TAG, "User data not found in database");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                textUsername.setText("Ошибка загрузки");
                Log.e(TAG, "Ошибка загрузки пользоваетля: " + databaseError.getMessage());
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Выберите изображение"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageAvatar.setImageBitmap(bitmap);
                Toast.makeText(this, "Фото обновлено", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
