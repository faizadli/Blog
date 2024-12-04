package com.example.blog.ui.blog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;
import com.example.blog.R;
import com.example.blog.databinding.FragmentBlogBinding;
import com.example.blog.ui.home.BlogAdapter;
import java.util.ArrayList;

public class BlogFragment extends Fragment {
    private FragmentBlogBinding binding;
    private BlogViewModel blogViewModel;
    private BlogAdapter blogAdapter;
    private DatabaseHelper databaseHelper;
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "UserPref";
    private static final String KEY_USER_ID = "user_id";
    private long userId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBlogBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize
        blogViewModel = new ViewModelProvider(this).get(BlogViewModel.class);
        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.open();

        // Get user ID
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong(KEY_USER_ID, -1);

        setupRecyclerView();
        setupSearchListener();

        blogViewModel.getFilteredPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                binding.textNoResults.setVisibility(View.VISIBLE);
                binding.recyclerSearchResults.setVisibility(View.GONE);
            } else {
                binding.textNoResults.setVisibility(View.GONE);
                binding.recyclerSearchResults.setVisibility(View.VISIBLE);
                blogAdapter.updateData(posts);
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        blogViewModel = new ViewModelProvider(this).get(BlogViewModel.class);

        // Get user ID
        sharedPreferences = requireContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        userId = sharedPreferences.getLong(KEY_USER_ID, -1);
        blogViewModel.setUserId(userId);

        setupRecyclerView();
        setupSearchListener();

        blogViewModel.getFilteredPosts().observe(getViewLifecycleOwner(), posts -> {
            if (posts == null || posts.isEmpty()) {
                binding.textNoResults.setVisibility(View.VISIBLE);
                binding.recyclerSearchResults.setVisibility(View.GONE);
            } else {
                binding.textNoResults.setVisibility(View.GONE);
                binding.recyclerSearchResults.setVisibility(View.VISIBLE);
                blogAdapter.updateData(posts);
            }
        });
    }

    private void setupRecyclerView() {
        blogAdapter = new BlogAdapter(new ArrayList<>(), new BlogAdapter.OnBlogClickListener() {
            @Override
            public void onBlogClick(BlogPost blogPost) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("blog_post", blogPost);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_search_to_blogDetailFragment, bundle);
            }

            @Override
            public void onDeleteClick(BlogPost blogPost) {
                if (blogPost.getUserId() == userId) {
                    showDeleteConfirmation(blogPost);
                }
            }
        }, true); // Set showActions to true

        binding.recyclerSearchResults.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerSearchResults.setAdapter(blogAdapter);
    }

    private void showDeleteConfirmation(BlogPost post) {
        new AlertDialog.Builder(requireContext())
                .setMessage("Are you sure you want to delete this post?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (databaseHelper.deletePost(post.getId())) {
                        // Refresh search results
                        String currentSearch = binding.editSearch.getText().toString();
                        blogViewModel.searchPosts(currentSearch);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void setupSearchListener() {
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                blogViewModel.searchPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        String currentSearch = binding.editSearch.getText().toString();
        blogViewModel.searchPosts(currentSearch);
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