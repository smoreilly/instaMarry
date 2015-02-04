package com.cs279.instamarry;

import android.graphics.Bitmap;

/**
 * Created by Sean on 2/4/2015.
 */
public class Post {
    private Bitmap my_image;
    private String my_title;
    private String my_description;

    public Post(Bitmap image, String title, String description){
        my_image = image;
        my_title = title;
        my_description = description;
    }

    public void editMy_image(Bitmap image){
        my_image = image;
    }

    public Bitmap getMy_image(){
        return my_image;
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
