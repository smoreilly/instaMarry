package com.cs279.instamarry;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

//http://developer.android.com/training/implementing-navigation/lateral.html#horizontal-paging

//TODO this is depreciated we need to change this
public class ProfileActivity extends ActionBarActivity implements ActionBar.TabListener {

    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private android.support.v7.app.ActionBar actionBar;
    private String[] tabs = { "Explore", "Personal"};
    private EditText search_bar;
    static final int CREATE_POST_REQUEST = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        //Set logo
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
        // Initialization
        getSupportActionBar().setLogo(R.drawable.ideal_logo);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ideal_logo);
        getSupportActionBar().setHomeButtonEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.pager);
            actionBar = getSupportActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

        /**
         * on swiping the viewpager make respective tab selected
         * */
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                // on changing the page
                // make respected tab selected
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add){
            Log.d("Profile Activity", "Add button working");

            Intent createPostIntent = new Intent(this, CreatePostActivity.class);
            startActivityForResult(createPostIntent, CREATE_POST_REQUEST);
            return true;
        }else if (id == R.id.action_settings) {

            return true;
        } else if (id == R.id.searchPerson) {
            Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(intent);
        } else if (id ==R.id.action_profile) {
            Intent intent = new Intent(this, DetailedProfileActivity.class);
            intent.putExtra("id", ParseUser.getCurrentUser().getObjectId());
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == FragmentPersonalTab.DELETE_POST_REQUEST) {
            mAdapter.getFragPersonal().update();
            mAdapter.getFragExplore().update();
        } else if(resultCode == CREATE_POST_REQUEST) {
            mAdapter.getFragPersonal().update();
            mAdapter.getFragExplore().update();
        }
    }
}