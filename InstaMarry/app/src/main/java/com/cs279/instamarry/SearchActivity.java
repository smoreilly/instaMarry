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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
    private ListView listView;
    private ImageView imageView;
    private EditText search_bar;
    private List<String> names;
    private List<ParseUser> usersList;
    private String searched_text;
    private LazyAdapter adapter;
    private String searched_person_object_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.search_person_list_view);
        names = new ArrayList<String>();
        usersList = new ArrayList<>();
//        if(searched_text == null) {
//            searched_text = "";
//        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(view.getContext(), SearchActivity.class);
                intent.putExtra("id", usersList.get(position).getObjectId());
                startActivity(intent);
            }
        });

        Log.i("Pulling From Parse", "Pulling");
        //Switch this activity to a fragment to use getActivity(). must pass this currently
        // to getactivity for Lazyadapter
//        search(searched_text);
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
    }

    private void search(String user_name){
            Observable.from(getParseUsers(user_name))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<ParseUser>() {
                        @Override
                        public void onCompleted() {
                            ArrayAdapter adapter = new ArrayAdapter(SearchActivity.this,
                                    android.R.layout.simple_list_item_1, names);
                            listView.setAdapter(adapter);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.d("SO", "Java RX Error: " + e);
                        }

                        @Override
                        public void onNext(ParseUser parseUser) {
                            usersList.add(parseUser);
                            names.add(parseUser.get("firstName") + " " + parseUser.get("lastName"));
                        }
                    });


    }

    private List<ParseUser> getParseUsers(String user_name){
        try {
            String[] names = user_name.split(" ");
            ParseQuery<ParseUser> queryUsers = ParseQuery.getQuery(ParseUser.class);
            queryUsers.whereEqualTo("firstName", names[0]);
            if(names.length > 1) {
                queryUsers.whereEqualTo("lastName", names[1]);
            }
            List<ParseUser> parseUsers = queryUsers.find();
            if(parseUsers.size() >= 1) {
                return parseUsers;
            } else {
                Toast.makeText(this, "No user match", Toast.LENGTH_LONG).show();
                return new ArrayList<ParseUser>();
            }
        }catch (ParseException err){
            throw new RuntimeException();
        }
    }

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
        onOptionsItemSelected(menu.findItem(R.id.searchPerson));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.searchPerson) {
            item.expandActionView();
            search_bar.requestFocus();
            ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
        return super.onOptionsItemSelected(item);
    }

}
