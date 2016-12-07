package com.example.den_f.shoppinglistapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;


import com.example.den_f.shoppinglistapp.activity.SettingsActivity;
import com.example.den_f.shoppinglistapp.fragments.auth.CustomAuthFragment;
import com.example.den_f.shoppinglistapp.fragments.preferences.MyPreferencesFragment;
import com.example.den_f.shoppinglistapp.fragments.shoppinglist.ShoppingFragment;
import com.example.den_f.shoppinglistapp.models.ShoppingListItem;
import com.example.den_f.shoppinglistapp.service.CustomAuthService;
import com.example.den_f.shoppinglistapp.service.ShoppingListService;
import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";
    static final int REQUEST_LANGUAGE = 1;
    Firebase mRef;
    ListView listView;
    ArrayList<ShoppingListItem> items = new ArrayList<>();

    private ValueEventListener mConnectedListener;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CustomAuthService service = new CustomAuthService();

    private Locale locale;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        Configuration config = getBaseContext().getResources().getConfiguration();

        String lang = settings.getString("LANG", "");
        if (!"".equals(lang) && !config.locale.getLanguage().equals(lang)) {
            Locale locale = new Locale(lang);
            locale.setDefault(locale);
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        }


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                      displayShoppingFragment();
                }
                else {
                    displayLoginFragment();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem logoutMenu = menu.findItem(R.id.action_logout);
        if (mUser != null) {
            logoutMenu.setVisible(true);
        }
        else {
            logoutMenu.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode== REQUEST_LANGUAGE) //from settings
        {
            String languageCode = MyPreferencesFragment.getLanguagePref(this);

                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("LANG", languageCode).commit();
                    setLangRecreate(languageCode);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.btn_item_deleteAll) {
          final ShoppingListService service = new ShoppingListService();
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setMessage(
            getResources().getString(R.string.warning_clearList))
            .setCancelable(false)
            .setPositiveButton(getResources().getString(R.string.btn_yes),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {
                  service.ClearShoppingList();
                  Toast.makeText(MainActivity.this, getResources().getString(R.string.progress_msg_clearAll_succesfull), Toast.LENGTH_SHORT).show();

                }
              })
            .setNegativeButton(getResources().getString(R.string.btn_no),
              new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,
                                    int id) {

                }
              });
          AlertDialog alert = builder.create();
          alert.show();

        }

      if (id == R.id.btn_item_share) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "My shopping list");
        intent.putExtra(Intent.EXTRA_TEXT, "Some interesting text");
        startActivity(Intent.createChooser(intent, "Send Email"));

      }

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivityForResult(intent, REQUEST_LANGUAGE);
            return true;
        }

        if (id == R.id.action_logout) {
            mAuth.signOut();
            service.logout();
            displayLoginFragment();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth != null && mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }

        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null && mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        Log.i(TAG, "onStop");
        /*mRef.getRoot().child("").removeEventListener(mConnectedListener);
        adapter.cleanup();*/
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    //This method is called before our activity is created
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //ALWAYS CALL THE SUPER METHOD
        super.onSaveInstanceState(outState);
        Log.i(TAG, "onSaveInstanceState");

		/* Here we put code now to save the state */


    }


    //this is called when our activity is recreated, but
    //AFTER our onCreate method has been called
    //EXTREMELY IMPORTANT DETAIL
    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        //MOST UI elements will automatically store the information
        //if we call the super.onRestoreInstaceState but other data will be lost.
        super.onRestoreInstanceState(savedState);
        Log.i(TAG, "onRestoreInstanceState");

    }

    public void setLangRecreate(String langval) {
        Configuration config = getBaseContext().getResources().getConfiguration();
        locale = new Locale(langval);
        Locale.setDefault(locale);
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
        recreate();
    }


    public void displayShoppingFragment() {
        ShoppingFragment shoppingFragment = new ShoppingFragment();
        shoppingFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, shoppingFragment);
        transaction.addToBackStack("shopping");
        transaction.commit();

    }

    public void displayLoginFragment() {
        CustomAuthFragment customAuthFragment = new CustomAuthFragment();
        customAuthFragment.setArguments(getIntent().getExtras());
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, customAuthFragment);
        transaction.addToBackStack("login");
        transaction.commit();
    }


}
