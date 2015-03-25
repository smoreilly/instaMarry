package com.cs279.instamarry;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ParseFile;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pauljs on 1/28/2015.
 */
public class FragmentPersonalTab extends Fragment {
    static final int VIEW_POST_REQUEST = 1;
    static final int DELETE_POST_REQUEST = 2;
    @InjectView(R.id.exploreListView)ListView list;
    LazyAdapter adapter;
    private List<Post> songsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_personal_tab_layout, container, false);
        ButterKnife.inject(this, v);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedItem.class);
                intent.putExtra("id", songsList.get(position).getMy_post_id());
                startActivityForResult(intent, VIEW_POST_REQUEST);
            }
        });
        songsList = new ArrayList<>();

        //TODO fix this so is only updates on pull to refresh and when first created. Not on every screen change

        List<Post> pList = new Select().
                from(Post.class).
                where("UserId = ?", ParseUser.getCurrentUser().
                        getObjectId())
                .execute();
        for(Post p: pList) p.delete();
        Log.i("Pulling From Parse", "Pulling");
        getPosts();
        return v;
    }

    private void getPosts(){
        Observable.from(getUserPosts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ParseObject>() {
                    @Override
                    public void onCompleted() {
                        songsList = new Select()
                                .from(Post.class)
                                .where("UserId = ?", ParseUser.getCurrentUser().getObjectId())
                                .execute();
                        Log.i("SONG SIZE", "" + songsList.size());
                        adapter = new LazyAdapter(getActivity(), songsList);
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
                        post.save();
                    }
                });

    }

    private List<ParseObject> getUserPosts(){
        try {
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.whereEqualTo("userId", ParseUser.getCurrentUser().getObjectId());
            return query.find();
        }catch (ParseException err){
            throw new RuntimeException();
        }
    }

    //TODO what does this do?
    public void addPost() {
        Log.i("TEST FOR CURSOR WINDOW", "BLAH");
        songsList = new Select().from(Post.class).execute();
        Log.i("TEST FOR CURSOR WINDOW", "BLAH2");
        adapter = new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }


    public void deletePost() {
        songsList = new Select().from(Post.class).execute();
        adapter = new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }
}