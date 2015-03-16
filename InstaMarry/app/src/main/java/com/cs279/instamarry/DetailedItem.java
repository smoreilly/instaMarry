package com.cs279.instamarry;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailedItem extends ActionBarActivity {
    private String id;
    private Post post;
    @InjectView(R.id.imageView) ImageView imageView;
    @InjectView(R.id.textViewArtist)TextView textViewArtist;
    @InjectView(R.id.textViewDescription)TextView textViewDescription;
    @InjectView(R.id.textViewTime)TextView textViewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
        ButterKnife.inject(this);

        id = getIntent().getStringExtra("id");
        Log.d("SODetail",id);
        post = new Select()
                .from(Post.class)
                .where("PostID = ?", id)
                .executeSingle();
        Picasso.with(getApplicationContext()).load(post.getMy_image_url()).into(imageView);
        textViewArtist.setText(post.getMy_artist());
        textViewDescription.setText(post.getMy_description());
        textViewTime.setText(post.getMy_time());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detailed_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.deletePost) {
            deletePost();


            return true;
        }else if (id == R.id.editPost){
            //TODO
        }

        return super.onOptionsItemSelected(item);
    }

    private void deletePost(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Post");
        query.getInBackground(post.getMy_post_id(), new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    object.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            post.delete();
                            complete();
                            finish();
                        }
                    });
                } else {
                    Log.d("SO", post.getMy_post_id());
                }
            }
        });
    }
    private void complete(){
        Intent intent = new Intent(this, ProfileActivity.class);
        setResult(FragmentPersonalTab.DELETE_POST_REQUEST, intent);
    }


}
