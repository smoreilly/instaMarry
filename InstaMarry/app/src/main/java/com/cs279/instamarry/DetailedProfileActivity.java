package com.cs279.instamarry;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DetailedProfileActivity extends ActionBarActivity {
    @InjectView(R.id.detail_profile_list)RecyclerView list;
    @InjectView(R.id.detail_profile_name) TextView name;
    @InjectView(R.id.follow_button) Button follow_button;
    @InjectView(R.id.detailed_profile_refresh)
    SwipeRefreshLayout refresh;
    PostAdapter adapter;

    String user_id;
    List <Post> post_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_profile);
        ButterKnife.inject(this);
        user_id = getIntent().getStringExtra("id");
        refresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        refresh.setOnRefreshListener(this::getPosts);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setItemAnimator(new DefaultItemAnimator());

        if(user_id.equals(ParseUser.getCurrentUser().getObjectId())){
            follow_button.setVisibility(View.GONE);
        } else {
            follow_button.setVisibility(View.VISIBLE);
            setFollowing();
        }




        ParseQuery<ParseUser> queryUsers = ParseQuery.getQuery(ParseUser.class);
        queryUsers.getInBackground(user_id, new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                name.setText(parseUser.getString("firstName") + " " + parseUser.getString("lastName"));
            }
        });
        getPosts();

    }

    private List<ParseObject> getUserPosts(String id){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.whereEqualTo("userId", id);
            return query.find();
        }catch (ParseException err){
            throw new RuntimeException();
        }
    }

    private void getPosts(){
                Observable.from(getUserPosts(user_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ParseObject>() {
                    @Override
                    public void onCompleted() {
                        post_list = new Select()
                                .from(Post.class)
                                .where("UserId = ?", user_id)
                                .execute();
                        adapter = new PostAdapter(post_list, R.layout.post_card, DetailedProfileActivity.this);
                        list.setAdapter(adapter);
                        refresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("SO", "Java RX Error: " + e);
                    }

                    @Override
                    public void onNext(ParseObject parseObject) {
                        Post post = new Post(parseObject.getObjectId(),
                                parseObject.getString("title"),
                                parseObject.getString("description"),
                                parseObject.getString("time"),
                                parseObject.getString("userId"),
                                ((ParseFile) parseObject.get("postImage")).getUrl());
                        Post y = new Select()
                                .from(Post.class)
                                .where("PostId = ?", parseObject.getObjectId())
                                .executeSingle();
                        if (y == null) {
                            post.save();
                        }
                    }
                });

    }

    private void setFollowing(){
        if (ParseUser.getCurrentUser().getList("following").contains(user_id)){
            follow_button.setText("UnFollow");
            follow_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.getCurrentUser().removeAll("following", Arrays.asList(user_id));
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            setFollowing();
                        }
                    });
                }
            });
        } else {
            follow_button.setText("Follow");
            follow_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseUser.getCurrentUser().addUnique("following", user_id);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            setFollowing();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
