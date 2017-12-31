package com.shoppa.shoppa.db.entity;

public class User {

    private String id;
    private String name;
    private String gender;
    private String email;
    private String password;
    private String profile;
    private double pocketMoney;

    public User() {
    }

    public User(String name, String email, String password, double pocketMoney) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.pocketMoney = pocketMoney;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public double getPocketMoney() {
        return pocketMoney;
    }

    public void setPocketMoney(double pocketMoney) {
        this.pocketMoney = pocketMoney;
    }
}
