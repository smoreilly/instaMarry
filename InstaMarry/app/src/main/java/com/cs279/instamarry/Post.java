package com.cs279.instamarry;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by Sean on 2/4/2015.
 */

public class Post extends Model{
//    @Column(name = "Image")
//    private byte[] my_image;
    @Column(name = "Title")
    private String my_title;
    @Column(name = "Description")
    private String my_description;
    @Column(name = "PostId")
    private String my_post_id;
    @Column(name = "Time")
    private String my_time;
    @Column(name = "UserId")
    private String my_artist;
    @Column(name = "ImageURL")
    private String my_image_url;

    public Post(){
        super();
    }

    public void setMy_post_id(String my_post_id) {
        this.my_post_id = my_post_id;
    }
    public void setMy_image_url(String my_image_url) {
        this.my_image_url = my_image_url;
    }
    public String getMy_image_url() {
        return my_image_url;
    }

    public Post(String post_id, String title, String description, String time, String artist){
        super();
        my_post_id = post_id;
        my_title = title;
        my_description = description;
        my_time = time;
        my_artist = artist;
        my_image_url = ""; //can't set image_url until file saved on parse

    }

    public static byte[] convertBitmapToByteArrayOS(Bitmap bitmap) {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bs);
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
