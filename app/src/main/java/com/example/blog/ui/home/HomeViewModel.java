package com.example.blog.ui.home;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HomeViewModel extends AndroidViewModel {
    private final MutableLiveData<List<BlogPost>> blogPosts = new MutableLiveData<>();
    private List<BlogPost> allPosts = new ArrayList<>();
    private long userId;

    public HomeViewModel(Application application) {
        super(application);
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void loadBlogPosts(DatabaseHelper databaseHelper) {
        allPosts = databaseHelper.getUserBlogPosts(userId); // Menggunakan getUserBlogPosts alih-alih getAllBlogPosts
        blogPosts.setValue(allPosts);
    }

    public LiveData<List<BlogPost>> getBlogPosts() {
        return blogPosts;
    }

    public void searchPosts(String query) {
        if (query.isEmpty()) {
            blogPosts.setValue(allPosts);
            return;
        }

        List<BlogPost> filteredPosts = allPosts.stream()
                .filter(post -> post.getTitle().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
        blogPosts.setValue(filteredPosts);
    }
}