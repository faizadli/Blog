package com.example.blog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {
    private NavController navController;
    private FrameLayout fabContainer;
    private BottomAppBar bottomAppBar;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Check login status
        if (!sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize views
        bottomAppBar = findViewById(R.id.bottomAppBar);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        fabContainer = findViewById(R.id.fabContainer);

        // Set up bottom navigation
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_search, R.id.navigation_saved, R.id.navigation_profile)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // Set up FAB click listener
        fabContainer.setOnClickListener(v -> {
            if (navController.getCurrentDestination().getId() != R.id.navigation_create) {
                navController.navigate(R.id.navigation_create);
            }
        });

        // Hide/show bottom app bar and FAB based on current destination
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_create) {
                bottomAppBar.setVisibility(View.GONE);
                fabContainer.setVisibility(View.GONE);
            } else {
                bottomAppBar.setVisibility(View.VISIBLE);
                fabContainer.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}