package com.example.movieapp.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.movieapp.R;
import com.example.movieapp.data.auth.TokenManager;
import com.example.movieapp.ui.Activities.LoginActivity;

public class ProfileFragment extends Fragment {

    private TextView welcome, email, emailWord;
    private Button logoutBtn;
    TokenManager tokenManager;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        tokenManager = new TokenManager(requireActivity());
        initViews(view);
        return view;
    }

    private void initViews(View view) {
        emailWord = view.findViewById(R.id.emailProfileTxt);
        email = view.findViewById(R.id.emailProfile);
        logoutBtn = view.findViewById(R.id.logoutBtn);

        welcome = view.findViewById(R.id.welcome);


        String token = tokenManager.getRefreshToken();
        if (token == null) {
            email.setVisibility(View.GONE);
            emailWord.setText("Login to enjoy more features");
            email.setTextSize(22);
            logoutBtn.setText("Login");

            welcome.setText("Welcome Guest");
            logoutBtn.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                getActivity().finish();
            });
            return;
        }

        String userEmail = tokenManager.getUserEmail();
        email.setText(userEmail);

        String name = userEmail.substring(0, userEmail.indexOf("@"));
        welcome.setText("Welcome " + name);

        logoutBtn.setOnClickListener(v -> {
            tokenManager.clearTokens();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            getActivity().finish();  // Close HomeActivity
        });
    }
}