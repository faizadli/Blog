package com.example.blog.ui.detail;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;
import com.example.blog.databinding.FragmentBlogDetailBinding;
import java.io.File;

public class BlogDetailFragment extends Fragment {
    private FragmentBlogDetailBinding binding;
    private BlogDetailViewModel viewModel;
    private Button saveButton;
    private boolean isBlogSaved = false;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_USER_ID = "user_id";
    private long userId;
    private long postId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBlogDetailBinding.inflate(inflater, container, false);
        saveButton = binding.buttonSave;
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(BlogDetailViewModel.class);
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.open();

        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong(KEY_USER_ID, -1);
        Log.d("BlogDetail", "User ID from SharedPreferences: " + userId);

        if (getArguments() != null) {
            BlogPost blogPost = getArguments().getParcelable("blog_post");
            if (blogPost != null) {
                viewModel.setBlogPost(blogPost);
                postId = blogPost.getId();
                Log.d("BlogDetail", "Post ID from BlogPost: " + postId);
                checkSaveStatus();
                setupSaveButton();
            }
        }

        viewModel.getBlogPost().observe(getViewLifecycleOwner(), this::displayBlogPost);
    }

    private void checkSaveStatus() {
        isBlogSaved = databaseHelper.isBlogPostSaved(userId, postId);
        updateSaveButtonState();
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            Log.d("BlogDetail", "Save button clicked");
            Log.d("BlogDetail", "UserId: " + userId + ", PostId: " + postId);

            if (userId == -1) {
                Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            if (postId == 0) {
                Toast.makeText(requireContext(), "Post ID not found", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success;
            if (isBlogSaved) {
                success = databaseHelper.unsaveBlogPost(userId, postId);
                Log.d("BlogDetail", "Unsave result: " + success);
            } else {
                success = databaseHelper.saveBlogPost(userId, postId);
                Log.d("BlogDetail", "Save result: " + success);
            }

            if (success) {
                isBlogSaved = !isBlogSaved;
                updateSaveButtonState();
                Toast.makeText(requireContext(),
                        isBlogSaved ? "Post saved successfully" : "Post unsaved successfully",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(),
                        "Failed to " + (isBlogSaved ? "unsave" : "save") + " post",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayBlogPost(BlogPost blogPost) {
        if (blogPost != null) {
            binding.textTitle.setText(blogPost.getTitle());
            binding.textContent.setText(blogPost.getContent());
            binding.textAuthorDate.setText(String.format("By %s on %s",
                    blogPost.getAuthorName(), blogPost.getCreatedAt()));

            String imagePath = blogPost.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                binding.imagePost.setVisibility(View.VISIBLE);
                Glide.with(requireContext())
                        .load(new File(imagePath))
                        .centerCrop()
                        .into(binding.imagePost);
            } else {
                binding.imagePost.setVisibility(View.GONE);
            }
        }
    }

    private void updateSaveButtonState() {
        if (isBlogSaved) {
            saveButton.setText("Unsave");
        } else {
            saveButton.setText("Save");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (databaseHelper != null) {
            checkSaveStatus();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        databaseHelper.close();
    }
}