package com.shoppa.shoppa.db.entity;

public class User {

    private String id;
    private String fname;
    private String lname;
    private char gender;
    private String email;

    public User(String id, String fname, String lname, char gender, String email) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.gender = gender;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
