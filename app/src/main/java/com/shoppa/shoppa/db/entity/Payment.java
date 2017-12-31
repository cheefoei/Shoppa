package com.shoppa.shoppa.db.entity;

public class Payment {

    private long date;
    private String type;
    private double amount;
    private String storeId;
    private String cardId;
    private String cartId;
    private String userId;

    public Payment() {
    }

    public Payment(long date,
                   String type,
                   double amount,
                   String storeId,
                   String cardId,
                   String cartId,
                   String userId) {

        this.date = date;
        this.type = type;
        this.amount = amount;
        this.storeId = storeId;
        this.cardId = cardId;
        this.cartId = cartId;
        this.userId = userId;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
