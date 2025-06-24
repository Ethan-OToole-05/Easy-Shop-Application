package org.yearup.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yearup.data.ProfileDao;
import org.yearup.data.ShoppingCartDao;
import org.yearup.data.UserDao;
import org.yearup.models.ShoppingCart;

@RestController
@RequestMapping("orders")
@CrossOrigin
public class OrdersController {
    private ShoppingCartDao shoppingCart;
    private ProfileDao profileDao;
    private UserDao userDao;


    @Autowired
    public OrdersController(ShoppingCartDao shoppingCartDao, ProfileDao profileDao, UserDao userDao) {
        this.shoppingCart = shoppingCartDao;
        this.profileDao = profileDao;
        this.userDao = userDao;
    }
}
