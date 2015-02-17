package com.cs279.instamarry;

import java.util.ArrayList;

/**
 * Created by Sean on 2/4/2015.
 */
public class User {
    private Long my_id;
    private ArrayList<Post> my_posts;
    private ArrayList<Long> my_following;

    public User(Long id, ArrayList<Post> posts, ArrayList<Long> following){
        my_id = -1L;
        my_posts = posts;
        my_following = following;
    }

    //new User
    public User(long id){
        my_id = id;
        my_posts = new ArrayList<Post>();
        my_following = new ArrayList<Long>();
    }

    public Long getMy_id(){
        return my_id;
    }

    public void addPost(Post post){
        my_posts.add(post);

    }

    public void addFollowing(Long id){
        my_following.add(id);
    }

    public ArrayList<Post> getMy_posts(){
        return my_posts;
    }

    public ArrayList<Long> getMy_following(){
        return my_following;
    }

}
