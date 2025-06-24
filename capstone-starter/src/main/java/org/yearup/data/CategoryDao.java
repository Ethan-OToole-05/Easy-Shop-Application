package org.yearup.data;

import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

public interface CategoryDao
{
    List<Category> getAllCategories();
    List<Product> getProductsByCategoryId(int categoryId);
    Category getById(int categoryId);
    Category create(Category category);
    void update(int categoryId, Category category);
    void delete(int categoryId);
}
