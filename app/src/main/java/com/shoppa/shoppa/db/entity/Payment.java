package com.shoppa.shoppa.db.entity;

public class Payment {

    private long date;
    private String type;
    private double amount;
    private String cardId;
    private String cartId;

    public Payment() {
    }

    public Payment(long date, String type, double amount, String cardId, String cartId) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.cardId = cardId;
        this.cartId = cartId;
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
}
