package com.example.movieapp.data.auth;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class TokenManager {

    private SharedPreferences sharedPreferences;

    public TokenManager(Context context) {
        try {
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);
            sharedPreferences = EncryptedSharedPreferences.create(
                    "AuthPrefs",
                    masterKeyAlias,
                    context,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    // Save tokens securely
//    public void saveTokens(String accessToken, String refreshToken) {
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("access_token", accessToken);
//        editor.putString("refresh_token", refreshToken);
//        editor.apply();
//    }
    public void saveTokens(Context context, String accessToken, String refreshToken, String email, String userId) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("access_token", accessToken);
        editor.putString("refresh_token", refreshToken);
        editor.putString("email", email);
        editor.putString("user_id", userId);
        editor.apply();
    }

    // Get user id
    public String getUserId() {
        return sharedPreferences.getString("user_id", null);
    }


    // Get access token
    public String getAccessToken() {
        return sharedPreferences.getString("access_token", null);
    }

    // Get refresh token
    public String getRefreshToken() {
        return sharedPreferences.getString("refresh_token", null);
    }

    // Get user email
    public String getUserEmail() {
        return sharedPreferences.getString("email", null);
    }

    // Clear tokens
    public void clearTokens() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("access_token");
        editor.remove("refresh_token");
        editor.remove("user_id");
        editor.apply();
    }
}

