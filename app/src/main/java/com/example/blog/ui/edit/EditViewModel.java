package com.example.blog.ui.edit;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.blog.DatabaseHelper;

public class EditViewModel extends AndroidViewModel {
    private final DatabaseHelper databaseHelper;

    public EditViewModel(Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
    }

    public LiveData<Boolean> updateBlogPost(long postId, String title, String content, String imagePath) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();
        try {
            databaseHelper.open();
            boolean success = databaseHelper.updatePost(postId, title, content);
            if (success) {
                success = databaseHelper.updatePostImage(postId, imagePath);
            }
            result.postValue(success);
        } finally {
            databaseHelper.close();
        }
        return result;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        databaseHelper.close();
    }
}