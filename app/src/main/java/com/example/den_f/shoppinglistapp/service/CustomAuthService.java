package com.example.den_f.shoppinglistapp.service;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.example.den_f.shoppinglistapp.fragments.auth.CustomLoginAuthFragment;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.example.den_f.shoppinglistapp.interfaces.ICustomAuth;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Den_F on 11-11-2016.
 */

public class CustomAuthService implements ICustomAuth {
    private static final String TAG = "CustomAuthService";
    /* TextView that is used to display information about the logged in user */
    private TextView mLoggedInStatusTextView;

    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;

    private FirebaseAuth mAuth;


    /* Data from the authenticated user */
    private AuthData mAuthData;

    /* Listener for Firebase session changes */
    private Firebase.AuthStateListener mAuthStateListener;

    private Button mPasswordLoginButton;
    Firebase firebase; //firebase reference
    Context context;
    private CustomLoginAuthFragment customLoginFragment;

    public CustomAuthService() {}

    public CustomAuthService(Context c, CustomLoginAuthFragment custLoginFragment) {
        context = c;
        this.customLoginFragment = custLoginFragment;
    }

    @Override
    public void login(String email, String password) {
        firebase = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/");
        final Firebase rootRef = firebase;
        //mAuthProgressDialog.show();

        rootRef.authWithPassword(email, password, new MyAuthResultHandler("password", email));
        mAuthStateListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                setAuthenticatedUser(authData);
            }
        };
        rootRef.addAuthStateListener(mAuthStateListener);
        customLoginFragment.displayShoppingFragment();

    }

    @Override
    public void registerUser(final String email, final String password) {

        firebase = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/");
        final Firebase rootRef = firebase;

        /*rootRef.createUser(email, password, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {
                // Great, we have a new user. Now log them in:
                rootRef.authWithPassword(
                        email,
                        password,
                        new Firebase.AuthResultHandler() {
                            @Override
                            public void onAuthenticated(AuthData authData) {
                                // Great, the new user is logged in.
                                // Create a node under "/users/uid/" and store some initial information,
                                // where "uid" is the newly generated unique id for the user:
                                rootRef.child("users").child(authData.getUid()).child("status").setValue("New User");
                                rootRef.child("users").child(authData.getUid()).child("email").setValue(email);
                                rootRef.child("users").child(authData.getUid()).child("password").setValue((password));
                                Toast toast = Toast.makeText(context, "User is registered and logged in!", Toast.LENGTH_LONG);
                                toast.show();
                                setAuthenticatedUser(authData);
                            }

                            @Override
                            public void onAuthenticationError(FirebaseError error) {
                                String errorMsg = error.toString();
                                Toast toast = Toast.makeText(context, errorMsg + error, Toast.LENGTH_LONG);
                                toast.show();
                                // Should hopefully not happen as we just created the user.
                            }
                        }
                );
            }

            @Override
            public void onError(FirebaseError error) {
                String errorMsg = error.toString();
                // Couldn't create the user, probably invalid email.
                // Show the error message and give them another chance.
                Toast toast = Toast.makeText(context, errorMsg + error, Toast.LENGTH_LONG);
                toast.show();
            }

        });*/
        //showProgressDialog();


    }



    @Override
    public void logout() {
        if (this.mAuthData != null) {
            /* logout of Firebase */
            firebase.unauth();

            /* Update authenticated user and show login buttons */
            setAuthenticatedUser(null);
            //TODO: Go to login screen
        }
    }

    @Override
    public void setAuthenticatedUser(AuthData authData) {
        if (authData != null) {
            /* Hide all the login buttons */

            mPasswordLoginButton.setVisibility(View.GONE);
            mLoggedInStatusTextView.setVisibility(View.VISIBLE);
            /* show a provider specific status text */
            String name = null;
            if (authData.getProvider().equals("anonymous")
                    || authData.getProvider().equals("password")) {
                name = authData.getUid();
            } else {
                Log.e(TAG, "Invalid provider: " + authData.getProvider());
            }
            if (name != null) {

                mLoggedInStatusTextView.setText("Logged in as " + name + " (" + authData.getProvider() + ")");
            }
        } else {
            /* No authenticated user show all the login buttons */
            //mPasswordLoginButton.setVisibility(View.VISIBLE);
            //mLoggedInStatusTextView.setVisibility(View.GONE);
        }
        this.mAuthData = authData;
        Log.e(TAG, "authData: " + authData);
        /* invalidate options menu to hide/show the logout button */
        //supportInvalidateOptionsMenu();
    }

    @Override
    public void showErrorDialog(String msg) {
        new AlertDialog.Builder(context)
                .setTitle("Error")
                .setMessage(msg)
                .setPositiveButton(android.R.string.ok, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private boolean validateForm(String email, String password) {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            //TODO: Toasts
            //mEmailField.setError("Required.");
            valid = false;
        } else {
            //mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            //mPasswordField.setError("Required.");
            valid = false;
        } else {
            //mPasswordField.setError(null);
        }

        return valid;
    }

    /**
     * Utility class for authentication results
     */
    private class MyAuthResultHandler implements Firebase.AuthResultHandler {

        private final String provider;
        private String email;

        public MyAuthResultHandler(String provider,String email) {
            this.provider = provider;
            this.email = email;
        }

        @Override
        public void onAuthenticated(AuthData authData) {
            mAuthProgressDialog.hide();
            Log.i(TAG, provider + " auth successful");
            setAuthenticatedUser(authData);
        }

        @Override
        public void onAuthenticationError(FirebaseError firebaseError) {
            mAuthProgressDialog.hide();
            showErrorDialog(firebaseError.toString()+ ", user:"+email);
        }
    }
}
