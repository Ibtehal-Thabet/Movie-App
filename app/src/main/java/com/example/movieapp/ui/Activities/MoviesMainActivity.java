package com.example.movieapp.ui.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.movieapp.R;
import com.example.movieapp.ui.Fragments.FavoriteFragment;
import com.example.movieapp.ui.Fragments.MainFragment;
import com.example.movieapp.ui.Fragments.ProfileFragment;
import com.example.movieapp.ui.Fragments.SearchFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.Locale;


public class MoviesMainActivity extends AppCompatActivity {

    private EditText searchET;
    private LinearLayout searchLayout;
    private TextView whatTxt;
    private ImageButton searchBtn, micBtn;
    private BottomNavigationView bottomNavigationView;
    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_movies);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        if (savedInstanceState == null) {
            pushFragment(new MainFragment(), "MAIN_FRAGMENT");
        }
        initViews();
    }

    private void initViews() {
        searchET = findViewById(R.id.searchEt);
        searchBtn = findViewById(R.id.searchIcon);
        micBtn = findViewById(R.id.micIcon);
        searchLayout = findViewById(R.id.searchLayout);

        micBtn.setOnClickListener(v -> {
            searchBtn.setImageResource(R.drawable.ic_arrow_back);
            searchET.requestFocus();
            checkAudioPermission();
            startSpeechToText();
        });

        final Fragment[] lastFragment = new Fragment[1];
        searchET.setOnFocusChangeListener((v, hasFocus) -> {
//            lastFragment[0] = getCurrentFragment();
            if (hasFocus) {
                lastFragment[0] = getCurrentFragment();

                bottomNavigationView.setVisibility(View.GONE);
                pushSearchFragment(searchET.getText().toString());

                searchBtn.setImageResource(R.drawable.ic_arrow_back);
                searchBtn.setOnClickListener(v1 -> {
                    searchET.clearFocus();
                    searchET.setText("");
                    hideKeyboard(v);
                    getSupportFragmentManager().popBackStack(lastFragment[0].getTag(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    speechRecognizer.destroy();
                });
            } else {
                if (lastFragment[0] != null) {
                    pushFragment(lastFragment[0], lastFragment[0].getTag());
                } else {
                    pushFragment(new MainFragment(), lastFragment[0].getTag());
                }

                searchBtn.setImageResource(R.drawable.search);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

        searchET.setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_BACK) {
                searchET.clearFocus();
                hideKeyboard(v);
            }
            return true;
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pushSearchFragment(charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                pushSearchFragment(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        whatTxt = findViewById(R.id.whatTV);

        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_main);


        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            String tag = "";
            if (item.getItemId() == R.id.nav_main) {
                fragment = new MainFragment();
                tag = "MAIN_FRAGMENT";
                whatTxt.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.VISIBLE);

            } else if (item.getItemId() == R.id.nav_fav) {
                fragment = new FavoriteFragment();
                tag = "FAV_FRAGMENT";
                whatTxt.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.VISIBLE);

            } else if (item.getItemId() == R.id.nav_profile) {
                fragment = new ProfileFragment();
                tag = "PROFILE_FRAGMENT";
                whatTxt.setVisibility(View.GONE);
                searchLayout.setVisibility(View.GONE);
            }
            if (fragment != null) {
                pushFragment(fragment, tag);
            }
            return true;
        });
    }

    private void pushFragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, fragment, tag);
        fragmentTransaction.commit();
    }

    private Fragment getCurrentFragment() {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        return fragmentManager.findFragmentById(R.id.fragment_content);
    }

    private void pushSearchFragment(String searchText) {
        SearchFragment searchFragment = new SearchFragment();

        Bundle args = new Bundle();
        args.putString("search_query", searchText);

        searchFragment.setArguments(args);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_content, searchFragment, "SEARCH_FRAGMENT")
                .addToBackStack("SEARCH_FRAGMENT")
                .commit();
    }

    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void startSpeechToText() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        }
        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                searchET.setText("");
                searchET.setHint("Listening...");
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {
                String message = "";
                switch (i) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        message = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        message = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        message = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        message = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        message = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        message = "No match found";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        message = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        message = "Error from server";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        message = "No speech input";
                        break;
                    default: {
                        message = "Unknown error";
                        break;
                    }
                }
                searchET.setText("");
                searchET.setHint("Search Movies...");
                searchET.clearFocus();
                Toast.makeText(MoviesMainActivity.this, "Error occurred: " + message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null) {
                    searchET.setText(data.get(0));
                }
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer.startListening(speechRecognizerIntent);
        } else {
            searchET.setHint("Search Movies...");
            Toast.makeText(this, "Speech recognition not available on this device", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkAudioPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // M = 23
        if (ContextCompat.checkSelfPermission(this, "android.permission.RECORD_AUDIO") != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.programmingtech.offlinespeechtotext"));
            startActivity(intent);
            Toast.makeText(this, "Allow Microphone Permission", Toast.LENGTH_SHORT).show();
        }
//        }
    }

    private void checkPermission() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, RecordAudioRequestCode);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }
}
