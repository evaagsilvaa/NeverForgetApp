package com.example.neverforget.adapters;

public interface ToAddListCallback {

    void onItemListNotKnownedClick(int id, int id_product, String barcode, int quantity);
    void onItemListKnownedClick(int id, int id_product);
}
