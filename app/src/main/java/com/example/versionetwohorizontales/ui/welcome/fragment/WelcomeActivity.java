package com.example.versionetwohorizontales.ui.welcome.fragment;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.versionetwohorizontales.R;

public class WelcomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);  // Qui utilizziamo il layout che contiene il NavHostFragment
    }
}