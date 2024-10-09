package com.example.movieapp.data.auth;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.movieapp.BuildConfig;
import com.example.movieapp.domain.user.LoginResponse;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.OkHttpClient;

public class SupabaseAuth {
    private static final String SUPABASE_URL = "https://" + BuildConfig.PROJECT_KEY + ".supabase.co";
    private static final String SUPABASE_API_KEY = BuildConfig.SUPABASE_API_KEY;
    private OkHttpClient client;
    private Context context;
    TokenManager tokenManager;

    public SupabaseAuth(Context context) {
        this.context = context;
        this.tokenManager = new TokenManager(context);
        client = new OkHttpClient();
    }

    public void signUpUser(String email, String password, SupabaseAuthCallback callback) {
        // Create the JSON request body for sign-up
        String json = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";
        String url = SUPABASE_URL + "/auth/v1/signup";

//        RequestBody body = RequestBody.create(
//                json, MediaType.parse("application/json; charset=utf-8")
//        );

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    callback.onRegisterSuccess();
//                    Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show();

                },
                error -> {
                    callback.onRegisterFailure("Registration failed!");
//                    Toast.makeText(context, "Registration failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("apikey", SUPABASE_API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);

//        String jsonBody;
//        try {
//             jsonBody = new JSONObject()
//                    .put("email", email)
//                    .put("password", password)
//                    .toString();
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        }

//        RequestBody requestBody = RequestBody.create(
//                jsonBody,
//                MediaType.parse("application/json")
//        );

        // Define the Supabase sign-up API endpoint

        // Build the HTTP request
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("apikey", SUPABASE_API_KEY)
//                .addHeader("Authorization", "Bearer YOUR_JWT_TOKEN")
//                .post(requestBody)
//                .build();
//
//        // Make the asynchronous HTTP call
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    Log.i("success", "User signed up successfully");
//                } else {
//                    Log.i("Error", "Sign-up failed: " + response.body().string());
//                }
//            }
//        });
    }

    public void login(Context context, String email, String password, SupabaseAuthCallback callback) {
        String url = SUPABASE_URL + "/auth/v1/token?grant_type=password";

//        String json = "{\"email\":\"" + email + "\", \"password\":\"" + password + "\"}";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("email", email);
            jsonBody.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody, response -> {
            try {
                handleLoginResponse(context, response.toString());
                String accessToken = response.getString("access_token");

                Log.i("success", "Login successful: " + response);
                callback.onLoginSuccess(accessToken);
                // Proceed with authenticated user actions
            } catch (JSONException e) {
                e.printStackTrace();
//                            Toast.makeText(context, "Error parsing JSON response", Toast.LENGTH_SHORT).show();
                callback.onLoginFailure("Error parsing JSON response");
                Log.i("Error Login ", "Error: " + response.toString());
            }
        }, error -> {
            callback.onLoginFailure("Login failed: " + error.getMessage());
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                java.util.Map<String, String> headers = new java.util.HashMap<>();
                headers.put("apikey", SUPABASE_API_KEY);
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);


//        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));
//        Request request = new Request.Builder()
//                .url(url)
//                .addHeader("apikey", SUPABASE_API_KEY)
//                .addHeader("Content-Type", "application/json")
//                .post(body)
//                .build();
//
//        // Make the request asynchronously
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();  // Handle failure
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response.isSuccessful()) {
//                    // Handle successful login response
//                    String responseData = response.body().string();
//                    handleLoginResponse(context, responseData);
//                    Log.i("success", "Login successful: " + responseData);
//                } else {
//                    // Handle error response
//                    Log.i("Error Login " + isLoginError(), "Error: " + response.body().string());
//                }
//            }
//        });
    }

    public void handleLoginResponse(Context context, String response) {
        // Parse the JSON response
        Gson gson = new Gson();
        LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);

        // Store the tokens
        tokenManager.saveTokens(context, loginResponse.getAccess_token(), loginResponse.getRefresh_token(), loginResponse.getUser().getEmail(), loginResponse.getUser().getId());

    }

//    private void saveTokens(Context context, String accessToken, String refreshToken, String email) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putString("access_token", accessToken);
//        editor.putString("refresh_token", refreshToken);
//        editor.putString("email", email);
//        editor.apply();
//    }

//    public void logout() {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPrefs", MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove("access_token");  // Remove the token
//        editor.apply();
//    }

    // Function to check if the user is logged in
    private boolean isUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", null);
        return accessToken != null;  // Return true if the access token exists
    }
}

