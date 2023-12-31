package com.example.neverforget;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;

import org.json.JSONException;
import org.json.JSONObject;

public class WelcomeActivity extends AppCompatActivity implements Callback {

    private Service service;
    private boolean flag_exist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        SharedPreferences prefs_ = getSharedPreferences("prefs", MODE_PRIVATE);
        Variables.shoppingListID_actual = prefs_.getInt("shoppingList_ID", 0);
        Variables.shoppingListName = prefs_.getString("shoppingList_NAME", Variables.shoppingListName);
        Variables.flag_create_list = false;
        Variables.flag_no_list = false;
        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getAllShoppingLists();
            }
        }, 1000);
    }

    private void getAllShoppingLists(){
        service.getAllShoppingLists();
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_SHOPPINGLISTS:
                try {
                    if(data.getString("data").equals("error")){
                        service = new Service(this.getApplicationContext(), this);
                        getAllShoppingLists();
                    }else if (data.getJSONArray("data").length() == 0) {
                        Intent i = new Intent(WelcomeActivity.this, CreateListActivity.class);
                        startActivity(i);
                        finish();
                    }else{
                        for (int i = 0; i<data.getJSONArray("data").length(); i++){
                            if (Variables.shoppingListID_actual == data.getJSONArray("data").getJSONObject(i).getInt("id")){
                                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                flag_exist = true;
                            }
                        }
                        if (!flag_exist){
                            Variables.flag_no_list = true;
                            Variables.shoppingListID_actual = data.getJSONArray("data").getJSONObject(0).getInt("id");
                            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}