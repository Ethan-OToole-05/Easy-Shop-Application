package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.ShoppingCartDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MySqlShoppingCartDao extends MySqlDaoBase implements ShoppingCartDao {
    public MySqlShoppingCartDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public ShoppingCart getAllProducts(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String sql = "SELECT * FROM products AS p JOIN shopping_cart AS sc ON p.product_id = sc.product_id WHERE sc.user_id = ? ";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);

            ResultSet row = statement.executeQuery();

            while (row.next()) {
                ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                shoppingCartItem.setProduct(MySqlProductDao.mapRow(row));
                shoppingCartItem.setQuantity(row.getInt("quantity"));
                shoppingCart.add(shoppingCartItem);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

    @Override
    public ShoppingCart addItemToCart(int userId, int productId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        int currentQuantity = 0;
        boolean matchingItem = false;

        String sql = "SELECT * FROM products AS p JOIN shopping_cart AS sc ON p.product_id = sc.product_id " +
                "WHERE sc.user_id = ? AND sc.product_id = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    //Checks to see if we have the item in our cart already.
                    matchingItem = true;
                    //Then we will get the current quantity of the item.
                    currentQuantity = row.getInt("quantity");
                }
            }

            if (matchingItem) {
                String updateSql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    // Increments quantity by 1 with the update statement.
                    int newQuantity = currentQuantity + 1;
                    updateStatement.setInt(1, newQuantity);
                    updateStatement.setInt(2, userId);
                    updateStatement.setInt(3, productId);
                    updateStatement.executeUpdate();
                }
            } else {
                // Handles the insert into logic if the item is not in our cart.
                String insertSql = "INSERT INTO shopping_cart(user_id, product_id, quantity) VALUES (?, ?, ?);";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setInt(1, userId);
                    insertStatement.setInt(2, productId);
                    //Default value of quantity will be one.
                    insertStatement.setInt(3, 1);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

    @Override
    public ShoppingCart setQuantityOfItem(int userId, int productId, int amount) {
        ShoppingCart shoppingCart = new ShoppingCart();
        int currentQuantity = 0, newQuantity = 0;
        boolean matchingItem = false;

        String sql = "SELECT * FROM products AS p JOIN shopping_cart AS sc ON p.product_id = sc.product_id " +
                "WHERE sc.user_id = ? AND sc.product_id = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.setInt(2, productId);

            try (ResultSet row = statement.executeQuery()) {
                if (row.next()) {
                    //Checks to see if we have the item in our cart already.
                    matchingItem = true;
                    //Then we will get the current quantity of the item.
                    currentQuantity = row.getInt("quantity");
                }
            }
            if (matchingItem) {
                String updateSql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    // Increments quantity by 1 with the update statement.
                    newQuantity = amount;
                    updateStatement.setInt(1, newQuantity);
                    updateStatement.setInt(2, userId);
                    updateStatement.setInt(3, productId);
                    updateStatement.executeUpdate();
                }
            }
            return shoppingCart;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public ShoppingCart removeItemsFromCart(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();
        String sql = "DELETE FROM shopping_cart " +
                "WHERE user_id = ?";
        try (Connection connection = getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, userId);
            statement.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        return shoppingCart;
    }

}

//    @Override
//    public ShoppingCart getByUserId(int userId) {
//        String sql = "SELECT * FROM shopping_cart WHERE user_id = ?";
//        try (Connection connection = getConnection()) {
//            PreparedStatement statement = connection.prepareStatement(sql);
//            statement.setInt(1, userId);
//
//            ResultSet row = statement.executeQuery();
//
//            if (row.next()) {
/// /                return mapRow(row);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }
