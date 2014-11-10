package com.cardfreetool.phototreasure;

/**
 * Created by grishmashah on 11/6/14.
 * This class calls google custom search api to get image urls from passed string
 */

import android.util.Log;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.customsearch.Customsearch;
import com.google.api.services.customsearch.model.Result;
import com.google.api.services.customsearch.model.Search;

import java.util.Iterator;

public class ServerHelper {

    HttpTransport httpTransport = new NetHttpTransport();
    JsonFactory jsonFactory = new JacksonFactory();
    Customsearch customsearch = null;
    String[] imageUrls = null;

    ServerHelper(){
        customsearch = new Customsearch.Builder(httpTransport, jsonFactory, null).setApplicationName("PhotoTreasure").build();
    }

    // method to call google custom search api for getting images urls for passed query string
    // used google api services customsearch classes to request and parse response
    public String[] getSearchResult(String queryStr, long startIndex) {
        int index = 0;
        try {
            Customsearch.Cse.List list = customsearch.cse().list(queryStr);
            list.setKey(Constants.API_KEY);
            list.setCx(Constants.SEARCH_ENGINE_ID);
            list.setImgSize("small");
            list.setSearchType("image");
            list.setStart(startIndex);

            Search searchResults = list.execute();
            if (searchResults != null) {
                Iterator<Result> searchResultsIter = searchResults.getItems().iterator();
                imageUrls = new String[searchResults.getItems().size()];
                while (searchResultsIter.hasNext()) {
                    Result result = searchResultsIter.next();
                    imageUrls[index] = result.getLink();
                    index++;
                }
            } else {
                Log.v(getClass().getName(), "No result found from server");
            }
        }catch (GoogleJsonResponseException e){
            Log.v(getClass().getName(), "Got the exception: " + e.getStatusCode() + "-" + e.getMessage());
            imageUrls = null;
        }catch (Exception e) {
            imageUrls = null;
        }
        return imageUrls;
    }
}
