package com.example.den_f.shoppinglistapp.fragments.shoppinglist;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.den_f.shoppinglistapp.MainActivity;
import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.fragments.auth.CustomAuthFragment;
import com.example.den_f.shoppinglistapp.models.ShoppingListItem;
import com.example.den_f.shoppinglistapp.adapter.MyAdapter;

import com.example.den_f.shoppinglistapp.service.ShoppingListService;

import com.firebase.client.Firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.Query;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Den_F on 13-11-2016.
 */

public class ShoppingFragment extends Fragment {

    private final static String SAVED_ADAPTER_ITEMS = "SAVED_ADAPTER_ITEMS";
    private final static String SAVED_ADAPTER_KEYS = "SAVED_ADAPTER_KEYS";
    private final static String TAG = "ShoppingFragment";
    private static final String BUNDLE_RECYCLER_LAYOUT = "ShoppingFragment.recyclerView.layout";

    private Button btnCreate, btnUpdate, btnDelete, btnEdit, btnSelectAll, btnDeselectAll;
    private EditText inputName, inputQuantity;
    private TextView lblWelcomeUser;
    private RecyclerView recyclerView;
    public ProgressDialog mProgressDialog;
    private CheckBox productSelectCbox;
    private List<ShoppingListItem> currentSelectedItems = new ArrayList<>();
    private ArrayList<ShoppingListItem> mAdapterItems;
    private ArrayList<String> mAdapterKeys;
    private Query mQuery;
    private Firebase mFirebaseRef;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private MyAdapter mMyAdapter;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference ref;

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handleInstanceState(savedInstanceState);
    //initializeFields();

  }

  @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

      setRetainInstance(true);

        final View view = inflater.inflate(R.layout.shopping_fragment, container, false);
        mAuth = FirebaseAuth.getInstance();

        mUser = mAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + mUser.getUid());
                    //displayShoppingFragment();
                }
                else {
                    displayLoginFragment();
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
        mFirebaseRef = new Firebase("https://shoppinglistapp-ec206.firebaseio.com/");

        ref = FirebaseDatabase.getInstance().getReference();

        mQuery = ref.child("items");
        if (mUser != null) {
            mQuery = ref.child("items").child(mUser.getUid());
        }
      handleInstanceState(savedInstanceState);
        //initializeFields();
        //setupRecyclerview();


        return view;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {


        convertView = null;

        if (convertView == null) {

            LayoutInflater mInflater = (LayoutInflater) getContext()
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.shopping_fragment, null);


            initializeFields();

            // click listiner for remove button
            addItemToShoppingList();

            updateItemFromShoppingList();

            deleteItemFromShoppingList();


        }

        ///#use    return convertView;
        return convertView;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
          CharSequence lblWelcomeUserChar = savedInstanceState.getCharSequence("lblWelcomeUser");
          lblWelcomeUser.setText(lblWelcomeUserChar);
        }
    }



  //https://github.com/mmazzarolo/firebase-recyclerview/blob/master/app/src/main/java/com/example/matteo/firebase_recycleview/MainActivity.java


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_ADAPTER_ITEMS, Parcels.wrap(mMyAdapter.getItems()));
        outState.putStringArrayList(SAVED_ADAPTER_KEYS, mMyAdapter.getKeys());


        CharSequence lblUserName = lblWelcomeUser.getText();
        CharSequence productName = inputName.getText();
        CharSequence productQuantity = inputQuantity.getText();

        outState.putCharSequence("lblWelcomeUser", lblUserName);
        outState.putCharSequence("inputName", productName);
        outState.putCharSequence("inputQuantity", productQuantity);

        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, recyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    public void onStart() {
        super.onStart();
        if (mAuth != null && mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
      mMyAdapter.destroy();
    }

  private void handleInstanceState(Bundle savedInstanceState) {
    if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_ADAPTER_ITEMS) && savedInstanceState.containsKey(SAVED_ADAPTER_KEYS)) {
      mAdapterItems = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_ADAPTER_ITEMS));
      //mAdapterKeys = Parcels.unwrap(savedInstanceState.getParcelable(SAVED_ADAPTER_KEYS));
      //lblWelcomeUser = Parcels.unwrap(savedInstanceState.getParcelable("lblWelcomeUser"));
    }
    else {

    }
  }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initializeFields();

        if (mUser != null && lblWelcomeUser != null) {
            lblWelcomeUser.setText("Welcome " + mUser.getEmail());
        }

        addItemToShoppingList();

        updateItemFromShoppingList();

        deleteItemFromShoppingList();

        deSelectItems();

    }

    private void addItemToShoppingList() {
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListService shoppingService = new ShoppingListService(ShoppingFragment.this);

                boolean inputsValid = true;
                if (isEmpty(inputName)) {
                    Toast.makeText(getActivity(), R.string.validation_product_name_empty, Toast.LENGTH_SHORT).show();
                    inputsValid = false;
                }
                if (isEmpty(inputQuantity)) {
                    Toast.makeText(getActivity(), R.string.validation_product_quantity_empty, Toast.LENGTH_SHORT).show();
                    inputsValid = false;
                }
                if (inputsValid) {
                    String productName = inputName.getText().toString();
                    String productQuantityStr = inputQuantity.getText().toString();
                    int productQuantity = Integer.parseInt(productQuantityStr);
                    showProgressDialog(getContext().getResources().getString(R.string.progress_msg_addingItemToList));
                    shoppingService.AddToShoppingList(productName, productQuantity);
                    hideProgressDialog();
                }
            }
        });


    }

    private void updateItemFromShoppingList() {
        final ShoppingListService shoppingService = new ShoppingListService(this);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShoppingListItem itemUpdate = new ShoppingListItem();
                for (ShoppingListItem item: currentSelectedItems) {
                    itemUpdate.productId = item.productId;
                }
                String productName = inputName.getText().toString();
                String productQuantityStr = inputQuantity.getText().toString();
                int productQuantity = Integer.parseInt(productQuantityStr);

                itemUpdate.productName = productName;
                itemUpdate.quantity = productQuantity;
                shoppingService.EditShoppingItem(itemUpdate);


            }
        });


    }

    private void deleteItemFromShoppingList() {
        final ShoppingListService shoppingService = new ShoppingListService(ShoppingFragment.this);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedItems.size() >= 1) {
                    for (ShoppingListItem item : currentSelectedItems)  {
                        shoppingService.DeleteShoppingItem(item);
                    }
                }
              final Snackbar snackbar = Snackbar.make(getView(), "",Snackbar.LENGTH_LONG)
                .setAction("UNDO", new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                    for (ShoppingListItem item: currentSelectedItems) {
                      shoppingService.AddToShoppingList(item.getProductName(), item.getQuantity());

                      Snackbar snackbar = Snackbar.make(getView(), getContext().getResources().getString(R.string.progress_msg_item_readded),Snackbar.LENGTH_LONG);
                      snackbar.show();
                    }

                  }

                });
              snackbar.show();
            }


        });

    }

  private void deSelectItems() {
    btnDeselectAll.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        for (ShoppingListItem item : currentSelectedItems) {
          item.setSelected(false);

        }
      }
    });

  }

    private void displayFunctionBtns() {
        if (currentSelectedItems.size() == 1) {

            btnEdit.setVisibility(View.VISIBLE);
            btnDeselectAll.setVisibility(View.VISIBLE);
            btnCreate.setEnabled(false);
            btnUpdate.setEnabled(true);
            btnDelete.setVisibility(View.VISIBLE);

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (ShoppingListItem item: currentSelectedItems) {
                        inputName.setText(item.getProductName());
                        String quantityStr = Integer.toString(item.getQuantity());
                        inputQuantity.setText(quantityStr);
                        btnUpdate.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        else if (currentSelectedItems.size() > 1) {
            btnCreate.setEnabled(false);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnEdit.setVisibility(View.INVISIBLE);
            btnDeselectAll.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);


        }
        else {
            btnCreate.setEnabled(true);
            btnUpdate.setVisibility(View.INVISIBLE);
            //btnUpdate.setEnabled(true);
            btnEdit.setVisibility(View.INVISIBLE);
            btnDelete.setVisibility(View.INVISIBLE);
          btnDeselectAll.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    public void displayLoginFragment() {
        CustomAuthFragment customAuthFragment = new CustomAuthFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, customAuthFragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void clearInputFields() {
        inputName.setText("");
        inputQuantity.setText("");
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

    private void initializeFields() {
        lblWelcomeUser = (TextView) getView().findViewById(R.id.lbl_welcome_user);

        inputName = (EditText) getView().findViewById(R.id.inputProductName);
        inputQuantity = (EditText) getView().findViewById(R.id.inputProductQuantity);

        btnCreate = (Button) getView().findViewById(R.id.btnAddToList);
        btnEdit = (Button) getView().findViewById(R.id.btn_item_edit);
        btnDelete = (Button) getView().findViewById(R.id.btn_item_delete);

        btnUpdate = (Button) getView().findViewById(R.id.btn_item_update);
        btnSelectAll = (Button) getView().findViewById(R.id.btnSelectAllItems);
        btnDeselectAll = (Button) getView().findViewById(R.id.btnDeselectAllItems);

        productSelectCbox = (CheckBox) getView().findViewById(R.id.headingProductSelected);

        recyclerView = (RecyclerView) getView().findViewById(R.id.shoppingList_items_recycler);
        mMyAdapter = new MyAdapter(mQuery, ShoppingListItem.class, mAdapterItems, mAdapterKeys, new MyAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(ShoppingListItem item) {
                currentSelectedItems.add(item);
                displayFunctionBtns();
            }

            @Override
            public void onItemUncheck(ShoppingListItem item) {
                currentSelectedItems.remove(item);
                displayFunctionBtns();

            }
        });
        if (recyclerView == null) {
            recyclerView = new RecyclerView(getContext());
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(mMyAdapter);
    }
}
