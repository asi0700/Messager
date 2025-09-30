package com.example.first_project.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        if ( auth != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else  {
            startActivity(new Intent(this, RegistrationActivity.class));
        }
        finish();
    }

}
