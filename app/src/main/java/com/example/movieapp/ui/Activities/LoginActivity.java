package com.example.movieapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.SupabaseAuth;
import com.example.movieapp.data.auth.SupabaseAuthCallback;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity implements SupabaseAuthCallback {

    private EditText email, password;
    private Button loginBtn;
    private TextView forgetPass, register;
    private TextInputLayout passwordInputLayout;
    private ProgressBar loadingLogin;

    private SupabaseAuth supabaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
    }

    private void initViews() {
        email = findViewById(R.id.emailLoginEt);
        password = findViewById(R.id.passwordLoginEt);
        forgetPass = findViewById(R.id.forgetPassTxt);
        loginBtn = findViewById(R.id.loginBtn);
        loadingLogin = findViewById(R.id.loginProgressBar);
        loadingLogin.setVisibility(View.GONE);

        passwordInputLayout = findViewById(R.id.passwordInputLayout);
        passwordInputLayout.setEndIconDrawable(R.drawable.ic_eye_off);

        passwordInputLayout.setEndIconOnClickListener(v -> {
            // Check the current input type and toggle visibility
            if (password.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                passwordInputLayout.setEndIconDrawable(R.drawable.ic_eye_on);
            } else {
                password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                passwordInputLayout.setEndIconDrawable(R.drawable.ic_eye_off);
            }
            password.setSelection(password.getText().length());
        });

        register = findViewById(R.id.registerNowTxt);

        supabaseAuth = new SupabaseAuth(this);
        loginBtn.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
            } else {
                loadingLogin.setVisibility(View.VISIBLE);
                loginBtn.setVisibility(View.GONE);
                supabaseAuth.login(this, email.getText().toString(), password.getText().toString(), this);
            }

        });

        register.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

    }

    @Override
    public void onLoginSuccess(String accessToken) {
        loadingLogin.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "User login successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, MoviesMainActivity.class);
        startActivity(intent);
        finish(); // Optional: closes the LoginActivity
    }

    @Override
    public void onLoginFailure(String errorMessage) {
        loadingLogin.setVisibility(View.GONE);
        loginBtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRegisterSuccess() {
    }

    @Override
    public void onRegisterFailure(String errorMessage) {
    }

}
