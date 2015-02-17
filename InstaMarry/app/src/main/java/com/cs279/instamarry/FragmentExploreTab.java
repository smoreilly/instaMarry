package com.cs279.instamarry;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by pauljs on 1/28/2015.
 */
public class FragmentExploreTab extends Fragment {

    // All static variables
    static final String URL = "http://api.androidhive.info/music/music.xml";
    // XML node keys
    static final String KEY_SONG = "song"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_TITLE = "title";
    static final String KEY_ARTIST = "artist";
    static final String KEY_DURATION = "duration";
    static final String KEY_THUMB_URL = "thumb_url";
    static final int VIEW_POST_REQUEST = 1;
    static final int DELETE_POST_REQUEST = 2;

    ListView list;
    LazyAdapter adapter;
    XMLParser parser;
    private ArrayList<Post> songsList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_explore_tab_layout, container, false);
        list=(ListView) v.findViewById(R.id.exploreListView);
        songsList = new ArrayList<Post>();
        parser = new XMLParser(URL);
        parser.execute(); // getting XML from URL


        return v;
    }

    public void addPost(Post post) {
//        HashMap<String, String> map = new HashMap<String, String>();
//        map.put(KEY_ID, "Test POST");
//        map.put(KEY_TITLE, "Oh yea");
//        map.put(KEY_ARTIST, "Ideal");
//        map.put(KEY_DURATION, "");
//        Log.i("SELECTED IMAGE", bitmap.toString());
//        map.put(KEY_THUMB_URL, bitmap.toString());
//        Post post = new Post("Post ID", "TITLE", "DESCRIPTION", "TIME", "ARTIST", bitmap);
        songsList.add(post);
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }

    public void fillListview() {
//        String xml = parser.getXML();
//        Document doc = parser.getDomElement(xml); // getting DOM element
////
//        NodeList nl = doc.getElementsByTagName(KEY_SONG);
        // looping through all song nodes <song>
//        for (int i = 0; i < nl.getLength(); i++) {
            // creating new HashMap
//            HashMap<String, String> map = new HashMap<String, String>();
//            Element e = (Element) nl.item(i);
            // adding each child node to HashMap key => value
//            map.put(KEY_ID, parser.getValue(e, KEY_ID));
//            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
//            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
//            map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
//            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));
//            Log.i("TESTING IMAGE", parser.getValue(e, KEY_THUMB_URL));
//        }
//        HashMap<String, String> map = new HashMap<String, String>();
//        Element e = (Element) nl.item(0);
//        // adding each child node to HashMap key => value
//            map.put(KEY_ID, parser.getValue(e, KEY_ID));
//            map.put(KEY_TITLE, parser.getValue(e, KEY_TITLE));
//            map.put(KEY_ARTIST, parser.getValue(e, KEY_ARTIST));
//            map.put(KEY_DURATION, parser.getValue(e, KEY_DURATION));
//            map.put(KEY_THUMB_URL, parser.getValue(e, KEY_THUMB_URL));
        //MY INSERTION
//            map.put(KEY_ID, "Polo Shirt");
//            map.put(KEY_TITLE, "Polo Shirt");
//            map.put(KEY_ARTIST, "This would look great with khakis");
//            map.put(KEY_DURATION, "");
//            map.put(KEY_THUMB_URL, "");

            // adding HashList to ArrayList
//            songsList.add(map);
        //}




        // Getting adapter by passing xml data ArrayList
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);


        // Click event for single list row
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedItem.class);
                intent.putExtra("post", adapter.getItem(position));
                intent.putExtra("position", position);
                startActivityForResult(intent, VIEW_POST_REQUEST);
            }
        });
    }

    public void deletePost(int position) {
        songsList.remove(position);
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }

    public class XMLParser extends AsyncTask<Void, Void, Void> {

        String url;
        String xml;
        // constructor
        public XMLParser(String url) {
            this.url = url;
            this.xml = "hello";
        }

        public String getXML() {
            return xml;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                xml = EntityUtils.toString(httpEntity);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // return XML
            return null;
        }

        /**
         * Getting XML from URL making HTTP request
         * @param url string
         * */
        public String getXmlFromUrl(String url) {
            String xml = null;

            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(url);

                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                xml = EntityUtils.toString(httpEntity);

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // return XML
            return xml;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            fillListview();
        }

        /**
         * Getting XML DOM element
         * @param //XML string
         * */
        public Document getDomElement(String xml){
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {

                DocumentBuilder db = dbf.newDocumentBuilder();

                InputSource is = new InputSource();
                is.setCharacterStream(new StringReader(xml));
                doc = db.parse(is);

            } catch (ParserConfigurationException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (SAXException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                return null;
            }

            return doc;
        }

        /** Getting node value
         * @param elem element
         */
        public final String getElementValue( Node elem ) {
            Node child;
            if( elem != null){
                if (elem.hasChildNodes()){
                    for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                        if( child.getNodeType() == Node.TEXT_NODE  ){
                            return child.getNodeValue();
                        }
                    }
                }
            }
            return "";
        }

        /**
         * Getting node value
         * @param //Element node
         * @param //key string
         * */
        public String getValue(Element item, String str) {
            NodeList n = item.getElementsByTagName(str);
            return this.getElementValue(n.item(0));
        }
    }
}


//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View v = inflater.inflate(R.layout.fragment_explore_tab_layout, container, false);
//        ListView listview = (ListView) v.findViewById(R.id.exploreListView);
//        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
//                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
//                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
//                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
//                "Android", "iPhone", "WindowsMobile" };
//
//        ArrayList<String> list = new ArrayList<String>();
//        for (int i = 0; i < values.length; ++i) {
//            list.add(values[i]);
//        }
//        ArrayAdapter adapter = new StableArrayAdapter(v.getContext(),
//                android.R.layout.simple_list_item_1, list);
//        if(listview == null) {
//            System.out.println("WHY");
//        }
//        listview.setAdapter(adapter);
//
//        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(AdapterView<?> parent, final View view,
//                                    int position, long id) {
//                /*final String item = (String) parent.getItemAtPosition(position);
//                view.animate().setDuration(2000).alpha(0)
//                        .withEndAction(new Runnable() {
//                            @Override
//                            public void run() {
//                                list.remove(item);
//                                adapter.notifyDataSetChanged();
//                                view.setAlpha(1);
//                            }
//                        });*/
//            }
//
//        });
//        return v;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    private class StableArrayAdapter extends ArrayAdapter<String> {
//
//        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();
//
//        public StableArrayAdapter(Context context, int textViewResourceId,
//                                  List<String> objects) {
//            super(context, textViewResourceId, objects);
//            for (int i = 0; i < objects.size(); ++i) {
//                mIdMap.put(objects.get(i), i);
//            }
//        }
//
//        @Override
//        public long getItemId(int position) {
//            String item = getItem(position);
//            return mIdMap.get(item);
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return true;
//        }
//
//    }