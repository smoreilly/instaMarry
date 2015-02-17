package com.cs279.instamarry;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<Post> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;
    
    public LazyAdapter(Activity a, ArrayList<Post> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
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
        
//        HashMap<String, String> song = new HashMap<String, String>();
//        song = data.get(position);
//
//        // Setting all values in listview
//        title.setText(song.get(FragmentExploreTab.KEY_TITLE));
//        artist.setText(song.get(FragmentExploreTab.KEY_ARTIST));
//        duration.setText(song.get(FragmentExploreTab.KEY_DURATION));
//        imageLoader.DisplayImage(song.get(FragmentExploreTab.KEY_THUMB_URL), thumb_image);
        //MY INSERT
        Post post = data.get(position);
        title.setText(post.getMy_title());
        artist.setText(post.getMy_description());
        duration.setText(post.getMy_time());
        Bitmap bitmap = post.getMy_image();
        imageLoader.DisplayImage(bitmap.toString(), thumb_image, bitmap);
        return vi;
    }
}