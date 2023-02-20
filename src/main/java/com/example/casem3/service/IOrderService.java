package com.example.casem3.service;



import com.example.casem3.model.Order;

import java.util.List;

public interface IOrderService {
    List<Order> getAllOrder();
    Order getOrderById(long id);

    void saveOrder(Order order);
}
