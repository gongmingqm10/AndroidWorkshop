package com.thoughtworks.workshop.book;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static android.util.Xml.Encoding.UTF_8;

public final class DataLoader {

    private static final String TAG = "DataLoader";

    public static final JSONObject loadJSONData(Context context) {
        JSONObject json = null;

        InputStream in = context.getResources().openRawResource(R.raw.data);

        try {
            int size = in.available();
            byte[] buffer = new byte[size];
            in.read(buffer);
            in.close();
            json = new JSONObject(new String(buffer, UTF_8.toString()));
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } catch (JSONException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return json;
    }
}
