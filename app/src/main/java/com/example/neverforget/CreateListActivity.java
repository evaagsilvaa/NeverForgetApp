package com.example.neverforget;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateListActivity extends AppCompatActivity implements Callback {

    private Service service;
    private TextInputEditText name;
    private ExtendedFloatingActionButton button;
    private TextView error;
    private TextInputLayout error_layout;

    public static String shoppingListName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_list);

        //------------------Inicializações-----------------//
        name = findViewById(R.id.create_list_name_choose_activity);
        button = findViewById(R.id.button_create_list_choose_fragment);
        error = findViewById(R.id.errorname_list_choose_fragment);
        error_layout = findViewById(R.id.name_new_list_layout_choose_activity);

        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (name.getText().toString().equals("")){
                        error.setVisibility(View.VISIBLE);
                        error.setText("Enter Name");
                        error_layout.setErrorEnabled(true);
                        error_layout.setError("Enter Name");
                    }else {
                        error.setVisibility(View.INVISIBLE);
                        JSONObject list = new JSONObject();
                        list.put("name",name.getText().toString());
                        createShoppingList(list);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private void createShoppingList(JSONObject data){
        service.createShoppingList(data);
    }


    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type){
            case CREATE_SHOPPINGLIST:
                try {
                    if (data.getString("data").equals("error")) Toast.makeText(CreateListActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else{
                        Toast.makeText(CreateListActivity.this, "List added successfully!", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(CreateListActivity.this, MainActivity.class);
                        Variables.flag_create_list = true;
                        Variables.shoppingList_create_list = data.getJSONObject("data").getInt("id");
                        shoppingListName = data.getJSONObject("data").getString("name");
                        startActivity(i);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}