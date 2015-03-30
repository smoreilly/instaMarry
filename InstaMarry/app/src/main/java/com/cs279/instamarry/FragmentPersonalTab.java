package com.cs279.instamarry;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    @InjectView(R.id.personal_list)
    RecyclerView list;

    @InjectView(R.id.personal_refresh)
    SwipeRefreshLayout refresh;
    PostAdapter adapter;
    private List<Post> personalPosts;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_personal_tab_layout, container, false);
        ButterKnife.inject(this, v);

        refresh.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE);
        refresh.setOnRefreshListener(this::update);
        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setItemAnimator(new DefaultItemAnimator());
        update();
        return v;
    }

    public void update(){
        personalPosts = new ArrayList<>();
        List<Post> pList = new Select().
                from(Post.class).
                where("UserId = ?", ParseUser.getCurrentUser().
                        getObjectId())
                .execute();
        for(Post p: pList) p.delete();
        getPosts();
    }

    private void getPosts(){
        Observable.from(getUserPosts())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ParseObject>() {
                    @Override
                    public void onCompleted() {
                        adapter = new PostAdapter(personalPosts, R.layout.post_card, getActivity());
                        list.setAdapter(adapter);
                        refresh.setRefreshing(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("SO", "Error in Personal Tab");
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
                        personalPosts.add(post);
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
}