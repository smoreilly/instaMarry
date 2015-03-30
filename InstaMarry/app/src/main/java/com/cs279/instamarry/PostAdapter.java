package com.cs279.instamarry;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Sean on 3/28/2015.
 */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>  {
    private List<Post> posts;
    private int rowLayout;
    private Context context;

    public PostAdapter(List<Post> posts, int rowLayout, Context context){
        this.posts = posts;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(rowLayout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.postId = posts.get(i).getMy_post_id();
        viewHolder.context = context;
        Post post = posts.get(i);
        viewHolder.postTitle.setText(post.getMy_title());
        Picasso.with(context.getApplicationContext()).load(post.getMy_image_url()).into(viewHolder.postImage);
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView postTitle;
        public ImageView postImage;
        public String postId;
        public Context context;


        public ViewHolder(View itemView){
            super(itemView);
            postTitle = (TextView) itemView.findViewById(R.id.post_title);
            postImage = (ImageView) itemView.findViewById(R.id.post_image);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailedItem.class);
                    intent.putExtra("id", postId);
                    context.startActivity(intent);
                }
            });
        }
    }
}
