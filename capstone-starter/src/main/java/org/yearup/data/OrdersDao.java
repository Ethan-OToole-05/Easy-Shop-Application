package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.ShoppingCart;

public interface OrdersDao {

    Order makeOrder(ShoppingCart shoppingCart, int user_id);
    Order getOrder(int user_id);

}
