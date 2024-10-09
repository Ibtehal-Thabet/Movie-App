package com.example.movieapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;

public class IntroActivity extends AppCompatActivity {

    Button loginBtn, guestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        initViews();
    }

    private void initViews() {
        loginBtn = findViewById(R.id.introLoginBtn);
        guestBtn = findViewById(R.id.guestBtn);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        guestBtn.setOnClickListener(v -> {
            Intent intent = new Intent(IntroActivity.this, MoviesMainActivity.class);
            startActivity(intent);
            finish();
        });

    }

}