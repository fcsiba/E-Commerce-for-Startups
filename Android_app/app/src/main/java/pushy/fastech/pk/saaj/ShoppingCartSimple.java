package pushy.fastech.pk.saaj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.adapter.AdapterGridShopProductCard;
import com.material.components.utils.Tools;
import com.material.components.widget.SpacingItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShoppingCartSimple extends AppCompatActivity {

    MaterialRippleLayout btnCheckout;
    private View parent_view;
    private RecyclerView recyclerView;
    private AdapterCart mAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Integer uID;
    TextView lblPrice;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart_main);
        parent_view = findViewById(R.id.parent_view);
        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Cart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);

        lblPrice = (TextView) findViewById(R.id.txtTotalPrice);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        uID = pref.getInt("uID", 0);

        LoadData();

        btnCheckout = (MaterialRippleLayout) findViewById(R.id.btn_Checkout);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (!lblPrice.getText().equals("Rs. 0"))
               {
                   Intent intent = new Intent(getApplicationContext(), ShoppingCheckoutStep.class);
                   startActivity(intent);
               }
               else
               {
                   snackBarIconError("Cart is empty.");
               }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        Tools.changeMenuIconColor(menu, getResources().getColor(R.color.grey_60));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoadData(){
        //Load Cart
        final List<Cart> items = new ArrayList<>();
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = LoginSimpleGreen.domain + "/App/GetCart?uID=" + uID + "";
        try {

            Log.d(LoginSimpleGreen.tag, "Loading cart: " + url);
            final String imgPath = "http://saajapparels.net/Uploads/Products/";
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d(LoginSimpleGreen.tag, "Loading cart data");
                            try {
                                Integer totalPrice = 0;
                                JSONArray jsonArray = response.getJSONArray("Data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Cart c = new Cart();
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setCartID(jsonObject.getInt("cartID"));
                                        c.setItemName(jsonObject.getString("itemName"));
                                        c.setPic(jsonObject.getString("pic"));
                                        c.setItemQty(Math.round(jsonObject.getDouble("itemQty")) + "");
                                        c.setNetPrice(jsonObject.getDouble("netPrice") + "");
                                        c.setUserMob(uID);
                                        c.setPr( Math.round(jsonObject.getDouble("pr"))  + "");
                                        c.setSize(jsonObject.getString("size"));;
                                        totalPrice += (int) Math.round(jsonObject.getDouble("pr"));
                                        items.add(c);
                                        //Log.d(LoginSimpleGreen.tag, "Count: " + i + 1);
                                    } catch (JSONException e) {
                                        Log.e(LoginSimpleGreen.tag, "C: " + e.getLocalizedMessage());
                                    }
                                }
                                lblPrice.setText("Rs. " + totalPrice);
                                Log.d(LoginSimpleGreen.tag, "Items: " + items.size());
                                //set data and list adapter
                                mAdapter = new AdapterCart(getApplicationContext(), items);
                                recyclerView.setAdapter(mAdapter);

                                // on item list clicked - remove from cart
                                mAdapter.setOnItemClickListener(new AdapterCart.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(View view, Cart obj, int pos) {
                                        Log.d(LoginSimpleGreen.tag, "Remove: " + obj.getCartID() + " from cart - " + pos);
                                        Remove(obj.getCartID(), pos, obj.getPr(), obj);
                                    }
                                });

//                                mAdapter.setOnMoreButtonClickListener(new AdapterGridShopProductCard.OnMoreButtonClickListener() {
//                                    @Override
//                                    public void onItemClick(View view, Products obj, MenuItem item) {
//                                        Snackbar.make(parent_view, obj.getItemName() + " (" + item.getTitle() + ") clicked", Snackbar.LENGTH_SHORT).show();
//                                    }
//                                });

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

    public void Add(View v) {
        ImageButton bt = (ImageButton) v;
        bt.setEnabled(false);
        String code = bt.getTag().toString();
        Log.d(LoginSimpleGreen.tag, "Code-Add: " + code);
        Integer itemID = Integer.parseInt(code.split("~")[0].trim());
        String size = code.split("~")[1].trim();
        Integer pos = Integer.parseInt(code.split("~")[2].trim());
        Cart c = mAdapter.GetItem(pos);

        //Add request
        AddMore(itemID, size, 1, bt, c, pos);
    }

    public void Sub(View v) {
        ImageButton bt = (ImageButton) v;
        bt.setEnabled(false);
        String code = bt.getTag().toString();
        Log.d(LoginSimpleGreen.tag, "Code-Add: " + code);
        Integer itemID = Integer.parseInt(code.split("~")[0].trim());
        String size = code.split("~")[1].trim();
        Integer pos = Integer.parseInt(code.split("~")[2].trim());
        Cart c = mAdapter.GetItem(pos);
        //Add request
        AddMore(itemID, size, -1, bt, c, pos);
    }

    public  void AddMore(final Integer code, final String size, final Integer _qty, final  ImageButton bt, final Cart c, final Integer pos)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://saajapparels.net/App/AddToCart";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            if(response.contains("error"))
                            {
                                String error = response.substring(26, response.length() - 2);
                                snackBarIconError(error);
                                bt.setEnabled(true);
                            }
                            else
                            {
                                //Update recycler view
                                c.setItemQty(Integer.parseInt(c.getItemQty()) + _qty + "");
                                Integer diff = Math.round(Float.parseFloat(c.getNetPrice()) * _qty);
                                c.setPr(Math.round(Float.parseFloat(c.getNetPrice()) * Integer.parseInt( c.getItemQty())) + "");
                                String lastTotal  = lblPrice.getText().toString().split(" ")[1].trim();
                                Log.d(LoginSimpleGreen.tag, "Last total: " + lastTotal);
                                Integer pre = Integer.parseInt(lastTotal);
                                lblPrice.setText("Rs. " + (pre + diff));

                                mAdapter.items.set(pos, c);
                                mAdapter.notifyItemChanged(pos);

                                if (_qty > 0)
                                    snackBarIconInfo("Added to cart.");
                                else
                                    snackBarIconInfo("Removed from cart.");

                                bt.setEnabled(true);
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(LoginSimpleGreen.tag,"error => "+error.toString());
                            bt.setEnabled(true);
                        }
                    }
            ) {
                // this is the relevant method
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("id", code + "");
                    params.put("uID", uID + "");
                    params.put("qty", _qty + "");
                    params.put("size", size + "");
                    return params;
                }
            };
            queue.add(postRequest);
        }
        catch (Exception x){ Log.e(LoginSimpleGreen.tag, "Catch: "+ x.getLocalizedMessage()); bt.setEnabled(true); }
    }


    public  void Remove(final Integer id, final Integer pos, final String amount, final Cart obj)
    {
        try {
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            String url = "http://saajapparels.net/App/RemoveFromCart";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response) {
                            if(response.contains("error"))
                            {
                                String error = response.substring(26, response.length() - 2);
                                snackBarIconError(error);
                            }
                            else
                            {
                                //Update recycler view
                                mAdapter.items.remove(obj);
                                mAdapter.notifyItemChanged(pos);
                                mAdapter.notifyItemRangeRemoved(pos, 1);
                                mAdapter.notifyDataSetChanged();

                                String lastTotal  = lblPrice.getText().toString().split(" ")[1].trim();
                                Integer pre = Integer.parseInt(lastTotal);
                                Integer amn = Integer.parseInt(amount);
                                lblPrice.setText("Rs. " + (pre - amn));
                                snackBarIconError("Removed from cart.");
                            }
                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(LoginSimpleGreen.tag,"error => "+error.toString());
                        }
                    }
            ) {
                // this is the relevant method
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("id", id + "");
                    params.put("uID", uID + "");
                    return params;
                }
            };
            queue.add(postRequest);
        }
        catch (Exception x){ Log.e(LoginSimpleGreen.tag, "Catch: "+ x.getLocalizedMessage()); }
    }



    private void snackBarIconInfo(String txt) {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(txt);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_error_outline);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.blue_500));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private void snackBarIconError(String txt) {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(txt);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

}
