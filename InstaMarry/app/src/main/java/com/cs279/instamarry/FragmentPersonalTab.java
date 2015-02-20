package com.cs279.instamarry;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import java.util.ArrayList;
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
    private ArrayList<Post> songsList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_personal_tab_layout, container, false);
        ButterKnife.inject(this, v);
        songsList = new ArrayList<Post>();
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
        return v;
    }

    public void addPost(Post post) {

        songsList.add(post);
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }


    public void deletePost(int position) {
        songsList.remove(position);
        adapter=new LazyAdapter(getActivity(), songsList);
        list.setAdapter(adapter);
    }
}