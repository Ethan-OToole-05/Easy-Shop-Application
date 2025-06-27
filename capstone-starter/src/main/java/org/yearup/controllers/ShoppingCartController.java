package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.ProductDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;
import org.yearup.models.ShoppingCartItem;
import org.yearup.models.User;

import java.security.Principal;

// convert this class to a REST controller
// only logged in users should have access to these actions
@RestController
@RequestMapping("cart")
@CrossOrigin
public class ShoppingCartController
{
    // a shopping cart requires
    private ShoppingCartDao shoppingCartDao;
    private UserDao userDao;
    private ProductDao productDao;

    @Autowired
    public ShoppingCartController(ShoppingCartDao shoppingCartDao, UserDao userDao, ProductDao productDao) {
        this.shoppingCartDao = shoppingCartDao;
        this.userDao = userDao;
        this.productDao = productDao;
    }

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    // each method in this controller requires a Principal object as a parameter
    public ShoppingCart getCart(Principal principal)
    {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            // use the shoppingcartDao to get all items in the cart and return the cart
            return shoppingCartDao.getAllProducts(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while getting products.");
        }
    }

    //POST method that is used to add an item into the user's cart.
    @PostMapping("/products/{productId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public ShoppingCart addItemToCart(Principal principal, @PathVariable int productId) {

        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            return shoppingCartDao.addItemToCart(userId, productId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while adding item to cart.");
        }
    }

    //PUT method that is used to change the quantity of an item that is in the user's cart.
    @PutMapping("/products/{productId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.OK)
    //Change return to shoppingcart? Had void in the beginning                              HAD TO CHANGE REQUEST BODY TO BE AN ITEM FOR QUANTITY
    public ShoppingCart updateItemFromCart(Principal principal, @PathVariable int productId, @RequestBody ShoppingCartItem shoppingCartItem) {
        ShoppingCart shoppingCart = new ShoppingCart();
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

           return shoppingCartDao.setQuantityOfItem(userId, productId, shoppingCartItem.getQuantity());
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred updating quantity");
        }
    }

    //DELETE method that is used to remove all the items inside of the user's cart.
    @DeleteMapping()
    @PreAuthorize("isAuthenticated()")
    public ShoppingCart removeItemsFromCart(Principal principal) {
        try
        {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();

            return shoppingCartDao.removeItemsFromCart(userId);
        }
        catch(Exception e)
        {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occurred while remove items");
        }
    }
}
