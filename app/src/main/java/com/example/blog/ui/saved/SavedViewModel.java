package com.example.blog.ui.saved;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.blog.BlogPost;
import com.example.blog.DatabaseHelper;
import java.util.List;

public class SavedViewModel extends ViewModel {
    private final MutableLiveData<List<BlogPost>> savedPosts = new MutableLiveData<>();

    public void loadSavedBlogPosts(long userId, DatabaseHelper databaseHelper) {
        List<BlogPost> posts = databaseHelper.getSavedBlogPosts(userId);
        savedPosts.postValue(posts);
    }

    public LiveData<List<BlogPost>> getSavedPosts() {
        return savedPosts;
    }
}