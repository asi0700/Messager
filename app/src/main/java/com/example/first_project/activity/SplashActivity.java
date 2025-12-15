package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.first_project.network.SessionManager;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            checkAuthAndRedirect();
        }, 1000);

    }

    private void checkAuthAndRedirect() {
        SessionManager session = new SessionManager(this);
        if (session.getToken() != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else  {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
        finish();
    }

}
