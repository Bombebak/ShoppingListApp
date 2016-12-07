package com.example.den_f.shoppinglistapp.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;

/**
 * Created by Den_F on 15-09-2016.
 */
@JsonIgnoreProperties(ignoreUnknown=true)
@Parcel(Parcel.Serialization.BEAN)
public class ShoppingListItem {
    public String productId;
    public String productName;
    public int quantity;
    public boolean selected;

    public ShoppingListItem() {}

    @ParcelConstructor
    public ShoppingListItem(String productId, String productName, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
}
