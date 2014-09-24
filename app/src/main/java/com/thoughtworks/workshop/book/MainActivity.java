package com.thoughtworks.workshop.book;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private static final String TAG = "Main";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new BooklistFragment()).commit();
        }

    }


}
