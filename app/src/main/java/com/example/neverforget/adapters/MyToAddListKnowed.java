package com.example.neverforget.adapters;

public class MyToAddListKnowed {

    private int id;
    private int id_product;
    private String name;
    private String brand;
    private int quantity;

    public MyToAddListKnowed(int id, int id_product, String name, String brand, int quantity){
        this.id = id;
        this.id_product = id_product;
        this.name = name;
        this.brand = brand;
        this.quantity = quantity;
    }

    public int getId(){
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_product(){
        return id_product;
    }

    public void setId_product(int id_product) {
        this.id_product = id_product;
    }

    public String getName(){
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand(){
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
