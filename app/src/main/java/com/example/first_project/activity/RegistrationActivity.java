package com.example.first_project.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private TextInputEditText confirmPasswordField;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        mAuth = FirebaseAuth.getInstance();

        MaterialButton buttonRegister = findViewById(R.id.buttonRegister);
        emailField = findViewById(R.id.editTextEmail);
        passwordField = findViewById(R.id.editTextPassword);
        confirmPasswordField = findViewById(R.id.editTextConfirmPassword);
        linkLogin = findViewById(R.id.link_login);

        buttonRegister.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();
            String confirmPassword = confirmPasswordField.getText().toString().trim();

            if (validate(email, password, confirmPassword)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(this, "Аккаунт создан!", Toast.LENGTH_SHORT).show();

                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    startActivity(new Intent(this, UsernameActivity.class));
                                    finish();
                                }, 500);

                            } else {
                                Toast.makeText(this, "Ошикба!" + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });

        linkLogin.setOnClickListener(v ->{
            startActivity(new Intent(this, LoginActivity.class));

        });

    }

    private boolean validate(String email, String password, String confirmPassword) {
        if (email.isEmpty()) {
            emailField.setError("Введите email или номер телефона");
            emailField.requestFocus();
            return false;
        }

        if (password.isEmpty()) {
            passwordField.setError("Введите пароль");
            passwordField.requestFocus();
            return false;
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordField.setError("Подтвердите пароль");
            confirmPasswordField.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setError("Пароли не совпадают");
            confirmPasswordField.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            passwordField.setError("Пароль должен быть не короче 6 символов");
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

}