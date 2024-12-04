package com.example.blog.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.blog.BlogPost;

public class BlogDetailViewModel extends ViewModel {
    private final MutableLiveData<BlogPost> blogPost = new MutableLiveData<>();

    public void setBlogPost(BlogPost post) {
        blogPost.setValue(post);
    }

    public LiveData<BlogPost> getBlogPost() {
        return blogPost;
    }
}