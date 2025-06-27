package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.CategoryDao;
import org.yearup.data.ProductDao;
import org.yearup.models.Category;
import org.yearup.models.Product;

import java.util.List;

// add the annotations to make this a REST controller
// add the annotation to make this controller the endpoint for the following url
// http://localhost:8080/categories
// add annotation to allow cross site origin requests

@RestController
@RequestMapping("categories")
@CrossOrigin
public class CategoriesController {
    private CategoryDao categoryDao;
    private ProductDao productDao;


    // create an Autowired controller to inject the categoryDao and ProductDao
    @Autowired
    public CategoriesController(CategoryDao categoryDao, ProductDao productDao) {
        this.categoryDao = categoryDao;
        this.productDao = productDao;
    }

    //GET method that is used to get all the categories available to choose from.
    @GetMapping()
    @PreAuthorize("permitAll()")
    public List<Category> getAll() {
        // find and return all categories
        try {
            return categoryDao.getAllCategories();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during getting all categories..");
        }
    }

    //GET method that is used to get a category by its id.
    @GetMapping("{id}")
    @PreAuthorize("permitAll()")
    public Category getById(@PathVariable int id) {
        // get the category by id
        try {
            if(categoryDao.getById(id) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error category could not be found");
            }
            return categoryDao.getById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during getting a category.");
        }
    }

    //GET method that is used to get a list of products by category id.
    @GetMapping("{categoryId}/products")
    @PreAuthorize("permitAll()")
    public List<Product> getProductsById(@PathVariable int categoryId) {
        // get a list of product by categoryId
        try {
            if(categoryDao.getById(categoryId) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error products from category could not be found");
            }
            return categoryDao.getProductsByCategoryId(categoryId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during creating a category.");
        }
    }

    //POST method that is only allowed by admins to make a new category.
    @PostMapping()
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Category addCategory(@RequestBody Category category) {
        try {
            if(categoryDao.create(category) == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: category could not be made");
            }
            return categoryDao.create(category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error: creating a category");
        }
    }

    //PUT method only allowed by admins to update the category's information.
    @PutMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void updateCategory(@PathVariable int id, @RequestBody Category category) {
        // update the category by id
        try {
            if(category == null || category.getCategoryId() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error occurred updating category");
            }
            categoryDao.update(id, category);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during updating category.");
        }
    }

    //DELETE method that only admins can delete categories from the list.
    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable int id) {
        try {
            if(id == 0 || id < 0) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error deleting category was not found.");
            }
            categoryDao.delete(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred during deleting category.");
        }
    }
}
