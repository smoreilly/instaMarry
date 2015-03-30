package com.cs279.instamarry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private List<Post> data;
    private static LayoutInflater inflater=null;

    public LazyAdapter(Activity a, List<Post> d) {
        activity = a;
        data = d;
        //TODO what does this do Justin? It breaks our code. It crashes sometimes.
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Post getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView title = (TextView)vi.findViewById(R.id.title); // title
        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        Post post = data.get(position);
        title.setText(post.getMy_title());
        artist.setText(post.getMy_description());
        duration.setText(post.getMy_time());

        Log.d("THE URL IS FUDGE: ", post.getMy_image_url());
        Picasso.with(activity.getApplicationContext()).load(post.getMy_image_url()).into(thumb_image);
        return vi;
    }
}