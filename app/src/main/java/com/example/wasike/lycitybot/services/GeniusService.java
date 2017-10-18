package com.example.wasike.lycitybot.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.wasike.lycitybot.Constants;
import com.example.wasike.lycitybot.models.Genius;

import okhttp3.Callback;
import okhttp3.HttpUrl;

/**
 * Created by wasike on 18/10/17.
 */

public class GeniusService  {
    //declared the genius model in the service
    private static Genius mGenius;

    //Creating a method responsible for making the requests to the Genius API
    public static void findSongInfo(String song, Callback callback) {

        //Constructing the url to be sent
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.GENIUS_BASE_URL).newBuilder();

        urlBuilder.addQueryParameter(Constants.GENIUS_QUERY_PARAMETER, song);

    }
}
