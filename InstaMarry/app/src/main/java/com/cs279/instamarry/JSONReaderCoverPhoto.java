package com.cs279.instamarry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

/**
 * Created by pauljs on 3/30/2015.
 */
public class JSONReaderCoverPhoto {

    public static String read(String url) throws Exception {
        HttpClient hc = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        HttpResponse rp = hc.execute(get);

        if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            String result = EntityUtils.toString(rp.getEntity());

            JSONObject JODetails = new JSONObject(result);

            if (JODetails.has("cover")) {
                String getInitialCover = JODetails.getString("cover");

                if (getInitialCover.equals("null")) {
                    throw new Exception("Cover is null upon getting string");
                } else {
                    JSONObject JOCover = JODetails.optJSONObject("cover");

                    if (JOCover.has("source")) {
                        return JOCover.getString("source");
                    } else {
                        throw new Exception("Cover Source is null");
                    }
                }
            } else {
                throw new Exception("Cover does not exist");

            }
        }
        throw new Exception("ERROR STATUS CODE: " + rp.getStatusLine().getStatusCode());
    }
}
