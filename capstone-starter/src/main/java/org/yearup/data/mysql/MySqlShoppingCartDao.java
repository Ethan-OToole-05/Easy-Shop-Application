package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
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

        try (Connection connection = getConnection()) {
            //Check if the item already exists in the cart ---
            String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?;";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {
                selectStatement.setInt(1, userId);
                selectStatement.setInt(2, productId);

                try (ResultSet row = selectStatement.executeQuery()) {
                    if (row.next()) {
                        matchingItem = true;
                        currentQuantity = row.getInt("quantity");
                    }
                }
            }

            //If the item exist we will update if not we will insert.
            if (matchingItem) {
                //Increasing the quantity by one.
                String updateSql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    int newQuantity = currentQuantity + 1; // Increment quantity by 1
                    updateStatement.setInt(1, newQuantity);
                    updateStatement.setInt(2, userId);
                    updateStatement.setInt(3, productId);
                    updateStatement.executeUpdate();
                }
            } else {
                // If item does not exist in our cart we will insert it.
                String insertSql = "INSERT INTO shopping_cart(user_id, product_id, quantity) VALUES (?, ?, ?);";
                try (PreparedStatement insertStatement = connection.prepareStatement(insertSql)) {
                    insertStatement.setInt(1, userId);
                    insertStatement.setInt(2, productId);
                    insertStatement.setInt(3, 1); // Default quantity will be one.
                    insertStatement.executeUpdate();
                }
            }

            //Retrieve the updated shopping cart to display to the screen.
            String retrieveCartSql = "SELECT * FROM products AS p JOIN shopping_cart AS sc " +
                    "ON p.product_id = sc.product_id WHERE sc.user_id = ? ";
            try (PreparedStatement retrieveCartStatement = connection.prepareStatement(retrieveCartSql)) {
                retrieveCartStatement.setInt(1, userId);
                try (ResultSet row = retrieveCartStatement.executeQuery()) {
                    while (row.next()) {
                        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                        // Assuming MySqlProductDao.mapRow correctly maps product details from the ResultSet
                        shoppingCartItem.setProduct(MySqlProductDao.mapRow(row));
                        shoppingCartItem.setQuantity(row.getInt("quantity"));
                        shoppingCart.add(shoppingCartItem);
                    }
                }
            }

            return shoppingCart; // Return the fully populated shopping cart

        } catch (SQLException e) {
            throw new RuntimeException("Failed to add item to cart.", e);
        }
    }

    @Override
    public ShoppingCart setQuantityOfItem(int userId, int productId, int amount) {
        ShoppingCart shoppingCart = new ShoppingCart();
        int currentQuantity = 0;
        boolean matchingItem = false;

        // Check if the item exists in the cart and get its current quantity
        String selectSql = "SELECT quantity FROM shopping_cart WHERE user_id = ? AND product_id = ?;";
        try (Connection connection = getConnection()) {
            PreparedStatement selectStatement = connection.prepareStatement(selectSql);
            selectStatement.setInt(1, userId);
            selectStatement.setInt(2, productId);

            try (ResultSet row = selectStatement.executeQuery()) {
                if (row.next()) {
                    matchingItem = true;
                    currentQuantity = row.getInt("quantity");
                }
            }

            // Now, perform the update based on whether the item was found
            if (matchingItem) {
                String updateSql = "UPDATE shopping_cart SET quantity = ? WHERE user_id = ? AND product_id = ?;";
                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {
                    updateStatement.setInt(1, amount); // Set the quantity to the specified amount
                    updateStatement.setInt(2, userId);
                    updateStatement.setInt(3, productId);
                    updateStatement.executeUpdate();
                }
            } else {
                System.out.println("Product not found in cart for user. No update performed.");
            }

            // After the update (or attempted update), retrieve the full shopping cart
            // This query fetches all items for the user from the shopping cart and joins with products
            String retrieveCartSql = "SELECT * FROM products AS p JOIN shopping_cart AS sc" +
                    " ON p.product_id = sc.product_id WHERE sc.user_id = ? ";
            try (PreparedStatement retrieveCartStatement = connection.prepareStatement(retrieveCartSql)) {
                retrieveCartStatement.setInt(1, userId);
                try (ResultSet row = retrieveCartStatement.executeQuery()) {
                    while (row.next()) {
                        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                        // Assuming MySqlProductDao.mapRow correctly maps product details from the ResultSet
                        shoppingCartItem.setProduct(MySqlProductDao.mapRow(row));
                        shoppingCartItem.setQuantity(row.getInt("quantity"));
                        shoppingCart.add(shoppingCartItem);
                    }
                }
            }
            return shoppingCart;

        } catch (Exception e) {
            System.err.println("Error in setQuantityOfItem: " + e.getMessage());
            return new ShoppingCart();
        }
    }


    @Override
    public ShoppingCart removeItemsFromCart(int userId) {
        ShoppingCart shoppingCart = new ShoppingCart();

        try (Connection connection = getConnection()) {
            // --- STEP 1: Delete all items for the user from the shopping cart table ---
            String deleteSql = "DELETE FROM shopping_cart WHERE user_id = ?";
            try (PreparedStatement deleteStatement = connection.prepareStatement(deleteSql)) {
                deleteStatement.setInt(1, userId);
                deleteStatement.executeUpdate();
            }

            //Received the empty query to make sure all is deleted.
            String retrieveCartSql = "SELECT * FROM products AS p JOIN shopping_cart AS sc " +
                    "ON p.product_id = sc.product_id WHERE sc.user_id = ? ";
            try (PreparedStatement retrieveCartStatement = connection.prepareStatement(retrieveCartSql)) {
                retrieveCartStatement.setInt(1, userId);
                try (ResultSet row = retrieveCartStatement.executeQuery()) {
                    while (row.next()) {
                        ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                        shoppingCartItem.setProduct(MySqlProductDao.mapRow(row));
                        shoppingCartItem.setQuantity(row.getInt("quantity"));
                        shoppingCart.add(shoppingCartItem);
                    }
                }
            }
            //Returning an empty cart.
            return shoppingCart;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to remove items from cart.", e);
        }
    }
}
