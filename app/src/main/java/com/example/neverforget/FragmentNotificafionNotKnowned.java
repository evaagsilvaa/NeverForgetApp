package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
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

import org.json.JSONException;
import org.json.JSONObject;

public class FragmentNotificafionNotKnowned extends AppCompatDialogFragment implements Callback {

    private Button buttonAdd, buttonRemove;
    private AlertDialog.Builder builder;

    private int id, id_product;

    private Service service;
    private CallBackListener callBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_notification_not_knowned, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        buttonAdd = view.findViewById(R.id.button_add_new_product_notification);
        buttonRemove = view.findViewById(R.id.button_remove_new_product_notification);

        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        //-----------Get Values From MainActivity--------//
        Bundle bundle = getArguments();
        id = bundle.getInt("id");
        id_product = bundle.getInt("id_product");

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(builder.getContext(), ProductNewActivity.class);
                i.putExtra("id", String.valueOf(id_product));
                Variables.flag_new_product = "raspberry_pi";
                startActivity(i);
            }
        });

        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteItemToAddList(id);
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

    @Override
    public void onResume() {
        super.onResume();
        if (Variables.knowned){
            if(callBackListener != null)
                callBackListener.onDismiss();
            dismiss();
        }
    }

    private void deleteItemToAddList(int id){
        service.deleteItemToAddList(id);
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case DELETE_ITEM_TO_ADD_LIST:
                Variables.removed = true;
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
            }

    }
}
