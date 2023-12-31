package com.example.neverforget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.adapters.MyProductAdapter;
import com.example.neverforget.adapters.ProductCallback;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


//Enviar informação para MainActivity https://www.youtube.com/watch?v=ARezg1D9Zd0


public class FragmentProductsFromCategory extends AppCompatDialogFragment implements ProductCallback, Callback {

    //Inicializações dos objectos nos layouts
    private RecyclerView recyclerView;
    private TextView name_category, total_of_itens, textView_items;
    private Service service;
    private ConstraintLayout add_new_product;
    private FloatingActionButton edit_button;
    private ImageView more_category;
    private ExtendedFloatingActionButton button_add_to_cart;
    private TextView button_add_new_product;

    //Variáveis auxiliares
    private int categoryID, categoryFatherID;
    private JSONArray id_and_number_to_buy, last_id_and_number_to_buy, name_brand_products;
    private int total_itens = 0;
    private int deleted_id;
    private boolean flag_updated = false;
    private int items_to_add = 0;

    private String type_of_request;
    private Snackbar snackbar;
    private String error = "error";

    private MyProductAdapter myAdapter;
    private AlertDialog.Builder builder;
    private CallBackListener callBackListener;

    //public
    public static String name_of_category;
    public static boolean flag_keep_number_of_products = false;
    public static boolean flag_update_category = false;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_products_of_category, null);
        builder.setView(view);


        //------------------Inicializações-----------------//
        name_category = view.findViewById(R.id.name_category);
        recyclerView = view.findViewById(R.id.recyclerview_products);
        total_of_itens = view.findViewById(R.id.total_itens);
        textView_items = view.findViewById(R.id.textView_items);
        add_new_product = view.findViewById(R.id.add_new_product_cardview);
        button_add_new_product = view.findViewById(R.id.button_add_new_product);
        edit_button = view.findViewById(R.id.edit_category_button);
        more_category = view.findViewById(R.id.more_category);
        button_add_to_cart = view.findViewById(R.id.button_add_to_cart);

        //-----------Get Values From MainActivity--------//
        Bundle bundle=getArguments();
        categoryID = Integer.parseInt(bundle.getString("categoryID"));
        name_category.setText(bundle.getString("categoryName"));
        categoryFatherID = Integer.parseInt(bundle.getString("categoryFatherID"));

        //------------------Service-----------------//
        service = new Service(builder.getContext(), this);

        //-------------Request Get Products--------------//
        type_of_request = "get_all_products";
        getProductsByCategory(categoryID);

        //---------------------Buttons------------------//
        button_add_new_product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(builder.getContext(), ProductNewActivity.class);
                Variables.flag_new_product = "category";
                i.putExtra("category", name_category.getText().toString());
                startActivity(i);
            }
        });

        button_add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getShoppingListByID(Variables.shoppingListID_actual);
            }
        });

        more_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupShow(v);
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

    public void popupShow(View v){
        PopupMenu popupMenu = new PopupMenu(getActivity(), v);
        popupMenu.getMenuInflater().inflate(R.menu.menu_category, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.add_product_button:
                    Intent i_add = new Intent(builder.getContext(), ProductNewActivity.class);
                    Variables.flag_new_product = "category";
                    i_add.putExtra("category", name_category.getText().toString());
                    startActivity(i_add);
                    break;
                case R.id.edit_category_button:
                    Variables.flag_category = "edit_category";
                    Intent i_edit = new Intent(getContext(), CreateCategoryActivity.class);
                    i_edit.putExtra("categoryID", categoryID);
                    i_edit.putExtra("categoryName", name_category.getText().toString().trim());
                    i_edit.putExtra("categoryFatherID", categoryFatherID);
                    startActivity(i_edit);
                    break;

                default:
                    break;

            }
            return true;
            }
        });
        popupMenu.show();
    }


    //----------------------onResume-------------------------//
    @Override
    public void onResume() {
        super.onResume();
        service = new Service(builder.getContext(), this);
        if (flag_update_category){
            name_category.setText(name_of_category);
        }
        type_of_request = "get_all_products";
        getProductsByCategory(categoryID);
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (flag_update_category){
            Variables.flag_update_categories = true;
            flag_update_category = false;
        }
        if(callBackListener != null)
            callBackListener.onDismiss();
        dismiss();
    }

    @Override
    public void onProductAdd(int id) {
        int quantity;
        for (int i=0;i<id_and_number_to_buy.length();i++){
            try {
                if (id_and_number_to_buy.getJSONObject(i).getInt("id") == id){
                    quantity = id_and_number_to_buy.getJSONObject(i).getInt("quantity");
                    id_and_number_to_buy.getJSONObject(i).put("quantity", quantity+1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        total_itens = total_itens + 1;
        total_of_itens.setText("Total: " + total_itens);
    }

    @Override
    public void onProductRemove(int id) {
        int quantity;
        for (int i=0;i<id_and_number_to_buy.length();i++){
            try {
                if (id_and_number_to_buy.getJSONObject(i).getInt("id") == id){
                    if (id_and_number_to_buy.getJSONObject(i).getInt("quantity") != 0){
                        quantity = id_and_number_to_buy.getJSONObject(i).getInt("quantity");
                        id_and_number_to_buy.getJSONObject(i).put("quantity", quantity-1);
                        total_itens = total_itens - 1;
                        total_of_itens.setText("Total: " + total_itens);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            switch (direction){
                case ItemTouchHelper.LEFT:
                    try {
                        deleted_id = id_and_number_to_buy.getJSONObject(position).getInt("id");
                        snackbar = Snackbar.make(recyclerView, name_brand_products.getJSONObject(position).getString("name"), Snackbar.LENGTH_LONG);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    View snackBarView = snackbar.getView();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        snackBarView.setBackgroundColor(builder.getContext().getColor(R.color.colorGreyDark));
                    }
                    type_of_request = "deleted_product";
                    getProductsByCategory(categoryID);
                    break;
                case ItemTouchHelper.RIGHT:
                    Variables.flag_new_product = "update_product";
                    Intent i = new Intent(getActivity(), ProductNewActivity.class);
                    try {
                        i.putExtra("id", String.valueOf(id_and_number_to_buy.getJSONObject(position).getInt("id")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(i);
                    break;
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(builder.getContext(),c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive)
                    .addSwipeLeftBackgroundColor(ContextCompat.getColor(builder.getContext(),R.color.colorGreyLight))
                    .addSwipeRightBackgroundColor(ContextCompat.getColor(builder.getContext(),R.color.colorGreyLight))
                    .addSwipeRightActionIcon(R.drawable.icon_edit_white)
                    .addSwipeLeftActionIcon(R.drawable.icon_delete_white)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };


    //-----------------Request DataBase--------------------//
    private void getProductsByCategory(int id){
        service.getProductsByCategory(id);
    }

    private void deleteProduct(int id){//https://www.youtube.com/watch?v=rcSNkSJ624U //https://www.youtube.com/watch?v=M1XEqqo6Ktg
        service.deleteProduct(id);
    }

    private void addItemToShoppingList(int id, JSONObject data){//https://www.youtube.com/watch?v=rcSNkSJ624U //https://www.youtube.com/watch?v=M1XEqqo6Ktg
        service.addItemToShoppingList(id,data);
    }

    private void updateShoppingListItemQuantity(int id, int idItem, JSONObject data){
        service.updateShoppingListItemQuantity(id, idItem, data);
    }

    private void getShoppingListByID(int id){
        service.getShoppingListByID(id);
    }

    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_PRODUCTS_BY_CATEGORY:
                if (type_of_request == "deleted_product") {
                    try {
                        JSONArray jsonArray = data.getJSONArray("data");
                        name_brand_products = new JSONArray();

                        last_id_and_number_to_buy = id_and_number_to_buy;
                        id_and_number_to_buy = new JSONArray();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject id_and_number = new JSONObject();
                            if (jsonArray.getJSONObject(i).getInt("id") != deleted_id) {
                                JSONObject name_brand_products_JSONObject = new JSONObject();
                                name_brand_products_JSONObject.put("name",jsonArray.getJSONObject(i).getString("name"));
                                name_brand_products_JSONObject.put("brand",jsonArray.getJSONObject(i).getJSONObject("brand").getString("name"));
                                name_brand_products.put(name_brand_products_JSONObject);

                                id_and_number.put("id",jsonArray.getJSONObject(i).getInt("id"));
                                id_and_number.put("quantity", last_id_and_number_to_buy.getJSONObject(i).getInt("quantity"));
                                id_and_number_to_buy.put(id_and_number);
                            }
                        }

                        total_itens = 0;
                        for (int k = 0; k<id_and_number_to_buy.length(); k++){
                            total_itens = total_itens + id_and_number_to_buy.getJSONObject(k).getInt("quantity");
                        }
                        total_of_itens.setText("Total: " + total_itens);
                        myAdapter = new MyProductAdapter(builder.getContext(),id_and_number_to_buy, name_brand_products, this);
                        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
                        recyclerView.setAdapter(myAdapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(builder.getContext()));

                        snackbar.setAction("Undo", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                flag_keep_number_of_products = true;
                                type_of_request = "get_all_products";
                                getProductsByCategory(categoryID);
                            }
                        }).setCallback(new Snackbar.Callback() {
                            @Override
                            public void onShown(Snackbar snackbar) {
                                super.onShown(snackbar);
                            }

                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                if (event != DISMISS_EVENT_ACTION) {
                                    deleteProduct(deleted_id);
                                }
                            }
                        }).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type_of_request == "get_all_products") {
                    try {
                        if (data.getJSONArray("data").length() == 0) {
                            recyclerView.setVisibility(View.INVISIBLE);
                            add_new_product.setVisibility(View.VISIBLE);
                            total_of_itens.setVisibility(View.INVISIBLE);
                            textView_items.setVisibility(View.INVISIBLE);
                            button_add_to_cart.setVisibility(View.GONE);
                        } else {
                            button_add_to_cart.setVisibility(View.VISIBLE);
                            if (flag_keep_number_of_products && id_and_number_to_buy != null){
                                //-------------Calculate Number of itens---------//
                                total_of_itens.setVisibility(View.VISIBLE);
                                textView_items.setVisibility(View.VISIBLE);

                                JSONArray jsonArray = data.getJSONArray("data");
                                name_brand_products = new JSONArray();

                                last_id_and_number_to_buy = id_and_number_to_buy;
                                id_and_number_to_buy = new JSONArray();
                                boolean flag = false;
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject name_brand_products_JSONObject = new JSONObject();
                                    name_brand_products_JSONObject.put("name",jsonArray.getJSONObject(i).getString("name"));
                                    name_brand_products_JSONObject.put("brand",jsonArray.getJSONObject(i).getJSONObject("brand").getString("name"));
                                    name_brand_products.put(name_brand_products_JSONObject);

                                    for (int j=0; j<last_id_and_number_to_buy.length(); j++){
                                        if (last_id_and_number_to_buy.getJSONObject(j).getInt("id") == jsonArray.getJSONObject(i).getInt("id")){
                                            JSONObject id_and_number = new JSONObject();
                                            id_and_number.put("id",jsonArray.getJSONObject(i).getInt("id"));
                                            id_and_number.put("quantity", last_id_and_number_to_buy.getJSONObject(j).getInt("quantity"));
                                            id_and_number_to_buy.put(id_and_number);
                                            flag = true;
                                        }
                                    }
                                    if (!flag){
                                        JSONObject id_and_number = new JSONObject();
                                        id_and_number.put("id",jsonArray.getJSONObject(i).getInt("id"));
                                        id_and_number.put("quantity", 0);
                                        id_and_number_to_buy.put(id_and_number);
                                    }
                                    flag = false;
                                }
                                flag_keep_number_of_products = false;
                            }else{
                                //-------------Calculate Number of itens---------//
                                total_of_itens.setVisibility(View.VISIBLE);
                                textView_items.setVisibility(View.VISIBLE);

                                JSONArray jsonArray = data.getJSONArray("data");
                                name_brand_products = new JSONArray();

                                id_and_number_to_buy = new JSONArray();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject id_and_number = new JSONObject();
                                    id_and_number.put("id",jsonArray.getJSONObject(i).getInt("id"));
                                    id_and_number.put("quantity", 0);
                                    id_and_number_to_buy.put(id_and_number);

                                    JSONObject name_brand_products_JSONObject = new JSONObject();
                                    name_brand_products_JSONObject.put("name",jsonArray.getJSONObject(i).getString("name"));
                                    name_brand_products_JSONObject.put("brand",jsonArray.getJSONObject(i).getJSONObject("brand").getString("name"));
                                    name_brand_products.put(name_brand_products_JSONObject);
                                }
                            }
                            total_itens = 0;
                            for (int k = 0; k<id_and_number_to_buy.length(); k++){
                                total_itens = total_itens + id_and_number_to_buy.getJSONObject(k).getInt("quantity");
                            }
                            total_of_itens.setText("Total: " + total_itens);
                            myAdapter = new MyProductAdapter(builder.getContext(),id_and_number_to_buy, name_brand_products, this);
                            new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
                            recyclerView.setAdapter(myAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(builder.getContext()));
                            recyclerView.setVisibility(View.VISIBLE);
                            add_new_product.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_ITEM_TO_SHOPPINGLIST:
            case UPDATE_SHOPPINGLIST_ITEM_QUANTITY:
                try {
                    if (data.getString("data").equals(error)) {
                        Toast.makeText(getContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                    } else {
                        Variables.flag_update_shopping_list_fragment = true;
                        items_to_add--;
                        Toast.makeText(getContext(), "Added to list successfully!", Toast.LENGTH_SHORT).show();
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                if (items_to_add == 0) {
                    if(callBackListener != null)
                        callBackListener.onDismiss();
                    dismiss();
                }
                break;
            case GET_SHOPPINGLIST_BY_ID:
                try {
                    JSONObject shoppingList_json = data.getJSONObject("data");
                    JSONArray shoppingListItems_json = shoppingList_json.getJSONArray("shoppingListItems");
                    if (shoppingListItems_json.length() != 0){
                        for (int j=0; j<id_and_number_to_buy.length(); j++) {
                            if (id_and_number_to_buy.getJSONObject(j).getInt("quantity") != 0){
                                for (int i=0; i<shoppingListItems_json.length(); i++) {
                                    if (shoppingListItems_json.getJSONObject(i).isNull("category")) {
                                        if (shoppingListItems_json.getJSONObject(i).getJSONObject("product").getInt("id") == id_and_number_to_buy.getJSONObject(j).getInt("id")){
                                            int idItem = shoppingListItems_json.getJSONObject(i).getInt("id");  //ID do product na shopping list
                                            JSONObject send_data_update = new JSONObject();
                                            send_data_update.put("quantity", id_and_number_to_buy.getJSONObject(j).getInt("quantity"));
                                            items_to_add++;
                                            flag_updated = true;
                                            updateShoppingListItemQuantity(Variables.shoppingListID_actual,idItem,send_data_update);
                                        }
                                    }
                                }
                                if (!flag_updated){
                                    JSONObject send_data_add = new JSONObject();
                                    send_data_add.put("productId", id_and_number_to_buy.getJSONObject(j).getInt("id"));
                                    send_data_add.put("quantity", id_and_number_to_buy.getJSONObject(j).getInt("quantity"));
                                    items_to_add++;
                                    addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                                }else{
                                    flag_updated = false;
                                }
                            }
                        }
                    }else{
                        for (int j=0; j<id_and_number_to_buy.length(); j++) {
                            JSONObject send_data_add = new JSONObject();
                            send_data_add.put("productId", id_and_number_to_buy.getJSONObject(j).getInt("id"));
                            send_data_add.put("quantity", id_and_number_to_buy.getJSONObject(j).getInt("quantity"));
                            items_to_add++;
                            addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_CATEGORY_BY_ID:
                Toast.makeText(getContext(), "Went wrong!", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
