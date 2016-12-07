package com.example.den_f.shoppinglistapp.activity;

import android.app.Activity;
import android.os.Bundle;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.fragments.preferences.MyPreferencesFragment;

/**
 * Created by Den_F on 17-11-2016.
 */

public class SettingsActivity extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        /*MyPreferencesFragment preferencesFragment = new MyPreferencesFragment();
        preferencesFragment.setArguments(getIntent().getExtras());
        getFragmentManager().beginTransaction().
                add(android.R.id.content, preferencesFragment).commit();*/

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferencesFragment())
          .addToBackStack("settings")
                .commit();
        //note - there is not setContentView and no xml layout
        //for this activity. Because that is defined 100 %
        //in the fragment
    }
}
