package com.example.neverforget;

import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CreateCategoryActivity extends AppCompatActivity implements Callback {

    //Inicializações dos objectos nos layouts
    private TextInputEditText name;
    private AutoCompleteTextView category_Parent;
    private Button button_add;
    private Service service;
    private TextView error_name, error_category;
    private TextInputLayout name_layout, category_layout;

    //Variáveis auxiliares
    private String categoryNameExtra, name_category_Father[];
    private int categoryIDExtra, categoryFatherIDExtra, id_category_Father[];
    private String type_of_request;
    private String error = "error";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        //------------------Inicializações-----------------//
        name = findViewById(R.id.create_category_name);
        name_layout = findViewById(R.id.textfield_name_category_create_category);
        category_Parent = findViewById(R.id.create_category_father);
        category_layout = findViewById(R.id.textfield_category_father_category_create_category);
        button_add = findViewById(R.id.button_create_category);
        error_name = findViewById(R.id.errorname_create_category);
        error_category = findViewById(R.id.errorcategoryfather_create_category);
        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);


        //--------------------Get Values------------------//
        if (Variables.flag_category == "edit_category"){
            button_add.setText("Update Category");
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    categoryIDExtra = Integer.parseInt(null);
                    categoryNameExtra = null;
                    categoryFatherIDExtra = Integer.parseInt(null);
                } else {
                    categoryIDExtra = extras.getInt("categoryID");
                    categoryNameExtra = extras.getString("categoryName");
                    categoryFatherIDExtra = extras.getInt("categoryFatherID");
                }
            } else {
                categoryIDExtra= (int) savedInstanceState.getSerializable("categoryID");
                categoryNameExtra= (String) savedInstanceState.getSerializable("name");
                categoryFatherIDExtra= (int) savedInstanceState.getSerializable("categoryFatherID");
            }
            name.setText(categoryNameExtra);

            //---------------Disable Exposed Dropdown-----------//
            category_Parent.setInputType(InputType.TYPE_NULL);

            //--------------Get Category Name from ID------------//
            JSONObject data = new JSONObject();
            try {
                data.put("id", categoryFatherIDExtra);
                data.put("subCategories", "false");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCategoryByID(data);
        }else if(Variables.flag_category == "new_category") {
            //-------------Request Service------------//
            JSONObject data = new JSONObject();
            try {
                data.put("name", "");
                data.put("categoriesFather", "true");
                data.put("organized", "false");
                type_of_request = "get_category_father";
                getCategoryByName(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        button_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    error_name.setVisibility(View.VISIBLE);
                    error_name.setText("Enter Name");
                    name_layout.setErrorEnabled(true);
                    name_layout.setError("Enter Name");
                }else error_name.setVisibility(View.INVISIBLE);

                if (category_Parent.getText().toString().equals("")){
                    error_category.setVisibility(View.VISIBLE);
                    error_category.setText("Enter Category");
                    category_layout.setActivated(true);
                }else error_category.setVisibility(View.INVISIBLE);

                if (!name.getText().toString().equals("") && !category_Parent.getText().toString().equals("")){
                    if (Variables.flag_category == "edit_category"){
                        try {
                            JSONObject data = new JSONObject();
                            data.put("name", name.getText().toString());
                            updateCategory(categoryIDExtra, data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }else if (Variables.flag_category == "new_category"){
                        try {
                            JSONObject data = new JSONObject();
                            data.put("name", name.getText().toString());
                            for (int i=0; i<name_category_Father.length; i++){
                                String name = name_category_Father[i];
                                if (category_Parent.getText().toString().equals(name)){
                                    data.put("categoryId", id_category_Father[i]);
                                    break;
                                }
                            }
                            createCategory(data);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        category_Parent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = category_Parent.getInputType(); // backup the input type
                category_Parent.setInputType(InputType.TYPE_NULL); // disable soft input
                category_Parent.onTouchEvent(event); // call native handler
                category_Parent.setInputType(inType); // restore input type

                return true; // consume touch even
            }
        });

    }


    //----------------------onResume-------------------------//
    @Override
    public void onResume(){
        super.onResume();
        service = new Service(this.getApplicationContext(), this);
    }




    //-----------------Request DataBase--------------------//
    private void getCategoryByName(JSONObject data){
        service.getCategoryByName(data);
    }

    private void getCategoryByID(JSONObject data){
        service.getCategoryByID(data);
    }

    private void createCategory(JSONObject data){
        service.createCategory(data);
    }

    private void updateCategory(int id,JSONObject data){
        service.updateCategory(id,data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_CATEGORY_BY_ID:
                try {
                    JSONObject jsonObject = data.getJSONObject("data");
                    category_Parent.setText(jsonObject.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_CATEGORY_BY_NAME:
                if (type_of_request.equals("validate_name")) {
                /*
                try {
                    JSONArray jsonObject = (JSONArray) data.getJSONArray("data");
                    if (jsonObject.getJSONObject(0).getString("name").equals(name.getText().toString().trim())){
                        name.setError("Categoria já existe");
                        requestFocus(name);
                        validate_category_name = false;
                    }
                    validate_finish = true;
                    break;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

                 */
                } else if (type_of_request.equals("get_category_father")) {
                    try {
                        JSONArray jsonObject = data.getJSONArray("data");
                        id_category_Father = new int[jsonObject.length()];
                        name_category_Father = new String[jsonObject.length()];
                        for (int i = 0; i < jsonObject.length(); i++) {
                            id_category_Father[i] = jsonObject.getJSONObject(i).getInt("id");
                            name_category_Father[i] = jsonObject.getJSONObject(i).getString("name");
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                R.layout.row_simple_dropdown,
                                name_category_Father
                        );
                        category_Parent.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CREATE_CATEGORY:
                try {
                    if (data.getString("data").equals(error)) Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this, "Category Added successfully!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case UPDATE_CATEGORY:
                try {
                    if (data.getString("data").equals(error)){
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }else{
                        FragmentProductsFromCategory.flag_update_category = true;
                        FragmentProductsFromCategory.name_of_category = data.getJSONObject("data").getString("name");
                        Toast.makeText(this, "Category Updated successfully!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                finish();
                break;
        }

    }
}
