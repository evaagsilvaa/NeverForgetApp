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

public class FragmentAddProductCategory extends AppCompatDialogFragment implements Callback {

    private TextView  quantityAddItems, addButton, removeButton,error_shoppinglist;
    private Button buttonAdd, buttonCancel;
    private AutoCompleteTextView shoppinglist;
    private AlertDialog.Builder builder;
    private TextInputLayout shoppinglist_layout;

    private int id;
    private String name;
    private int numberToAdd = 1;

    private int id_list[], id_selected;
    private String list_name[];

    private Service service;
    private CallBackListener callBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_product_category, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        quantityAddItems = view.findViewById(R.id.number_add_items_add_product_category);
        buttonAdd = view.findViewById(R.id.button_add_to_list_add_product_category);
        buttonCancel = view.findViewById(R.id.button_cancel_add_product_category);
        addButton = view.findViewById(R.id.add_item_add_product_category);
        removeButton = view.findViewById(R.id.remove_item_add_product_category);
        shoppinglist = view.findViewById(R.id.exposed_dropdown_name_list_add_product_category);
        error_shoppinglist = view.findViewById(R.id.errorshoppinglist_);
        shoppinglist_layout = view.findViewById(R.id.name_list_change_list_layout);

        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        getAllShoppingLists();

        //-----------Get Values From MainActivity--------//
        Bundle bundle=getArguments();
        id = bundle.getInt("id");

        numberToAdd = 1;
        quantityAddItems.setText(String.valueOf(numberToAdd));

        shoppinglist.setFocusable(false);
        shoppinglist.setFocusableInTouchMode(false);
        shoppinglist.dismissDropDown();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberToAdd++;
                quantityAddItems.setText(String.valueOf(numberToAdd));

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


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (shoppinglist.getText().toString().isEmpty()){
                    error_shoppinglist.setVisibility(View.VISIBLE);
                    shoppinglist_layout.setActivated(true);
                }else{
                    error_shoppinglist.setVisibility(View.INVISIBLE);
                    try {
                        service();
                        JSONObject add = new JSONObject();
                        add.put("productId", id);
                        add.put("quantity", numberToAdd);
                        addItemToShoppingList(id_selected, add);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
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

    private void service() {
        service = new Service(builder.getContext(), this);
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

    private void addItemToShoppingList(int id, JSONObject data){
        service.addItemToShoppingList(id, data);
    }

    private void getProductByID(int id){
        service.getProductByID(id);
    }

    private void getAllShoppingLists(){
        service.getAllShoppingLists();
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
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
                    getProductByID(id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_PRODUCT_BY_ID:
                try {
                    JSONObject jsonObject = data.getJSONObject("data");
                    name = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case ADD_ITEM_TO_SHOPPINGLIST:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)) {
                        Toast.makeText(builder.getContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(builder.getContext(), numberToAdd + " items of " + name + " was add successfully!", Toast.LENGTH_SHORT).show();
                        Variables.flag_update_shopping_list_fragment = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
                break;
        }
    }
}
