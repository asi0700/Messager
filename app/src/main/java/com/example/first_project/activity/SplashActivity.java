package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(() -> {
            checkAuthAndRedirect();
        }, 1000);

    }

    private void checkAuthAndRedirect() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if ( currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else  {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
        finish();
    }

}
