package com.wasike.lycitybot.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.wasike.lycitybot.Constants;
import com.wasike.lycitybot.models.Genius;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GeniusService  {
    //declared the genius model in the service
    private static Genius mGenius;

    //Creating a method responsible for making the requests to the Genius API
    public static void findSongInfo(String specSong, Callback callback) {
        OkHttpClient client = new OkHttpClient();

        //Constructing the url to be sent
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.GENIUS_BASE_URL).newBuilder();

        urlBuilder.addQueryParameter(Constants.GENIUS_QUERY_PARAMETER, specSong);

        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .header("Authorization", Constants.GENIUS_API_KEY)
                .url(url)
                .build();

        Log.v("Information", url);

        Call call = client.newCall(request);
        call.enqueue(callback);

    }

    //Receives the response from the API
    public ArrayList<Genius> processResults(Response response){
        ArrayList<Genius> songs = new ArrayList<>();

        try{
            String jsonData = response.body().string();
            Log.v("jsonData", jsonData);
            if(response.isSuccessful()) {
                JSONObject geniusJSON = new JSONObject(jsonData);
                JSONObject responseJSON = geniusJSON.getJSONObject("response");


                JSONArray songJsonObject = responseJSON.getJSONArray("hits");
                for (int i = 0; i < songJsonObject.length(); i++){
                    JSONObject hitsJSON = songJsonObject.getJSONObject(i);
                    Log.v("hitsJSON", hitsJSON.toString());
                    String songTitle = hitsJSON.getJSONObject("result").getString("title");
                    String lyricsUrl = hitsJSON.getJSONObject("result").getString("url");
                    String artistName = hitsJSON.getJSONObject("result").getJSONObject("primary_artist").getString("name");
                    String imageThumbnail = hitsJSON.getJSONObject("result").getString("header_image_url");

                    Genius genius = new Genius(songTitle, lyricsUrl, artistName, imageThumbnail);
                    songs.add(genius);
                    break;
                }

            }

        }catch (IOException e){
            e.printStackTrace();
        }catch(JSONException e) {
            e.printStackTrace();
        }
        return songs;
    }
}
