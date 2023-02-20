package com.example.casem3.model;

import com.example.casem3.service.IProductService;
import com.example.casem3.service.mysql.MSProductService;

import java.util.List;

public class Order {
    private Long id;
    private String name;
    private String phone;
    private String address;

    private double total;
    public double total(){
        total = 0;
        IProductService iProductService = new  MSProductService();
        for (OrderItem o :orderItems
        ) {
            Product product = iProductService.findProductById(o.getIdProduct());
            total += product.getPrice()*o.getQuantiy() +5 ;
        }
        return total;
    }
    public double getTotal() {
        return total;
    }
    public int getQuantityItem(){
        int items = 0;
        for (OrderItem o: orderItems
             ) {
            items += o.getQuantiy();
        }
        return items;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    private List<OrderItem> orderItems;

    public Order(Long id, String name, String phone, String address, double total) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.total = total;
    }

    public OrderDTO toDTO() {
        OrderDTO orderDTO = new OrderDTO();

        orderDTO.setName(this.getName());
        orderDTO.setPhone(this.getPhone());
        orderDTO.setAddress(this.getAddress());
        orderDTO.setId(this.getId());



        return orderDTO;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Order(Long id, String name, String phone, String address, List<OrderItem> orderItems) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.orderItems = orderItems;
    }

    public Order() {
    }
}
