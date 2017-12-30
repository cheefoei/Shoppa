package com.shoppa.shoppa.db.entity;

public class Item {

    private String id;
    private String name;
    private String description;
    private double price;
    private String barcode;
    private String img;
    private String storeId;

    public Item() {
    }

    public Item(String id, String name, String description, double price, String img, String storeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.img = img;
        this.storeId = storeId;
    }

    public Item(String name, String description, double price, String barcode, String img, String storeId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.barcode = barcode;
        this.img = img;
        this.storeId = storeId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }
}
