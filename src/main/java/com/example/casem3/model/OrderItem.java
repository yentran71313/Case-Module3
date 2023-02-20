package com.example.casem3.model;

public class OrderItem {
    private Long id;
    private Long idProduct;
    private int quantity;

    public OrderItem(Long id, Long idProduct, int quantity) {
        this.id = id;
        this.idProduct = idProduct;
        this.quantity = quantity;
    }

    public OrderItem() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(Long idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantiy() {
        return quantity;
    }

    public void setQuantiy(int quantity) {
        this.quantity = quantity;
    }
}
