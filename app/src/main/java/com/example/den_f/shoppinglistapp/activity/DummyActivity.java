package com.example.den_f.shoppinglistapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.fragments.preferences.DummyFragment;
import com.example.den_f.shoppinglistapp.fragments.preferences.MyPreferencesFragment;

/**
 * Created by Den_F on 17-11-2016.
 */

public class DummyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        /*android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, new DummyFragment());
        transaction.addToBackStack(null);
        transaction.commit();*/

        //note - there is not setContentView and no xml layout
        //for this activity. Because that is defined 100 %
        //in the fragment
    }
}