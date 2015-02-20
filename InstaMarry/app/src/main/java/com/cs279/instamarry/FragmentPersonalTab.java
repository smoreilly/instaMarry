package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.activeandroid.query.Select;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by pauljs on 1/28/2015.
 */
public class FragmentPersonalTab extends Fragment {
    static final int VIEW_POST_REQUEST = 1;
    static final int DELETE_POST_REQUEST = 2;
    @InjectView(R.id.exploreListView)ListView list;
    LazyAdapter adapter;
    private List<Post> songsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_tab_layout, container, false);
        ButterKnife.inject(this, v);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), DetailedItem.class);
                intent.putExtra("id", songsList.get(position).getMy_post_id());
                startActivityForResult(intent, VIEW_POST_REQUEST);
            }
        });

        songsList = new Select().from(Post.class).execute();
        adapter = new LazyAdapter(getActivity(),songsList);
        list.setAdapter(adapter);

        return v;
    }

    public void addPost() {

        songsList = new Select().from(Post.class).execute();
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }


    public void deletePost() {
        songsList = new Select().from(Post.class).execute();
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }
}