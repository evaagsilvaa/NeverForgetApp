package com.example.neverforget.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.neverforget.R;

import java.util.ArrayList;

public class MyToAddList extends BaseAdapter {
    ArrayList<Object> list;

    private static final int KNOWED_PRODUCTS = 0;
    private static final int NOT_KNOWED_PRODUCTS = 1;
    private static final int HEADER = 2;
    private LayoutInflater inflater;
    ToAddListCallback callback;

    public MyToAddList(Context context, ArrayList<Object> list, ToAddListCallback callback) {
        this.list = list;
        this.callback = callback;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof MyToAddListNotKnowed){
            return NOT_KNOWED_PRODUCTS;
        }else if (list.get(position) instanceof MyToAddListKnowed){
            return KNOWED_PRODUCTS;
        }else{
            return HEADER;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            switch (getItemViewType(position)){
                case NOT_KNOWED_PRODUCTS:
                    convertView = inflater.inflate(R.layout.row_to_add_list_not_knowed, null);
                    break;
                case KNOWED_PRODUCTS:
                    convertView = inflater.inflate(R.layout.row_to_add_list_knowed, null);
                    break;
                case HEADER:
                    convertView = inflater.inflate(R.layout.row_to_add_list_type, null);
                    break;
            }
        }
        switch (getItemViewType(position)){
            case NOT_KNOWED_PRODUCTS:
                TextView barcode_not_knowned = convertView.findViewById(R.id.barcodeToAddListNotKnowed);
                TextView qty_not_knowned = convertView.findViewById(R.id.qtyToAddListNotKnowed);

                barcode_not_knowned.setText(((MyToAddListNotKnowed)list.get(position)).getBarcode());
                qty_not_knowned.setText(String.valueOf(((MyToAddListNotKnowed)list.get(position)).getQuantity()));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onItemListNotKnownedClick(((MyToAddListNotKnowed)list.get(position)).getId(), ((MyToAddListNotKnowed)list.get(position)).getId_product(), ((MyToAddListNotKnowed)list.get(position)).getBarcode(),((MyToAddListNotKnowed)list.get(position)).getQuantity());
                    }
                });

                break;

            case KNOWED_PRODUCTS:
                TextView name_knowned = convertView.findViewById(R.id.nameToAddListKnowed);
                TextView brand_knowned = convertView.findViewById(R.id.brandToAddListKnowed);
                TextView qty_knowned = convertView.findViewById(R.id.qtyToAddListKnowed);

                name_knowned.setText(((MyToAddListKnowed)list.get(position)).getName());
                brand_knowned.setText(((MyToAddListKnowed)list.get(position)).getBrand());
                qty_knowned.setText(String.valueOf(((MyToAddListKnowed)list.get(position)).getQuantity()));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onItemListKnownedClick(((MyToAddListKnowed)list.get(position)).getId(), ((MyToAddListKnowed)list.get(position)).getId_product());
                    }
                });
                break;
            case HEADER:
                TextView title = convertView.findViewById(R.id.toAddListType);
                title.setText(((String)list.get(position)));
                break;
        }
        return convertView;
    }
}
