package org.yearup.data.mysql;

import org.springframework.stereotype.Component;
import org.yearup.data.OrdersDao;
import org.yearup.models.Order;
import org.yearup.models.Product;
import org.yearup.models.ShoppingCart;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
@Component
public class MySqlOrdersDao extends MySqlDaoBase implements OrdersDao {
    public MySqlOrdersDao(DataSource dataSource) {
        super(dataSource);
    }


    @Override
    public Order makeOrder(ShoppingCart shoppingCart, int user_id) {
        Order order = new Order();
        String sql = "INSERT INTO orders(user_id, date, address, city, state, zip, shipping_amount) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?);";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setInt(1, order.getUser_id());
            statement.setDate(2, (Date) order.getDate());
            statement.setString(3, order.getAddress());
            statement.setString(4, order.getCity());
            statement.setString(5, order.getState());
            statement.setString(6, order.getZip());
            statement.setBigDecimal(7, order.getShipping_amount());
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int orderId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getOrder(orderId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

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

    protected static Order mapRow(ResultSet row) throws SQLException {
        int order_id = row.getInt("order_id");
        int user_id = row.getInt("user_id");
        Date date = row.getDate("date");
        String address = row.getString("address");
        String city = row.getString("city");
        String state = row.getString("state");
        String zip = row.getString("zip");
        BigDecimal shipping_amount = row.getBigDecimal("shipping_amount");

        return new Order(order_id, user_id, date, address, city, state, zip, shipping_amount);
    }
}
