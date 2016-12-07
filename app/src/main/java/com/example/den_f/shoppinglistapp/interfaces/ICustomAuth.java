package com.example.den_f.shoppinglistapp.interfaces;

import com.firebase.client.AuthData;

/**
 * Created by Den_F on 11-11-2016.
 */

public interface ICustomAuth {
    void login(String email, String password);
    void registerUser(String email, String password);
    void logout();
    void setAuthenticatedUser(AuthData authData);
    void showErrorDialog(String msg);

}
