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
import androidx.fragment.app.DialogFragment;

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

public class FragmentList extends DialogFragment implements Callback {


    private TextInputEditText name;
    private ExtendedFloatingActionButton button;
    private TextView error, title;
    private TextInputLayout error_layout;
    private AlertDialog.Builder builder;
    private Service service;

    private boolean flag_already_exists = false;
    private CallBackListener callBackListener;
    private String error_service = "error";
    private int id_update;

    public static int id_list_just_created;
    public static boolean flag_list_just_created = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_add_list, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        name = view.findViewById(R.id.create_list_name);
        button = view.findViewById(R.id.button_create_list);
        error = view.findViewById(R.id.errorname_list);
        error_layout = view.findViewById(R.id.name_new_list_layout);
        title = view.findViewById(R.id.title_fragment_list);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id_update = bundle.getInt("listID");
            name.setText(bundle.getString("listName"));
            title.setText("Update List");
            button.setText("Update List");
            ChangeShoppingListActivity.flag_edit = true;
        }else{
            title.setText("New List");
            button.setText("Create List");
        }
        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    error.setVisibility(View.VISIBLE);
                    error.setText("Enter Name");
                    error_layout.setErrorEnabled(true);
                    error_layout.setError("Enter Name");
                }else {
                    getAllShoppingLists();
                }
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

    private void createShoppingList(JSONObject data){
        service.createShoppingList(data);
    }

    private void updateShoppingListName(int id, JSONObject data){
        service.updateShoppingListName(id, data);
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
                    String name_of_list = name.getText().toString();
                    if (!ChangeShoppingListActivity.flag_edit){
                        for (int i=0; i<jsonObject.length();i++) {
                            if (name_of_list.equals(jsonObject.getJSONObject(i).getString("name"))){
                                flag_already_exists = true;
                            }
                        }
                        if (flag_already_exists){
                            error.setVisibility(View.VISIBLE);
                            error.setText("Enter another name");
                            error_layout.setErrorEnabled(true);
                            error_layout.setError("Enter another name");
                            Toast.makeText(builder.getContext(), "List already exists!", Toast.LENGTH_SHORT).show();
                            flag_already_exists = false;
                        }else{
                            error.setVisibility(View.INVISIBLE);
                            JSONObject list = new JSONObject();
                            list.put("name",name.getText().toString());
                            createShoppingList(list);
                        }
                    }else{
                        error.setVisibility(View.INVISIBLE);
                        JSONObject list = new JSONObject();
                        list.put("name",name.getText().toString());
                        updateShoppingListName(id_update, list);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CREATE_SHOPPINGLIST:
                try {
                    if (data.getString("data").equals(error_service)) Toast.makeText(builder.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else {
                        Toast.makeText(builder.getContext(), "List added successfully!", Toast.LENGTH_SHORT).show();
                        id_list_just_created = data.getJSONObject("data").getInt("id");
                        flag_list_just_created = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
            case UPDATE_SHOPPINGLISTNAME:
                try {
                    if (data.getString("data").equals(error_service)) Toast.makeText(builder.getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(builder.getContext(), "List updated successfully!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(callBackListener != null)
                    callBackListener.onDismiss();
                dismiss();
                break;
        }

    }
}
