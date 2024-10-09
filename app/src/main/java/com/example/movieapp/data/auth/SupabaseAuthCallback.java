package com.example.movieapp.data.auth;

public interface SupabaseAuthCallback {
    void onLoginSuccess(String accessToken);

    void onLoginFailure(String errorMessage);

    void onRegisterSuccess();

    void onRegisterFailure(String errorMessage);
}
