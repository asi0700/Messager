package com.example.first_project.activity;


import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.example.first_project.network.ApiClient;
import com.example.first_project.network.ApiService;
import com.example.first_project.network.SessionManager;
import com.example.first_project.network.dto.AuthRequest;
import com.example.first_project.network.dto.AuthResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView linkRegister;
    private ApiService api;
    private SessionManager session;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        api = ApiClient.get(this);
        session = new SessionManager(this);

        MaterialButton login_btn = findViewById(R.id.buttonLogin);
        linkRegister = findViewById(R.id.textGoToRegister);
        email = findViewById(R.id.editTextLoginEmail);
        password = findViewById(R.id.editTextLoginPassword);

        login_btn.setOnClickListener(v -> {
            String emailStr = email.getText().toString().trim();
            String passwordStr = password.getText().toString().trim();

            if (emailStr.isEmpty() || passwordStr.isEmpty()) {
                Toast.makeText(this, "Заполните все поля" , Toast.LENGTH_SHORT).show();
                return;
            }

            api.login(new AuthRequest(emailStr, passwordStr))
                    .enqueue(new Callback<AuthResponse>() {
                        @Override
                        public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                AuthResponse body = response.body();
                                session.saveSession(
                                        body.token,
                                        body.user.id,
                                        body.user.username,
                                        body.user.displayName != null ? body.user.displayName : body.user.username
                                );
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Ошибка входа", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AuthResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Сервер недоступен: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });

        linkRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegistrationActivity.class));
            finish();
        });

    }

}

