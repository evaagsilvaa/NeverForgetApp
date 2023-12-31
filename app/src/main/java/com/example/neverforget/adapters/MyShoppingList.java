package com.example.neverforget.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.neverforget.R;

import java.util.ArrayList;

public class MyShoppingList extends BaseAdapter{
    ArrayList<Object> list;

    private static final int MYSHOPPINGLISTITEMNOTBOUGHT = 0;
    private static final int MYSHOPPINGLISTITEMBOUGHT = 1;
    private static final int HEADER = 2;
    private LayoutInflater inflater;
    ShoppingListCallback callback;

    public MyShoppingList(Context context, ArrayList<Object> list,ShoppingListCallback callback) {
        this.list = list;
        this.callback = callback;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position) instanceof MyShoppingListItemNotBought){
            return MYSHOPPINGLISTITEMNOTBOUGHT;
        }else if (list.get(position) instanceof MyShoppingListItemBought){
            return MYSHOPPINGLISTITEMBOUGHT;
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
                case MYSHOPPINGLISTITEMNOTBOUGHT:
                case MYSHOPPINGLISTITEMBOUGHT:
                    convertView = inflater.inflate(R.layout.row_shoppinglist, null);
                    break;
                case HEADER:
                    convertView = inflater.inflate(R.layout.row_shoppinglist_category, null);
                    break;
            }
        }
        switch (getItemViewType(position)){
            case MYSHOPPINGLISTITEMNOTBOUGHT:
                CheckBox name_not_bought = convertView.findViewById(R.id.checkBoxList);
                TextView quantity_not_bought = convertView.findViewById(R.id.numberItemsShoppingList);

                name_not_bought.setText(((MyShoppingListItemNotBought)list.get(position)).getName());
                quantity_not_bought.setText(String.valueOf(((MyShoppingListItemNotBought)list.get(position)).getQuantity()));
                name_not_bought.setChecked(false);
                name_not_bought.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callback.onShoppingListChecked(((MyShoppingListItemNotBought)list.get(position)).getId(), ((MyShoppingListItemNotBought)list.get(position)).getName(), ((MyShoppingListItemNotBought)list.get(position)).getQuantity(),position);
                    }
                });
                break;

            case MYSHOPPINGLISTITEMBOUGHT:
                CheckBox name_bought = convertView.findViewById(R.id.checkBoxList);
                TextView quantity_bought = convertView.findViewById(R.id.numberItemsShoppingList);

                name_bought.setText(((MyShoppingListItemBought)list.get(position)).getName());
                quantity_bought.setText(String.valueOf(((MyShoppingListItemBought)list.get(position)).getQuantity()));

                name_bought.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                name_bought.setChecked(true);
                name_bought.setEnabled(false);
                break;
            case HEADER:
                TextView title = convertView.findViewById(R.id.shoppingListCategory);
                title.setText(((String)list.get(position)));
                break;
        }
        return convertView;
    }
}
