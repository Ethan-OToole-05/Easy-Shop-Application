package org.yearup.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Order {

    private int order_id;
    private int user_id;
    private Timestamp date; //Might be problem later
    private String address;
    private String city;
    private String state;
    private String zip;
    private BigDecimal shipping_amount;

    public Order(){}

    public Order(int order_id, int user_id, Timestamp date, String address, String city, String state, String zip, BigDecimal shipping_amount) {
        this.order_id = order_id;
        this.user_id = user_id;
        this.date = date;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.shipping_amount = shipping_amount;
    }

    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public BigDecimal getShipping_amount() {
        return shipping_amount;
    }

    public void setShipping_amount(BigDecimal shipping_amount) {
        this.shipping_amount = shipping_amount;
    }
}
