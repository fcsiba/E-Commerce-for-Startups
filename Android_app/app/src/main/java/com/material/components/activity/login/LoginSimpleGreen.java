package com.material.components.activity.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.material.components.R;
import com.material.components.activity.profile.ProfileCardHeader;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import pushy.fastech.pk.admin.adminportal.AdminDashboard;
import pushy.fastech.pk.saaj.ShoppingCategoryImage;
import pushy.fastech.pk.staff.staffportal.StaffDashboard;
import pushy.fastech.pk.walkthrough.IntroActivityStaff;

public class LoginSimpleGreen extends AppCompatActivity {

    // Domain name
    public static String domain = "http://saajapparels.net/";
    public static String picPath = "http://saajapparels.net/Uploads/Products/";
    public static String tag = "saaj.fastech.pk";
    public static String firebaseApp = "gs://ssdsasdd.appspot.com/";

    private View parent_view;
    boolean passwordVisibility = false;
    ProgressBar progressBar;
    TextView txtSignUp;
    EditText txtUser, txtPw;
    Button btnLogin;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String username;
    Integer type;
    RequestQueue queue;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_simple_green);
        parent_view = findViewById(android.R.id.content);

        initToolbar();
        progressBar = findViewById(R.id.sign_up_progress);

        queue = Volley.newRequestQueue(getApplicationContext());

        //Check already logged IN
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();
        String user = pref.getString("name", "");
        Boolean loggedIn = pref.getBoolean("isLoggedIn", false);
        if (loggedIn) {
            Intent intent = new Intent(getApplicationContext(), ShoppingCategoryImage.class);
            startActivity(intent);
        }

        txtUser = findViewById(R.id.txtUsername);
        if (!user.equals(""))
            txtUser.setText(user);
        txtUser.setSelection(txtUser.getText().length());

        txtPw = findViewById(R.id.txtPw);
        txtPw.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (txtPw.getRight() - txtPw.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!passwordVisibility) {
                            passwordVisibility = true;
                            txtPw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_visibility_off, 0);
                            txtPw.setTransformationMethod(null);
                            txtPw.setSelection(txtPw.getText().length());
                        } else {
                            passwordVisibility = false;
                            txtPw.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_visibility, 0);
                            txtPw.setTransformationMethod(new PasswordTransformationMethod());
                            txtPw.setSelection(txtPw.getText().length());
                        }
                    }
                }
                return false;
            }
        });

        txtSignUp = findViewById(R.id.txt_sign_up);
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackBarIconInfo("Contact Institute Administration for account");
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternet()) {

                    if (txtUser.getText().length() == 0) {
                        snackBarIconError("Please enter username");
                        return;
                    }

                    if (txtPw.getText().length() == 0) {
                        snackBarIconError("Please enter password");
                        return;
                    }

                    btnLogin.setEnabled(false);
                    progressBar.setVisibility(View.VISIBLE);
                    username = txtUser.getText().toString();

                    Login(txtUser.getText().toString(), txtPw.getText().toString());

                } else
                    snackBarIconError("Please check internet");
            }
        });


    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, android.R.color.black);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Subscribe to premium account", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
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


    public boolean checkInternet() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getApplicationContext().getSystemService(getApplicationContext().CONNECTIVITY_SERVICE);

        boolean isConnected = false;
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            isConnected = (activeNetwork != null) && (activeNetwork.isConnectedOrConnecting());
        }

        return isConnected;
    }

    private void Login(final String user, final String pw) {

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,domain + "/Login/LoginApp?username=" + user + "&pw=" + pw,
                null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject resp = response.getJSONObject("Data");
                    Log.d(tag, resp.toString());

                    Integer uID = resp.getInt("uID");
                    if(uID > 0)
                    {
                        Integer localID = resp.getInt("localID");
                        String name = resp.getString("name");
                        editor.putInt("uID", uID);
                        editor.putInt("localID", localID);
                        editor.putString("name", name);
                        editor.putBoolean("isLoggedIn", true);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(), ShoppingCategoryImage.class);
                        startActivity(intent);
                    }
                    else
                    {
                        snackBarIconError("Invalid login!");
                        btnLogin.setEnabled(true);
                        progressBar.setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    snackBarIconError("Invalid login!");
                    btnLogin.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                }



            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(tag, "Error: " + error.getMessage());
                Log.e(tag, "Site Info Error: " + error.getMessage());
                snackBarIconError("Network error!");
                btnLogin.setEnabled(true);
                progressBar.setVisibility(View.GONE);


            }
        });
        queue.add(req);




        //end
//        JsonArrayRequest request = new JsonArrayRequest(domain + "/Login/LoginApp?username=" + user + "&pw=" + pw,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray jsonArray) {
//                        String db = "";
//                        for (int i = 0; i < jsonArray.length(); i++) {
//                            CampusDetails c = new CampusDetails();
//                            try {
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                Log.d("data.fastech.pk", "Response: " + jsonObject);
//                                c.setCampus_name(jsonObject.getString("campusName"));
//                                c.setDb_name(jsonObject.getString("db_name"));
//                                db = jsonObject.getString("db_name");
//                                Log.d("data.fastech.pk", "db_name: " + db);
//                                list.add(c);
//                            } catch (JSONException e) {
//                                Log.d("data.fastech.pk", e.getLocalizedMessage());
//                                //mEntries.add("Error: " + e.getLocalizedMessage());
//                            }
//                        }
//                        if (list.size() > 0) {
//                            editor.putInt("type", 1);
//                            editor.putString("username", user);
//                            editor.putString("dbName", db);
//                            editor.putString("pw", pw);
//                            editor.putBoolean("login", true);
//                            Gson gson = new Gson();
//                            String json = gson.toJson(list);
//                            editor.putString("campus", json);
//                            editor.commit();
//
//                            Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
//                            startActivity(intent);
//                            finish();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError volleyError) { //Admin login failed
//                        //snackBarIconError("Invalid Login.");
//                        Log.d("data.fastech.pk", "Trying user login:" + volleyError.getMessage());
//                        //Login(user, pw);
//
//
//                        Intent intent = new Intent(getApplicationContext(), ShoppingCategoryImage.class);
//                        startActivity(intent);
//                    }
//                });


    }

