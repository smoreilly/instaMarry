package com.cs279.instamarry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Sean on 2/4/2015.
 */
public class Post implements Serializable{
    private byte[] my_image;
    private String my_title;
    private String my_description;
    private String my_post_id;
    private String my_time;
    private String my_artist;

    public Post(String post_id, String title, String description, String time, String artist, Bitmap bitmap){
        my_post_id = post_id;
        my_title = title;
        my_description = description;
        my_time = time;
        my_artist = artist;
        my_image = Post.convertBitmapToByteArrayOS(bitmap);
    }

    public Post(String post_id, String title, String description, String time, String artist, byte[] byteArray){
        my_post_id = post_id;
        my_title = title;
        my_description = description;
        my_time = time;
        my_artist = artist;
        my_image = byteArray;
    }

    public static byte[] convertBitmapToByteArrayOS(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
        return bs.toByteArray();
    }

    public static Bitmap convertByteArrayToBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }


    public String getMy_time() {
        return my_time;
    }

    public String getMy_post_id() {
        return my_post_id;
    }

    public String getMy_artist() {
        return my_artist;
    }

    public void editMy_image(Bitmap image){
        my_image = Post.convertBitmapToByteArrayOS(image);
    }

    public Bitmap getMy_image(){
        return Post.convertByteArrayToBitmap(my_image);
    }

    public void editMy_Title(String title){
        my_title = title;
    }

    public String getMy_title(){
        return my_title;
    }

    public void editMy_description(String description){
        my_description = description;
    }

    public String getMy_description(){
        return my_description;
    }

}
