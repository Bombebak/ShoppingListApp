package com.example.den_f.shoppinglistapp.fragments.preferences;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.den_f.shoppinglistapp.R;

/**
 * Created by Den_F on 17-11-2016.
 */

public class MyPreferencesFragment extends PreferenceFragment {
    public static String SETTINGS_LANGUAGE_CODE = "language_preference";
    //https://bhavyanshu.me/tutorials/provide-multiple-language-support-in-your-android-app/08/20/2015

    public static String getLanguagePref(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(SETTINGS_LANGUAGE_CODE, "");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //adding the prefs
        addPreferencesFromResource(R.xml.prefs);
    }





}
