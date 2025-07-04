package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.OrdersDao;
import org.yearup.models.*;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;

@Component
public class MySqlOrdersDao extends MySqlDaoBase implements OrdersDao {

    @Autowired
    public MySqlOrdersDao(DataSource dataSource) {
        super(dataSource);
    }


    //Used to make a new order based on the profile of the user and the user's shopping cart.
    @Override
    public Order makeOrder(ShoppingCart shoppingCart, Profile profile) {
        int orderId = 0;
        Order order = new Order();

        BigDecimal total = new BigDecimal("0.00");
        int itemCounter = 0;
        for (ShoppingCartItem item : shoppingCart.getItems().values()) {
            total = total.add(item.getLineTotal());
            itemCounter++;
        }
        order.setShipping_amount(total);

        LocalDateTime now = LocalDateTime.now();

        Timestamp timestamp = Timestamp.valueOf(now);

        //Insert into orders first to make the transaction.
        String sql = "INSERT INTO orders(user_id, date, address, city, state, zip, shipping_amount) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, profile.getUserId());
            statement.setTimestamp(2, timestamp);
            statement.setString(3, profile.getAddress());
            statement.setString(4, profile.getCity());
            statement.setString(5, profile.getState());
            statement.setString(6, profile.getZip());
            statement.setBigDecimal(7, order.getShipping_amount());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    orderId = generatedKeys.getInt(1);
                }
            }
            //We will take each item that was inside the cart into the order_line_items db table.
            for (ShoppingCartItem item : shoppingCart.getItems().values()) {
                String insertItemSql = "INSERT INTO order_line_items(order_id, product_id, sales_price, quantity, discount) " +
                        " VALUES (?, ?, ?, ?, ?);";
                PreparedStatement insertItemStatement = connection.prepareStatement(insertItemSql, PreparedStatement.RETURN_GENERATED_KEYS);
                insertItemStatement.setInt(1, orderId);
                insertItemStatement.setInt(2, item.getProductId());
                insertItemStatement.setBigDecimal(3, item.getProduct().getPrice());
                insertItemStatement.setInt(4, item.getQuantity());
                insertItemStatement.setBigDecimal(5, item.getDiscountPercent());
                int insertRowsAffected = insertItemStatement.executeUpdate();

                if (insertRowsAffected > 0) {
                    // Retrieve the generated keys
                    ResultSet generatedKeys = insertItemStatement.getGeneratedKeys();

                    if (generatedKeys.next()) {
                        // Retrieve the auto-incremented ID
                        int order_line_item_id = generatedKeys.getInt(1);

                    }
                }
            }

            shoppingCart.clear();

            return getOrder(orderId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Used to grab an order that a person made by the order_id.
    @Override
    public Order getOrder(int order_id) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, order_id);

            ResultSet row = statement.executeQuery();

            if (row.next()) {
                return mapRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    //Helper method that is used to map out what makes an order.
    protected static Order mapRow(ResultSet row) throws SQLException {
        int order_id = row.getInt("order_id");
        int user_id = row.getInt("user_id");
        Timestamp date = row.getTimestamp("date");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");
        BigDecimal shipping_amount = row.getBigDecimal("shipping_amount");

        return new Order(order_id, user_id, date, address, city, state, zip, shipping_amount);
    }
}
