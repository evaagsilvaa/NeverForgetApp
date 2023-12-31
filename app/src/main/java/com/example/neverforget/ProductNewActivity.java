package com.example.neverforget;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.google.android.material.textfield.TextInputLayout.END_ICON_CLEAR_TEXT;
import static com.google.android.material.textfield.TextInputLayout.END_ICON_NONE;

public class ProductNewActivity extends AppCompatActivity implements Callback, CallBackListener {

    //Inicializações dos objectos nos layouts
    private Service service;
    private TextView title;
    private AutoCompleteTextView category, brand;
    private TextInputEditText name, barcode;
    private TextInputLayout name_layout, category_layout,barcode_layout;
    private TextView error_name, error_category, error_brand;
    private ExtendedFloatingActionButton button;

    //Variáveis auxiliares
    private int id_category, id_brand;
    private String type_of_request, id_product;
    private String transition_string;
    private String error = "error";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_product);

        Variables.flag_create_new_category = false;

        //------------Inicializações-------------//
        category = findViewById(R.id.category_product_new_product);
        name = findViewById(R.id.name_new_product);
        brand = findViewById(R.id.brand_new_product);
        barcode = findViewById(R.id.barcode_new_product);
        category_layout = findViewById(R.id.category_product_new_product_layout);
        name_layout = findViewById(R.id.name_new_product_layout);
        barcode_layout = findViewById(R.id.barcode_new_product_layout);
        button = findViewById(R.id.add_new_product);
        title = findViewById(R.id.new_product);

        error_name = findViewById(R.id.errorname);
        error_brand = findViewById(R.id.errorbrand);
        error_category = findViewById(R.id.errorcategory);

        barcode_layout.setEndIconMode(END_ICON_CLEAR_TEXT);

        //----------------Service-------------//
        service = new Service(this.getApplicationContext(), this);


        //--------------Get Extras------------//
        if (Variables.flag_new_product == "update_product"){
            button.setText("Update product");
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    id_product= null;
                } else {
                    id_product= extras.getString("id");
                }
            } else {
                id_product= (String) savedInstanceState.getSerializable("id");
            }
            getProductById(Integer.parseInt(id_product));
        }else if (Variables.flag_new_product == "raspberry_pi") {
            transition_string = null;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    id_product = null;
                } else {
                    id_product = extras.getString("id");
                }
            } else {
                id_product = (String) savedInstanceState.getSerializable("id");
            }
            getProductById(Integer.parseInt(id_product));
        }else{
            transition_string = null;
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    transition_string= null;
                } else {
                    if (Variables.flag_new_product == "barcode"){
                        transition_string= extras.getString("barcode");
                    }else if (Variables.flag_new_product == "category"){
                        transition_string= extras.getString("category");
                    }
                }
            } else {
                transition_string= (String) savedInstanceState.getSerializable("barcode");
            }
        }
        if (Variables.flag_new_product == "barcode"){
            barcode.setText(transition_string);
            barcode_layout.setEndIconMode(END_ICON_NONE);
            barcode.setEnabled(false);
        }else if (Variables.flag_new_product == "category"){
            category.setFocusable(false);
            category.setFocusableInTouchMode(false);
            category.dismissDropDown();
            category_layout.setEndIconMode(END_ICON_NONE);
            category.setText(transition_string);
        }else if (Variables.flag_new_product == "update_product"){
            category.setFocusable(false);
            category.setFocusableInTouchMode(false);
            category.dismissDropDown();
            category_layout.setEndIconMode(END_ICON_NONE);
            barcode_layout.setEndIconMode(END_ICON_NONE);
            barcode.setEnabled(false);
        }else if (Variables.flag_new_product == "raspberry_pi"){
            barcode_layout.setEndIconMode(END_ICON_NONE);
            barcode.setEnabled(false);
        }





        //-------------Request Service------------//
        if (Variables.flag_new_product != "category" && Variables.flag_new_product != "update_product"){
            JSONObject data_category = new JSONObject();
            try {
                data_category.put("name", "");
                data_category.put("categoriesFather", "false");
                data_category.put("organized","true");
                type_of_request = "get_all_subcategories";
                getCategoryByName(data_category);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        getAllBrands();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (name.getText().toString().equals("")){
                        error_name.setVisibility(View.VISIBLE);
                        error_name.setText("Enter Name");
                        name_layout.setErrorEnabled(true);
                        name_layout.setError("Enter Name");
                    }else error_name.setVisibility(View.INVISIBLE);

                    if (brand.getText().toString().equals("")) {
                        error_brand.setVisibility(View.VISIBLE);
                        error_brand.setText("Enter Brand");
                    }else error_brand.setVisibility(View.INVISIBLE);

                    if (category.getText().toString().equals("")){
                        error_category.setVisibility(View.VISIBLE);
                        error_category.setText("Enter Category");
                        category_layout.setActivated(true);
                    }else error_category.setVisibility(View.INVISIBLE);
                    if (!name.getText().toString().equals("") && !brand.getText().toString().equals("") && !category.getText().toString().equals("")) {
                        service();
                        JSONObject data = new JSONObject();
                        data.put("name", category.getText().toString());
                        data.put("categoriesFather", "false");
                        data.put("organized","false");
                        type_of_request = "get_category";
                        getCategoryByName(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onResume(){
        super.onResume();
        service = new Service(this.getApplicationContext(), this);
        if (Variables.flag_new_product == "barcode"){
            barcode.setText(transition_string);
            barcode_layout.setEndIconMode(END_ICON_NONE);
            barcode.setEnabled(false);
            category.setEnabled(true);
            category_layout.setEndIconMode(TextInputLayout.END_ICON_DROPDOWN_MENU);
        }else if (Variables.flag_new_product == "category"){
            category.setFocusable(false);
            category.setFocusableInTouchMode(false);
            category.dismissDropDown();
            category_layout.setEndIconMode(END_ICON_NONE);
            category.setText(transition_string);
            barcode.setEnabled(true);
            barcode_layout.setEndIconMode(END_ICON_CLEAR_TEXT);
        }else if (Variables.flag_new_product == "update_product"){
            category.setFocusable(false);
            category.setFocusableInTouchMode(false);
            category.dismissDropDown();
            category_layout.setEndIconMode(END_ICON_NONE);
            barcode_layout.setEndIconMode(END_ICON_NONE);
            barcode.setEnabled(false);
        }



    }

    @Override
    public void onDismiss() {
        service();
        if (Variables.flag_create_new_category){
            try {
                JSONObject data = new JSONObject();
                data.put("name", category.getText().toString());
                data.put("categoriesFather", "false");
                data.put("organized","false");
                type_of_request = "get_category";
                getCategoryByName(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //------------------------Service-------------------------//
    private void service() {
        service = new Service(this.getApplicationContext(), this);
    }
    //-----------------Request DataBase--------------------//
    private void getCategoryByName(JSONObject data){
        service.getCategoryByName(data);
    }

    private void getAllBrands(){
        service.getAllBrands();
    }

    private void getBrandByName(String name){
        service.getBrandByName(name);
    }

    private void createProduct(JSONObject data){
        service.createProduct(data);
    }

    private void getProductById(int id){
        service.getProductByID(id);
    }

    private void updateProduct(int id, JSONObject data){
        service.updateProduct(id, data);
    }

    @Override
    public void onBackPressed() {
        Variables.flag_new_product = "";
        super.onBackPressed();
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type){
            case GET_CATEGORY_BY_NAME:
                if (type_of_request == "get_all_subcategories"){
                    try {
                        JSONArray jsonObject = data.getJSONArray("data");
                        String[] list_name_son = new String[jsonObject.length()];
                        for (int i=0; i<jsonObject.length();i++) {
                            list_name_son[i] = jsonObject.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                R.layout.row_simple_dropdown,
                                list_name_son
                        );
                        category.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else if(type_of_request == "get_category"){
                    try {
                        if (data.getJSONArray("data").length() == 0){
                            Variables.flag_category = "create_category_with_name";
                            FragmentCreateCategory fragment = new FragmentCreateCategory();
                            Bundle bundle = new Bundle();
                            bundle.putString("categoryName", category.getText().toString());
                            fragment.setArguments(bundle);
                            fragment.show(getSupportFragmentManager(),"MyCreateCategoryFragment");
                        }else{
                            service();
                            JSONArray jsonObject = data.getJSONArray("data");
                            JSONObject category = jsonObject.getJSONObject(0);
                            id_category= category.getInt("id");
                            getBrandByName(brand.getText().toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GET_ALL_BRANDS:
                try {
                    JSONArray jsonObject = data.getJSONArray("data");
                    String[] list_brand = new String[jsonObject.length()];
                    for (int i=0; i<jsonObject.length();i++) {
                        list_brand[i] = jsonObject.getJSONObject(i).getString("name");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.row_simple_dropdown,
                            list_brand
                    );
                    brand.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_BRAND_BY_NAME:
                try {
                    JSONObject product = new JSONObject();
                    JSONObject brand_details = new JSONObject();
                    JSONObject product_details = new JSONObject();

                    if (Variables.flag_new_product == "update_product" || Variables.flag_new_product == "raspberry_pi"){
                        product.put("name", name.getText().toString());
                        product.put("barcode", barcode.getText().toString());
                        product.put("categoryId", id_category);
                    }else{
                        product_details.put("name", name.getText().toString());
                        product_details.put("barcode", barcode.getText().toString());
                        product.put("product",product_details);
                        product.put("categoryId", id_category);
                    }

                    String error = "error";
                    if (data.getString("data").equals(error)){
                        brand_details.put("name", brand.getText().toString());
                        product.put("brand", brand_details);
                    }else{
                        JSONObject jsonObject = data.getJSONObject("data");
                        id_brand = jsonObject.getInt("id");
                        product.put("brandId", id_brand);
                    }
                    if (Variables.flag_new_product == "update_product" || Variables.flag_new_product == "raspberry_pi"){
                        updateProduct(Integer.parseInt(id_product),product);
                    }else {
                        createProduct(product);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CREATE_PRODUCT:
                if (Variables.flag_scan){
                    Variables.flag_barcode_knowed = true;
                }
                try {
                    if (data.getString("data").equals(error)) Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else{
                        if (Variables.flag_new_product == "new_product") Variables.id_new_product = data.getJSONObject("data").getInt("id");
                        Toast.makeText(this, "Product added successfully!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                FragmentProductsFromCategory.flag_keep_number_of_products = true;
                finish();
                break;

            case GET_PRODUCT_BY_ID:
                try {
                    if (Variables.flag_new_product == "raspberry_pi"){
                        JSONObject jsonObject = data.getJSONObject("data");
                        barcode.setText(jsonObject.getString("barcode"));
                    }else{
                        JSONObject jsonObject = data.getJSONObject("data");
                        JSONObject brand_details = jsonObject.getJSONObject("brand");
                        JSONObject category_details = jsonObject.getJSONObject("category");
                        name.setText(jsonObject.getString("name"));
                        barcode.setText(jsonObject.getString("barcode"));
                        brand.setText(brand_details.getString("name"));
                        category.setText(category_details.getString("name"));
                        title.setText(jsonObject.getString("name"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case UPDATE_PRODUCT:
                try {
                    if (data.getString("data").equals(error)) Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(this, "Product Updated successfully!", Toast.LENGTH_SHORT).show();
                        if (Variables.flag_new_product == "update_product"){
                            FragmentProductsFromCategory.flag_keep_number_of_products = true;
                        }else if(Variables.flag_new_product == "raspberry_pi"){
                            Variables.id_knowned = data.getJSONObject("data").getInt("id");
                            Variables.knowned = true;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                finish();
                break;
        }
    }
}
