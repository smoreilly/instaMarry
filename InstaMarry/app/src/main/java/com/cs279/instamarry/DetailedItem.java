package com.cs279.instamarry;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;


public class DetailedItem extends ActionBarActivity {
    private int position;
    private Post post;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_item);
        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        TextView textViewArtist = (TextView) findViewById(R.id.textViewArtist);
        TextView textViewDescription = (TextView) findViewById(R.id.textViewDescription);
        TextView textViewTime = (TextView) findViewById(R.id.textViewTime);
        position = getIntent().getIntExtra("position", -1);
        post = (Post) getIntent().getExtras().getSerializable("post");
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
            Log.d("Profile Activity", "Delete button working");
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.putExtra("position", position);
            setResult(FragmentExploreTab.DELETE_POST_REQUEST, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
