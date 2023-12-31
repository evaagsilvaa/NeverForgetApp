package com.example.neverforget.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.neverforget.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;

public class MyCategoryFatherAdapter extends BaseExpandableListAdapter {

    private Context context;
    private JSONArray id_name_Father, id_quantity_Sons;
    private HashMap<String,List<String>> listItem;
    CategoryCallback callback;

    public MyCategoryFatherAdapter(Context context, JSONArray id_name, HashMap<String, List<String>> listItem, JSONArray number, CategoryCallback callback) {
        this.context = context;
        this.id_name_Father = id_name;
        this.listItem = listItem;
        this.id_quantity_Sons = number;
        this.callback = callback;
    }

    @Override
    public int getGroupCount() {
        return this.id_name_Father.length();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        String name = null;
        try {
            name = this.id_name_Father.getJSONObject(groupPosition).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.listItem.get(name).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        String name = null;
        try {
            name = this.id_name_Father.getJSONObject(groupPosition).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return name;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String name = null;
        try {
            name = this.id_name_Father.getJSONObject(groupPosition).getString("name");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return this.listItem.get(name).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String group = (String) getGroup(groupPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_category_father,null);
        }

        TextView textView = convertView.findViewById(R.id.name_brand);
        textView.setText(group);
        return  convertView;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        String child = (String) getChild(groupPosition,childPosition);
        if (convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.row_category, null);
        }
        final TextView textView = convertView.findViewById(R.id.category_son);
        final TextView number_item = convertView.findViewById(R.id.category_to_list_number);
        TextView myButton1 = convertView.findViewById(R.id.add_category_to_list);
        TextView myButton2 = convertView.findViewById(R.id.remove_category_to_list);

        textView.setText(child);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCategoryClick(childPosition, textView, false);
            }
        });

        myButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCategoryAdd(childPosition, false);
                try {
                    number_item.setText(String.valueOf(id_quantity_Sons.getJSONObject(childPosition).getInt("quantity")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        myButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onCategoryRemove(childPosition, false);
                try {
                    number_item.setText(String.valueOf(id_quantity_Sons.getJSONObject(childPosition).getInt("quantity")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
