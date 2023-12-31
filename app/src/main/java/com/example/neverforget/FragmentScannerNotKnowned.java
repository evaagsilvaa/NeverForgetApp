package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class FragmentScannerNotKnowned extends AppCompatDialogFragment{

    private AlertDialog.Builder builder;
    private TextView barcode;
    private ExtendedFloatingActionButton button;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_scan_not_knowed_product, null);
        builder.setView(view);

        //------------------Inicializações-----------------//
        barcode = view.findViewById(R.id.barcode_product_barcode_not_knowned);
        button = view.findViewById(R.id.button_product_barcode_not_knowned);

        //-------------------Get Arguments-----------------//
        Bundle bundle=getArguments();
        barcode.setText(bundle.getString("barcode"));

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(getActivity(), ProductNewActivity.class);
            Variables.flag_new_product = "barcode";
            intent.putExtra("barcode", barcode.getText().toString());
            startActivity(intent);
            }
        });

        return builder.create();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
