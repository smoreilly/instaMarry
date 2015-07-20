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

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

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
        Post post = posts.get(i);

        ParseQuery<ParseUser> queryUsers = ParseQuery.getQuery(ParseUser.class);
        queryUsers.getInBackground(post.getMy_userId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                Picasso.with(context.getApplicationContext())
                        .load("https://graph.facebook.com/" + parseUser.get("facebook_id") + "/picture?type=large").
                        into(viewHolder.profile_image);
                viewHolder.user_name.setText(parseUser.getString("firstName") + " " + parseUser.getString("lastName"));
            }
        });
        viewHolder.time.setText(post.getMy_time());
        viewHolder.title.setText(post.getMy_title());
        viewHolder.description.setText(post.getMy_description());
        Picasso.with(context.getApplicationContext()).load(post.getMy_image_url()).into(viewHolder.post_image);


        viewHolder.postId = post.getMy_post_id();
        viewHolder.context = context;
    }

    @Override
    public int getItemCount() {
        return posts == null ? 0 : posts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView profile_image;
        TextView user_name;
        TextView time;
        TextView title;
        TextView description;
        ImageView post_image;
        public String postId;
        public Context context;


        public ViewHolder(View itemView){
            super(itemView);
            profile_image = (ImageView) itemView.findViewById(R.id.card_profile_picture);
            user_name = (TextView) itemView.findViewById(R.id.card_user_name);
            time = (TextView) itemView.findViewById(R.id.card_time);
            title = (TextView) itemView.findViewById(R.id.card_title);
            description = (TextView) itemView.findViewById(R.id.card_description);
            post_image = (ImageView) itemView.findViewById(R.id.card_image);

            ButterKnife.inject(itemView);
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
