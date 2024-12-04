package com.example.blog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail, editPassword;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_USER_ID = "user_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.activity_login);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.open();

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check if user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            return;
        }

        // Initialize views
        editEmail = findViewById(R.id.login_email);
        editPassword = findViewById(R.id.login_password);
        Button buttonLogin = findViewById(R.id.login_button);
        TextView signupRedirect = findViewById(R.id.signupRedirectText);

        // Set click listeners
        buttonLogin.setOnClickListener(v -> performLogin());
        signupRedirect.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            finish();
        });
    }

    private void performLogin() {
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        // Validate input
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get username from email for existing method compatibility
        String username = email.split("@")[0]; // Temporary solution

        // Check credentials
        if (databaseHelper.checkUser(username, password)) {
            // Get user ID
            long userId = databaseHelper.getUserId(username);

            // Save login state
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_USERNAME, username);
            editor.putLong(KEY_USER_ID, userId);
            editor.apply();

            // Navigate to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}