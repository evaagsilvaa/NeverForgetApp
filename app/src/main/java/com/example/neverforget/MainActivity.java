package com.example.neverforget;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.neverforget.adapters.CallBackListener;
import com.example.neverforget.adapters.CategoryCallback;
import com.example.neverforget.adapters.MyCategoryAdapter;
import com.example.neverforget.adapters.MyCategoryFatherAdapter;
import com.example.neverforget.adapters.MyShoppingList;
import com.example.neverforget.adapters.MyShoppingListItemBought;
import com.example.neverforget.adapters.MyShoppingListItemNotBought;
import com.example.neverforget.adapters.ShoppingListCallback;
import com.example.neverforget.services.Callback;
import com.example.neverforget.services.CallbackTypes;
import com.example.neverforget.services.NotificationService;
import com.example.neverforget.services.Service;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.github.nkzawa.socketio.client.Socket;


public class MainActivity extends AppCompatActivity implements Callback, CategoryCallback, ShoppingListCallback, CallBackListener {

    //Serviço
    private Service service;
    //Inicializações dos objectos nos layouts
    private EditText search_bar;
    private RecyclerView recyclerView_son;
    private ListView shoppingList;
    private ImageView button_scan, button_clear;
    private ExpandableListView expandableListView;
    private FloatingActionButton mMainAddFab, mCategoryAddFab, mProductAddFab, mListChangeFab;
    private TextView mCategoryAddText, mProductAddText, mListChangeText, listName, listEmpty, noResults;
    private TextView toolbarTextView;

    //Notifications
    private ImageView notificationIcon;
    private TextView notificationCounter;
    private CardView notificationCounterContainer;

    //Layouts
    private BottomAppBar bottomToolbar;
    private ConstraintLayout categoryByNameLayout, shoppingListLayout;
    private RelativeLayout categoryFatherLayout;


    //Variáveis auxiliares
    //ExpandableView
    private JSONArray id_name_Father, id_quantity_Sons_Expandable;
    private MyCategoryFatherAdapter myCategoryFatherAdapter;
    private int last_group_position, actual_group_position;
    private HashMap<String, List<String>> category_dropdown;
    //RecyclerView - Subcategories
    private JSONArray subcategories_recycler;
    private List<String> category_Sons_names;


    private String type_of_request, state;
    private MyShoppingList shoppingListAdapter;
    //Barcode
    private String barcode;
    private int total_itens = 0;
    private int categoryFatherID;
    private Animation mFabOpenFirstAnim,mFabCloseFirstAnim,mFabOpenSecondAnim,mFabCloseSecondAnim, mFabOpenThirdAnim,mFabCloseThirdAnim, mFabOpenFirstTextAnim, mFabOpenSecondTextAnim, mFabOpenThirdTextAnim, mFabCloseFirstTextAnim, mFabCloseSecondTextAnim, mFabCloseThirdTextAnim;


    //FLAGS
    private boolean flag_updated_quantity = false;
    private boolean fab_isOpen;
    private boolean flag_recycler_atual;
    private int listView_position;

    private int items_to_add = 0;
    private String error = "error";

    private FragmentScannerKnowned fragmentScannerKnowned;
    private FragmentScannerNotKnowned fragmentScannerNotKnowned;

    private int n_of_Parents;

    //SHOPPING LIST
    private ArrayList<Object> shopping_list;

