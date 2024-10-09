package com.example.movieapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;

public class SplashActivity extends AppCompatActivity {

    private TokenManager tokenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        tokenManager = new TokenManager(this);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            if (isUserLoggedIn()) {
                Intent intent = new Intent(SplashActivity.this, MoviesMainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    // check if the user is logged in
    private boolean isUserLoggedIn() {
        String accessToken = tokenManager.getAccessToken();
        return accessToken != null;
    }

}
