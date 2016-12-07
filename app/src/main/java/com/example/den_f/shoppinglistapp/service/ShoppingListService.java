package com.example.den_f.shoppinglistapp.service;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.fragments.shoppinglist.ShoppingFragment;
import com.example.den_f.shoppinglistapp.models.ShoppingListItem;
import com.example.den_f.shoppinglistapp.interfaces.IShoppingList;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Den_F on 13-11-2016.
 */

public class ShoppingListService implements IShoppingList {
    private Firebase mFirebaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final static String LOG_TAG = "ShoppingListService";
    private ShoppingFragment shoppingFragment;

    public ShoppingListService() {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRef = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/").child("items");
        user = mAuth.getCurrentUser();
    }

    public ShoppingListService(ShoppingFragment shoppingFragment) {
        mAuth = FirebaseAuth.getInstance();
        mFirebaseRef = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/").child("items");
        user = mAuth.getCurrentUser();
        this.shoppingFragment = shoppingFragment;
    }

    @Override
    public void AddToShoppingList(String productName, int productQuantity) {
        Map<String, Object> newEntry = new HashMap<>();
        String productId = UUID.randomUUID().toString();
        newEntry.put("productId", productId);
        newEntry.put("productName", productName);
        newEntry.put("quantity", productQuantity);
        mFirebaseRef.child(user.getUid()).child(productId).setValue(newEntry, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(shoppingFragment.getContext() ,firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG, "Data could not be saved. " + firebaseError.getMessage());
                } else {
                    shoppingFragment.clearInputFields();
                    Toast.makeText(shoppingFragment.getContext() , R.string.progress_msg_item_created_successfully, Toast.LENGTH_LONG).show();
                    Log.v(LOG_TAG, "Data saved successfully.");
                }
            }
        });
    }

    @Override
    public void EditShoppingItem(ShoppingListItem item) {
        String name = item.getProductName();
        int quantity = item.getQuantity();
        Map<String, Object> updateEntry = new HashMap<>();

        updateEntry.put("productName", name);
        updateEntry.put("quantity", quantity);

        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ShoppingListItem item = dataSnapshot.getValue(ShoppingListItem.class);
                shoppingFragment.clearInputFields();
                Log.v(LOG_TAG, "Data updated successfully." + item);
                Toast.makeText(shoppingFragment.getContext() , R.string.progress_msg_item_updated_successfully, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v(LOG_TAG, "Data could not be updated. " + firebaseError.getMessage());
                Toast.makeText(shoppingFragment.getContext() ,firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        mFirebaseRef.addValueEventListener(itemListener);
        mFirebaseRef.child(user.getUid()).child(item.getProductId()).updateChildren(updateEntry);

    }

    @Override
    public void DeleteShoppingItem(ShoppingListItem item) {
        ValueEventListener itemListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ShoppingListItem item = dataSnapshot.getValue(ShoppingListItem.class);
                shoppingFragment.clearInputFields();
                Log.v(LOG_TAG, "Data removed successfully." + item);

                Toast.makeText(shoppingFragment.getContext() , R.string.progress_msg_item_deleted_successfully, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v(LOG_TAG, "Data could not be removed. " + firebaseError.getMessage());
                Toast.makeText(shoppingFragment.getContext() ,firebaseError.getMessage(), Toast.LENGTH_LONG).show();
            }
        };
        mFirebaseRef.addValueEventListener(itemListener);
        mFirebaseRef.child(user.getUid()).child(item.getProductId()).setValue(null);
    }

    @Override
    public void DeleteShoppingItems(ShoppingListItem[] items) {

    }

    @Override
    public void ClearShoppingList() {
      mFirebaseRef.child(user.getUid()).setValue(null);
    }

  public String ShareShoppingList() {

    final String[] bodyMsg = new String[1];
    mFirebaseRef.addValueEventListener(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        StringBuilder builder = new StringBuilder();

        for (DataSnapshot itemssnapshot: dataSnapshot.getChildren()) {
          ShoppingListItem item = itemssnapshot.getValue(ShoppingListItem.class);


        }


      }

      @Override
      public void onCancelled(FirebaseError firebaseError) {
  }
});
    return "";
  }
}
