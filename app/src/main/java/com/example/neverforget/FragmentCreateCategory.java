package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FragmentCreateCategory extends AppCompatDialogFragment implements Callback {

    private TextInputEditText name;
    private AutoCompleteTextView category_father;
    private Button button;
    private Service service;
    private TextView error_name, error_category;
    private TextInputLayout name_layout, category_layout;

    private String[] list;
    private int[] id_category_Father;
    private AlertDialog.Builder builder;
    private CallBackListener callBackListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_create_category, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        name = view.findViewById(R.id.create_category_name_fragment);
        category_father = view.findViewById(R.id.create_category_father_fragment);
        button = view.findViewById(R.id.button_create_category_fragment);
        name_layout = view.findViewById(R.id.name_new_category_layout);
        category_layout = view.findViewById(R.id.category_father_new_category_layout);

        error_name = view.findViewById(R.id.errorcategorycreate);
        error_category = view.findViewById(R.id.errorcategoryfathercreate);

        //-----------Get Values From MainActivity--------//
        if(Variables.flag_category == "create_category_with_name"){
            Bundle bundle=getArguments();
            name.setText(bundle.getString("categoryName"));
        }


        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        //-------------Request Service------------//
        JSONObject data = new JSONObject();
        try {
            data.put("name", "");
            data.put("categoriesFather", "true");
            data.put("organized","false");
            getCategoryByName(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        category_father.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int inType = category_father.getInputType(); // backup the input type
                category_father.setInputType(InputType.TYPE_NULL); // disable soft input
                category_father.onTouchEvent(event); // call native handler
                category_father.setInputType(inType); // restore input type

                return true; // consume touch even
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (name.getText().toString().equals("")){
                        error_name.setVisibility(View.VISIBLE);
                        error_name.setText("Enter Name");
                        name_layout.setErrorEnabled(true);
                        name_layout.setError("Enter Name");
                    }else error_name.setVisibility(View.INVISIBLE);

                    if (category_father.getText().toString().equals("")){
                        error_category.setVisibility(View.VISIBLE);
                        error_category.setText("Enter Category");
                        category_layout.setErrorEnabled(true);
                        category_layout.setError("Enter Category");
                    }else error_category.setVisibility(View.INVISIBLE);

                    if (!name.getText().toString().equals("") && !category_father.getText().toString().equals("")) {
                        for (int i=0; i<list.length; i++){
                            String name_ = list[i];
                            if (category_father.getText().toString().equals(name_)){
                                JSONObject data = new JSONObject();
                                data.put("name", name.getText().toString());
                                data.put("categoryId", id_category_Father[i]);
                                createCategory(data);
                                break;
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
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


    //----------------------onResume-------------------------//
    @Override
    public void onResume() {
        super.onResume();
        service = new Service(builder.getContext(), this);
    }

    //-----------------Request DataBase--------------------//
    private void getCategoryByName(JSONObject data){
        service.getCategoryByName(data);
    }

    private void createCategory(JSONObject data){
        service.createCategory(data);
    }


    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type){
            case GET_CATEGORY_BY_NAME:
                try {
                    JSONArray jsonObject = data.getJSONArray("data");
                    id_category_Father = new int[jsonObject.length()];
                    list = new String[jsonObject.length()];
                    for (int i=0; i<jsonObject.length();i++) {
                        id_category_Father[i] = jsonObject.getJSONObject(i).getInt("id");
                        list[i] = jsonObject.getJSONObject(i).getString("name");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            builder.getContext(),
                            R.layout.row_simple_dropdown,
                            list);
                    category_father.setAdapter(adapter);
                    break;

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case CREATE_CATEGORY:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)) Toast.makeText(builder.getContext(), "Something wrong!", Toast.LENGTH_SHORT).show();
                    else{
                        Variables.flag_create_new_category = true;
                    }
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
