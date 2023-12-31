package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;

import org.json.JSONException;
import org.json.JSONObject;
import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.services.Service;

public class FragmentBuyItem extends AppCompatDialogFragment implements Callback {

    private TextView  quantityBuyItems, addButton, removeButton;
    private Button buttonBuyX, buttonRemove;
    private AlertDialog.Builder builder;

    private int id, quantity;
    private String name;
    private int numberToBuy;

    private Service service;
    private CallBackListener callBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_buy_item, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        quantityBuyItems = view.findViewById(R.id.number_buy_items);
        buttonBuyX = view.findViewById(R.id.button_buy);
        buttonRemove = view.findViewById(R.id.button_remove_item_from_shopping_list);
        addButton = view.findViewById(R.id.add_buy_item);
        removeButton = view.findViewById(R.id.remove_buy_item);
        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        //-----------Get Values From MainActivity--------//
        Bundle bundle=getArguments();
        id = bundle.getInt("id");
        name = bundle.getString("name");
        quantity = bundle.getInt("quantity");

        numberToBuy = quantity;
        quantityBuyItems.setText(String.valueOf(numberToBuy));

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberToBuy < quantity){
                    numberToBuy++;
                    quantityBuyItems.setText(String.valueOf(numberToBuy));
                }
            }
        });

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numberToBuy > 1){
                    numberToBuy--;
                    quantityBuyItems.setText(String.valueOf(numberToBuy));
                }
            }
        });

        addButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberToBuy = quantity;
                quantityBuyItems.setText(String.valueOf(numberToBuy));
                return true;
            }
        });

        removeButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                numberToBuy = 1;
                quantityBuyItems.setText(String.valueOf(numberToBuy));
                return true;
            }
        });

        buttonBuyX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject data = new JSONObject();
                    data.put("quantity", -numberToBuy);
                    updateShoppingListItemQuantity(Variables.shoppingListID_actual, id, data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemFromShoppingList(Variables.shoppingListID_actual, id);
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
        Variables.flag_update_shopping_list_fragment = true;
        if(callBackListener != null)
            callBackListener.onDismiss();
        dismiss();
    }

    private void updateShoppingListItemQuantity(int id, int idItem, JSONObject data){
        service.updateShoppingListItemQuantity(id, idItem, data);
    }

    private void deleteItemFromShoppingList(int id, int idItem){
        service.deleteItemFromShoppingList(id, idItem);
    }


    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case UPDATE_SHOPPINGLIST_ITEM_QUANTITY:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)) {
                        Toast.makeText(builder.getContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                    } else {
                        Variables.flag_update_shopping_list_fragment = true;
                        Toast.makeText(builder.getContext(), numberToBuy + " items of " + name + " was bought successfully!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dismiss();
                break;
            case DELETE_ITEM_SHOPPING_LIST:
                Variables.flag_update_shopping_list_fragment = true;
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
        }


    }
}
