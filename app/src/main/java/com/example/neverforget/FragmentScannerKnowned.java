package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentScannerKnowned extends AppCompatDialogFragment implements Callback{

    private AlertDialog.Builder builder;
    private TextView name, brand, barcode, category, total_itens,add, remove;
    private ExtendedFloatingActionButton add_to_cart;
    private int total=0, product_id;
    private Service service;
    private CallBackListener callBackListener;

    private boolean flag_updated = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_scan_knowed_product, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        name = view.findViewById(R.id.barcode_product_name);
        brand = view.findViewById(R.id.barcode_product_brand);
        barcode = view.findViewById(R.id.barcode_product_barcode);
        category = view.findViewById(R.id.barcode_product_category);
        add = view.findViewById(R.id.add_product);
        remove = view.findViewById(R.id.remove_product);
        total_itens = view.findViewById(R.id.total_itens_);
        add_to_cart = view.findViewById(R.id.button_add_product);
        total_itens.setText("Total: 0");

        //-------------Request Get Products--------------//
        service = new Service(builder.getContext(), this);
        Bundle bundle=getArguments();
        product_id = bundle.getInt("id");
        name.setText(bundle.getString("name"));
        brand.setText(bundle.getString("brand"));
        barcode.setText(bundle.getString("barcode"));
        category.setText(bundle.getString("category"));

        total=0;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            total=total+1;
            total_itens.setText("Total: " + String.valueOf(total));
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (total != 0){
                total=total-1;
                total_itens.setText("Total: " + total);
            }
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShoppingListByID(Variables.shoppingListID_actual);
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof CallBackListener)
            callBackListener = (CallBackListener) getActivity();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(callBackListener != null)
            callBackListener.onDismiss();
        dismiss();
    }

    //----------------------onResume-------------------------//
    @Override
    public void onResume() {
        super.onResume();
        service = new Service(builder.getContext(), this);
    }

    //-----------------Request DataBase--------------------//
    private void addItemToShoppingList(int id, JSONObject data){    //https://www.youtube.com/watch?v=rcSNkSJ624U //https://www.youtube.com/watch?v=M1XEqqo6Ktg
        service.addItemToShoppingList(id,data);
    }

    private void updateShoppingListItemQuantity(int id, int idItem, JSONObject data){
        service.updateShoppingListItemQuantity(id, idItem, data);
    }

    private void getShoppingListByID(int id){
        service.getShoppingListByID(id);
    }

    //---------------------Callback-----------------------//
    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type){
            case GET_SHOPPINGLIST_BY_ID:
                try {
                    JSONObject shoppingList_json = data.getJSONObject("data");
                    JSONArray shoppingListItems_json = shoppingList_json.getJSONArray("shoppingListItems");
                    if (shoppingListItems_json.length() != 0){
                        for (int i=0; i<shoppingListItems_json.length(); i++) {
                            if (shoppingListItems_json.getJSONObject(i).isNull("category")) {
                                if (shoppingListItems_json.getJSONObject(i).getJSONObject("product").getInt("id") == product_id){
                                    int idItem = shoppingListItems_json.getJSONObject(i).getInt("id");
                                    JSONObject send_data_update = new JSONObject();
                                    send_data_update.put("quantity", total);
                                    flag_updated = true;
                                    updateShoppingListItemQuantity(Variables.shoppingListID_actual,idItem,send_data_update);
                                }
                            }
                        }

                    }
                    if (!flag_updated){
                        JSONObject send_data_add = new JSONObject();
                        send_data_add.put("productId", product_id);
                        send_data_add.put("quantity", total);
                        addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                    }else{
                        flag_updated = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            case ADD_ITEM_TO_SHOPPINGLIST:
            case UPDATE_SHOPPINGLIST_ITEM_QUANTITY:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)){
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    } else{
                        Variables.flag_update_shopping_list_fragment = true;
                        Toast.makeText(getContext(), "Added to list successfully!", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
        }
    }
}
