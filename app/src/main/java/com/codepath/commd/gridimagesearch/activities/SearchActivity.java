package com.codepath.commd.gridimagesearch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;

import com.codepath.commd.gridimagesearch.R;
import com.codepath.commd.gridimagesearch.adapters.ImageResultsAdapter;
import com.codepath.commd.gridimagesearch.listeners.EndlessScrollListener;
import com.codepath.commd.gridimagesearch.models.ImageResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SearchActivity extends ActionBarActivity {

    private EditText etQuery;
    private GridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter aImageResults;  // I would have call this aImageResultsAdapter
    private int start = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        setupViews();
        // Creates the datasource
        imageResults = new ArrayList<ImageResult>();
        // Attach datasource to the adapter
        aImageResults = new ImageResultsAdapter(this, imageResults);
        // Link the adapter to the adapter view (our gridview in this case)
        gvResults.setAdapter(aImageResults);
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        gvResults.setOnScrollListener(new EndlessScrollListener() {
//            From Hints:
//            @Override
//            public void onLoadMore(int page, int totalItemsCount) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to your AdapterView
//                customLoadMoreDataFromApi(page);
//                // or customLoadMoreDataFromApi(totalItemsCount);
//            }
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("DEBUG", "LOADMORE " + page + " " + totalItemsCount);
                // loadMorePhotos(page);
                downImages(true);   // should be true if it's the first search.
            }
        });
        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch the image display activity
                // Create an intent
                Intent i = new Intent(SearchActivity.this, ImageDisplayActivity.class);
                // Get the image result to display
                ImageResult result = imageResults.get(position);
                // Pass the image result into the intent
                // i.putExtra("url", result.fullUrl);
                i.putExtra("result", result);  // we will use serializable to pass the whole result object.
                // Launch the new activity
                startActivity(i);
            }
        });
    }

/*    private void loadMorePhotos(int page){
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + keyword + "&start=" + ((page-1) * 4);
        // Log.i("DEBUG-APPEND-"+page, url);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, null, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray imagesJSON = null;
                try {
                    imagesJSON = response.getJSONObject("responseData").getJSONArray("results");
                    // Log.i("DEBUG-APPEND", imagesJSON.toString());
                    imageResults.addAll(ImageResult.fromJSONArray(imagesJSON));
                    aImageResults.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    // Fired whenever the button is pressed.
    public void onImageSearch(View v) {
        downImages(true); // We run onImageSearch when the search button is clicked.
                          // This is our first search for our this query so we run with true.
    }


    private void downImages (boolean firstSearch){
        // int start; // change this !!!

        if (firstSearch){
            firstSearch = false;
            start = 0;
        } else {
            start = start + 8;

        }
        String query = etQuery.getText().toString();
        // Toast.makeText(this, "Search for: " + query, Toast.LENGTH_SHORT).show();

        AsyncHttpClient client = new AsyncHttpClient();
        // https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=android&rsz=8
        String searchUrl = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" +
                query + "&rsz=8&Start=" + start;
        client.get(searchUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("DEBUG", response.toString());
                JSONArray imageResultsJson = null;
                try {
                    imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    imageResults.clear();    // clear existing images from array (in cases where it's a new search.
                    // Note that when you make changes to the adapter, it make the underlying changes to the data
                    // When you add items to the adapter, it adds the data to the array list and triggers the notify!
                    aImageResults.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.i("INFO", imageResults.toString());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