    private final int NOTIFICATION_ID = 001;
    private final String CHANNEL_ID = "testing";
    public static int numbernotificationCounter;
    public static boolean flag_numbernotificationCounter = false;
    private int total_not_bought = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);

        //------------Inicializações-------------//
        Variables.flag_barcode_knowed = false;
        Variables.flag_scan = false;
        Variables.flag_category = "new_category";
        Variables.flag_update_categories = false;
        Variables.flag_update_shopping_list_fragment = false;
        Variables.flag_update_shopping_list_activity = false;
        Variables.shoppingListName = "";

        //------------Inicializações-------------//
        search_bar = findViewById(R.id.edittext_search_bar);
        recyclerView_son = findViewById(R.id.recyclerview_category);
        shoppingList = findViewById(R.id.shoppingList);
        categoryByNameLayout = findViewById(R.id.category_son_item);
        categoryFatherLayout = findViewById(R.id.category_father_item);
        shoppingListLayout = findViewById(R.id.shoppingListLayout);
        button_clear = findViewById(R.id.button_close_item);
        button_scan = findViewById(R.id.button_barcode_icon);
        listName = findViewById(R.id.list_name);
        listEmpty = findViewById(R.id.list_empty);
        noResults = findViewById(R.id.no_results_subcategories);
        mMainAddFab = findViewById(R.id.fab_add);
        mCategoryAddFab = findViewById(R.id.fab_category);
        mProductAddFab = findViewById(R.id.fab_product);
        mListChangeFab = findViewById(R.id.fab_list);
        mCategoryAddText = findViewById(R.id.fab_category_text);
        mProductAddText = findViewById(R.id.fab_product_text);
        mListChangeText = findViewById(R.id.fab_list_text);
        bottomToolbar = findViewById(R.id.bottomAppBar);
        notificationCounter = findViewById(R.id.notificationNumber);
        notificationCounterContainer = findViewById(R.id.notificationNumberContainer);
        notificationIcon = findViewById(R.id.notificationIcon);


        //-----------Floating Action Button-------------//
        fab_isOpen = false;
        flag_recycler_atual = false;

        //Animation First Button
        mFabOpenFirstAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open_first);
        mFabCloseFirstAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_first);

        //Animation Second Button
        mFabOpenSecondAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open_second);
        mFabCloseSecondAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_second);

        //Animation Third Button
        mFabOpenThirdAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_open_third);
        mFabCloseThirdAnim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_close_third);

        //Animation First Text
        mFabOpenFirstTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open_first_text);
        mFabCloseFirstTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close_first_text);

        //Animation Second Text
        mFabOpenSecondTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open_second_text);
        mFabCloseSecondTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close_second_text);

        //Animation Third Text
        mFabOpenThirdTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_open_third_text);
        mFabCloseThirdTextAnim = AnimationUtils.loadAnimation(this, R.anim.fab_close_third_text);

        //Animation on click main button
        mMainAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (total_itens == 0){
                    if (fab_isOpen){
                        mCategoryAddFab.setVisibility(View.GONE);
                        mProductAddFab.setVisibility(View.GONE);
                        mListChangeFab.setVisibility(View.GONE);
                        mCategoryAddFab.setEnabled(false);
                        mProductAddFab.setEnabled(false);
                        mListChangeFab.setEnabled(false);
                        mMainAddFab.setImageResource(R.drawable.icon_add_white);
                        rotateFabBackward();
                        mCategoryAddFab.startAnimation(mFabCloseFirstAnim);
                        mProductAddFab.startAnimation(mFabCloseSecondAnim);
                        mListChangeFab.startAnimation(mFabCloseThirdAnim);
                        mCategoryAddText.startAnimation(mFabCloseFirstTextAnim);
                        mProductAddText.startAnimation(mFabCloseSecondTextAnim);
                        mListChangeText.startAnimation(mFabCloseThirdTextAnim);
                        fab_isOpen = false;
                    } else{
                        mCategoryAddFab.setVisibility(View.VISIBLE);
                        mProductAddFab.setVisibility(View.VISIBLE);
                        mListChangeFab.setVisibility(View.VISIBLE);
                        mCategoryAddFab.setEnabled(true);
                        mProductAddFab.setEnabled(true);
                        mListChangeFab.setEnabled(true);
                        mMainAddFab.setImageResource(R.drawable.icon_add_white);
                        rotateFabForward();
                        mCategoryAddFab.startAnimation(mFabOpenFirstAnim);
                        mProductAddFab.startAnimation(mFabOpenSecondAnim);
                        mListChangeFab.startAnimation(mFabOpenThirdAnim);
                        mCategoryAddText.startAnimation(mFabOpenFirstTextAnim);
                        mProductAddText.startAnimation(mFabOpenSecondTextAnim);
                        mListChangeText.startAnimation(mFabOpenThirdTextAnim);
                        fab_isOpen = true;
                    }
                }else{
                    service();
                    type_of_request = "add_or_update_categories";
                    getShoppingListById(Variables.shoppingListID_actual);
                }
            }
        });

        //Action when clicked button
        mCategoryAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.flag_category = "new_category";
                Intent i = new Intent(MainActivity.this, CreateCategoryActivity.class);
                startActivity(i);
            }
        });

        mProductAddFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.flag_new_product = "new_product";
                Intent i = new Intent(MainActivity.this, ProductNewActivity.class);
                startActivity(i);
            }
        });

        mListChangeFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChangeShoppingListActivity.class);
                startActivity(i);
            }
        });

        mCategoryAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.flag_category = "new_category";
                Intent i = new Intent(MainActivity.this, CreateCategoryActivity.class);
                startActivity(i);
            }
        });

        mProductAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Variables.flag_new_product = "new_product";
                Intent i = new Intent(MainActivity.this, ProductNewActivity.class);
                startActivity(i);
            }
        });

        mListChangeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChangeShoppingListActivity.class);
                startActivity(i);
            }
        });

        listName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, ChangeShoppingListActivity.class);
                startActivity(i);
            }
        });

        //--------------ExpandableList------------------//
        expandableListView = findViewById(R.id.expandablelist);
        category_Sons_names= new ArrayList<>();
        category_dropdown= new HashMap<>();

        //--------------ShoppingList------------------//
        shopping_list = new ArrayList<>();

        //---------------Setup Toolbar--------------//
        toolbarTextView = bottomToolbar.findViewById(R.id.toolbarTextView);
        this.setSupportActionBar(bottomToolbar);
        this.getSupportActionBar().setDisplayShowTitleEnabled(false);

        //-------------Dialog Fragments------------//
        fragmentScannerKnowned = new FragmentScannerKnowned();
        fragmentScannerNotKnowned = new FragmentScannerNotKnowned();

        //------------------Service-----------------//
        service = new Service(this.getApplicationContext(), this);

        getAllItemsToAddList();

        //------------Bell Notifications------------//
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (numbernotificationCounter != 0){
                    Intent notificationIntent = new Intent(MainActivity.this, AddProductToListActivity.class);
                    startActivity(notificationIntent);
                }
            }
        });

        if(Variables.flag_create_list){
            Variables.shoppingListID_actual = Variables.shoppingList_create_list;
            Variables.shoppingListName = CreateListActivity.shoppingListName;
            listName.setText(Variables.shoppingListName);
        }

        //-----------Get Categories Father----------//
        JSONObject data = new JSONObject();
        try {
            data.put("name", "");
            data.put("categoriesFather", "true");
            data.put("organized", "false");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        type_of_request = "category_father";
        state = "first_request";
        getCategoriesByName(data);

        //--------------Search Bar------------------//
        search_bar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                service();
                fabOpen();
                if (hasFocus) {
                    //--------------Aceder à lista de categorias Father---------//
                    shoppingListLayout.setVisibility(View.INVISIBLE);
                    categoryByNameLayout.setVisibility(View.INVISIBLE);
                    categoryFatherLayout.setVisibility(View.VISIBLE);
                    button_clear.setVisibility(View.VISIBLE);
                    toolbarTextView.setText("Total: " + total_itens);
                    if (total_itens != 0){
                        mMainAddFab.setImageResource(R.drawable.icon_check_black);
                    }
                } else {
                    //-----------------Aceder à lista de compras------------//
                    categoryFatherLayout.setVisibility(View.INVISIBLE);
                    categoryByNameLayout.setVisibility(View.INVISIBLE);
                    shoppingListLayout.setVisibility(View.VISIBLE);
                    toolbarTextView.setText("Total: "+ total_not_bought);

                    search_bar.setText("");
                    mMainAddFab.setImageResource(R.drawable.icon_add_white);

                    button_clear.setVisibility(View.GONE);
                }
            }
        });

        //.........................
        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                service();
                fabOpen();
                String name=search_bar.getText().toString();
                if (search_bar.hasFocus()){
                    if (name.equals("")){
                    //-----------Mostrar lista das categorias Father com dropdown fechadas----------//
                        categoryByNameLayout.setVisibility(View.INVISIBLE);
                        shoppingListLayout.setVisibility(View.INVISIBLE);
                        if (expandableListView.isGroupExpanded(last_group_position)){
                            //-----------------Fechar Expandable View------------//
                            expandableListView.collapseGroup(last_group_position);
                        }
                        categoryFatherLayout.setVisibility(View.VISIBLE);
                        noResults.setVisibility(View.INVISIBLE);
                    }else {
                        //------------Procurar pela barra de procura------------//
                        JSONObject data = new JSONObject();
                        try {
                            data.put("name", name);
                            data.put("categoriesFather", "false");
                            data.put("organized", "true");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        type_of_request = "category_son_on_change";
                        getCategoriesByName(data);
                    }
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });


        //--------------ExpandableListView------------------//
        expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                service();
                fabOpen();
                actual_group_position = groupPosition;
                if (expandableListView.isGroupExpanded(groupPosition)){
                    //-----------------Fechar Expandable View------------//
                    expandableListView.collapseGroup(groupPosition);
                    last_group_position = groupPosition;
                    total_itens = 0;
                    toolbarTextView.setText("Total: " + total_itens);
                    mMainAddFab.setImageResource(R.drawable.icon_add_white);
                }else{
                    //-----------------Abrir Expandable View------------//
                    category_Sons_names.clear();
                    JSONObject data = new JSONObject();
                    try {
                        data.put("id", id_name_Father.getJSONObject(groupPosition).getInt("id"));
                        data.put("subCategories", "true");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    shoppingListLayout.setVisibility(View.INVISIBLE);
                    categoryByNameLayout.setVisibility(View.INVISIBLE);
                    categoryFatherLayout.setVisibility(View.VISIBLE);
                    getCategoryById(data);
                }
                return true;
            }
        });


        //---------------------Scanner button---------------//
        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });


        button_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search_bar.clearFocus();
            }
        });
        //------------------Notifications-------------------//
        mSocket.on("raspberrypi", onNewMessage);
        mSocket.connect();

    }

    //------------------Notifications-------------------------//
    private Socket mSocket;{
        try {
            mSocket = IO.socket("http://never-forget.duckdns.org/events");
        } catch (URISyntaxException e) {}
    }



    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            MainActivity .this.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    flag_numbernotificationCounter = true;
                    getAllItemsToAddList();


                    //JSONObject data = (JSONObject) args[0];
                    /*
                    if (!AddProductToListActivity.addProductToListActivity){
                        n_of_new_products++;
                        JSONObject data = (JSONObject) args[0];
                        String message_name = null, message_barcode;
                        Boolean new_product;
                        try {
                            new_product = data.getBoolean("new_product");
                            message_name = data.getString("name");
                            message_barcode = data.getString("barcode");
                        } catch (JSONException e) {
                            return;
                        }
                        Intent notificationIntent = new Intent(MainActivity.this, AddProductToListActivity.class);
                        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this,1,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, CHANNEL_ID);
                        builder.setSmallIcon(R.drawable.scan);
                        builder.setContentTitle("New product Added");
                        if (n_of_new_products == 1){
                            if (new_product){
                                builder.setContentText(message_barcode);
                            }else{
                                builder.setContentText(message_name);
                            }
                        }else {
                            builder.setContentText("You have now " + n_of_new_products + " items to add");
                        }
                        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                        builder.setContentIntent(pendingIntent);
                        builder.setAutoCancel(true);
                        builder.build().flags |= Notification.FLAG_AUTO_CANCEL;

                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(MainActivity.this);
                        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

                        getAllItemsToAddList();
                    }else{
                        AddProductToListActivity.update_list = true;
                    }

                     */
                }
            });
        }
    };

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

    private void fabOpen() {
        if (fab_isOpen){
            mCategoryAddFab.setVisibility(View.GONE);
            mProductAddFab.setVisibility(View.GONE);
            mListChangeFab.setVisibility(View.GONE);
            mCategoryAddFab.setEnabled(false);
            mProductAddFab.setEnabled(false);
            mListChangeFab.setEnabled(false);
            rotateFabBackward();
            mMainAddFab.setImageResource(R.drawable.icon_add_white);
            mCategoryAddFab.startAnimation(mFabCloseFirstAnim);
            mProductAddFab.startAnimation(mFabCloseSecondAnim);
            mListChangeFab.startAnimation(mFabCloseThirdAnim);
            mCategoryAddText.startAnimation(mFabCloseFirstTextAnim);
            mProductAddText.startAnimation(mFabCloseSecondTextAnim);
            mListChangeText.startAnimation(mFabCloseThirdTextAnim);
            fab_isOpen = false;
        }
    }

    //------------------------Service-------------------------//
    private void service() {
        service = new Service(this.getApplicationContext(), this);
    }

    //------------------------Scan Result--------------------//
    private void scanCode(){
        Variables.flag_scan = true;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(true);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.PRODUCT_CODE_TYPES);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            if (result.getContents() != null) {
                barcode = result.getContents();
                getProductByBarcode(barcode);
            }else{
                Toast.makeText(this, "No results", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //-----------------RecyclerView Click---------------------//
    @Override
    public void onCategoryClick(int pos, TextView category, boolean category_recycler) {
        service();
        fabOpen();
        JSONObject data = new JSONObject();
        try {
            if (category_recycler) {
                data.put("name", subcategories_recycler.getJSONObject(pos).getString("name"));
            }else {
                data.put("name", category_Sons_names.get(pos));
            }
            data.put("categoriesFather", "false");
            data.put("organized", "true");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        type_of_request = "get_id_from_category_name";
        getCategoriesByName(data);
    }

    @Override
    public void onCategoryAdd(int pos, boolean recycler) {
        flag_recycler_atual = recycler;
        if (recycler){
            try {
                subcategories_recycler.getJSONObject(pos).put("quantity",subcategories_recycler.getJSONObject(pos).getInt("quantity") + 1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            total_itens = total_itens + 1;
            toolbarTextView.setText("Total: " + total_itens);
        }else{
            try {
                id_quantity_Sons_Expandable.getJSONObject(pos).put("quantity", id_quantity_Sons_Expandable.getJSONObject(pos).getInt("quantity") +1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            total_itens = total_itens + 1;
            toolbarTextView.setText("Total: " + total_itens);
        }

        if (fab_isOpen){
            mCategoryAddFab.setVisibility(View.GONE);
            mProductAddFab.setVisibility(View.GONE);
            mListChangeFab.setVisibility(View.GONE);
            mCategoryAddFab.setEnabled(false);
            mProductAddFab.setEnabled(false);
            mListChangeFab.setEnabled(false);
            rotateFabBackward();
            mMainAddFab.setImageResource(R.drawable.icon_check_black);
            mCategoryAddFab.startAnimation(mFabCloseFirstAnim);
            mProductAddFab.startAnimation(mFabCloseSecondAnim);
            mListChangeFab.startAnimation(mFabCloseThirdAnim);
            mCategoryAddText.startAnimation(mFabCloseFirstTextAnim);
            mProductAddText.startAnimation(mFabCloseSecondTextAnim);
            mListChangeText.startAnimation(mFabCloseThirdTextAnim);
            fab_isOpen = false;
        }else{
            mMainAddFab.setImageResource(R.drawable.icon_check_black);
        }
    }

    @Override
    public void onCategoryRemove(int pos, boolean recycler) {
        flag_recycler_atual = recycler;
        if (total_itens != 0){
            if (recycler){
                try {
                    if (subcategories_recycler.getJSONObject(pos).getInt("quantity") != 0){
                        subcategories_recycler.getJSONObject(pos).put("quantity",subcategories_recycler.getJSONObject(pos).getInt("quantity") - 1);
                        total_itens = total_itens - 1;
                        toolbarTextView.setText("Total: " + total_itens);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }else{
                try {
                    if (id_quantity_Sons_Expandable.getJSONObject(pos).getInt("quantity") != 0){
                        id_quantity_Sons_Expandable.getJSONObject(pos).put("quantity", id_quantity_Sons_Expandable.getJSONObject(pos).getInt("quantity") -1);
                        total_itens = total_itens - 1;
                        toolbarTextView.setText("Total: " + total_itens);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if(!fab_isOpen && total_itens == 0){
                mMainAddFab.setImageResource(R.drawable.icon_add_white);
            }
        }
    }


    //----------------Check list-----------------------//
    @Override
    public void onShoppingListChecked(int id, String name, int quantity, int pos) {
        fabOpen();
        FragmentBuyItem fragment = new FragmentBuyItem();
        Bundle bundle = new Bundle();
        bundle.putInt("id", id);
        bundle.putString("name", name);
        bundle.putInt("quantity", quantity);
        View c = shoppingList.getChildAt(0);
        listView_position = -c.getTop() + shoppingList.getFirstVisiblePosition() * c.getHeight();
        fragment.setArguments(bundle);
        fragment.show(getSupportFragmentManager(), "MyFragmentBuyItem");
    }

    //----------------------onPause-------------------------//
    @Override
    protected void onPause() {
        super.onPause();
        //----------Close Floating Button----------//
        if (fab_isOpen){
            mCategoryAddFab.setVisibility(View.GONE);
            mProductAddFab.setVisibility(View.GONE);
            mListChangeFab.setVisibility(View.GONE);
            mCategoryAddFab.setEnabled(false);
            mProductAddFab.setEnabled(false);
            mListChangeFab.setEnabled(false);
            mMainAddFab.setImageResource(R.drawable.icon_add_white);
            rotateFabBackward();
            mCategoryAddFab.startAnimation(mFabCloseFirstAnim);
            mProductAddFab.startAnimation(mFabCloseSecondAnim);
            mListChangeFab.startAnimation(mFabCloseThirdAnim);
            mCategoryAddText.startAnimation(mFabCloseFirstTextAnim);
            mProductAddText.startAnimation(mFabCloseSecondTextAnim);
            mListChangeText.startAnimation(mFabCloseThirdTextAnim);
            fab_isOpen = false;
        }
    }

    //----------------------onResume-------------------------//
    @Override
    public void onResume(){
        super.onResume();
        listName.setText(Variables.shoppingListName);

        //---------Clear Text and Focus Search Bar--------//
        search_bar.setText("");
        search_bar.clearFocus();

        mSocket.connect();
        service();

        if (Variables.flag_barcode_knowed){
            Variables.flag_scan = false;
            type_of_request = "flag_barcode_knowed";
            getProductByBarcode(barcode);
        }

        if (Variables.flag_update_shopping_list_activity){
            Variables.flag_update_shopping_list_activity = false;
            service();
            getShoppingListById(Variables.shoppingListID_actual);
        }
        if(numbernotificationCounter == 0){
            notificationCounterContainer.setVisibility(View.INVISIBLE);
        }else if (numbernotificationCounter > 99){
            notificationCounter.setText("99");
            notificationCounterContainer.setVisibility(View.VISIBLE);
        }else{
            notificationCounter.setText(String.valueOf(numbernotificationCounter));
            notificationCounterContainer.setVisibility(View.VISIBLE);
        }

        if (Variables.flag_new_product == "new_product"){
            Variables.flag_new_product = "";
            FragmentAddProductCategory fragment = new FragmentAddProductCategory();
            Bundle bundle = new Bundle();
            bundle.putInt("id", Variables.id_new_product);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(), "MyFragmentAddProductCategory");
        }

        getAllItemsToAddList();

    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("shoppingList_ID", Variables.shoppingListID_actual);
        editor.putString("shoppingList_NAME",Variables.shoppingListName);

        editor.apply();

        mSocket.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences prefs_ = getSharedPreferences("prefs", MODE_PRIVATE);
        if (!Variables.flag_update_shopping_list_activity && !Variables.flag_create_list && !Variables.flag_no_list){
            Variables.shoppingListID_actual = prefs_.getInt("shoppingList_ID", 0);
            Variables.shoppingListName = prefs_.getString("shoppingList_NAME", Variables.shoppingListName);
        }
        Variables.flag_create_list = false;
        mSocket.connect();
    }
    //---------------------onDismiss-------------------------//
    @Override
    public void onDismiss() {
        service();
        if(Variables.flag_update_shopping_list_fragment){
            //----------Shopping View----------//
            search_bar.setText("");
            search_bar.clearFocus();
            type_of_request = "get_shopping_list";
            getShoppingListById(Variables.shoppingListID_actual);
        }


        //----------Reload categories--------//
        if (Variables.flag_update_categories){
            Variables.flag_update_categories = false;
            category_Sons_names.clear();
            JSONObject data = new JSONObject();
            try {
                data.put("id", categoryFatherID);
                data.put("subCategories", "true");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            getCategoryById(data);
        }
    }

    //-----------------Request DataBase--------------------//
    private void getCategoriesByName(JSONObject data){
        service.getCategoryByName(data);
    }

    private void getCategoryById(JSONObject data){
        service.getCategoryByID(data);
    }

    private void getProductByBarcode(String barcode){
        service.getProductByBarcode(barcode);
    }

    private void getShoppingListById(int id){
        service.getShoppingListByID(id);
    }

    private void addItemToShoppingList(int id, JSONObject data){
        service.addItemToShoppingList(id,data);
    }

    private void updateShoppingListItemQuantity(int id, int idItem, JSONObject data){
        service.updateShoppingListItemQuantity(id, idItem, data);
    }

    private void getAllItemsToAddList(){
        service.getAllItemsToAddList();
    }


    //---------------------Callback-----------------------//
    @Override
    public void callback(CallbackTypes type, JSONObject data) {
        switch (type) {
            case GET_CATEGORY_BY_ID:
                try {
                    JSONObject jsonObject = data.getJSONObject("data");
                    JSONArray categories = jsonObject.getJSONArray("categories");
                    id_quantity_Sons_Expandable = new JSONArray();
                    category_Sons_names = new ArrayList<>();
                    for (int i = 0; i < categories.length(); i++) {
                        JSONObject id_quantity_Sons_Expandable_JSONObject = new JSONObject();
                        id_quantity_Sons_Expandable_JSONObject.put("id",categories.getJSONObject(i).getInt("id"));
                        id_quantity_Sons_Expandable_JSONObject.put("quantity",0);
                        id_quantity_Sons_Expandable.put(id_quantity_Sons_Expandable_JSONObject);
                        category_Sons_names.add(categories.getJSONObject(i).getString("name"));
                    }
                    category_dropdown.put(jsonObject.getString("name"), category_Sons_names);
                    myCategoryFatherAdapter = new MyCategoryFatherAdapter(getApplicationContext(), id_name_Father, category_dropdown, id_quantity_Sons_Expandable, this);
                    expandableListView.setAdapter(myCategoryFatherAdapter);

                    expandableListView.collapseGroup(last_group_position);
                    for (int i = 0; i < id_name_Father.length(); i++){
                        if (jsonObject.getInt("id") == id_name_Father.getJSONObject(i).getInt("id")){
                            expandableListView.expandGroup(i);
                        }
                    }
                    expandableListView.setSelection(actual_group_position);
                    expandableListView.getSelectedId();
                    last_group_position = actual_group_position;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_PRODUCT_BY_BARCODE:
                try {
                    if (Variables.flag_barcode_knowed) {
                        Variables.flag_barcode_knowed = false;
                        fragmentScannerNotKnowned.dismiss();
                        JSONObject product = data.getJSONObject("data");
                        JSONObject brand_name = product.getJSONObject("brand");
                        JSONObject category_name = product.getJSONObject("category");

                        Bundle bundle = new Bundle();
                        bundle.putInt("id", product.getInt("id"));
                        bundle.putString("name", product.getString("name"));
                        bundle.putString("brand", brand_name.getString("name"));
                        bundle.putString("barcode", product.getString("barcode"));
                        bundle.putString("category", category_name.getString("name"));
                        fragmentScannerKnowned.setArguments(bundle);
                        fragmentScannerKnowned.show(getSupportFragmentManager(), "MyScanKnownedFragment");

                    } else {
                        String error = "error";
                        if (data.getString("data").equals(error)) {
                            Bundle bundle = new Bundle();
                            bundle.putString("barcode", barcode);
                            fragmentScannerNotKnowned.setArguments(bundle);
                            fragmentScannerNotKnowned.show(getSupportFragmentManager(), "MyScanNotKnownedFragment");
                        } else {
                            JSONObject product = data.getJSONObject("data");
                            JSONObject brand_name = product.getJSONObject("brand");
                            JSONObject category_name = product.getJSONObject("category");

                            Bundle bundle = new Bundle();
                            bundle.putInt("id", product.getInt("id"));
                            bundle.putString("name", product.getString("name"));
                            bundle.putString("brand", brand_name.getString("name"));
                            bundle.putString("barcode", product.getString("barcode"));
                            bundle.putString("category", category_name.getString("name"));
                            fragmentScannerKnowned.setArguments(bundle);
                            fragmentScannerKnowned.show(getSupportFragmentManager(), "MyScanKnownedFragment");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case GET_CATEGORY_BY_NAME:
                if (type_of_request == "category_father") {
                    try {
                        JSONArray jsonArray = data.getJSONArray("data");
                        n_of_Parents = jsonArray.length();
                        id_name_Father = new JSONArray();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject id_name_Father_JSON = new JSONObject();
                            id_name_Father_JSON.put("id",jsonArray.getJSONObject(i).getInt("id"));
                            id_name_Father_JSON.put("name",jsonArray.getJSONObject(i).getString("name"));
                            id_name_Father.put(id_name_Father_JSON);
                        }
                        myCategoryFatherAdapter = new MyCategoryFatherAdapter(getApplicationContext(), id_name_Father, category_dropdown, id_quantity_Sons_Expandable, this);
                        expandableListView.setAdapter(myCategoryFatherAdapter);

                        if (state == "first_request"){
                            type_of_request = "get_shopping_list";
                            getShoppingListById(Variables.shoppingListID_actual);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type_of_request == "category_son_on_change") {
                    try {
                        shoppingListLayout.setVisibility(View.INVISIBLE);
                        categoryFatherLayout.setVisibility(View.INVISIBLE);
                        JSONArray jsonObject = data.getJSONArray("data");
                        subcategories_recycler = new JSONArray();
                        for (int i = 0; i < jsonObject.length(); i++) {
                            JSONObject subcategories_recycler_JSONObject = new JSONObject();
                            subcategories_recycler_JSONObject.put("id", jsonObject.getJSONObject(i).getInt("id"));
                            subcategories_recycler_JSONObject.put("name", jsonObject.getJSONObject(i).getString("name"));
                            subcategories_recycler_JSONObject.put("quantity", 0);
                            subcategories_recycler.put(subcategories_recycler_JSONObject);
                        }
                        MyCategoryAdapter myCategoryAdapter = new MyCategoryAdapter(this, subcategories_recycler, this);
                        recyclerView_son.setAdapter(myCategoryAdapter);
                        recyclerView_son.setLayoutManager(new LinearLayoutManager(this));
                        categoryByNameLayout.setVisibility(View.VISIBLE);
                        if (jsonObject.length() == 0){
                            noResults.setVisibility(View.VISIBLE);
                        }else {
                            noResults.setVisibility(View.INVISIBLE);
                        }
                        total_itens = 0;
                        toolbarTextView.setText("Total: " + total_itens);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (type_of_request == "get_id_from_category_name") {
                    try {
                        JSONArray jsonObject = data.getJSONArray("data");
                        JSONObject category = jsonObject.getJSONObject(0);
                        categoryFatherID = Integer.parseInt(category.getString("categoryId"));
                        FragmentProductsFromCategory fragment = new FragmentProductsFromCategory();
                        Bundle bundle = new Bundle();
                        bundle.putString("categoryID", String.valueOf(category.getInt("id")));
                        bundle.putString("categoryName", category.getString("name"));
                        bundle.putString("categoryFatherID", category.getString("categoryId"));
                        fragment.setArguments(bundle);
                        fragment.show(getSupportFragmentManager(), "MyFragment");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case GET_SHOPPINGLIST_BY_ID:
                if (type_of_request == "get_shopping_list"){
                    try {
                        if (data.getString("data").equals(error)){
                            Toast.makeText(this, "Wrong: getshoppinglist", Toast.LENGTH_SHORT).show();
                        }else{
                            shopping_list.clear();
                            categoryFatherLayout.setVisibility(View.INVISIBLE);
                            categoryByNameLayout.setVisibility(View.INVISIBLE);
                            JSONObject jsonObject = data.getJSONObject("data");
                            JSONArray shoppingItems = jsonObject.getJSONArray("shoppingListItems");
                            total_not_bought = 0;
                            for (int i = 0; i < n_of_Parents; i++){
                                ArrayList<Object> items = new ArrayList<>();
                                for (int j = 0; j < shoppingItems.length(); j++) {
                                    if (!shoppingItems.getJSONObject(j).getBoolean("bought")){
                                        if (shoppingItems.getJSONObject(j).isNull("product")) {
                                            if (shoppingItems.getJSONObject(j).getJSONObject("category").getInt("categoryId") == id_name_Father.getJSONObject(i).getInt("id")) {
                                                total_not_bought++;
                                                items.add(new MyShoppingListItemNotBought(shoppingItems.getJSONObject(j).getInt("id"),shoppingItems.getJSONObject(j).getJSONObject("category").getString("name"), shoppingItems.getJSONObject(j).getInt("quantity")));
                                            }
                                        } else {
                                            if (shoppingItems.getJSONObject(j).getJSONObject("product").getJSONObject("category").getInt("categoryId") == id_name_Father.getJSONObject(i).getInt("id")) {
                                                total_not_bought++;
                                                items.add(new MyShoppingListItemNotBought(shoppingItems.getJSONObject(j).getInt("id"),shoppingItems.getJSONObject(j).getJSONObject("product").getString("name") + " - " + shoppingItems.getJSONObject(j).getJSONObject("product").getJSONObject("brand").getString("name"), shoppingItems.getJSONObject(j).getInt("quantity")));
                                            }
                                        }
                                    }
                                }
                                for (int j = 0; j < shoppingItems.length(); j++) {
                                    if (shoppingItems.getJSONObject(j).getBoolean("bought")){
                                        if (shoppingItems.getJSONObject(j).isNull("product")) {
                                            if (shoppingItems.getJSONObject(j).getJSONObject("category").getInt("categoryId") == id_name_Father.getJSONObject(i).getInt("id")) {
                                                items.add(new MyShoppingListItemBought(shoppingItems.getJSONObject(j).getInt("id"),shoppingItems.getJSONObject(j).getJSONObject("category").getString("name"), shoppingItems.getJSONObject(j).getInt("quantity")));
                                            }
                                        } else {
                                            if (shoppingItems.getJSONObject(j).getJSONObject("product").getJSONObject("category").getInt("categoryId") == id_name_Father.getJSONObject(i).getInt("id")) {
                                                items.add(new MyShoppingListItemBought(shoppingItems.getJSONObject(j).getInt("id"),shoppingItems.getJSONObject(j).getJSONObject("product").getString("name") + " - " + shoppingItems.getJSONObject(j).getJSONObject("product").getJSONObject("brand").getString("name"), shoppingItems.getJSONObject(j).getInt("quantity")));
                                            }
                                        }
                                    }
                                }
                                if (items.size() != 0) {
                                    shopping_list.add(id_name_Father.getJSONObject(i).getString("name"));
                                    for (int k = 0; k < items.size(); k++) {
                                        shopping_list.add(items.get(k));
                                    }
                                }
                            }
                            toolbarTextView.setText("Total: "+ total_not_bought);
                            shoppingListAdapter = new MyShoppingList(this, shopping_list, this);
                            shoppingList.setAdapter(shoppingListAdapter);
                            shoppingList.setSelection(listView_position);
                            Variables.flag_update_shopping_list_activity = false;
                            Variables.flag_update_shopping_list_fragment = false;
                            if (shoppingItems.length() == 0){
                                listEmpty.setVisibility(View.VISIBLE);
                                shoppingListLayout.setVisibility(View.INVISIBLE);
                            }else {
                                listEmpty.setVisibility(View.INVISIBLE);
                                shoppingListLayout.setVisibility(View.VISIBLE);
                                toolbarTextView.setText("Total: "+ total_not_bought);
                            }
                            button_clear.setVisibility(View.GONE);
                            search_bar.setFocusable(true);
                            if (Variables.flag_no_list) {
                                Variables.shoppingListName = data.getJSONObject("data").getString("name");
                                listName.setText(Variables.shoppingListName);
                                Variables.flag_no_list = false;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(type_of_request == "add_or_update_categories"){
                    try {
                        JSONObject shoppingList_json = data.getJSONObject("data");
                        JSONArray shoppingListItems_json = shoppingList_json.getJSONArray("shoppingListItems");
                        if (flag_recycler_atual){
                            if (shoppingListItems_json.length() != 0){
                                for (int j=0; j<subcategories_recycler.length(); j++) {
                                    if (subcategories_recycler.getJSONObject(j).getInt("quantity") != 0){
                                        for (int i=0; i<shoppingListItems_json.length(); i++) {
                                            if (shoppingListItems_json.getJSONObject(i).isNull("product")) {
                                                if (shoppingListItems_json.getJSONObject(i).getJSONObject("category").getInt("id") == subcategories_recycler.getJSONObject(j).getInt("id")){
                                                    int idItem = shoppingListItems_json.getJSONObject(i).getInt("id");
                                                    JSONObject send_data_update = new JSONObject();
                                                    send_data_update.put("quantity", subcategories_recycler.getJSONObject(j).getInt("quantity"));
                                                    items_to_add++;
                                                    flag_updated_quantity = true;
                                                    updateShoppingListItemQuantity(Variables.shoppingListID_actual, idItem, send_data_update);
                                                }
                                            }
                                        }
                                        if (!flag_updated_quantity){
                                            JSONObject send_data_add = new JSONObject();
                                            send_data_add.put("categoryId", subcategories_recycler.getJSONObject(j).getInt("id"));
                                            send_data_add.put("quantity", subcategories_recycler.getJSONObject(j).getInt("quantity"));
                                            items_to_add++;
                                            addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                                        }else{
                                            flag_updated_quantity = false;
                                        }
                                    }
                                }
                            }else{
                                for (int j=0; j<subcategories_recycler.length(); j++) {
                                    if (subcategories_recycler.getJSONObject(j).getInt("quantity") != 0){
                                        JSONObject send_data_add = new JSONObject();
                                        send_data_add.put("categoryId", subcategories_recycler.getJSONObject(j).getInt("id"));
                                        send_data_add.put("quantity", subcategories_recycler.getJSONObject(j).getInt("quantity"));
                                        items_to_add++;
                                        addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                                    }
                                }
                            }
                        }else{
                            if (shoppingListItems_json.length() != 0){
                                for (int j=0; j<id_quantity_Sons_Expandable.length(); j++) {
                                    if (id_quantity_Sons_Expandable.getJSONObject(j).getInt("quantity") != 0){
                                        for (int i=0; i<shoppingListItems_json.length(); i++) {
                                            if (shoppingListItems_json.getJSONObject(i).isNull("product")) {
                                                if (shoppingListItems_json.getJSONObject(i).getJSONObject("category").getInt("id") == id_quantity_Sons_Expandable.getJSONObject(j).getInt("id")){
                                                    int idItem = shoppingListItems_json.getJSONObject(i).getInt("id");
                                                    JSONObject send_data_update = new JSONObject();
                                                    send_data_update.put("quantity", id_quantity_Sons_Expandable.getJSONObject(j).getInt("quantity"));
                                                    items_to_add++;
                                                    flag_updated_quantity = true;
                                                    updateShoppingListItemQuantity(Variables.shoppingListID_actual,idItem,send_data_update);
                                                }
                                            }
                                        }
                                        if (!flag_updated_quantity){
                                            JSONObject send_data_add = new JSONObject();
                                            send_data_add.put("categoryId", id_quantity_Sons_Expandable.getJSONObject(j).getInt("id"));
                                            send_data_add.put("quantity", id_quantity_Sons_Expandable.getJSONObject(j).getInt("quantity"));
                                            items_to_add++;
                                            addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                                        }else{
                                            flag_updated_quantity = false;
                                        }
                                    }
                                }
                            }else{
                                for (int j=0; j<id_quantity_Sons_Expandable.length(); j++) {
                                    if (id_quantity_Sons_Expandable.getJSONObject(j).getInt("quantity") != 0){
                                        JSONObject send_data_add = new JSONObject();
                                        send_data_add.put("categoryId", id_quantity_Sons_Expandable.getJSONObject(j).getInt("id"));
                                        send_data_add.put("quantity", id_quantity_Sons_Expandable.getJSONObject(j).getInt("quantity"));
                                        items_to_add++;
                                        addItemToShoppingList(Variables.shoppingListID_actual, send_data_add);
                                    }
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                break;
            case ADD_ITEM_TO_SHOPPINGLIST:
            case UPDATE_SHOPPINGLIST_ITEM_QUANTITY:
                try {
                    items_to_add--;
                    if (items_to_add == 0) {
                        if (data.getString("data").equals(error)) {
                            Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Added to list successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
                mMainAddFab.setImageResource(R.drawable.icon_add_white);
                total_itens = 0;
                search_bar.setText("");
                search_bar.clearFocus();
                type_of_request = "get_shopping_list";
                state = "";
                getShoppingListById(Variables.shoppingListID_actual);
                break;
            case GET_ALL_ITEMS_TO_ADD_LIST:
                try {
                    numbernotificationCounter = data.getJSONArray("data").length();
                    if (data.getJSONArray("data").length() != 0){
                        if (flag_numbernotificationCounter){
                            Intent notificationIntent = new Intent(MainActivity.this, NotificationService.class);
                            ContextCompat.startForegroundService(MainActivity.this,notificationIntent);
                            flag_numbernotificationCounter = false;
                        }
                        notificationCounter.setText(String.valueOf(numbernotificationCounter));
                        notificationCounterContainer.setVisibility(View.VISIBLE);
                    }else{
                        notificationCounterContainer.setVisibility(View.INVISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}