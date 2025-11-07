package com.example.first_project.activity;

import android.app.ProgressDialog;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {


    private static final String TAG = "ProfileActivity";

    private TextView textUsername, textEmail, textUserId, textRegistrationDate, textChatsCount;
    private MaterialButton buttonEditProfile;
    private ImageView imageAvatar;
    private TextView textUsernameTitle;

    private DatabaseReference usersRef;
    private DatabaseReference userChatsRef;
    private StorageReference storageRef;
    private FirebaseStorage storage;
    private String currentUserId;
    private User currentUserData;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState );
        setContentView(R.layout.activity_profile);

        setupToolbar();
        initViews();
        
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        
        // Initialize progress dialog for image uploads
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Загрузка изображения...");
        progressDialog.setCancelable(false);
        
        loadUserData();

        imageAvatar.setOnClickListener(v -> {
            openImagePicker();
        });

        buttonEditProfile.setOnClickListener(v -> {
            showEditProfileDialog();
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

        currentUserId = currentUser.getUid();
        Log.d(TAG, "Загрузка данных пользователя: " + currentUserId);

        textEmail.setText(currentUser.getEmail() != null ? currentUser.getEmail() : "Не указан");
        textUserId.setText(currentUserId);

        if (currentUser.getMetadata() != null) {
            long creationTimestamp = currentUser.getMetadata().getCreationTimestamp();
            String registrationDate = new SimpleDateFormat("dd.MM.yyyy 'в' HH:mm", Locale.getDefault())
                    .format(new Date(creationTimestamp));
            textRegistrationDate.setText(registrationDate);
        }

        String databaseUrl = "https://messenger-86a14-default-rtdb.europe-west1.firebasedatabase.app";
        usersRef = FirebaseDatabase.getInstance(databaseUrl).getReference("users").child(currentUserId);

        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserData = dataSnapshot.getValue(User.class);
                    if (currentUserData != null) {
                        if (currentUserData.getUsername() != null) {
                            textUsername.setText(currentUserData.getUsername());
                            textUsernameTitle.setText(currentUserData.getUsername());
                            Log.d(TAG, "Ник пользователя загружен: " + currentUserData.getUsername());
                        } else {
                            textUsername.setText("Не установлен");
                            textUsernameTitle.setText("Мой профиль");
                        }
                        
                        // Load profile image if available
                        if (currentUserData.getProfileImage() != null && !currentUserData.getProfileImage().isEmpty()) {
                            loadProfileImage(currentUserData.getProfileImage());
                        }
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
    
    private void loadProfileImage(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                .load(imageUrl)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(imageAvatar);
        }
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
                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imageAvatar.setImageBitmap(bitmap);
                
                // Upload the image to Firebase Storage
                uploadImageToFirebase();
                
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    private void uploadImageToFirebase() {
        if (imageUri == null) {
            return;
        }
        
        progressDialog.show();
        
        // Create a unique filename
        String filename = "profile_" + currentUserId + "_" + UUID.randomUUID().toString();
        StorageReference fileRef = storageRef.child("profile_images/" + filename);
        
        fileRef.putFile(imageUri)
            .addOnSuccessListener(taskSnapshot -> {
                // Get the download URL
                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    
                    // Update user profile in database
                    if (currentUserData != null) {
                        currentUserData.setProfileImage(imageUrl);
                        usersRef.child("profileImage").setValue(imageUrl)
                            .addOnSuccessListener(aVoid -> {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Фото профиля обновлено", Toast.LENGTH_SHORT).show();
                            })
                            .addOnFailureListener(e -> {
                                progressDialog.dismiss();
                                Toast.makeText(ProfileActivity.this, "Ошибка обновления профиля: " + e.getMessage(), 
                                        Toast.LENGTH_SHORT).show();
                            });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Ошибка: данные пользователя не найдены", 
                                Toast.LENGTH_SHORT).show();
                    }
                });
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Ошибка загрузки: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            })
            .addOnProgressListener(snapshot -> {
                double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                progressDialog.setMessage("Загрузка: " + (int) progress + "%");
            });
    }
    
    private void showEditProfileDialog() {
        if (currentUserData == null) {
            Toast.makeText(this, "Данные пользователя не загружены", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Create dialog with EditText fields
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Редактировать профиль");
        
        // Create layout for dialog
        android.widget.LinearLayout layout = new android.widget.LinearLayout(this);
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(50, 30, 50, 30);
        
        // Username field
        final android.widget.EditText usernameInput = new android.widget.EditText(this);
        usernameInput.setHint("Имя пользователя");
        usernameInput.setText(currentUserData.getUsername());
        layout.addView(usernameInput);
        
        // Status field
        final android.widget.EditText statusInput = new android.widget.EditText(this);
        statusInput.setHint("Статус");
        statusInput.setText(currentUserData.getStatus());
        layout.addView(statusInput);
        
        // Display name field
        final android.widget.EditText displayNameInput = new android.widget.EditText(this);
        displayNameInput.setHint("Отображаемое имя");
        displayNameInput.setText(currentUserData.getDisplayName());
        layout.addView(displayNameInput);
        
        builder.setView(layout);
        
        // Add buttons
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newUsername = usernameInput.getText().toString().trim();
            String newStatus = statusInput.getText().toString().trim();
            String newDisplayName = displayNameInput.getText().toString().trim();
            
            if (newUsername.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Имя пользователя не может быть пустым", Toast.LENGTH_SHORT).show();
                return;
            }
            
            updateUserProfile(newUsername, newStatus, newDisplayName);
        });
        
        builder.setNegativeButton("Отмена", (dialog, which) -> dialog.cancel());
        
        builder.show();
    }
    
    private void updateUserProfile(String username, String status, String displayName) {
        progressDialog.setMessage("Обновление профиля...");
        progressDialog.show();
        
        // Update user data
        currentUserData.setUsername(username);
        currentUserData.setStatus(status);
        currentUserData.setDisplayName(displayName);
        
        // Update in Firebase
        usersRef.child("username").setValue(username);
        usersRef.child("status").setValue(status);
        usersRef.child("displayName").setValue(displayName)
            .addOnSuccessListener(aVoid -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Профиль обновлен", Toast.LENGTH_SHORT).show();
                
                // Update UI
                textUsername.setText(username);
                textUsernameTitle.setText(username);
            })
            .addOnFailureListener(e -> {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Ошибка обновления профиля: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
            });
    }



}
