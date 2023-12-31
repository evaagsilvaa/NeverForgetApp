package com.example.neverforget;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.adapters.MyToAddList;
import com.example.neverforget.adapters.MyToAddListKnowed;
import com.example.neverforget.adapters.MyToAddListNotKnowed;
import com.example.neverforget.adapters.ToAddListCallback;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;


public class AddProductToListActivity extends AppCompatActivity implements Callback, ToAddListCallback, CallBackListener {

    private ListView toAddList;
    private ArrayList<Object> toAddArrayList;
    private Service service;
    private int id_toaddlist_knowned;
    private int numbernotificationCounter;

    public static boolean addProductToListActivity, update_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_shoppinglist);

        //------------Inicializações-------------//
        toAddList = findViewById(R.id.toAddList);

        toAddArrayList = new ArrayList<>();
        addProductToListActivity = true;
        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);
        getAllItemsToAddList();
        mSocket.on("raspberrypi", onNewMessage);
        mSocket.connect();
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            AddProductToListActivity .this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getAllItemsToAddList();
                }
            });
        }
    };
    private Socket mSocket;{
        try {
            mSocket = IO.socket("http://never-forget.duckdns.org/events");
        } catch (URISyntaxException e) {}
    }

    @Override
    public void onItemListNotKnownedClick(int id, int id_product, String barcode, int quantity) {
        Variables.knowned = false;
        FragmentNotificafionNotKnowned fragment = new FragmentNotificafionNotKnowned();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("id_product", id_product);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "MyFragmentNotificafionNotKnowned");
    }

    @Override
    public void onItemListKnownedClick(int id,int id_product) {
        FragmentNotificafionKnowned fragment = new FragmentNotificafionKnowned();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putInt("id_product", id_product);
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "MyFragmentNotificafionKnowned");
    }

    @Override
    protected void onResume() {
        super.onResume();
        addProductToListActivity = true;
        if (Variables.knowned){
            service = new Service(this.getApplicationContext(), this);
            getAllItemsToAddList();
        }
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(1);

    }

    @Override
    public void onDismiss() {
        if (Variables.removed){
            service = new Service(this.getApplicationContext(), this);
            getAllItemsToAddList();
            Variables.removed = false;
        }
    }

    private void getAllItemsToAddList(){
        service.getAllItemsToAddList();
    }

    @Override
    public void onBackPressed() {
        addProductToListActivity = false;
        super.onBackPressed();
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_ALL_ITEMS_TO_ADD_LIST:
                try {
                    toAddArrayList.clear();
                    JSONArray items = data.getJSONArray("data");
                    ArrayList<Object> items_not_knowed = new ArrayList<>();
                    ArrayList<Object> items_knowed = new ArrayList<>();
                    numbernotificationCounter = items.length();
                    if (items.length() == 0){
                        finish();
                    }else{
                        for (int i = 0; i < items.length(); i++){
                            JSONObject item = items.getJSONObject(i);
                            if (item.getJSONObject("product").isNull("name")){
                                items_not_knowed.add(new MyToAddListNotKnowed(item.getInt("id"), item.getInt("productId"),item.getJSONObject("product").getString("barcode"), item.getInt("quantity")));
                            }else{
                                if (Variables.knowned) {
                                    if (Variables.id_knowned == item.getJSONObject("product").getInt("id")){
                                        id_toaddlist_knowned = item.getInt("id");
                                    }
                                }
                                items_knowed.add(new MyToAddListKnowed(item.getInt("id"), item.getInt("productId"), item.getJSONObject("product").getString("name"),item.getJSONObject("product").getJSONObject("brand").getString("name"), item.getInt("quantity")));
                            }
                        }
                        if (items_not_knowed.size() != 0) {
                            toAddArrayList.add("New Products");
                            for (int k = 0; k < items_not_knowed.size(); k++) {
                                toAddArrayList.add(items_not_knowed.get(k));
                            }
                        }
                        if (items_knowed.size() != 0) {
                            toAddArrayList.add("Others");
                            for (int k = 0; k < items_knowed.size(); k++) {
                                toAddArrayList.add(items_knowed.get(k));
                            }
                        }

                        toAddList.setAdapter(new MyToAddList(this, toAddArrayList, this));
                        if (Variables.knowned){
                            Variables.knowned = false;
                            FragmentNotificafionKnowned fragment = new FragmentNotificafionKnowned();
                            Bundle bundle = new Bundle();
                            bundle.putInt("id", id_toaddlist_knowned);
                            bundle.putInt("id_product", Variables.id_knowned);
                            fragment.setArguments(bundle);
                            fragment.show(getSupportFragmentManager(), "MyFragmentNotificafionKnowned");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
