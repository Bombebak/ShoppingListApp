package com.example.den_f.shoppinglistapp.fragments.auth;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.den_f.shoppinglistapp.R;
import com.google.firebase.auth.FirebaseAuth;


/**
 * Created by Den_F on 11-11-2016.
 */

public class CustomAuthFragment extends Fragment {
    Button btnCustomSignUp, btnCustomLogin;
    public ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_fragment, container, false);
        }
    /*
    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        mAuth.addAuthStateListener(mAuthListener);
        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)

    }*/

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btnCustomSignUp = (Button) getView().findViewById(R.id.btnCustomAuthSignUp);
        btnCustomSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();

                CustomSignUpAuthFragment customSignUpAuthFragment = new CustomSignUpAuthFragment();
                fragmentTransaction.replace(R.id.fragment_container, customSignUpAuthFragment);

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
        btnCustomLogin = (Button) getView().findViewById(R.id.btnCustomAuthLogin);
        btnCustomLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                CustomLoginAuthFragment customLoginAuthFragment = new CustomLoginAuthFragment();
                fragmentTransaction.replace(R.id.fragment_container, customLoginAuthFragment);

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

            }
        });
    }



    }


