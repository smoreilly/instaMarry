package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.parse.*;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HomeScreen extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.initialize(this, "RqmsoL9ivWpicFS1H3ClO9VWUiPr1XmwLzJoLGRp", "qWXtduM6NlaffGawHe4CJS9aOWHtfb611KGG0oyi");
        //Added so we can simulate a new user every time
        /*ParseUser.logOut();
        com.facebook.Session fbs = com.facebook.Session.getActiveSession();
        if (fbs == null) {
            fbs = new com.facebook.Session(this);
            com.facebook.Session.setActiveSession(fbs);
        }
        fbs.closeAndClearTokenInformation();*/
        //end addition

        ParseUser user = ParseUser.getCurrentUser();
        Intent intent;
        if(user != null){
            intent = new Intent(this, ProfileActivity.class);
        } else{
            intent = new Intent(this, Login.class);
        }
        startActivity(intent);
        finish();
    }


    public void facebookLogin(final View v) {
        ParseFacebookUtils.logIn(this, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException err) {
                if (user == null) {
                    Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                } else if (user.isNew()) {
                    Log.d("MyApp", "User signed up and logged in through Facebook!");
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    startActivity(intent);
                } else {
                    Log.d("MyApp", "User logged in through Facebook!");
                    Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
