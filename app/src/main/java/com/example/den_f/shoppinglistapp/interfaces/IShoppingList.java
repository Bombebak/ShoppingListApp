package com.example.den_f.shoppinglistapp.interfaces;

import com.example.den_f.shoppinglistapp.models.ShoppingListItem;

/**
 * Created by Den_F on 13-11-2016.
 */

public interface IShoppingList {
    void AddToShoppingList(String productName, int productQuantity);
    void EditShoppingItem(ShoppingListItem item);
    void DeleteShoppingItem(ShoppingListItem item);
    void DeleteShoppingItems(ShoppingListItem[] items);
    void ClearShoppingList();
}
