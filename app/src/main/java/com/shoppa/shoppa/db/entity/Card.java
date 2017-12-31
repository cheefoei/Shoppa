package com.shoppa.shoppa.db.entity;

public class Card {

    private String id;
    private String holderName;
    private String cardNumber;
    private int cvc;
    private int year;
    private int month;
    private String type;
    private String userId;

    public Card() {
    }

    public Card(String holderName,
                String cardNumber,
                int cvc,
                int year,
                int month,
                String type,
                String userId) {

        this.holderName = holderName;
        this.cardNumber = cardNumber;
        this.cvc = cvc;
        this.year = year;
        this.month = month;
        this.type = type;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getCvc() {
        return cvc;
    }

    public void setCvc(int cvc) {
        this.cvc = cvc;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
