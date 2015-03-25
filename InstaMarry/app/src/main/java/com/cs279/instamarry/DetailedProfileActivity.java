package com.cs279.instamarry;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class DetailedProfileActivity extends ActionBarActivity {
    @InjectView(R.id.detail_profile_list) ListView list;
    @InjectView(R.id.detail_profile_name) TextView name;
    @InjectView(R.id.follow_button) Button follow_button;
    LazyAdapter adapter;

    String user_id;
    List <Post> post_list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_profile);
        ButterKnife.inject(this);
        user_id = getIntent().getStringExtra("id");

        if(user_id.equals(ParseUser.getCurrentUser().getObjectId())){
            Log.d("SO", "ID's are the same and it didn't work");
            follow_button.setVisibility(View.GONE);
        } else {
            follow_button.setVisibility(View.VISIBLE);
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), DetailedItem.class);
                intent.putExtra("id", post_list.get(position).getMy_post_id());
                startActivity(intent);
            }
        });

        follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.getCurrentUser().addUnique("following", user_id);
                ParseUser.getCurrentUser().saveInBackground();
            }
        });

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
                        adapter = new LazyAdapter(DetailedProfileActivity.this, post_list);
                        list.setAdapter(adapter);
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
