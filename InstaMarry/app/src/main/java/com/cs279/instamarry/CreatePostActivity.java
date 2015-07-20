package com.cs279.instamarry;

import android.content.Context;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


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
//    private Bitmap bitmap;
    private Uri selectedImage;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.inject(this);
//        bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                Intent galleryIntent = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent , RESULT_GALLERY );
            }
        });

        buttonPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
                if(selectedImage == null) {
                    Toast.makeText(CreatePostActivity.this, "No Image Selected!", Toast.LENGTH_LONG).show();
                    return;
                }
                new CompressAsyncTask(CreatePostActivity.this, selectedImage).execute();
            }
        });

        descriptionText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                buttonPost.performClick();
                return true;
            }
        });
        editTitle.requestFocus();
        ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void createPost(byte[] data){
        Time now = new Time();
        now.setToNow();
        String title = editTitle.getText().toString();
        String description = descriptionText.getText().toString();
        String time = now.format("%H:%M:%S");
        String user = ParseUser.getCurrentUser().getObjectId();
        Log.d("SO","After Compression");
        final ParseFile file = new ParseFile("image.jpg", data);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != ProfileActivity.RESULT_CANCELED) {
            if (requestCode == RESULT_GALLERY) {
                editTitle.requestFocus();
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                selectedImage = data.getData();
                Picasso.with(getApplicationContext()).load(selectedImage).into(imageView);
            }
        }
    }

    private class CompressAsyncTask extends AsyncTask<Void, Void, byte[]> {
        Context context;
        Uri uri;

        public CompressAsyncTask(Context context, Uri uri) {
            this.uri = uri;
            this.context = context;
        }

        @Override
        protected byte[] doInBackground(Void... params) {
            Bitmap bitmap = decodeSampledBitmapFromResource(uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            return stream.toByteArray();
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            createPost(bytes);
        }

        private Bitmap decodeSampledBitmapFromResource(Uri uri) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            try {
                BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            } catch(FileNotFoundException e) {
                e.printStackTrace();
                //TODO display to user
            }
            Log.i("HEIGHT", "" + options.outHeight);
            Log.i("WEIDTH", "" + options.outWidth);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, 750, 750);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            try {
                return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            } catch(IOException e) {
                Log.d("FAILURE", "FAILS");
                return null;
            }
        }

        private int calculateInSampleSize(
                BitmapFactory.Options options, int reqWidth, int reqHeight) {
            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 8;

            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return inSampleSize;
        }
    }


}
