package org.yearup.data;

import org.yearup.models.Order;
import org.yearup.models.Profile;
import org.yearup.models.ShoppingCart;

public interface OrdersDao {

    Order makeOrder(ShoppingCart shoppingCart, Profile profile);
    Order getOrder(int user_id);

}
