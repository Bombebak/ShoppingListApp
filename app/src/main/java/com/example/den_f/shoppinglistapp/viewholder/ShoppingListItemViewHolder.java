package com.example.den_f.shoppinglistapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.den_f.shoppinglistapp.R;
import com.example.den_f.shoppinglistapp.models.ShoppingListItem;


/**
 * Created by Den_F on 14-11-2016.
 */

public class ShoppingListItemViewHolder extends RecyclerView.ViewHolder {
    public TextView productNameView, productQuantityView;

    public ShoppingListItemViewHolder(View itemView) {
        super(itemView);
        productNameView = (TextView) itemView.findViewById(R.id.headingProductName);
        productQuantityView = (TextView) itemView.findViewById(R.id.headingProductQuantity);
    }


    public void bindToShoppingList(ShoppingListItem item) {
        //TODO: Edit selected item
    }
}
