package com.example.luxevistaresort;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SplashActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if user is already logged in
        android.content.SharedPreferences session = getSharedPreferences("session", MODE_PRIVATE);
        String userEmail = session.getString("user_email", null);
        boolean isAdmin = session.getBoolean("is_admin", false);

        if (userEmail != null) {
            // User is logged in, go to appropriate activity
            Intent intent;
            if (isAdmin) {
                intent = new Intent(SplashActivity.this, AdminDashboardActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            }
            startActivity(intent);
            finish();
        } else {
            // Not logged in, show welcome screen
            setContentView(R.layout.activity_welcome);

            Button getStarted = findViewById(R.id.get_started_button);
            getStarted.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }
} 