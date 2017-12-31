package com.shoppa.shoppa.db.entity;

public class Cart {

    private long id;
    private double total;

    public Cart() {
    }

    public Cart(double total) {
        this.total = total;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
