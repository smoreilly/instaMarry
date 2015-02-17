package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.facebook.*;
import com.facebook.model.GraphUser;
import com.parse.*;
import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class Login extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button b = (Button) findViewById(R.id.authButton);
        b.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v)
            {
                facebookLogin(v);
            }
        });
    }


    public void facebookLogin(final View v){
        final List<String> x = new ArrayList<>();
        List<String> permissions = Arrays.asList("public_profile", "user_about_me", "email");
        ParseFacebookUtils.logIn(permissions,this, new LogInCallback() {
            @Override
            public void done(final ParseUser pUser, ParseException err) {
                com.facebook.Request.newMeRequest(ParseFacebookUtils.getSession(), new com.facebook.Request.GraphUserCallback() {
                    @Override
                    public void onCompleted(GraphUser user, Response response) {
                        pUser.put("email",user.getProperty("email").toString());
                        pUser.put("firstName", user.getFirstName());
                        pUser.put("lastName", user.getLastName());
                        pUser.saveInBackground();
                        if (pUser == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (pUser.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                            startActivity(intent);
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                            Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                            startActivity(intent);
                        }
                        finish();
                    }
                }).executeAsync();
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
        if (id == R.id.action_search){
            return true;
        }
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }


}
