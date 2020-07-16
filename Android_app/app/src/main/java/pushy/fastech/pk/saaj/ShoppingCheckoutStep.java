package pushy.fastech.pk.saaj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.balysv.materialripple.MaterialRippleLayout;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.fragment.FragmentConfirmation;
import com.material.components.fragment.FragmentPayment;
import com.material.components.fragment.FragmentShipping;
import com.material.components.utils.Tools;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCheckoutStep extends AppCompatActivity {

    MaterialRippleLayout btnCompleteOrder;
    private EditText txtName, txtMob, txtAddress, txtCity, txtCountry, txtBill, txtDC;
    private View parent_view;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Integer uID;
    TextView lblComp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_checkout_step);
        initToolbar();
        initComponent();

        btnCompleteOrder = (MaterialRippleLayout) findViewById(R.id.btnCompleteOrder);
        btnCompleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Save();
            }
        });
    }

    private void initComponent() {

        parent_view = findViewById(R.id.parent_view);
        txtName = (EditText) findViewById(R.id.txtName);
        txtMob = (EditText) findViewById(R.id.txtMob);
        txtAddress = (EditText) findViewById(R.id.txtAddress);
        txtCity = (EditText) findViewById(R.id.txtCity);
        txtCountry = (EditText) findViewById(R.id.txtCountry);
        txtBill = (EditText) findViewById(R.id.txtBill);
        lblComp = (TextView) findViewById(R.id.lblComp);
        txtDC = (EditText) findViewById(R.id.txtDC);
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        uID = pref.getInt("uID", 0);

    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.grey_60), PorterDuff.Mode.SRC_ATOP);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, android.R.color.white);
        Tools.setSystemBarLight(this);
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

    public void Save()
    {
        if (txtName.getText().length() != 0){
            if (txtMob.getText().length() != 0){
                if (txtAddress.getText().length() != 0){
                    if (txtBill.getText().length() != 0){
                        if (txtDC.getText().length() != 0){
                            if (txtCity.getText().length() == 0)
                                txtCity.setText("-");
                            if (txtCountry.getText().length() == 0)
                                txtCountry.setText("-");
                            try {
                                btnCompleteOrder.setEnabled(false);
                                lblComp.setText("Saving...");
                                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                                String url = "http://saajapparels.net/App/Checkout";
                                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                if(response.contains("error"))
                                                {
                                                    String error = response.substring(26, response.length() - 2);
                                                    snackBarIconError(error);
                                                    btnCompleteOrder.setEnabled(true);
                                                }
                                                else
                                                {
                                                    lblComp.setText("ORDER SAVED");
                                                    snackBarIconInfo("Order saved!");
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Intent intent = new Intent(getApplicationContext(), ShoppingCategoryImage.class);
                                                            startActivity(intent);
                                                        }
                                                    }, 2000);

                                                }
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                Log.d(LoginSimpleGreen.tag,"error => "+error.toString());
                                                btnCompleteOrder.setEnabled(true);
                                            }
                                        }
                                ) {
                                    // this is the relevant method
                                    @Override
                                    protected Map<String, String> getParams()
                                    {
                                        Map<String, String>  params = new HashMap<String, String>();
                                        params.put("uID", uID + "");
                                        params.put("name", txtName.getText() + "");
                                        params.put("address", txtAddress.getText() + "");
                                        params.put("mob", txtMob.getText() + "");
                                        params.put("city", txtCity.getText() + "");
                                        params.put("country", txtCountry.getText() + "");
                                        params.put("bill", txtBill.getText() + "");
                                        params.put("dc", txtDC.getText() + "");
                                        return params;
                                    }
                                };
                                queue.add(postRequest);
                            }
                            catch (Exception x){ Log.e(LoginSimpleGreen.tag, "Catch: "+ x.getLocalizedMessage());  btnCompleteOrder.setEnabled(true); }
                            finally {
                                lblComp.setText("COMPLETE ORDER");
                            }
                        }
                        else{
                            snackBarIconError("Enter delivery charges.");
                        }
                    }
                    else{
                        snackBarIconError("Enter bill amount.");
                    }
                }
                else{
                    snackBarIconError("Enter customer's address.");
                }
            }
            else{
                snackBarIconError("Enter mobile number.");
            }
        }
        else{
            snackBarIconError("Enter customer's name.");
        }

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

