package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Date;
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
public class FragmentExploreTab extends Fragment {
    @InjectView(R.id.explore_list)
    RecyclerView list;
    PostAdapter adapter;
    private List<Post> songsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_explore_tab_layout, container, false);
        ButterKnife.inject(this, v);

        list.setLayoutManager(new LinearLayoutManager(getActivity()));
        list.setItemAnimator(new DefaultItemAnimator());

        /*
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedItem.class);
                intent.putExtra("id", songsList.get(position).getMy_post_id());
                startActivity(intent);
            }
        });*/
        update();
        return v;
    }

    public void update(){
        songsList = new ArrayList<Post>();

        //TODO fix this so is only updates on pull to refresh and when first created. Not on every screen change

        List<Post> pList = new Select().
                from(Post.class).
                where("UserId != ?", ParseUser.getCurrentUser().
                        getObjectId())
                .execute();
        for(Post p: pList) p.delete();
        getUserFollowingPosts();
    }

    private void getUserFollowingPosts(){

        Observable.from(ParseUser.getCurrentUser().getList("following"))
                .flatMap(userId ->
                    Observable.from(getUserPosts(userId.toString())).subscribeOn(Schedulers.io()))
                .toSortedList((p1,p2) -> p2.getUpdatedAt().compareTo(p1.getUpdatedAt()))
                .flatMap(Observable::from)
                .take(10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ParseObject>() {
                    @Override
                    public void onCompleted() {
                        adapter = new PostAdapter(songsList, R.layout.post_card, getActivity());
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("SO", "Error in explore tab");
                    }

                    @Override
                    public void onNext(ParseObject p) {
                        Post post = new Post(p.getObjectId(),
                                p.getString("title"),
                                p.getString("description"),
                                p.getString("time"),
                                p.getString("userId"),
                                ((ParseFile) p.get("postImage")).getUrl());
                        post.save();
                        songsList.add(post);
                    }
                });
    }


    private List<ParseObject> getUserPosts(String id){
        try {
            Log.i("SO: id of following", id);
            ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
            query.whereEqualTo("userId", id);
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
        adapter = new PostAdapter(songsList, R.layout.post_card, getActivity());
        list.setAdapter(adapter);
    }

}