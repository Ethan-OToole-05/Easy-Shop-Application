package org.yearup.data.mysql;

import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;

public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {super(dataSource);}

    @Override
    public ShoppingCart getByUserId(int userId) {
        return null;
    }

    @Override
    public ShoppingCart getAllProducts() {
        return null;
    }

    @Override
    public ShoppingCart addItemToCart(int productId) {
        return null;
    }

    @Override
    public ShoppingCart addQuantityOfItem(int productId, int amount) {
        return null;
    }

    @Override
    public ShoppingCart removeItemFromCart(int productId) {
        return null;
    }
}
