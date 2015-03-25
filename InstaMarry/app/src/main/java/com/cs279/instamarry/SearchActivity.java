package com.cs279.instamarry;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphMultiResult;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphObjectList;
import com.facebook.model.GraphUser;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by pauljs on 3/16/2015.
 */
public class SearchActivity extends ActionBarActivity {
    private ListView list;
    private ImageView imageView;
    private EditText search_bar;
    private List<Post> songsList;
    private String searched_text;
    private LazyAdapter adapter;
    private String searched_person_object_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        list = (ListView) findViewById(R.id.search_person_list_view);
        imageView = (ImageView) findViewById(R.id.search_person_image_view);
        songsList = new ArrayList<>();
        searched_text = getIntent().getStringExtra("user_name");
        if(searched_text == null) {
            searched_text = "";
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), DetailedItem.class);
                intent.putExtra("id", songsList.get(position).getMy_post_id());
                startActivityForResult(intent, FragmentPersonalTab.VIEW_POST_REQUEST);
            }
        });

        Log.i("Pulling From Parse", "Pulling");
        //Switch this activity to a fragment to use getActivity(). must pass this currently
        // to getactivity for Lazyadapter
        search(searched_text);
        //FOR USE WHEN WANT BETTER SUGGESTIONS FOR SEARCHES
//        ParseFacebookUtils.initialize(getString(R.string.applicationId));
//        requestMyAppFacebookFriendsWithAppInstalled(ParseFacebookUtils.getSession());
    }

    private void requestMyAppFacebookFriendsWithAppInstalled(Session session) {
        Request.newMyFriendsRequest(session, new Request.GraphUserListCallback() {
            @Override
            public void onCompleted(List<GraphUser> graphUsers, Response response) {
                Log.d("USERS", graphUsers.size() + "");
                List<GraphUser> suggestions = new ArrayList<GraphUser>();
                for(GraphUser user : graphUsers) {
                    if((user.getFirstName() + " " + user.getLastName()).contains(searched_text)) {
//                        suggestions.add(user);
                        // For Now just fill activity instead of doing suggestions first
//                        pullFromParseWithRXJava(user.getId());
                    }
                }

            }
        }).executeAsync();


// Ignore Example Code below
//        Request friendsRequest = createRequest(session);
//        friendsRequest.setCallback(new Request.Callback() {
//            @Override
//            public void onCompleted(Response response) {
//                //SetUpList
//                List<GraphUser> friends = getResults(response);
//                GraphUser user;
//                boolean installed = false;
//                if (friends != null) {
//                    Log.d("FRIENDS NOT NULL", "" + friends.size());
//                    for (int count = 0; count < friends.size(); count++) {
//                        user = friends.get(count);
//                        Log.i("FRIENDS", user.getFirstName() + user.getLastName());
//                        if (user.getProperty("installed") != null) {
//                            installed = (Boolean) user.getProperty("installed");
//                            Log.i("Ideal Installed? YES ", "user: " + user.getInnerJSONObject());
//                        }
//                    }
//                } else {
//                    Log.d("FRIENDS NULL", "WAH");
//                }
//            }
//        });
//        friendsRequest.executeAsync();
    }

    private void search(String user_name){
            Observable.from(getUserPosts(user_name))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ParseObject>() {
                        @Override
                        public void onCompleted() {
                            songsList = new Select()
                                    .from(Post.class)
                                    .where("UserId = ?", searched_person_object_id)
                                    .execute();
                            Log.i("SONG SIZE", "" + songsList.size());
                            adapter = new LazyAdapter(SearchActivity.this, songsList);
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

    private List<ParseObject> getUserPosts(String user_name){
        try {
            String[] names = user_name.split(" ");
            ParseQuery<ParseUser> queryUsers = ParseQuery.getQuery(ParseUser.class);
            queryUsers.whereEqualTo("firstName", names[0]);

            List<ParseUser> parseUsers = queryUsers.find();
            ParseQuery<ParseObject> queryPosts = ParseQuery.getQuery("Post");
            if(parseUsers.size() >= 1) {
                searched_person_object_id = parseUsers.get(0).getObjectId();
                ParseUser.getCurrentUser().addUnique("following", searched_person_object_id);
                 ParseUser.getCurrentUser().saveInBackground();//Should be a button but just doing this for now
                //TODO fix this so is only updates on pull to refresh and when first created. Not on every screen change

                List<Post> pList = new Select().
                        from(Post.class).
                        where("UserId = ?", searched_person_object_id)
                        .execute();
                for(Post p: pList) p.delete();

                queryPosts.whereEqualTo("userId", searched_person_object_id);
                return queryPosts.find();
            } else {
                Toast.makeText(this, "No user match", Toast.LENGTH_LONG).show();
                return new ArrayList<ParseObject>();
            }
        }catch (ParseException err){
            throw new RuntimeException();
        }
    }


// Ignore Example Code below

//    private Request createRequest(Session session) {
////        Request request = Request.newGraphPathRequest(session, "me/friends", null);
////        Request request = Request.newMeRequest()
//        Bundle params = new Bundle();
//        params.putString("fields", "id,name,installed");
////        Request request = new Request(session, "me/friends", params, HttpMethod.GET);
////        Set<String> fields = new HashSet<String>();
////        String[] requiredFields = new String[] { "id", "name", "picture","hometown",
////                "installed" };
////        fields.addAll(Arrays.asList(requiredFields));
//
////        Bundle parameters = request.getParameters();
////        parameters.putString("fields", TextUtils.join(",", fields));
////        request.setParameters(parameters);
//
//        return request;
//    }



//    private class FB_FriendsListStructure
//    {
//        String Name,ID,ImageUrl;
//        boolean selected;
//    }
//
//
//    private List<GraphUser> getResults(Response response) throws NullPointerException
//    {
//        try{
//            GraphMultiResult multiResult = response
//                    .getGraphObjectAs(GraphMultiResult.class);
//            GraphObjectList<GraphObject> data = multiResult.getData();
//            return data.castToListOf(GraphUser.class);
//        }
//        catch(NullPointerException e)
//        {
//            return null;
//            //at times the flow enters this catch block. I could not figure out the reason for this.
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search_activity, menu);
        /** Get the action view of the menu item whose id is search */
        View v = (View) menu.findItem(R.id.searchPerson).getActionView();

        /** Get the edit text from the action view */
        search_bar = (EditText) v.findViewById(R.id.editText_person_search);

        /** Setting an action listener */
        search_bar.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                searched_text = v.getText().toString();
                search(searched_text);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.searchPerson) {
            search_bar.requestFocus();
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onOptionsItemSelected(item);
    }

}
