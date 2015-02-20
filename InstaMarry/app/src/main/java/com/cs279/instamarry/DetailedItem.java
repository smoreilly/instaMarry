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

import butterknife.ButterKnife;
import butterknife.InjectView;


public class DetailedItem extends ActionBarActivity {
    private int id;
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

        id = getIntent().getIntExtra("id", -1);
        post = new Select()
                .from(Post.class)
                .where("PostID = ?", id)
                .executeSingle();
        imageView.setImageBitmap(post.getMy_image());
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
            post.delete();
            Intent intent = new Intent(this, ProfileActivity.class);
            setResult(FragmentPersonalTab.DELETE_POST_REQUEST, intent);

            finish();
            return true;
        }else if (id == R.id.editPost){
            //TODO
        }

        return super.onOptionsItemSelected(item);
    }


}
