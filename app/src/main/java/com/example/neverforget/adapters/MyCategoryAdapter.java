package com.example.neverforget.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neverforget.R;

import org.json.JSONArray;
import org.json.JSONException;

public class MyCategoryAdapter extends RecyclerView.Adapter<MyCategoryAdapter.MyViewHolder> {

    JSONArray list;
    Context context;
    CategoryCallback callback;

    public MyCategoryAdapter(Context ct, JSONArray list, CategoryCallback callback){
        this.context = ct;
        this.list = list;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.row_category, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            holder.myText1.setText(list.getJSONObject(position).getString("name"));
            holder.myText2.setText(list.getJSONObject(position).getString("quantity"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.length();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView myText1, myText2;
        TextView myButton1, myButton2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myText1 = itemView.findViewById(R.id.category_son);
            myText2 = itemView.findViewById(R.id.category_to_list_number);
            myButton1 = itemView.findViewById(R.id.add_category_to_list);
            myButton2 = itemView.findViewById(R.id.remove_category_to_list);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onCategoryClick(getAdapterPosition(), myText1, true);
                }
            });
            myButton1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onCategoryAdd(getAdapterPosition(), true);
                    try { myText2.setText(String.valueOf(list.getJSONObject(getAdapterPosition()).getInt("quantity")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            myButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.onCategoryRemove(getAdapterPosition(),true);
                    try { myText2.setText(String.valueOf(list.getJSONObject(getAdapterPosition()).getInt("quantity")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }

    }

}
