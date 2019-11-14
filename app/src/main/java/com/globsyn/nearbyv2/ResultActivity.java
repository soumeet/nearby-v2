package com.globsyn.nearbyv2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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


public class ResultActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{

    private ListView lv_result;
    private List<Venue> data;
    private String REQUEST_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private String LOCATION = "location=22.5667,88.3667";
    private String RADIUS = "&radius=1000";
    private static String TYPE = "&type=atm";
    private String BROWSER_KEY = "&key=AIzaSyDZ8LlDjowb9TsVFESb_z8mcgLRAvR-z3c";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        lv_result = (ListView) findViewById(R.id.lv_result);
        if(getIntent().getStringExtra("type")!=null)
            TYPE = "&type=" + getIntent().getStringExtra("type");
        LOCATION="location=22.5667,88.3667";
        REQUEST_URL = REQUEST_URL + LOCATION + RADIUS + TYPE + BROWSER_KEY;

        VenueSingleton data_read = new VenueSingleton();
        /*if (!data_read.isEmpty()){
            data = data_read.create().getDatasource();
            lv_result.setAdapter(new ResultAdapter(ResultActivity.this, data));
        }else {*/
            Log.d("ResultActivity", "url:" + REQUEST_URL);
            new downloadPlaces(findViewById(R.id.lv_result).getRootView(), ResultActivity.this).execute(REQUEST_URL);
//        }
        lv_result.setOnItemClickListener(this);

    }

    public class downloadPlaces extends AsyncTask<String, Void, List<Venue>> {
        private Context context;
        private View view;

        private ResultAdapter adapter;

        downloadPlaces(View view, Context context){
            this.context=context;
            this.view=view;

        }
        protected List<Venue> doInBackground(String... params) {
            List<Venue> placeList= new ArrayList<>();
            String data;
            try {
				URL url = new URL(params[0]);
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("GET");
				connection.setReadTimeout(10000);
				connection.setConnectTimeout(15000);
				connection.setDoInput(true);
				connection.connect();
				if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
					data = convertInputStreamtoString(connection
							.getInputStream());
                    Log.d("ResultActivity", ""+data);
                    placeList = parseJSON(data);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return placeList;
        }
        protected void onPostExecute(List<Venue> result) {
            super.onPostExecute(null);
//            lv_result.setAdapter(new ArrayAdapter<>(ResultActivity.this, android.R.layout.simple_list_item_1, strings));
            adapter= new ResultAdapter(ResultActivity.this, result);
            lv_result.setAdapter(adapter);
            VenueSingleton data_store= new VenueSingleton();
            data_store.create().setDatasource(result);

        }
        private List<Venue> parseJSON(String response)
                throws JSONException {
//            List<String> tmp= new ArrayList<>();
            List<Venue> tmp= new ArrayList<Venue>();
            JSONObject rootObject= new JSONObject(response);
            JSONArray resultArray= rootObject.getJSONArray("results");
            for (int i=0; i<resultArray.length(); i++){
                JSONObject place=resultArray.getJSONObject(i);
                String place_name=place.getString("name");
                String place_id=place.getString("place_id");
                String place_vicinity=place.getString("vicinity");
                double place_lat=Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lat"));
                double place_lng=Double.parseDouble(place.getJSONObject("geometry").getJSONObject("location").getString("lng"));
                tmp.add(new Venue(place_id, place_name, place_vicinity, place_lat, place_lng));
            }
            return tmp;
        }
        private String convertInputStreamtoString(InputStream input)
                throws IOException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    input));
            StringBuilder builder = new StringBuilder();
            String line = new String();
            while ((line = reader.readLine()) != null) {
                builder = builder.append(line);
            }
            return builder.toString();
        }
    }

    public void onBackPressed() {
        startActivity(new Intent(ResultActivity.this, MainActivity.class));
//        VenueSingleton data_clear= new VenueSingleton();
//        data_clear.create();
        finish();
    }

    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {
        Log.d("ResultActivity", "Starting LastActivity");
        Intent intent = new Intent(this, LastActivity.class);
        intent.putExtra("position", position);
        startActivity(intent);
//        startActivityForResult(intent, 1010);
        finish();
    }
/*
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        List<Venue> dat;
        if (requestCode==1010){
            VenueSingleton data_read = new VenueSingleton();
            dat = data_read.create().getDatasource();
            lv_result.setAdapter(new ResultAdapter(ResultActivity.this, dat));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
*/
}
