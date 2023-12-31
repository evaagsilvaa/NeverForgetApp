package com.example.neverforget.adapters;

import android.widget.TextView;

public interface CategoryCallback {
    void onCategoryClick(int pos, TextView category, boolean category_recycler);
    void onCategoryAdd(int pos, boolean recycler);
    void onCategoryRemove(int pos, boolean recycler);
}
