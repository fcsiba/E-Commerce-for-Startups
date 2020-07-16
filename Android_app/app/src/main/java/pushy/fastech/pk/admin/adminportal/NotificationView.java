package pushy.fastech.pk.admin.adminportal;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.Helper.Thread_Messages;
import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.adapters.AdapterNotificationView;

public class NotificationView extends AppCompatActivity {

    RequestQueue queue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String username = "", dbName = "";
    private Integer cID = 0, localID = 0;
    private static LinearLayout progressAttendanceActivity;
    private RecyclerView recyclerView;
    private AdapterNotificationView mAdapter;
    List<Thread_Messages> items;
    FloatingActionButton btnNew;
    Integer type = 0;
    private BroadcastReceiver mNetworkReceiver;
    private static TextView tv_check_connection;
    private RelativeLayout changeAccount;
    // For Refreshing layout
    static Context context;
    static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        cID = pref.getInt("campusID",0);
        type = pref.getInt("type", 2);

        editor = pref.edit();
        progressAttendanceActivity = findViewById(R.id.progress_bar_attendance);
        tv_check_connection = findViewById(R.id.tv_check_connection);

        context = getApplicationContext();
        activity = this;

        initToolbar();


        changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationView.this, SelectAccount.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        if (type == 1)
            getSupportActionBar().setTitle("Messages");
        else
            getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        iniData();
    }

    private void iniData() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        items = new ArrayList<>();

        btnNew = findViewById(R.id.btnNewNotification);
        btnNew.setVisibility(View.VISIBLE);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NotificationView.this, AdminNotification.class);
                startActivity(intent);
            }
        });

        // Show loader
        progressAttendanceActivity.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        try {
            String url = "http://pushy.fastech.pk/Notifications/GetThreads?number=" + username + "&_db=" + dbName;
            Log.d("data.fastech.pk", "Threads URL: " + url);
            JsonArrayRequest request = new JsonArrayRequest(url,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray jsonArray) {
                            items.clear();
                            Log.d("data.fastech.pk", "Loading Threads...");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Thread_Messages c = new Thread_Messages();
                                try {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    c.setId(jsonObject.getInt("id"));
                                    c.setTitle(jsonObject.getString("title"));
                                    c.setDesc(jsonObject.getString("description"));
                                    c.setBody(jsonObject.getString("body"));
                                    c.setReciept(jsonObject.getString("reciept"));
                                    c.setSenderNumber(jsonObject.getString("senderNumber"));
                                    items.add(c);
                                } catch (JSONException e) {
                                    Log.e("data.fastech.pk", e.getLocalizedMessage());
                                }
                            }

                            //Show Adapter
                            mAdapter = new AdapterNotificationView(getApplicationContext(), items, NotificationView.this, recyclerView);
                            recyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new AdapterNotificationView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Thread_Messages obj, int position) {

                                }

                                @Override
                                public void onItemLongClick(View view, Thread_Messages obj, int position) {

                                }

                            });

                            // Hide loader
                            progressAttendanceActivity.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            AlertDialog alertDialog = new AlertDialog.Builder(NotificationView.this).create();
                            alertDialog.setTitle("Error occurred");
                            alertDialog.setMessage("Please check your internet connection");
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
        } catch (Exception x) {
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    // Network receiver ================ Start ==================
    public static void dialogNotificationView(boolean value) {
        if (value) {
            try {
                tv_check_connection.setVisibility(View.VISIBLE);
                tv_check_connection.setText("Internet Connected");
                tv_check_connection.setBackgroundColor(Color.parseColor("#00b300"));
                tv_check_connection.setTextColor(Color.WHITE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Handler handler = new Handler();
            Runnable delay = new Runnable() {
                @Override
                public void run() {
                    try {
                        tv_check_connection.setVisibility(View.GONE);
                        refreshLyt();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(delay, 3000);
        } else {
            try {
                tv_check_connection.setVisibility(View.VISIBLE);
                tv_check_connection.setText("No Internet Connection");
                tv_check_connection.setBackgroundColor(Color.parseColor("#cc0000"));
                tv_check_connection.setTextColor(Color.WHITE);
                progressAttendanceActivity.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Handler handler = new Handler();
            Runnable delay = new Runnable() {
                @Override
                public void run() {
                    try {
                        tv_check_connection.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(delay, 3000);
        }
    }

    protected void unregisterNetworkChanges() {
        try {
            unregisterReceiver(mNetworkReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterNetworkChanges();
    }
    // Network receiver ================ End ==================

    // Refresh activity
    public static void refreshLyt() {
        context.startActivity(activity.getIntent());
        activity.finish();
    }
}