package com.example.den_f.shoppinglistapp.fragments.auth;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.fragments.shoppinglist.ShoppingFragment;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Den_F on 11-11-2016.
 */

public class CustomSignUpAuthFragment extends Fragment {
    Button btnRegister;
    EditText inputEmail, inputPassword;
    Firebase firebase; //firebase reference
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mProgressDialog;
    private TextView mStatusTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //set the layout you want to display in First Fragment
        View view = inflater.inflate(R.layout.custom_signup_fragment,
                container, false);
        mAuth = FirebaseAuth.getInstance();
        firebase = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                //updateUI(user);
                // [END_EXCLUDE]
            }
        };

        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        Bundle args = getArguments();
        firebase = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/");

        mAuth.addAuthStateListener(mAuthListener);
        // When in two-pane layout, set the listview to highlight the selected list item
        // (We do this during onStart because at the point the listview is available.)

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mStatusTextView = (TextView) getView().findViewById(R.id.lblLogin_val);
        btnRegister = (Button) getView().findViewById(R.id.btnRegisterUser);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //CustomAuthService userService = new CustomAuthService(getContext());
                inputEmail = (EditText) getView().findViewById(R.id.inputEmail);
                inputPassword = (EditText) getView().findViewById(R.id.inputPassword);
                CharSequence emailChar = inputEmail.getText();
                boolean inputsValid = true;
                if (isEmpty(inputEmail)) {
                    inputEmail.setError(getContext().getResources().getString((R.string.validation_email_empty)));
                    inputEmail.requestFocus();
                    inputsValid = false;
                }
                /*if (isEmailValid(emailChar)) {
                    Toast.makeText(getActivity(), R.string.validation_email_invalid, Toast.LENGTH_SHORT).show();
                    inputsValid = false;
                }*/
                if (isEmpty(inputPassword)) {
                    inputPassword.setError(getContext().getResources().getString((R.string.validation_pass)));
                    inputPassword.requestFocus();
                    inputsValid = false;
                }
                if (inputsValid) {
                    String email = inputEmail.getText().toString();
                    String hashed = md5(inputPassword.getText().toString());

                    showProgressDialog(getContext().getResources().getString(R.string.progress_msg_register));

                    //userService.registerUser(email, hashed);
                    mAuth.createUserWithEmailAndPassword(email, hashed)
                            .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                                    if (task.isSuccessful()) {
                                        displayShoppingFragment();
                                    }
                                    else {
                                        try {
                                            throw task.getException();
                                        } catch(FirebaseAuthWeakPasswordException e) {
                                            inputPassword.setError(getContext().getResources().getString((R.string.validation_pass)));
                                            inputPassword.requestFocus();
                                        } catch(FirebaseAuthInvalidCredentialsException e) {
                                            inputEmail.setError(getContext().getResources().getString((R.string.validation_email_password_invalid)));
                                            inputEmail.requestFocus();
                                        } catch(FirebaseAuthUserCollisionException e) {
                                            mStatusTextView.setVisibility(View.VISIBLE);
                                            mStatusTextView.setError(getContext().getResources().getString((R.string.validation_user_exists)));
                                            mStatusTextView.requestFocus();
                                        } catch(Exception e) {
                                            Log.e(TAG, e.getMessage());
                                        }

                                    }

                                    hideProgressDialog();

                                }
                            });
                }

            }
        });
    }



    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
    private boolean isEmailValid(CharSequence target) {
        return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void displayShoppingFragment() {
        FragmentManager fragManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        ShoppingFragment shoppingFragment = new ShoppingFragment();

        fragmentTransaction.replace(R.id.fragment_container, shoppingFragment);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    public void showProgressDialog(String msg) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(msg);
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

}
