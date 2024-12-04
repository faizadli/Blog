package com.example.blog.ui.edit;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.bumptech.glide.Glide;
import com.example.blog.BlogPost;
import com.example.blog.databinding.FragmentEditBinding;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.Context;

public class EditFragment extends Fragment {

    private FragmentEditBinding binding;
    private EditViewModel editViewModel;
    private BlogPost blogPost;
    private String selectedImagePath;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEditBinding.inflate(inflater, container, false);
        editViewModel = new ViewModelProvider(this).get(EditViewModel.class);

        if (getArguments() != null) {
            blogPost = getArguments().getParcelable("blog_post");
            if (blogPost != null) {
                binding.editTitle.setText(blogPost.getTitle());
                binding.editContent.setText(blogPost.getContent());
                selectedImagePath = blogPost.getImagePath();
                if (selectedImagePath != null) {
                    displayImage(selectedImagePath);
                }
            }
        }

        setupClickListeners();

        return binding.getRoot();
    }

    private void displayImage(String imagePath) {
        binding.imagePost.setVisibility(View.VISIBLE);
        Glide.with(requireContext())
                .load(new File(imagePath))
                .centerCrop()
                .into(binding.imagePost);
        binding.buttonRemoveImage.setVisibility(View.VISIBLE);
    }

    private void setupClickListeners() {
        binding.buttonAddImage.setOnClickListener(v -> openImagePicker());
        binding.buttonRemoveImage.setOnClickListener(v -> removeImage());
        binding.buttonSave.setOnClickListener(v -> saveChanges());
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

    private void saveChanges() {
        String title = binding.editTitle.getText().toString().trim();
        String content = binding.editContent.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        editViewModel.updateBlogPost(blogPost.getId(), title, content, selectedImagePath)
                .observe(getViewLifecycleOwner(), success -> {
                    if (success) {
                        Toast.makeText(requireContext(), "Post updated successfully", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(requireView()).navigateUp();
                    } else {
                        Toast.makeText(requireContext(), "Failed to update post", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}