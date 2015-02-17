package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import com.parse.*;
import android.util.Log;



public class HomeScreen extends ActionBarActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Parse.initialize(this, "RqmsoL9ivWpicFS1H3ClO9VWUiPr1XmwLzJoLGRp", "qWXtduM6NlaffGawHe4CJS9aOWHtfb611KGG0oyi");

        //Added so we can simulate a new user every time
        ParseUser.logOut();
        com.facebook.Session fbs = com.facebook.Session.getActiveSession();
        if (fbs == null) {
            fbs = new com.facebook.Session(this);
            com.facebook.Session.setActiveSession(fbs);
        }
        fbs.closeAndClearTokenInformation();
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

}
