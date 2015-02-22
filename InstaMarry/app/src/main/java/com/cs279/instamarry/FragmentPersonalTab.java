package com.cs279.instamarry;

import android.content.Intent;
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
import retrofit.RestAdapter;
import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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





//TODO rework to use retrolambda and proper RXJava
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
                                    Log.d("SO", "" + Thread.currentThread());
                                    return ((ParseFile) p.get("postImage")).getData();

                                } catch (ParseException e) {
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
                                    if (p != null) {
                                        p.editMy_description(postList.get(i).getString("description"));
                                        p.editMy_Title(postList.get(i).getString("title"));
                                        p.editMy_image(images.get(i));
                                    } else {
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
//This is the way we should be doing this but it ain't working for some reason...
   /* private void betterPull()throws ParseException{
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("https://api.parse.com").build();
        final ParseReq pr = restAdapter.create(ParseReq.class);
        pr.getUserPosts(ParseUser.getCurrentUser().getObjectId())
                .flatMap(new Func1<List<ParseObject>, Observable<ParseObject>>() {
                    @Override
                    public Observable<ParseObject> call(List<ParseObject> parseObjects) {
                        return Observable.from(parseObjects);
                    }
                })
                .flatMap(new Func1<ParseObject, Observable<Pair<ParseObject, byte[]>>>() {
                    @Override
                    public Observable<Pair<ParseObject, byte[]>> call(ParseObject parseObject) {
                        return Observable.just(parseObject).zipWith(
                                getPostImage(parseObject).subscribeOn(Schedulers.io()),
                                new Func2<ParseObject, byte[], Pair<ParseObject, byte[]>>() {
                                    @Override
                                    public Pair<ParseObject, byte[]> call(ParseObject parseObject, byte[] parseFile) {
                                        return Pair.create(parseObject, parseFile);
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Pair<ParseObject, byte[]>>() {
                    @Override
                    public void onCompleted() {
                        songsList = new Select()
                                .from(Post.class)
                                .where("UserId = ?", ParseUser.getCurrentUser().getObjectId())
                                .execute();
                        adapter = new LazyAdapter(getActivity(), songsList);
                        list.setAdapter(adapter);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("SO", "Error in better RXJava implementation");
                    }

                    @Override
                    public void onNext(Pair<ParseObject, byte[]> pair) {
                        Post p = new Select()
                                .from(Post.class)
                                .where("PostId = ?", pair.first.getObjectId())
                                .where("UserId = ?", ParseUser.getCurrentUser().getObjectId())
                                .executeSingle();
                        if (p != null) {
                            p.editMy_description(pair.first.getString("description"));
                            p.editMy_Title(pair.first.getString("title"));
                            p.editMy_image(pair.second);
                        } else {
                            (new Post(pair.first.getObjectId(),
                                    pair.first.getString("title"),
                                    pair.first.getString("description"),
                                    pair.first.getString("time"),
                                    pair.first.getString("userId"),
                                    pair.second)).save();
                        }
                    }
                });


    }

    private Observable<byte[]> getPostImage(ParseObject parseObject){
        try{
            Log.d("SO", Thread.currentThread().toString());
            return Observable.just(((ParseFile) parseObject.get("postImage")).getData());
        }catch(ParseException e){
            Log.d("SO", "getPOstImage exception");
            throw new RuntimeException();
        }
    }

//jenky way of doing this. Keep for educational purposes
    private void pullFromParse(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        //query.whereEqualTo("UserId", ParseUser.getCurrentUser().getObjectId());
        //Log.d("SO",ParseUser.getCurrentUser().getObjectId());
        //We are in callback hell here
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
    }*/

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