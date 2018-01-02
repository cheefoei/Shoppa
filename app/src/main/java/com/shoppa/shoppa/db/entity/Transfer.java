package com.shoppa.shoppa.db.entity;

public class Transfer {

    private String userFrom;
    private String userTo;
    private long date;
    private double amount;

    public Transfer() {
    }

    public Transfer(String userFrom, String userTo, long date, double amount) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.date = date;
        this.amount = amount;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
