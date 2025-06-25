package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.yearup.data.OrdersDao;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.Order;
import org.yearup.models.User;

import java.security.Principal;

@RestController
@RequestMapping("orders")
@CrossOrigin
public class OrdersController {
    private OrdersDao ordersDao;
    private ShoppingCartDao shoppingCartDao;
    private ProfileDao profileDao;
    private UserDao userDao;


    @Autowired
    public OrdersController(OrdersDao ordersDao, ShoppingCartDao shoppingCartDao, ProfileDao profileDao, UserDao userDao) {
        this.ordersDao = ordersDao;
        this.shoppingCartDao = shoppingCartDao;
        this.profileDao = profileDao;
        this.userDao = userDao;
    }

    @PostMapping()
    @PreAuthorize("permitAll()")
    public Order makeOrder(Principal principal)
    {
        try {
            // get the currently logged in username
            String userName = principal.getName();
            // find database user by userId
            User user = userDao.getByUserName(userName);
            int userId = user.getId();
            return ordersDao.makeOrder(shoppingCartDao.getAllProducts(userId), profileDao.getByUserId(userId));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Oops... our bad.");
        }
    }
}
