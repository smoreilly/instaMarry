package com.cs279.instamarry;

import com.parse.ParseFile;
import com.parse.ParseObject;

import java.util.List;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Sean on 2/21/2015.
 */
public interface ParseReq {

    @GET("/1/classes/Post")
    Observable<List<ParseObject>> getUserPosts(@Query("UserId") String id);

    @GET("/1/classes/Post/{objectId}")
    Observable<ParseFile> getPostImage(@Path("objectId") String id);
}
