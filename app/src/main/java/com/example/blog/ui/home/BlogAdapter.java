package com.example.blog.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.blog.BlogPost;
import com.example.blog.R;
import java.io.File;
import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private List<BlogPost> blogPosts;
    private final OnBlogClickListener listener;
    private final boolean showActions;

    public interface OnBlogClickListener {
        void onBlogClick(BlogPost blogPost);
        void onDeleteClick(BlogPost blogPost);
    }

    public BlogAdapter(List<BlogPost> blogPosts, OnBlogClickListener listener, boolean showActions) {
        this.blogPosts = blogPosts;
        this.listener = listener;
        this.showActions = showActions;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_blog_post, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        BlogPost post = blogPosts.get(position);

        holder.textTitle.setText(post.getTitle());
        holder.textContent.setText(post.getContent());
        holder.textAuthor.setText("By " + post.getAuthorName());
        holder.textDate.setText(post.getCreatedAt());

        // Handle image
        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            holder.imagePost.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(new File(post.getImagePath()))
                    .centerCrop()
                    .into(holder.imagePost);
        } else {
            holder.imagePost.setVisibility(View.GONE);
        }

        // Get current user ID from SharedPreferences
        SharedPreferences sharedPreferences = holder.itemView.getContext()
                .getSharedPreferences("UserPref", Context.MODE_PRIVATE);
        long currentUserId = sharedPreferences.getLong("user_id", -1);

        if (showActions && post.getUserId() == currentUserId) {
            holder.layoutActions.setVisibility(View.VISIBLE);

            holder.buttonEdit.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putParcelable("blog_post", post);
                Navigation.findNavController(v)
                        .navigate(R.id.action_navigation_search_to_editFragment, bundle);
            });

            holder.buttonDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(post);
                }
            });
        } else {
            holder.layoutActions.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBlogClick(post);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return blogPosts.size();
    }

    public void updateData(List<BlogPost> newPosts) {
        this.blogPosts = newPosts;
        notifyDataSetChanged();
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textContent, textAuthor, textDate;
        LinearLayout layoutActions;
        Button buttonEdit, buttonDelete;
        ImageView imagePost;

        BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textContent = itemView.findViewById(R.id.textContent);
            textAuthor = itemView.findViewById(R.id.textAuthor);
            textDate = itemView.findViewById(R.id.textDate);
            layoutActions = itemView.findViewById(R.id.layoutActions);
            buttonEdit = itemView.findViewById(R.id.buttonEdit);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            imagePost = itemView.findViewById(R.id.imagePost);
        }
    }
}