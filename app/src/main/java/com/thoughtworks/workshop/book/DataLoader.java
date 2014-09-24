package com.thoughtworks.workshop.book;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public final class DataLoader {

    private static final String TAG = "DataLoader";

    public static final JSONObject loadJSONData(final String urlString) {
        StringBuilder contentBuilder = new StringBuilder();

        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());

            byte[] buffer = new byte[1024];

            while (inputStream.read(buffer) != -1) {
                contentBuilder.append(new String(buffer, "UTF-8"));
            }

            inputStream.close();
            connection.disconnect();
            return new JSONObject(contentBuilder.toString());
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        }

        return null;
    }
}
