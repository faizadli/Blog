package com.example.blog.ui.common;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.blog.R;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BlogImageAdapter extends RecyclerView.Adapter<BlogImageAdapter.ImageViewHolder> {
    private final List<String> images;

    public BlogImageAdapter(List<String> images) {
        this.images = images != null ? images : new ArrayList<>();
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        String imagePath = images.get(position);
        if (imagePath != null && !imagePath.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(new File(imagePath))
                    .centerCrop()
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}