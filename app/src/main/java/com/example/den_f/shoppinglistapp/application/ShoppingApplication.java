package com.example.den_f.shoppinglistapp.application;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.Firebase;

/**
 * Created by Den_F on 08-11-2016.
 */

public class ShoppingApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
        Firebase.getDefaultConfig().setPersistenceEnabled(true);


    }
}
