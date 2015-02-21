package com.cs279.instamarry;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by pauljs on 2/15/2015.
 */
public class CreatePostActivity extends ActionBarActivity {
    private static int RESULT_GALLERY = 0;
    @InjectView(R.id.imageViewCreatePostImage) ImageView imageView;
    @InjectView(R.id.editTitle)EditText editTitle;
    @InjectView(R.id.editTextDescription)EditText descriptionText;
    @InjectView(R.id.buttonPost) Button buttonPost;
    private Bitmap bitmap;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.inject(this);

        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost();
            }
        });
    }

    private void createPost(){
        Time now = new Time();
        now.setToNow();
        Log.d("SO", "Before compression");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] data = stream.toByteArray();
        final Post post = new Post("-1",
                editTitle.getText().toString(),
                descriptionText.getText().toString(),
                now.format("%H:%M:%S"),
                ParseUser.getCurrentUser().getObjectId(),
                data);
        Log.d("SO","After Compression");
        final ParseFile file = new ParseFile("image.jpg", data);
        file.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                final ParseObject parsePost = new ParseObject("Post");
                parsePost.put("title", post.getMy_title());
                parsePost.put("description", post.getMy_description());
                parsePost.put("time", post.getMy_time());
                parsePost.put("userId", post.getMy_artist());
                parsePost.put("postImage", file);
                parsePost.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        post.setMy_post_id(parsePost.getObjectId());
                        post.save();
                        Intent intent = new Intent();
                        setResult(ProfileActivity.CREATE_POST_REQUEST, intent);
                        finish();
                    }
                });
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ProfileActivity.RESULT_CANCELED) {
            if (requestCode == RESULT_GALLERY) {
                Uri selectedImage = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                    imageView.setImageBitmap(bitmap);
                }catch(Exception e){
                    Log.d("SO","Could not find file");
                }
            }
        }
    }
}
