package org.yearup.data.mysql;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.yearup.data.CategoryDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MySqlCategoryDao extends MySqlDaoBase implements CategoryDao {
    @Autowired
    public MySqlCategoryDao(DataSource dataSource) {
        super(dataSource);
    }

    //Getting all the categories that is available in the db table to select.
    @Override
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        // get all categories
        String sql = "SELECT * FROM categories";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet row = statement.executeQuery();

            while (row.next()) {
                categories.add(mapRow(row));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return categories;
    }

    //Getting the category info by just the category id.

    @Override
    public Category getById(int categoryId) {
        // get category by id
        Category category = new Category();
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            ResultSet row = statement.executeQuery();

            if (row.next()) {
                category = mapRow(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    //Getting products by a category id.
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM categories AS c JOIN products AS p ON c.category_Id = p.category_id WHERE c.category_Id = ?";
        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);

            ResultSet row = statement.executeQuery();

            while (row.next()) {
                products.add(MySqlProductDao.mapRow(row));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    //Creating an entire category to be used based on what is passed in.
    @Override
    public Category create(Category category) {
        // create a new category
        String sql = "INSERT INTO categories(name, description) " +
                " VALUES (?, ?);";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                // Retrieve the generated keys
                ResultSet generatedKeys = statement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    // Retrieve the auto-incremented ID
                    int categoryId = generatedKeys.getInt(1);

                    // get the newly inserted category
                    return getById(categoryId);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return category;
    }

    //Update a category's name and description of the category by the id.
    @Override
    public void update(int categoryId, Category category) {
        String sql = "UPDATE categories" +
                " SET name = ?, description = ? WHERE category_id = ?";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, category.getName());
            statement.setString(2, category.getDescription());
            statement.setInt(3, categoryId);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(int categoryId) {
        // delete category
        String sql = "DELETE FROM categories " +
                " WHERE category_id = ?;";

        try (Connection connection = getConnection()) {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setInt(1, categoryId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //Helper method that is used to map out what makes a category.
    private Category mapRow(ResultSet row) throws SQLException {
        int categoryId = row.getInt("category_id");
        String name = row.getString("name");
        String description = row.getString("description");

        Category category = new Category() {{
            setCategoryId(categoryId);
            setName(name);
            setDescription(description);
        }};

        return category;
    }

}
