package com.example.blog.ui.create;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.blog.DatabaseHelper;

public class CreateViewModel extends AndroidViewModel {
    private final DatabaseHelper databaseHelper;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<String> message;

    public CreateViewModel(Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        isLoading = new MutableLiveData<>(false);
        message = new MutableLiveData<>();
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public long createPost(String title, String content, long userId) {
        isLoading.setValue(true);
        try {
            databaseHelper.open();
            long postId = databaseHelper.createPost(title, content, userId);
            if (postId != -1) {
                message.setValue("Post created successfully");
            } else {
                message.setValue("Failed to create post");
            }
            return postId;
        } finally {
            databaseHelper.close();
            isLoading.setValue(false);
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        databaseHelper.close();
    }
}