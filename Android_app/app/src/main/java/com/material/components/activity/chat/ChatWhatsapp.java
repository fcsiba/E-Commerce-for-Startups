package com.material.components.activity.chat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.adapter.AdapterChatWhatsapp;
import com.material.components.model.Message;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;

public class ChatWhatsapp extends AppCompatActivity {

    private FloatingActionButton btn_send;
    private EditText et_content;
    private AdapterChatWhatsapp adapter;
    private RecyclerView recycler_view;
    private TextView lblTitle, lblStatus;
    private ActionBar actionBar;
    RequestQueue queue;
    String username;
    String name = "Fusion";
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    LinearLayout lyt_progress;
    int type = 3;
    String db = "";
    Integer cID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_whatsapp);

        // Check whether the user has granted us the READ/WRITE_EXTERNAL_STORAGE permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Request both READ_EXTERNAL_STORAGE and WRITE_EXTERNAL_STORAGE so that the
            // Pushy SDK will be able to persist the device token in the external storage
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        db = pref.getString("dbName", "");
        editor = pref.edit();

        Bundle extras = getIntent().getExtras();
        try {
            username = extras.getString("username");
            type = extras.getInt("typeN");
            Log.d("data.fastech.pk", "Username: " + username);
            Log.d("data.fastech.pk", "Type Ini: " + type);
            if (type == 1)
                name = extras.getString("name", "Fusion");
        } catch (Exception x) {
            username = pref.getString("username", "");
            type = 3;
            Log.d("data.fastech.pk", "From Pref: " + username);
        }
        cID = pref.getInt("campusID", 0);
        initToolbar();
        iniComponent();
    }

    public void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setHomeButtonEnabled(false);
        actionBar.setTitle(null);
        Tools.setSystemBarColor(this);
    }

    public void iniComponent() {
        recycler_view = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recycler_view.setLayoutManager(layoutManager);
        recycler_view.setHasFixedSize(true);

        lblTitle = findViewById(R.id.lblTitle);
        lblStatus = findViewById(R.id.lblStatus);

        lblTitle.setText(name);
        lblStatus.setText("Online");

        adapter = new AdapterChatWhatsapp(this);
        recycler_view.setAdapter(adapter);

        //Loading
        lyt_progress = findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
//        lyt_progress.setAlpha(1.0f);

        LoadChat();

        btn_send = findViewById(R.id.btn_send);
        et_content = findViewById(R.id.text_content);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendChatToServer();
            }
        });
        et_content.addTextChangedListener(contentWatcher);

        (findViewById(R.id.lyt_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        (findViewById(R.id.btnBack)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        (findViewById(R.id.btnCamera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Upgrade to Pro version.", Toast.LENGTH_SHORT).show();
            }
        });

        (findViewById(R.id.btnEmoji)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Upgrade to Pro version.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendChatToServer() {
        final String msg = et_content.getText().toString();
        if (msg.isEmpty()) return;
        if (msg.length() == 0) return;
        //Send Volley
        SendToServer();
    }

    private void sendChat() {
        final String msg = et_content.getText().toString();
        if (msg.isEmpty()) return;
        if (msg.length() == 0) return;
        adapter.insertItem(new Message(adapter.getItemCount(), msg, true, adapter.getItemCount() % 5 == 0, Tools.getFormattedTimeEvent(System.currentTimeMillis())));
        et_content.setText("");
        recycler_view.scrollToPosition(adapter.getItemCount() - 1);
    }

    private void SendToServer() {
        Log.d("data.fastech.pk", "Type: " + type);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://pushy.fastech.pk/Home/SendMsg?number=" + username + "&msg=" + et_content.getText() + "&sender=2";
        if (type == 1)
            url = "http://pushy.fastech.pk/Home/SendMsg?number=" + username + "&msg=" + et_content.getText() + "&sender=ByAdmin";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Convert String to json object
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            Log.d("data.fastech.pk", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        // get value from  Json Object
                        try {
                            String status = json.getString("status");
                            if (status.equals("Success")) {
                                sendChat();
                            } else {
                                Toast.makeText(getApplicationContext(), "Unable to send.", Toast.LENGTH_SHORT).show();
                                Log.d("data.fastech.pk", "Status: " + status);
                            }
                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Unable to send.", Toast.LENGTH_SHORT).show();
                            Log.d("data.fastech.pk", e.getLocalizedMessage());
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(stringRequest);
    }

    private void LoadChat() {

        String url = "http://app.fastech.pk/Fill/GetMsgs?username=" + username + "&cID=" + cID + "&_db=" + db + "";
        Log.d("data.fastech.pk", "Chat url: " + url);
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String msg = jsonObject.getString("message");
                                String side = jsonObject.getString("sender");
                                String policyEffective = jsonObject.getString("date");
                                long time = Long.parseLong(policyEffective.replaceFirst("^.*Date\\((\\d+)\\).*$", "$1"));
                                Date date = new Date(time);
                                String newstring;
                                SimpleDateFormat df = new SimpleDateFormat("dd-MM hh:mm aa");
                                newstring = df.format(date);

                                Boolean mySide = false;
                                if (side.equals("2"))
                                    mySide = true;

                                if (type == 1)
                                    mySide = !mySide;

                                adapter.insertItem(new Message(adapter.getItemCount(), msg, mySide, adapter.getItemCount() % 5 == 0, newstring));

                                Log.d("data.fastech.pk", msg);
                            } catch (JSONException e) {
                                Log.d("data.fastech.pk", e.getLocalizedMessage());
                            }
                        }

                        recycler_view.scrollToPosition(adapter.getItemCount() - 1);

                        ViewAnimation.fadeOut(lyt_progress);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("data.fastech.pk", volleyError.getMessage());
                        AlertDialog alertDialog = new AlertDialog.Builder(ChatWhatsapp.this).create();
                        alertDialog.setTitle("Unable to load chat");
                        alertDialog.setMessage(volleyError.getMessage());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CLOSE",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        alertDialog.show();
                    }
                });
        queue.add(request);
    }

    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hideKeyboard();
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher contentWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable etd) {
        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }

        @Override
        public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat_whatsapp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle() + " clicked", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}

