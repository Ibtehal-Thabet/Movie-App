package com.example.movieapp.ui.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.SupabaseAuth;
import com.example.movieapp.data.auth.SupabaseAuthCallback;
import com.google.android.material.textfield.TextInputLayout;

public class RegisterActivity extends AppCompatActivity implements SupabaseAuthCallback {

    private EditText email, password;
    private Button registerBtn;
    private TextView login;
    private TextInputLayout passwordInputLayout;

    private SupabaseAuth supabaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();

    }

    private void initViews() {
//        username = findViewById(R.id.userNameEt);
        email = findViewById(R.id.emailEt);
        password = findViewById(R.id.passwordEt);
        registerBtn = findViewById(R.id.registerBtn);
        login = findViewById(R.id.loginNowTxt);

        passwordInputLayout = findViewById(R.id.passwordInputLayoutReg);
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

        supabaseAuth = new SupabaseAuth(this);

        registerBtn.setOnClickListener(v -> {
            if (email.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
            } else {
                supabaseAuth.signUpUser(email.getText().toString(), password.getText().toString(), this);
            }

        });

        login.setOnClickListener(v -> {
            goToLogin();
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginSuccess(String accessToken) {

    }

    @Override
    public void onLoginFailure(String errorMessage) {

    }

    @Override
    public void onRegisterSuccess() {
        Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show();
        goToLogin();
    }

    @Override
    public void onRegisterFailure(String errorMessage) {
        Toast.makeText(this, "Registration failed!", Toast.LENGTH_SHORT).show();
    }
}
