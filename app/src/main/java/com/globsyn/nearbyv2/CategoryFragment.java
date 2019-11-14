package com.globsyn.nearbyv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment implements ListView.OnItemClickListener {

    private ListView lv_tb2_category;
    private String datasource[]={"airport", "hospital", "atm", "restaurant", "bar", "movie_theater", "pharmacy", "train_station"};

    public CategoryFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            View rootView=inflater.inflate(R.layout.fragment_category, container, false);

            lv_tb2_category= (ListView) rootView.findViewById(R.id.lv_tb2_category);
            lv_tb2_category.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, datasource));
            lv_tb2_category.setOnItemClickListener(this);

            return rootView;
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*Toast.makeText(getActivity(), "Tapped on: " + lv_tb2_category.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();*/
        String type=lv_tb2_category.getItemAtPosition(position).toString().toLowerCase();
        Intent intent= new Intent(getActivity(), ResultActivity.class);
        intent.putExtra("type", type);
        Log.d("CategoryFragment", "type:" + type);
        startActivity(intent);
        getActivity().finish();
    }

}