//    public void Login(final String _username, final String _pw) {
//        String url = domain + "/Login/LoginWeb?username=" + _username + "&pw=" + _pw + "&app=1";
//        Log.d("data.fastech.pk", "Login url: " + url);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
//                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.d("data.fastech.pk", "Response: " + response.toString());
//
//                        // Convert String to json object
//                        JSONObject json = null;
//                        try {
//                            json = new JSONObject(response.toString());
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        JSONObject jsonDetails = null;
//                        try {
//                            jsonDetails = json.getJSONObject("details");
//                            // get value from  Json Object
//                            try {
//                                Integer userID = jsonDetails.getInt("id");
//                                // User name and password already have
//                                String dbName = jsonDetails.getString("dbName");
//                                Integer clientID = jsonDetails.getInt("clientID");
//                                Boolean blocked = jsonDetails.getBoolean("isBlocked");
//                                Integer campusID = jsonDetails.getInt("campusID");
//                                type = jsonDetails.getInt("type");
//                                String deviceID = jsonDetails.getString("deviceID");
//                                Integer localID = jsonDetails.getInt("localID");
//
//                                if (blocked) {
//                                    editor.putBoolean("blocked", true);
//                                    editor.putInt("type", type);
//                                    editor.putBoolean("login", false);
//                                    editor.putString("dbName", dbName);
//                                    editor.putString("deviceID", deviceID);
//                                    editor.putInt("userID", userID);
//                                    editor.putString("username", _username);
//                                    editor.putString("pw", _pw);
//                                    editor.putInt("clientID", clientID);
//                                    editor.putInt("campusID", campusID);
//                                    editor.putInt("localID", localID);
//                                    editor.commit();
//                                    progressBar.setVisibility(View.GONE);
//                                    snackBarIconError("Your account is blocked.");
//                                    btnLogin.setEnabled(true);
//                                } else { //Navigate to Next Page
//                                    editor.putBoolean("blocked", false);
//                                    editor.putInt("type", type);
//                                    editor.putBoolean("login", true);
//                                    editor.putString("dbName", dbName);
//                                    editor.putString("deviceID", deviceID);
//                                    editor.putInt("userID", userID);
//                                    editor.putString("username", _username);
//                                    editor.putString("pw", _pw);
//                                    editor.putInt("clientID", clientID);
//                                    editor.putInt("campusID", campusID);
//                                    editor.putInt("localID", localID);
//                                    editor.commit();
//                                    username = _username;
//                                    registerDevice(dbName);
//
//                                    if (type <= 3) //Parents
//                                    {
//                                        progressBar.setVisibility(View.GONE);
//                                        Intent intent = new Intent(getApplicationContext(), ProfileCardHeader.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putInt("loginType", type);
//                                        intent.putExtras(bundle);
//                                        startActivity(intent);
//                                        finish();
//                                    } else if (type == 5) //Admin
//                                    {
//                                        progressBar.setVisibility(View.GONE);
//                                        Intent intent = new Intent(getApplicationContext(), AdminDashboard.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putInt("loginType", type);
//                                        intent.putExtras(bundle);
//                                        startActivity(intent);
//                                        finish();
//                                    } else if (type == 4) //Staff
//                                    {
//                                        Intent intent = new Intent(getApplicationContext(), IntroActivityStaff.class);
//                                        Bundle bundle = new Bundle();
//                                        bundle.putString("username", txtUser.getText().toString());
//                                        bundle.putInt("typeN", 2);
//                                        bundle.putInt("loginType", type);
//                                        intent.putExtras(bundle);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                progressBar.setVisibility(View.GONE);
//                                snackBarIconError("Invalid Login");
//                                btnLogin.setEnabled(true);
//                                Log.d("data.fastech.pk", "Wrong ID pw");
//                                e.printStackTrace();
//                            }
//                        } catch (JSONException e) {
//                            progressBar.setVisibility(View.GONE);
//                            snackBarIconError("Invalid Login");
//                            btnLogin.setEnabled(true);
//                            Log.d("data.fastech.pk", "Wrong ID pw");
//                            e.printStackTrace();
//                            //e.printStackTrace();
//                        }
//
//
//
//                    }
//                }, new Response.ErrorListener() {
//
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        // TODO: Handle error
//                        //lbl.setText("Error: " + error.getLocalizedMessage());
//                        //Log.d("data.fastech.pk", "Error: " + error.getLocalizedMessage());
//                        progressBar.setVisibility(View.GONE);
//                        snackBarIconError("Invalid Login");
//                        btnLogin.setEnabled(true);
//                    }
//                });
//
//        // Execute your request
//        queue.add(jsonObjectRequest);
//    }
}

