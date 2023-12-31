package com.example.neverforget.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neverforget.R;

import org.json.JSONArray;
import org.json.JSONException;

public class MyProductAdapter extends RecyclerView.Adapter<MyProductAdapter.MyViewHolder> {

    JSONArray id_quantity, name_brand;
    Context context;
    ProductCallback callback;

    public MyProductAdapter(Context ct, JSONArray id_and_quantity, JSONArray name_brand_product, ProductCallback callback){
        this.context = ct;
        this.id_quantity = id_and_quantity;
        this.name_brand = name_brand_product;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_product_add, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.myText1.setText(name_brand.getJSONObject(position).getString("name"));
            holder.myText2.setText(name_brand.getJSONObject(position).getString("brand"));
            holder.myText3.setText(String.valueOf(id_quantity.getJSONObject(position).getInt("quantity")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return name_brand.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView myText1, myText2, myText3;
        TextView myButton1, myButton2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.name_product);
            myText2 = itemView.findViewById(R.id.name_brand);
            myText3 = itemView.findViewById(R.id.number_items);

            myButton1 = itemView.findViewById(R.id.add_product);
            myButton2 = itemView.findViewById(R.id.remove_product);

            myButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        callback.onProductAdd(id_quantity.getJSONObject(getAdapterPosition()).getInt("id"));
                        myText3.setText(String.valueOf(id_quantity.getJSONObject(getAdapterPosition()).getInt("quantity")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }
            });

            myButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        callback.onProductRemove(id_quantity.getJSONObject(getAdapterPosition()).getInt("id"));
                        myText3.setText(String.valueOf(id_quantity.getJSONObject(getAdapterPosition()).getInt("quantity")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    };
                }
            });

        }
    }


}
