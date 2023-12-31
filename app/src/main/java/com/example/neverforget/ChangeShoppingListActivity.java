package com.example.neverforget;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.Service;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChangeShoppingListActivity extends AppCompatActivity implements Callback, CallBackListener {

    //Inicializações dos objectos nos layouts
    private AutoCompleteTextView name;
    private ExtendedFloatingActionButton button_change_list;
    private TextInputLayout name_layout;
    private TextView error_name;
    private TextView n_items;
    private Service service;

    private FloatingActionButton mMainAddFab, mListAddFab, mListEditFab, mListDeleteFab;
    private Animation mFabOpenFirstAnim,mFabCloseFirstAnim,mFabOpenSecondAnim,mFabCloseSecondAnim, mFabOpenThirdAnim,mFabCloseThirdAnim;
    private boolean fab_isOpen;
    private String name_selected;
    private int id_selected;
    private int id_list[];
    private String list_name[];
    private boolean flag_on_back_pressed = true;
    private int total_items[];


    public static boolean flag_edit = false;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_change_list);

        //------------------Inicializações-----------------//
        name = findViewById(R.id.exposed_dropdown_name_list);
        button_change_list = findViewById(R.id.button_change_list);
        n_items = findViewById(R.id.n_of_items_list);
        error_name = findViewById(R.id.errorname_change_list);
        name_layout = findViewById(R.id.name_list_change_list_layout);
        mMainAddFab = findViewById(R.id.fab_list_options);
        mListDeleteFab = findViewById(R.id.fab_delete_list);
        mListEditFab = findViewById(R.id.fab_edit_list);
        mListAddFab = findViewById(R.id.fab_add_list);

        //-----------Floating Action Button-------------//
        fab_isOpen = false;

        //Animation First Button
        mFabOpenFirstAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_open_first);
        mFabCloseFirstAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_close_first);

        //Animation Second Button
        mFabOpenSecondAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_open_second);
        mFabCloseSecondAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_close_second);

        //Animation Third Button
        mFabOpenThirdAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_open_third);
        mFabCloseThirdAnim = AnimationUtils.loadAnimation(ChangeShoppingListActivity.this, R.anim.fab_close_third);

        //Animation on click main button
        mMainAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fab_isOpen){
                    mListDeleteFab.setVisibility(View.GONE);
                    mListEditFab.setVisibility(View.GONE);
                    mListAddFab.setVisibility(View.GONE);
                    mMainAddFab.setImageResource(R.drawable.icon_add_white);
                    rotateFabBackward();
                    mListDeleteFab.startAnimation(mFabCloseFirstAnim);
                    mListEditFab.startAnimation(mFabCloseSecondAnim);
                    mListAddFab.startAnimation(mFabCloseThirdAnim);
                    fab_isOpen = false;
                } else if (!fab_isOpen){
                    mListDeleteFab.setVisibility(View.VISIBLE);
                    mListEditFab.setVisibility(View.VISIBLE);
                    mListAddFab.setVisibility(View.VISIBLE);
                    mMainAddFab.setImageResource(R.drawable.icon_add_white);
                    rotateFabForward();
                    mListDeleteFab.startAnimation(mFabOpenFirstAnim);
                    mListEditFab.startAnimation(mFabOpenSecondAnim);
                    mListAddFab.startAnimation(mFabOpenThirdAnim);
                    fab_isOpen = true;
                }
            }
        });

        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);

        getAllShoppingLists();


        button_change_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    error_name.setVisibility(View.VISIBLE);
                    error_name.setText("Choose a list");
                    name_layout.setErrorEnabled(true);
                    name_layout.setError("Choose a list");
                }else{
                    Variables.shoppingListName = name_selected;
                    Variables.shoppingListID_actual = id_selected;
                    Variables.flag_update_shopping_list_activity = true;
                    finish();
                }
            }
        });

        name.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            int inType = name.getInputType(); // backup the input type
            name.setInputType(InputType.TYPE_NULL); // disable soft input
            name.onTouchEvent(event); // call native handler
            name.setInputType(inType); // restore input type
            return true; // consume touch even
            }
        });

        name.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                id_selected = id_list[position];
                name_selected = list_name[position];
                n_items.setText(String.valueOf(total_items[position]));
            }
        });

        mListAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentList fragment = new FragmentList();
                fragment.show(getSupportFragmentManager(),"MyListFragment");
            }
        });


        mListEditFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    error_name.setVisibility(View.VISIBLE);
                    error_name.setText("Choose a list");
                    name_layout.setErrorEnabled(true);
                    name_layout.setError("Choose a list");
                    Toast.makeText(ChangeShoppingListActivity.this, "Choose a list", Toast.LENGTH_SHORT).show();
                }else{
                    error_name.setVisibility(View.INVISIBLE);
                    name_layout.setErrorEnabled(false);
                    FragmentList fragment = new FragmentList();
                    Bundle bundle = new Bundle();
                    bundle.putString("listName", name_selected);
                    bundle.putInt("listID", id_selected);
                    fragment.setArguments(bundle);
                    fragment.show(getSupportFragmentManager(),"MyListEditFragment");
                }

            }
        });
        mListDeleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.getText().toString().equals("")){
                    error_name.setVisibility(View.VISIBLE);
                    error_name.setText("Choose a list");
                    name_layout.setErrorEnabled(true);
                    name_layout.setError("Choose a list");
                    Toast.makeText(ChangeShoppingListActivity.this, "Choose a list", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ChangeShoppingListActivity.this);
                    builder.setMessage("Are you sure you want to delete the list " + name_selected + "?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteShoppingList(id_selected);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        id_selected = Variables.shoppingListID_actual;
        name_selected = Variables.shoppingListName;
    }

    @Override
    public void onDismiss() {
        service = new Service(this.getApplicationContext(), this);
        getAllShoppingLists();
    }

    @Override
    public void onBackPressed() {
        if(flag_on_back_pressed){
            super.onBackPressed();
        }
    }

    //--------------Floating Button Animations-----------------//
    public void rotateFabForward() {
        ViewCompat.animate(mMainAddFab)
                .rotation(45.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(0.0F))
                .start();
    }

    public void rotateFabBackward() {
        ViewCompat.animate(mMainAddFab)
                .rotation(0.0F)
                .withLayer()
                .setDuration(300L)
                .setInterpolator(new OvershootInterpolator(0.0F))
                .start();
    }


    private void getAllShoppingLists(){
        service.getAllShoppingLists();
    }

    private void deleteShoppingList(int id){
        service.deleteShoppingList(id);
    }


    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_SHOPPINGLISTS:
                try {
                    JSONArray jsonObject = data.getJSONArray("data");
                    list_name = new String[jsonObject.length()];
                    id_list = new int[jsonObject.length()];
                    total_items = new int[jsonObject.length()];
                    if (jsonObject.length() == 0) flag_on_back_pressed = false;
                    else flag_on_back_pressed = true;
                    for (int i = 0; i < jsonObject.length(); i++) {
                        int get_id = jsonObject.getJSONObject(i).getInt("id");
                        for(int j=0; j<jsonObject.getJSONObject(i).getJSONArray("shoppingListItems").length(); j++){
                            if (!jsonObject.getJSONObject(i).getJSONArray("shoppingListItems").getJSONObject(j).getBoolean("bought")){
                                total_items[i]++;
                            }
                        }

                        if(!FragmentList.flag_list_just_created){
                            if (Variables.shoppingListID_actual == get_id) {
                                name.setText(jsonObject.getJSONObject(i).getString("name"));
                                n_items.setText(String.valueOf(total_items[i]));
                                name_selected = jsonObject.getJSONObject(i).getString("name");
                                id_selected = get_id;
                            }
                        }
                        if (FragmentList.id_list_just_created == get_id){
                            if(FragmentList.flag_list_just_created){
                                name.setText(jsonObject.getJSONObject(i).getString("name"));
                                n_items.setText(String.valueOf(total_items[i]));
                                name_selected = jsonObject.getJSONObject(i).getString("name");
                                id_selected = get_id;
                            }
                        }
                        id_list[i] = jsonObject.getJSONObject(i).getInt("id");
                        list_name[i] = jsonObject.getJSONObject(i).getString("name");


                        if (flag_edit) {
                            if (jsonObject.getJSONObject(i).getInt("id") == id_selected) {
                                name.setText(jsonObject.getJSONObject(i).getString("name"));
                                name_selected = jsonObject.getJSONObject(i).getString("name");
                                flag_edit = false;
                            }
                        }
                    }
                    FragmentList.flag_list_just_created = false;
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            this,
                            R.layout.row_simple_dropdown,
                            list_name
                    );
                    name.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case DELETE_SHOPPING_LIST:
                String error = "error";
                try {
                    if (data.getString("data").equals(error)) Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(this, "List Deleted successfully!", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                name.setText("");
                n_items.setText("");
                getAllShoppingLists();
                break;
        }
    }


}
