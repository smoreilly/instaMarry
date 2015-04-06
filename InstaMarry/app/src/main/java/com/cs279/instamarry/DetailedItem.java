package com.cs279.instamarry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailedItem extends ActionBarActivity {
    private String id;
    private Post post;
    @InjectView(R.id.imageView) ImageView imageView;
    @InjectView(R.id.detailed_item_profile_imageView) ImageView profileImageView;
    @InjectView(R.id.textViewArtist)TextView textViewArtist;
    @InjectView(R.id.textViewDescription)TextView textViewDescription;
    @InjectView(R.id.textViewTime)TextView textViewTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
        ButterKnife.inject(this);

        id = getIntent().getStringExtra("id");
        post = new Select()
                .from(Post.class)
                .where("PostID = ?", id)
                .executeSingle();
        Picasso.with(getApplicationContext()).load(post.getMy_image_url()).into(imageView);
        textViewDescription.setText(post.getMy_description());
        textViewTime.setText(post.getMy_time());
        setTitle(post.getMy_title());
        ParseQuery<ParseUser> queryUsers = ParseQuery.getQuery(ParseUser.class);
        queryUsers.getInBackground(post.getMy_userId(), new GetCallback<ParseUser>() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                Picasso.with(getApplicationContext()).load("https://graph.facebook.com/" + parseUser.get("facebook_id") + "/picture?type=large").into(profileImageView);
                textViewArtist.setText(parseUser.getString("firstName") + " " + parseUser.getString("lastName"));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(post.getMy_userId().equals(ParseUser.getCurrentUser().getObjectId())) {
            getMenuInflater().inflate(R.menu.menu_detailed_item, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_detailed_item_other_user, menu);
        }
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
        } else if(id == R.id.add) {
            new CompressAsyncTask(imageView).execute();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createPost(byte[] bytes) {
        Time now = new Time();
        now.setToNow();
        String title = post.getMy_title();
        String description = post.getMy_description();
        String time = now.format("%H:%M:%S");
        String user = ParseUser.getCurrentUser().getObjectId();
        final ParseFile file = new ParseFile("image.jpg", bytes);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                final ParseObject parsePost = new ParseObject("Post");
                parsePost.put("title", title);
                parsePost.put("description", description);
                parsePost.put("time", time);
                parsePost.put("userId", user);
                parsePost.put("postImage", file);
                parsePost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        final Post post = new Post(parsePost.getObjectId(),
                                title,
                                description,
                                time,
                                user,
                                ((ParseFile) parsePost.get("postImage")).getUrl() //can't set image_url until file saved on parse
                        );
                        post.save();
                        Intent intent = new Intent();
                        setResult(ProfileActivity.CREATE_POST_REQUEST, intent);
                        finish();
                    }
                });
            }
        });
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

    private class CompressAsyncTask extends AsyncTask<Void, Void, byte[]> {
        ImageView iv;

        public CompressAsyncTask(ImageView iv) {
            this.iv = iv;
        }

        @Override
        protected byte[] doInBackground(Void... params) {
//            iv.setDrawingCacheEnabled(true);
//
//            iv.buildDrawingCache();
            BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
            Bitmap bm = drawable.getBitmap();
//            Bitmap bm = iv.getDrawingCache();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            createPost(bytes);
        }
    }
}
