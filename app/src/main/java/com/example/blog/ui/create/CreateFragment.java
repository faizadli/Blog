package com.example.blog.ui.create;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.blog.DatabaseHelper;
import com.example.blog.databinding.FragmentCreateBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateFragment extends Fragment {
    private FragmentCreateBinding binding;
    private DatabaseHelper databaseHelper;
    private String selectedImagePath;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        String imagePath = saveImageToInternalStorage(imageUri);
                        if (imagePath != null) {
                            selectedImagePath = imagePath;
                            displayImage(imagePath);
                        }
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentCreateBinding.inflate(inflater, container, false);

        // Initialize
        databaseHelper = new DatabaseHelper(requireContext());
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        setupClickListeners();

        return binding.getRoot();
    }

    private void setupClickListeners() {
        binding.buttonAddImage.setOnClickListener(v -> openImagePicker());
        binding.buttonRemoveImage.setOnClickListener(v -> removeImage());
        binding.buttonPublish.setOnClickListener(v -> publishPost());
    }

    private void displayImage(String imagePath) {
        binding.imagePost.setVisibility(View.VISIBLE);
        Glide.with(requireContext())
                .load(new File(imagePath))
                .centerCrop()
                .into(binding.imagePost);
        binding.buttonRemoveImage.setVisibility(View.VISIBLE);
    }

    private void removeImage() {
        selectedImagePath = null;
        binding.imagePost.setVisibility(View.GONE);
        binding.buttonRemoveImage.setVisibility(View.GONE);
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            ContentResolver resolver = requireContext().getContentResolver();
            InputStream inputStream = resolver.openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            File directory = requireContext().getDir("blog_images", Context.MODE_PRIVATE);
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "IMG_" + timeStamp + ".jpg";
            File file = new File(directory, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();

            return file.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void publishPost() {
        String title = binding.editTitle.getText().toString().trim();
        String content = binding.editContent.getText().toString().trim();

        // Validate input
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get user ID from SharedPreferences
        long userId = sharedPreferences.getLong("user_id", -1);
        if (userId == -1) {
            Toast.makeText(requireContext(), "Error: User not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create post
        databaseHelper.open();
        long postId = databaseHelper.createPost(title, content, userId);

        if (postId != -1) {
            // Add image if selected
            if (selectedImagePath != null) {
                databaseHelper.addPostImage(postId, selectedImagePath);
            }

            Toast.makeText(requireContext(), "Blog post published successfully", Toast.LENGTH_SHORT).show();
            clearForm();
            Navigation.findNavController(requireView()).navigateUp();
        } else {
            Toast.makeText(requireContext(), "Error publishing post", Toast.LENGTH_SHORT).show();
        }
        databaseHelper.close();
    }

    private void clearForm() {
        binding.editTitle.setText("");
        binding.editContent.setText("");
        removeImage();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}