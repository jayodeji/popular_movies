package com.jayodeji.android.popularmovies.utilities;

import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;

import com.jayodeji.android.popularmovies.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String GOOGLE_DNS = "8.8.8.8";

    private static final String MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3";

    private static final String API_KEY_PARAM = "api_key";

    public static URL buildUrl(String apiKey, String path) {

        Uri builtUri = Uri.parse(MOVIE_DB_BASE_URL + path).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, apiKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built Url: " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        if (isOnline()) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = urlConnection.getInputStream();
                Scanner scanner = new Scanner(in);
                scanner.useDelimiter("\\A");
                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                }
            } finally {
                urlConnection.disconnect();
            }
        }
        return null;

    }

    public static boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        Process ipProcess = null;
        try {
            ipProcess = runtime.exec("/system/bin/ping -c 1 " + GOOGLE_DNS);
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}