package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.FindCallback;
import com.parse.ParseUser;
import com.parse.ParseFile;

import java.io.InvalidObjectException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
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
        //pullFromParse();
        /*TODO fix this so is only updates on pull to refresh and when first created. Not on
        every screen change*/
        pullFromParseWithRXJava();
        return v;
    }

//TODO not really multithreading here only on one background thread
    private void pullFromParseWithRXJava(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        final List<byte[]> images = new ArrayList<>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(final List<ParseObject> postList, ParseException e) {
                Observable.from(postList)
                        .map(new Func1<ParseObject, byte[]>() {
                            @Override
                            public byte[] call(ParseObject p) {
                                try {
                                    Log.d("SO", ""+ Thread.currentThread());
                                    return ((ParseFile) p.get("postImage")).getData();
                                }catch(ParseException e){
                                    throw new RuntimeException();
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<byte[]>() {
                            @Override
                            public void onCompleted() {
                                for (int i = 0; i < postList.size(); ++i) {
                                    Post p = new Select()
                                            .from(Post.class)
                                            .where("PostId = ?", postList.get(i).getObjectId())
                                            .where("UserId = ?", ParseUser.getCurrentUser().getObjectId())
                                            .executeSingle();
                                    if(p != null){
                                        p.editMy_description(postList.get(i).getString("description"));
                                        p.editMy_Title(postList.get(i).getString("title"));
                                        p.editMy_image(images.get(i));
                                    }else {
                                        (new Post(postList.get(i).getObjectId(),
                                                postList.get(i).getString("title"),
                                                postList.get(i).getString("description"),
                                                postList.get(i).getString("time"),
                                                postList.get(i).getString("userId"),
                                                images.get(i))).save();
                                    }
                                }
                                songsList = new Select()
                                        .from(Post.class)
                                        .where("UserId = ?", ParseUser.getCurrentUser().getObjectId())
                                        .execute();
                                adapter = new LazyAdapter(getActivity(), songsList);
                                list.setAdapter(adapter);
                                Log.d("SO", "" + Thread.currentThread());
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(byte[] o) {
                                images.add(o);
                            }
                        });
            }
        });
    }


    private void pullFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        //query.whereEqualTo("UserId", ParseUser.getCurrentUser().getObjectId());
        //Log.d("SO",ParseUser.getCurrentUser().getObjectId());
        //We are in callback hell here// TODO ask KZ how to fix
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> postList, ParseException e) {
                Log.d("SO","" + postList.size());
                final int[] x = new int[1];
                x[0] = postList.size();
                for(final ParseObject p: postList){
                    ParseFile file = (ParseFile)p.get("postImage");
                    file.getDataInBackground(new GetDataCallback() {
                        @Override
                        public void done(byte[] bytes, ParseException e) {
                            Post post = new Post(p.getObjectId(),
                                    p.getString("title"),
                                    p.getString("description"),
                                    p.getString("time"),
                                    p.getString("userId"),
                                    bytes);
                            post.save();
                            --x[0];
                            if(x[0] == 0){
                                songsList = new Select().from(Post.class).execute();
                                adapter = new LazyAdapter(getActivity(),songsList);
                                list.setAdapter(adapter);
                            }
                        }
                    });
                }
            }
        });
    }

    public void addPost() {

        songsList = new Select().from(Post.class).execute();
        adapter = new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }


    public void deletePost() {
        songsList = new Select().from(Post.class).execute();
        adapter = new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }
}