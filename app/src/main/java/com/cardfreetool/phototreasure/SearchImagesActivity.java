package com.cardfreetool.phototreasure;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SearchImagesActivity extends Activity {

    GridView gridView = null;
    CustomImageAdapter customImageAdapter = null;
    SearchView searchView = null;
    List<String> imageUrls = null;
    int ind = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_images);

        imageUrls = new ArrayList<String>();
        gridView = (GridView) findViewById(R.id.img_gridView);
        customImageAdapter = new CustomImageAdapter(SearchImagesActivity.this, R.layout.image_view, imageUrls);
        gridView.setAdapter(customImageAdapter);

        searchView = (SearchView) findViewById(R.id.img_searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
                if(imageUrls!=null && imageUrls.size() > 0) {
                    imageUrls.clear();
                    gridView.postInvalidate();
                }
                if(Utils.isNetworkConnected(SearchImagesActivity.this)) {
                    // 2 tasks started for getting urls from custom search api. 2nd one is pre-fetching more data as custom search
                    // returns only 10 per request
                    new DownloadImageUrlsTask().execute(s, "1");
                    new DownloadImageUrlsTask().execute(s, "11");
                }else{
                    NoConnectionToast();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            // This will track the number of download counts of Image urls from google custom search API
            int downloadCount = 0;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if(totalItemCount == 0){
                    downloadCount = totalItemCount;
                }
                if(totalItemCount!=0) {
                    downloadCount = Math.max(downloadCount, totalItemCount);
                    if(firstVisibleItem + 20 > downloadCount) {
                        if(Utils.isNetworkConnected(SearchImagesActivity.this)) {
                            // Pre-fetching urls from google custom search onScroll
                            new DownloadImageUrlsTask().execute(searchView.getQuery().toString(), String.valueOf(downloadCount + 1));
                            new DownloadImageUrlsTask().execute(searchView.getQuery().toString(), String.valueOf(downloadCount + 11));
                            downloadCount = downloadCount + 20;
                            ind++;
                        }else{
                            NoConnectionToast();
                        }
                    }
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_images, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // this is asynctask to load urls from google custom search api
    private class DownloadImageUrlsTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {
            ServerHelper serverHelper = new ServerHelper();
            String[] urls = serverHelper.getSearchResult(params[0], Long.parseLong(params[1]));
            return urls;
        }

        protected void onPostExecute(String[] urls) {
            if(urls!=null && urls.length > 0) {
                imageUrls.addAll(Arrays.asList(urls));
                customImageAdapter.notifyDataSetChanged();
            }
        }
    }

    // showing Toast for no internet connection
    private void NoConnectionToast(){
        Toast t = Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }

}
