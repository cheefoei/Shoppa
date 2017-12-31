package com.shoppa.shoppa.db.entity;

public class CartDetail {

    private long id;
    private int quantity;
    private String itemId;

    public CartDetail() {
    }

    public CartDetail(int quantity, String itemId) {
        this.quantity = quantity;
        this.itemId = itemId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
