package com.example.blog.ui.blog;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BlogViewModel extends AndroidViewModel {
    private final DatabaseHelper databaseHelper;
    private final MutableLiveData<List<BlogPost>> filteredPosts;
    private List<BlogPost> allPosts;
    private long userId;
    private String lastSearchTerm;

    public BlogViewModel(Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        filteredPosts = new MutableLiveData<>();
        allPosts = new ArrayList<>();
    }

    public void setUserId(long userId) {
        this.userId = userId;
        loadUserPosts();
    }

    public void loadUserPosts() {
        try {
            databaseHelper.open();
            allPosts = databaseHelper.getUserBlogPosts(userId);
            // Refresh dengan search term terakhir
            searchPosts(lastSearchTerm != null ? lastSearchTerm : "");
        } catch (Exception e) {
            e.printStackTrace();
            allPosts = new ArrayList<>();
            filteredPosts.setValue(allPosts);
        } finally {
            databaseHelper.close();
        }
    }

    public void searchPosts(String query) {
        lastSearchTerm = query;
        if (query == null || query.isEmpty()) {
            filteredPosts.setValue(allPosts);
            return;
        }

        List<BlogPost> filtered = allPosts.stream()
                .filter(post -> post.getTitle().toLowerCase()
                        .contains(query.toLowerCase()))
                .collect(Collectors.toList());
        filteredPosts.setValue(filtered);
    }

    public LiveData<List<BlogPost>> getFilteredPosts() {
        return filteredPosts;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        databaseHelper.close();
    }
}