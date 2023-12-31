package com.example.neverforget.adapters;

public class MyToAddListNotKnowed {

    private int id;
    private int id_product;
    private String barcode;
    private int quantity;

    public MyToAddListNotKnowed(int id, int id_product, String barcode, int quantity){
        this.id = id;
        this.id_product = id_product;
        this.barcode = barcode;
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

    public String getBarcode(){
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
