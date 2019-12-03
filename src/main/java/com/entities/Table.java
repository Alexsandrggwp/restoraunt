package com.entities;

import java.util.ArrayList;
import java.util.List;

public class Table {

    private int id;
    private int clientId;

    private List<Order> orders = new ArrayList<>();

    public Table() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void addOrder(Order order){
        orders.add(order);
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
