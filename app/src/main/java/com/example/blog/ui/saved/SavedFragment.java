package com.example.blog.ui.saved;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;
import com.example.blog.R;
import com.example.blog.databinding.FragmentSavedBinding;
import com.example.blog.ui.home.BlogAdapter;

import java.util.ArrayList;
import java.util.List;

public class SavedFragment extends Fragment {
    private FragmentSavedBinding binding;
    private SavedViewModel viewModel;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_USER_ID = "user_id";
    private BlogAdapter adapter;
    private long userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSavedBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SavedViewModel.class);

        // Initialize database and preferences
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.open();
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong(KEY_USER_ID, -1);

        setupRecyclerView();
        loadSavedPosts();

        // Observe changes to saved posts
        viewModel.getSavedPosts().observe(getViewLifecycleOwner(), this::displaySavedPosts);
    }

    private void setupRecyclerView() {
        adapter = new BlogAdapter(new ArrayList<>(), new BlogAdapter.OnBlogClickListener() {
            @Override
            public void onBlogClick(BlogPost blogPost) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("blog_post", blogPost);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_saved_to_blogDetailFragment, bundle);
            }

            @Override
            public void onDeleteClick(BlogPost blogPost) {
                // Not needed in SavedFragment
            }
        }, false);

        binding.recyclerViewSaved.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerViewSaved.setAdapter(adapter);
    }

    private void loadSavedPosts() {
        if (userId != -1) {
            viewModel.loadSavedBlogPosts(userId, databaseHelper);
        }
    }

    private void displaySavedPosts(List<BlogPost> savedPosts) {
        if (savedPosts == null || savedPosts.isEmpty()) {
            binding.textNoSavedPosts.setVisibility(View.VISIBLE);
            binding.recyclerViewSaved.setVisibility(View.GONE);
        } else {
            binding.textNoSavedPosts.setVisibility(View.GONE);
            binding.recyclerViewSaved.setVisibility(View.VISIBLE);
            adapter.updateData(savedPosts);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadSavedPosts();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
        binding = null;
    }
}