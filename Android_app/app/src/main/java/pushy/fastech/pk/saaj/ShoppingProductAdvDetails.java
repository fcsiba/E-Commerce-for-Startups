package pushy.fastech.pk.saaj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class ShoppingProductAdvDetails extends AppCompatActivity {

    private View parent_view;
    private TextView tv_qty;
    Products pro;
    Integer selectedQty;
    Integer uID;
    String selectedSize;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static int[] array_color_fab = {
            R.id.fab_color_blue,
            R.id.fab_color_pink,
            R.id.fab_color_grey,
            R.id.fab_color_green
    };

    private static int[] array_size_bt = new int[100];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_product_adv_details);
        parent_view = findViewById(R.id.parent_view);
        pro = (Products) getIntent().getSerializableExtra("obj");
        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Ecommerce");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        Tools.displayImageOriginal(this, (ImageView) findViewById(R.id.image), pro.getCoverImage());
        TextView title = (TextView) findViewById(R.id.lblPrdctName);
        TextView desc = (TextView) findViewById(R.id.lblPrdctDesc);
        TextView price = (TextView) findViewById(R.id.price);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        uID = pref.getInt("uID", 0);

        title.setText(pro.getItemName());
        desc.setText(pro.getItemCode());
        int val = Math.round(pro.getNetPrice());
        price.setText("Rs. " + val + "");

        LinearLayout ll = (LinearLayout)findViewById(R.id.layoutSizes);

        //Add sizes
        int i = 0;
        for (String item: pro.getSizes())
        {
            Button btn = new Button(this);
            btn.setText(item.replace("~", " : "));
            btn.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            if (i == 0){
                btn.setBackgroundResource(R.drawable.my_border);
                selectedQty = Integer.parseInt(item.split("~")[1]);
                selectedSize = item.split("~")[0].trim();
                Log.d(LoginSimpleGreen.tag, "Selected qty: " + selectedQty + " : " + selectedSize);
            }
            else {
                btn.setBackgroundResource(R.drawable.btn_rounded_green_selection);
            }
            btn.setPadding(25, 10, 25,10);
            btn.setId(i*100);
            array_size_bt[i] = i * 100;
            i++;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSize(v);
                }
            });
            ll.addView(btn);
        }

        tv_qty = (TextView) findViewById(R.id.tv_qty);
        ((FloatingActionButton) findViewById(R.id.fab_qty_sub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty > 1) {
                    qty--;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_qty_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = Integer.parseInt(tv_qty.getText().toString());
                if (qty < selectedQty) {
                    qty++;
                    tv_qty.setText(qty + "");
                }
            }
        });

        ((AppCompatButton) findViewById(R.id.bt_add_to_cart)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                                      }
                                      else
                                      {
                                          snackBarIconInfo("Added to cart.");
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
                              params.put("id", pro.getId() + "");
                              params.put("uID", uID + "");
                              params.put("qty", tv_qty.getText() + "");
                              params.put("size", selectedSize + "");
                              return params;
                          }
                      };
                      queue.add(postRequest);
                  }
                  catch (Exception x){ Log.e(LoginSimpleGreen.tag, "Catch: "+ x.getLocalizedMessage()); }

            }
        });
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

    public void setSize(View v) {
        Button bt = (Button) v;
        bt.setEnabled(false);
        //bt.setTextColor(Color.WHITE);
        for (Integer id : array_size_bt) {
            if (v.getId() != id) {
                Button bt_unselect = (Button) findViewById(id);
                try
                {
                    bt_unselect.setEnabled(true);
                    bt_unselect.setTextColor(Color.BLACK);
                    bt_unselect.setBackgroundResource(R.drawable.btn_rounded_green_selection);
                }
                catch(Exception e){}

            }
            else
                {
                    tv_qty.setText("1");
                    v.setBackgroundResource(R.drawable.my_border);
                    selectedQty = Integer.parseInt(((Button) v).getText().toString().split(":")[1].trim());
                    selectedSize = ((Button) v).getText().toString().split(":")[0].trim();
                    Log.d(LoginSimpleGreen.tag, "Selected qty: " + selectedSize + " : " + selectedQty);
                }
        }
    }

  /*  public void setColor(View v) {
        ((FloatingActionButton) v).setImageResource(R.drawable.ic_done);
        for (int id : array_color_fab) {
            if (v.getId() != id) {
                ((FloatingActionButton) findViewById(id)).setImageResource(android.R.color.transparent);
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getTitle().equals("Cart")){
            Intent intent = new Intent(getApplicationContext(), ShoppingCartSimple.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
