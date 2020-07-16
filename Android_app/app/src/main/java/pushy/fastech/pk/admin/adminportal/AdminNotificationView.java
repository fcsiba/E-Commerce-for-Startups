package pushy.fastech.pk.admin.adminportal;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import pushy.fastech.pk.adapters.AdminAdapterNotificationView;

public class AdminNotificationView extends AppCompatActivity {

    RequestQueue queue;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String username = "", dbName = "";
    private LinearLayout progressAttendanceActivity;
    private RecyclerView recyclerView;
    private AdminAdapterNotificationView mAdapter;
    List<Thread_Messages> items;
    FloatingActionButton btnNew;
    Integer type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_view);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        type = pref.getInt("type", 2);
        editor = pref.edit();
        progressAttendanceActivity = findViewById(R.id.progress_bar_attendance);

        initToolbar();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
        //iniData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        iniData();
    }

    private void iniData()
    {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        items = new ArrayList<>();

        btnNew = (FloatingActionButton) findViewById(R.id.btnNewNotification);
        btnNew.setVisibility(View.VISIBLE);

        btnNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminNotificationView.this, AdminNotification.class);
                startActivity(intent);
            }
        });

        // Show loader
        progressAttendanceActivity.setVisibility(View.VISIBLE);

        try {
            String url = "http://pushy.fastech.pk/Notifications/GetAllThreads?_db="+ dbName;
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
                            mAdapter = new AdminAdapterNotificationView(getApplicationContext(), items, AdminNotificationView.this, recyclerView);
                            recyclerView.setAdapter(mAdapter);

                            mAdapter.setOnItemClickListener(new AdminAdapterNotificationView.OnItemClickListener() {
                                @Override
                                public void onItemClick(View view, Thread_Messages obj, int position) {

                                }

                                @Override
                                public void onItemLongClick(View view, Thread_Messages obj, int position) {

                                }
                            });

                            // Hide loader
                            progressAttendanceActivity.setVisibility(View.GONE);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("data.fastech.pk", volleyError.getLocalizedMessage());
                        }
                    });

            // mEntries = new ArrayList<>();
            queue.add(request);
        } catch (Exception x) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_notification_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_notification_verify_all) {
            dialogVerifyAll();
        }
        return super.onOptionsItemSelected(item);
    }

    // Dialog Verify All
    private void dialogVerifyAll() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_verify_all);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_verify_all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Verifying all", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        ((AppCompatButton) dialog.findViewById(R.id.bt_deny_all)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}