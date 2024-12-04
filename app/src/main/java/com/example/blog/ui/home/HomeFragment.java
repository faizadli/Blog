package com.example.blog.ui.home;

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
import com.example.blog.databinding.FragmentHomeBinding;
import com.example.blog.ui.home.BlogAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private BlogAdapter adapter;
    private DatabaseHelper databaseHelper;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.open();

        // Get user ID
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        long userId = sharedPreferences.getLong("user_id", -1);
        viewModel.setUserId(userId);

        setupRecyclerView();
        setupSearch();

        viewModel.getBlogPosts().observe(getViewLifecycleOwner(), this::displayBlogPosts);

        // Load initial data
        viewModel.loadBlogPosts(databaseHelper);
    }

    private void setupRecyclerView() {
        adapter = new BlogAdapter(new ArrayList<>(), new BlogAdapter.OnBlogClickListener() {
            @Override
            public void onBlogClick(BlogPost blogPost) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("blog_post", blogPost);
                Navigation.findNavController(requireView())
                        .navigate(R.id.action_navigation_home_to_blogDetailFragment, bundle);
            }

            @Override
            public void onDeleteClick(BlogPost blogPost) {
                // Not needed in HomeFragment
            }
        }, false);
        binding.recyclerBlogPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerBlogPosts.setAdapter(adapter);
    }

    private void setupSearch() {
        binding.editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                viewModel.searchPosts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void displayBlogPosts(List<BlogPost> posts) {
        if (posts == null || posts.isEmpty()) {
            binding.textNoBlogs.setVisibility(View.VISIBLE);
            binding.recyclerBlogPosts.setVisibility(View.GONE);
        } else {
            binding.textNoBlogs.setVisibility(View.GONE);
            binding.recyclerBlogPosts.setVisibility(View.VISIBLE);
            adapter.updateData(posts);
        }
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