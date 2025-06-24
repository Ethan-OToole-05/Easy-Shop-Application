package org.yearup.data;

import org.yearup.models.ShoppingCart;

public interface ShoppingCartDao
{
    ShoppingCart getByUserId(int userId);
    ShoppingCart getAllProducts();
    ShoppingCart addItemToCart(int productId);
    ShoppingCart addQuantityOfItem(int productId, int amount);
    ShoppingCart removeItemFromCart(int productId);
    // add additional method signatures here
    /*
    Get all products?
    Add item to cart
    Update item in cart
    Remove item in cart (look in quantity removal or entire item with number)
    EX: iPhone x3 (REMOVE) -> (How many to remove? Input: 2) = iPhone x1?
     */
}
