package com.example.neverforget.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

public class Service {
    private static final String prefixURL = "http://never-forget.duckdns.org/";
    private static final String categories_path = "categories";
    private static final String products_path = "products";
    private static final String brands_path = "brands";
    private static final String shoppinglists_path = "shoppinglists";
    private static final String toaddlist_path = "to-add-list";

    public Service(Context context, Callback callback) {
        API.setup(context, callback);
    }

    //-----------------Category-------------//
    public void getCategoryByName(JSONObject data) {
        String path = null;
        try {
            path = prefixURL + categories_path + "?name=" + data.getString("name") + "&categoriesFather=" + data.getString("categoriesFather") + "&organized=" + data.getString("organized");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        API.get(path, CallbackTypes.GET_CATEGORY_BY_NAME, true);
    }

    public void getCategoryByID(JSONObject data) {
        String path = null;
        try {
            path = prefixURL + categories_path + "/" + data.getInt("id") + "?subCategories=" + data.getString("subCategories");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        API.get(path, CallbackTypes.GET_CATEGORY_BY_ID, false);
    }

    public void createCategory(JSONObject data) {
        String path = prefixURL + categories_path;
        API.post(path, data, CallbackTypes.CREATE_CATEGORY);
    }

    public void updateCategory(int id, JSONObject data) {
        String path = prefixURL + categories_path + "/" + id;
        API.patch(path, data, CallbackTypes.UPDATE_CATEGORY);
    }

    //-----------------Product-------------//
    public void getProductByBarcode(String barcode) {
        String path = prefixURL + products_path + "/barcode?barcode=" + barcode;
        API.get(path, CallbackTypes.GET_PRODUCT_BY_BARCODE, false);
    }

    public void getProductsByCategory(int id) {
        String path = prefixURL + products_path + "/category?id=" + id;
        API.get(path, CallbackTypes.GET_PRODUCTS_BY_CATEGORY, true);
    }

    public void getProductByID(int id) {
        String path = prefixURL + products_path + "/" + id;
        API.get(path, CallbackTypes.GET_PRODUCT_BY_ID, false);
    }

    public void createProduct(JSONObject data) {
        String url = prefixURL + products_path;
         API.post(url, data, CallbackTypes.CREATE_PRODUCT);
    }

    public void updateProduct(int id, JSONObject data) {
        String url = prefixURL + products_path + "/" + id;
        API.patch(url, data, CallbackTypes.UPDATE_PRODUCT);
    }

    public void deleteProduct(int id) {
        String path = prefixURL + products_path + "/" + id;
        API.delete(path, CallbackTypes.DELETE_PRODUCT);
    }

    //-----------------Brand-------------//
    public void getAllBrands() {
        String path = prefixURL + brands_path;
        API.get(path, CallbackTypes.GET_ALL_BRANDS, true);
    }

    public void getBrandByName(String name) {
        String path = prefixURL + brands_path + "/find?name=" + name;
        API.get(path, CallbackTypes.GET_BRAND_BY_NAME, false);
    }

    //-------------Shopping List-----------//
    public void createShoppingList(JSONObject data) {
        String path = prefixURL + shoppinglists_path;
        API.post(path, data, CallbackTypes.CREATE_SHOPPINGLIST);
    }

    public void getAllShoppingLists() {
        String path = prefixURL + shoppinglists_path;
        API.get(path, CallbackTypes.GET_SHOPPINGLISTS, true);
    }

    public void getShoppingListByID(int id) {
        String path = prefixURL + shoppinglists_path + "/" + id;
        API.get(path, CallbackTypes.GET_SHOPPINGLIST_BY_ID, false);
    }

    public void addItemToShoppingList(int id, JSONObject data) {
        String path = prefixURL + shoppinglists_path + "/" + id + "/add-item";
        API.post(path, data, CallbackTypes.ADD_ITEM_TO_SHOPPINGLIST);
    }

    public void updateShoppingListName(int id, JSONObject data) {
        String path = prefixURL + shoppinglists_path + "/" + id + "/name";
        API.patch(path, data, CallbackTypes.UPDATE_SHOPPINGLISTNAME);
    }

    public void updateShoppingListItemQuantity(int id, int idItem, JSONObject data) {
        String path = prefixURL + shoppinglists_path + "/" + id + "/item/" + idItem;
        API.patch(path, data, CallbackTypes.UPDATE_SHOPPINGLIST_ITEM_QUANTITY);
    }

    public void deleteShoppingList(int id) {
        String path = prefixURL + shoppinglists_path + "/" + id;
        API.delete(path, CallbackTypes.DELETE_SHOPPING_LIST);
    }

    public void deleteItemFromShoppingList(int id, int idItem) {
        String path = prefixURL + shoppinglists_path + "/" + id + "/item/" + idItem;
        API.delete(path, CallbackTypes.DELETE_ITEM_SHOPPING_LIST);
    }

    //-----------------ToAddList-------------//
    public void getAllItemsToAddList() {
        String path = prefixURL + toaddlist_path;
        API.get(path, CallbackTypes.GET_ALL_ITEMS_TO_ADD_LIST, true);
    }

    public void getProductByIdToAddList(int id) {
        String path = prefixURL + toaddlist_path + "/" + id;
        API.get(path, CallbackTypes.GET_PRODUCT_BY_ID_TO_ADD_LIST, false);
    }

    public void deleteItemToAddList(int id) {
        String path = prefixURL + toaddlist_path + "/" + id;
        API.delete(path, CallbackTypes.DELETE_ITEM_TO_ADD_LIST);
    }

    public void updateToAddListItemQuantity(int idItem, JSONObject data) {
        String path = prefixURL + toaddlist_path + "/" + idItem;
        API.patch(path, data, CallbackTypes.UPDATE_TOADDLIST_ITEM_QUANTITY);
    }

}
