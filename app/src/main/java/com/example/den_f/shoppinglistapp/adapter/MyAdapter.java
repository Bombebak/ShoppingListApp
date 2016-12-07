package com.example.den_f.shoppinglistapp.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.models.ShoppingListItem;

import java.util.ArrayList;

/**
 * Created by Den_F on 15-11-2016.
 */

public class MyAdapter extends FirebaseRecyclerAdapter<MyAdapter.ViewHolder, ShoppingListItem> {

    private OnItemCheckListener onItemCheckListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView productNameTextView, productQuantityTextView, productIdTextView;
        CheckBox productSelectCbox;
        private final ArrayList<ShoppingListItem> items = new ArrayList<>();



        public ViewHolder(View itemView) {
            super(itemView);
            productIdTextView = (TextView) itemView.findViewById(R.id.headingProductId);
            productNameTextView = (TextView) itemView.findViewById(R.id.headingProductName);
            productQuantityTextView = (TextView) itemView.findViewById(R.id.headingProductQuantity);
            productSelectCbox = (CheckBox) itemView.findViewById(R.id.headingProductSelected);
            productSelectCbox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
          productSelectCbox.setChecked(false);
        }


    }

    public MyAdapter(com.google.firebase.database.Query query, Class<ShoppingListItem> itemClass, @Nullable ArrayList<ShoppingListItem> items,
                     @Nullable ArrayList<String> keys, OnItemCheckListener onItemCheckListener) {
        super(query, itemClass, items, keys);
        this.onItemCheckListener = onItemCheckListener;
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customview, parent, false);

        ViewHolder vh = new ViewHolder(view);

        return vh;
    }
    @Override public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
        final ShoppingListItem item = getItem(position);
        holder.productIdTextView.setText(item.getProductId());
        holder.productNameTextView.setText(item.getProductName());
        holder.productQuantityTextView.setText(String.valueOf(item.getQuantity()));

        holder.productSelectCbox.setOnCheckedChangeListener(null);

        holder.productSelectCbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if ((holder).productSelectCbox.isChecked()) {

                    onItemCheckListener.onItemCheck(item);
                    item.setSelected(true);
                    holder.productSelectCbox.setChecked(true);
                }
                else {
                    onItemCheckListener.onItemUncheck(item);
                    item.setSelected(false);
                }
            }
        });
        holder.productSelectCbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    holder.productSelectCbox.setChecked(true);
                    item.setSelected(true);
                }
                else {
                    holder.productSelectCbox.setChecked(false);
                    item.setSelected(false);
                }

            }
        });
    }



    @Override protected void itemAdded(ShoppingListItem item, String key, int position) {
        Log.d("MyAdapter", "Added a new item to the adapter.");
    }

    @Override protected void itemChanged(ShoppingListItem oldItem, ShoppingListItem newItem, String key, int position) {
        Log.d("MyAdapter", "Changed an item.");
    }

    @Override protected void itemRemoved(ShoppingListItem item, String key, int position) {
        Log.d("MyAdapter", "Removed an item from the adapter.");
    }

    @Override protected void itemMoved(ShoppingListItem item, String key, int oldPosition, int newPosition) {
        Log.d("MyAdapter", "Moved an item.");
    }
    @Override
    public int getItemCount() {
        return getItems().size();
    }

    public interface OnItemCheckListener {
        void onItemCheck(ShoppingListItem item);
        void onItemUncheck(ShoppingListItem item);
    }
}
