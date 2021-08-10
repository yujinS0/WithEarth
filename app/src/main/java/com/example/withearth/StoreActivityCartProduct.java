package com.example.withearth;

public class StoreActivityCartProduct {

    private String name, price, quantity;

    public StoreActivityCartProduct(){}

    public StoreActivityCartProduct(String name, String price, String quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}