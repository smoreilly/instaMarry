package com.cs279.instamarry;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseUser;

/**
 * Created by pauljs on 2/15/2015.
 */
public class CreatePostActivity extends ActionBarActivity {
    private static int RESULT_GALLERY = 0;
    private ImageView imageView;
    private Bitmap bitmap;
    private EditText titleText;
    private EditText descriptionText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        imageView = (ImageView) findViewById(R.id.imageViewCreatePostImage);
        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        Button button = (Button) findViewById(R.id.buttonPost);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Time now = new Time();
                now.setToNow();
                Post post = new Post("id",
                    ((EditText) findViewById(R.id.editTitle)).getText().toString(),
                        ((EditText) findViewById(R.id.editTextDescription)).getText().toString(),
                        now.format("%k:%M:%S"),
                        ParseUser.getCurrentUser().get("firstName") + " " + ParseUser.getCurrentUser().get("lastName"),
                        bitmap);
                Log.d("SO", post.toString());
                Intent intent = new Intent();
                intent.putExtra("post", post);
                setResult(ProfileActivity.CREATE_POST_REQUEST, intent);
                finish();
            }
        });



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ProfileActivity.RESULT_CANCELED) {
            if (requestCode == RESULT_GALLERY) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor.moveToFirst()) {
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    bitmap = BitmapFactory.decodeFile(filePath);
                    Log.d("ProfileActivity", "image callback");
                    imageView.setImageBitmap(bitmap);
                }
            }
        }
        Log.i("BLAH", "" + requestCode + " " + resultCode);
    }
}
