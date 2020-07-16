package pushy.fastech.pk.saaj;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.adapter.AdapterGridShopProductCard;
import com.material.components.data.DataGenerator;
import com.material.components.model.People;
import com.material.components.model.ShopProduct;
import com.material.components.utils.Tools;
import com.material.components.widget.SpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ShoppingProductGrid extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterGridShopProductCard mAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Integer mainCategory = 0; //All by default

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_product_grid);
        parent_view = findViewById(R.id.parent_view);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Products");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        mainCategory = pref.getInt("mainCategory", 1);
        Log.d(LoginSimpleGreen.tag, "Main Category:" + mainCategory);
        Snackbar.make(parent_view, "Loading.. please wait..", Snackbar.LENGTH_SHORT).show();

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        final List<Products> items = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = LoginSimpleGreen.domain + "/App/GetProducts?mainCat=" + mainCategory + "&subCat=" + 0;
        try {
            Log.d(LoginSimpleGreen.tag, "Loading products: " + url);
            final String imgPath = "http://saajapparels.net/Uploads/Products/";
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(LoginSimpleGreen.tag, "Loading data");
                            try {
                                JSONArray jsonArray = response.getJSONArray("Data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Products c = new Products();
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setItemCode(jsonObject.getString("itemCode"));
                                        c.setItemName(jsonObject.getString("itemName"));
                                        c.setFabric(jsonObject.getString("fabric"));

                                        c.setMainCategory(jsonObject.getInt("mainCategory"));
                                        c.setSubCategory(jsonObject.getInt("subCategory"));
                                        c.setOriginalPrice(Float.parseFloat(jsonObject.getDouble("originalPrice") + ""));
                                        c.setDiscount(Float.parseFloat(jsonObject.getDouble("discount") + ""));
                                        c.setNetPrice(Float.parseFloat(jsonObject.getDouble("netPrice") + ""));
                                        c.setDescription(jsonObject.getString("description"));
                                        c.setCoverImage(imgPath + jsonObject.getString("coverImage"));
                                        JSONArray sizes = jsonObject.getJSONArray("sizes");
                                        List<String> sz = new ArrayList<>();
                                        for (int j = 0; j < sizes.length(); j++) //add sizes to array
                                        {
                                            String sObj = sizes.getString(j);
                                            //Log.d(LoginSimpleGreen.tag, "Size: " + c.getItemCode() + " - " +  sObj);
                                            sz.add(sObj);
                                        }

                                        c.setSizes(sz);
                                        items.add(c);
                                        //Log.d(LoginSimpleGreen.tag, "Count: " + i + 1);
                                    } catch (JSONException e) {
                                        Log.e(LoginSimpleGreen.tag, "C: " + e.getLocalizedMessage());
                                    }
                                }

                                Log.d(LoginSimpleGreen.tag, "Items: " + items.size());
                                //set data and list adapter
                                mAdapter = new AdapterGridShopProductCard(getApplicationContext(), items);
                                recyclerView.setAdapter(mAdapter);

                                // on item list clicked
                                mAdapter.setOnItemClickListener(new AdapterGridShopProductCard.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Products obj, int position) {
                                        Intent intent = new Intent(getApplicationContext(), ShoppingProductAdvDetails.class);
                                        intent.putExtra("obj", obj);
                                        startActivity(intent);
                                    }
                                });


                            } catch (JSONException e) {
                                Log.e(LoginSimpleGreen.tag, "C2: " +  e.getLocalizedMessage());
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e(LoginSimpleGreen.tag, "V: " + volleyError.getLocalizedMessage());
                        }
                    });

            queue.add(request);
        } catch (Exception x) {
            Log.e(LoginSimpleGreen.tag, "Catch last: " +  x.getLocalizedMessage());
        }





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_setting_new, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        AutoCompleteTextView searchTextView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, R.drawable.cursor);
        } catch (Exception e) {

        }

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.d(LoginSimpleGreen.tag, "Search: " + newText);
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if (item.getTitle().equals("Cart")){
            Intent intent = new Intent(getApplicationContext(), ShoppingCartSimple.class);
            startActivity(intent);
        }
        else if (item.getTitle().equals("Sort")){
           showBottomSheetDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_list_sub, null);

        ((TextView) view.findViewById(R.id.all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "All clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ((TextView) view.findViewById(R.id.shirts)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Shirts clicked", Toast.LENGTH_SHORT).show();
            }
        });

        ((TextView) view.findViewById(R.id.jeans)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Jeans clicked", Toast.LENGTH_SHORT).show();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }
}
