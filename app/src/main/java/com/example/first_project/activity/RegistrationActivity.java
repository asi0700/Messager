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
import com.example.first_project.network.ApiClient;
import com.example.first_project.network.ApiService;
import com.example.first_project.network.SessionManager;
import com.example.first_project.network.dto.AuthRequest;
import com.example.first_project.network.dto.AuthResponse;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputEditText emailField;
    private TextInputEditText passwordField;
    private TextInputEditText confirmPasswordField;
    private TextView linkLogin;
    private ApiService api;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registration);

        api = ApiClient.get(this);
        session = new SessionManager(this);

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
                api.register(new AuthRequest(email, password, email))
                        .enqueue(new retrofit2.Callback<AuthResponse>() {
                            @Override
                            public void onResponse(retrofit2.Call<AuthResponse> call, retrofit2.Response<AuthResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    AuthResponse body = response.body();
                                    session.saveSession(
                                            body.token,
                                            body.user.id,
                                            body.user.username,
                                            body.user.displayName != null ? body.user.displayName : body.user.username
                                    );
                                    Toast.makeText(RegistrationActivity.this, "Аккаунт создан!", Toast.LENGTH_SHORT).show();
                                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                        startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                                        finish();
                                    }, 500);
                                } else {
                                    Toast.makeText(RegistrationActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(retrofit2.Call<AuthResponse> call, Throwable t) {
                                Toast.makeText(RegistrationActivity.this, "Сервер недоступен: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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