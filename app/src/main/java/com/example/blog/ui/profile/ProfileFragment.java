package com.example.blog.ui.profile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.blog.DatabaseHelper;
import com.example.blog.LoginActivity;
import com.example.blog.databinding.FragmentProfileBinding;
import android.content.Context;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize database helper
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.open();

        // Initialize SharedPreferences
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        // Get user data
        String username = sharedPreferences.getString(KEY_USERNAME, "");
        String email = databaseHelper.getUserEmail(username);

        // Set user data to views
        binding.textUsername.setText(username);
        binding.textEmail.setText(email);

        // Setup logout button
        binding.buttonLogout.setOnClickListener(v -> logout());

        return root;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();

        databaseHelper.close();

        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        databaseHelper.close();
        binding = null;
    }
}