package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentNotificafionKnowned extends AppCompatDialogFragment implements Callback {

    private TextView  quantityAddItems, addButton, removeButton;
    private Button buttonAdd, buttonRemove;
    private AutoCompleteTextView shoppinglist;
    private TextInputLayout shoppinglist_layout;
    private AlertDialog.Builder builder;
    private TextView error_shoppinglist;

    private int id, id_product, quantity;
    private String name;
    private int numberToAdd;

    private int id_list[], id_selected;
    private String list_name[];

    private Service service;
    private CallBackListener callBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_knowned, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        quantityAddItems = view.findViewById(R.id.number_add_items_notification);
        buttonAdd = view.findViewById(R.id.button_add_to_list_notification);
        buttonRemove = view.findViewById(R.id.button_remove_notification);
        addButton = view.findViewById(R.id.add_item_notification);
        removeButton = view.findViewById(R.id.remove_item_notification);
        shoppinglist = view.findViewById(R.id.exposed_dropdown_name_list_notification);
        shoppinglist_layout = view.findViewById(R.id.name_list_change_list_layout);
        error_shoppinglist = view.findViewById(R.id.errorshoppinglist);

        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        getAllShoppingLists();

        //-----------Get Values From MainActivity--------//
        Bundle bundle=getArguments();
        id = bundle.getInt("id");
        id_product = bundle.getInt("id_product");

        getProductByIdToAddList(id_product);

        shoppinglist.setFocusable(false);
        shoppinglist.setFocusableInTouchMode(false);
        shoppinglist.dismissDropDown();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberToAdd < quantity){
                    numberToAdd++;
                    quantityAddItems.setText(String.valueOf(numberToAdd));
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberToAdd > 1){
                    numberToAdd--;
                    quantityAddItems.setText(String.valueOf(numberToAdd));
                }
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberToAdd = quantity;
                quantityAddItems.setText(String.valueOf(numberToAdd));
                return true;
            }
        });

        removeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberToAdd = 1;
                quantityAddItems.setText(String.valueOf(numberToAdd));
                return true;
            }
        });

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shoppinglist.getText().toString().isEmpty()){
                    error_shoppinglist.setVisibility(View.VISIBLE);
                    shoppinglist_layout.setActivated(true);
                }else{
                    error_shoppinglist.setVisibility(View.INVISIBLE);
                    getShoppingListByID(id_selected);
                }
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemToAddList(id);
            }
        });

        shoppinglist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                id_selected = id_list[position];
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

    private void updateShoppingListItemQuantity(int id, int idItem, JSONObject data){
        service.updateShoppingListItemQuantity(id, idItem, data);
    }

    private void getProductByIdToAddList(int id){
        service.getProductByIdToAddList(id);
    }

    private void addItemToShoppingList(int id, JSONObject data){
        service.addItemToShoppingList(id, data);
    }

    private void getAllShoppingLists(){
        service.getAllShoppingLists();
    }


    private void getShoppingListByID(int id){
        service.getShoppingListByID(id);
    }

    private void deleteItemToAddList(int id){
        service.deleteItemToAddList(id);
    }

    private void updateToAddListItemQuantity(int idItem, JSONObject data){
        service.updateToAddListItemQuantity(idItem, data);
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_PRODUCT_BY_ID_TO_ADD_LIST:
                try {
                    name = data.getJSONObject("data").getJSONObject("product").getString("name");
                    quantity = data.getJSONObject("data").getInt("quantity");
                    numberToAdd = quantity;
                    quantityAddItems.setText(String.valueOf(numberToAdd));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_SHOPPINGLISTS:
                try {
                    JSONArray jsonObject = data.getJSONArray("data");
                    list_name = new String[jsonObject.length()];
                    id_list = new int[jsonObject.length()];
                    for (int i = 0; i < jsonObject.length(); i++) {
                        id_list[i] = jsonObject.getJSONObject(i).getInt("id");
                        list_name[i] = jsonObject.getJSONObject(i).getString("name");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            builder.getContext(),
                            R.layout.row_simple_dropdown,
                            list_name
                    );
                    shoppinglist.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_SHOPPINGLIST_BY_ID:
                boolean flag_updated_quantity = false;
                try {
                    JSONObject shoppingList_json = data.getJSONObject("data");
                    JSONArray shoppingListItems_json = shoppingList_json.getJSONArray("shoppingListItems");
                    if (shoppingListItems_json.length() != 0){
                        for (int i = 0; i<shoppingListItems_json.length(); i++){
                            if (shoppingListItems_json.getJSONObject(i).isNull("category")){
                                if (shoppingListItems_json.getJSONObject(i).getJSONObject("product").getInt("id") == id_product){
                                    flag_updated_quantity = true;
                                    JSONObject update = new JSONObject();
                                    update.put("quantity", numberToAdd);
                                    updateShoppingListItemQuantity(id_selected, shoppingListItems_json.getJSONObject(i).getInt("id"), update);
                                }
                            }
                        }
                    }
                    if (!flag_updated_quantity){
                        flag_updated_quantity = false;
                        JSONObject add = new JSONObject();
                        add.put("productId", id_product);
                        add.put("quantity", numberToAdd);
                        addItemToShoppingList(id_selected, add);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ADD_ITEM_TO_SHOPPINGLIST:
            case UPDATE_SHOPPINGLIST_ITEM_QUANTITY:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)) {
                        Toast.makeText(builder.getContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(builder.getContext(), numberToAdd + " items of " + name + " was add successfully!", Toast.LENGTH_SHORT).show();
                        if (numberToAdd < quantity){
                            JSONObject update_quantity = new JSONObject();
                            update_quantity.put("quantity", numberToAdd);
                            updateToAddListItemQuantity(id,update_quantity);
                        }else {
                            deleteItemToAddList(id);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
                break;
            case UPDATE_TOADDLIST_ITEM_QUANTITY:
            case DELETE_ITEM_TO_ADD_LIST:
                Variables.removed = true;
                Variables.flag_update_shopping_list_activity = true;
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
        }
    }
}
