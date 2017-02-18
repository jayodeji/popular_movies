package com.jayodeji.android.popularmovies.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by joshuaadeyemi on 12/28/16.
 */

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String GOOGLE_DNS = "8.8.8.8";

    /**
     * Inspiration for this came from the android sample projects in:
     * Project Sunshine in the Android developer nanodegree
     * @param url
     * @return
     * @throws IOException
     */
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

    /**
     * This was gotten from the stackoverflow post:
     * http://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
     * @return
     */
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