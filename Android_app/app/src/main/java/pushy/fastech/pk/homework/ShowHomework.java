package pushy.fastech.pk.homework;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.widget.ViewLoadingDotsBounce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pushy.fastech.pk.Helper.Homeworks;
import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.adapters.AdapterShowHomework;
import pushy.fastech.pk.staff.homework.PostHomework;

public class ShowHomework extends AppCompatActivity {

    private View parent_view;
    private RecyclerView recyclerView;
    private AdapterShowHomework mAdapter;
    private FloatingActionButton floatBtn, floatCalendar;
    private RelativeLayout changeAccount;
    private List<Homeworks> items;
    String oldDate = "";
    private RequestQueue queue;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String username = "", dbName = "";
    private Integer cID = 0, localID = 0;
    private ViewLoadingDotsBounce onScreenLoader;

    int dayOfMonth, month, year;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat df;
    String formattedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_homework);
        parent_view = findViewById(R.id.content);
        recyclerView = findViewById(R.id.recyclerViewShowHomework);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Homework");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        onScreenLoader = findViewById(R.id.progress_bar_homework);
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time: " + c);
        df = new SimpleDateFormat("EEEE, d MMM, yyyy");
        formattedDate = df.format(c);

        floatBtn = findViewById(R.id.btn_goto_post_homework);
        floatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowHomework.this, PostHomework.class);
                startActivity(intent);
            }
        });

        floatCalendar = findViewById(R.id.btn_date_post_homework);
        floatCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(ShowHomework.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        formattedDate = df.format(calendar.getTime());
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowHomework.this, SelectAccount.class);
                startActivity(intent);
            }
        });

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0);
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        cID = pref.getInt("campusID", 0);
        localID = pref.getInt("localID", 0);
        editor = pref.edit();

        items = new ArrayList<>();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(this, "hello", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void LoadData(String url, final Context context) {
        //On screen show loader
        onScreenLoader.setVisibility(View.VISIBLE);
        try {
            Log.d("data.fastech.pk", "Loading homework data: " + url);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.d("data.fastech.pk", "Loading data");
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                items.clear();
                                Log.d("data.fastech.pk", "Getting hw..");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if (!oldDate.equals(jsonObject.getString("dateStr"))) {
                                            items.add(new Homeworks(jsonObject.getString("dateStr"), true));
                                            oldDate = jsonObject.getString("dateStr");
                                            Integer id = jsonObject.getInt("id");
                                            String cls = jsonObject.getString("cls");
                                            String sec = jsonObject.getString("sec");
                                            String fac = jsonObject.getString("fac");
                                            String sub = jsonObject.getString("sub");
                                            String date = jsonObject.getString("dateStr");
                                            String hw = jsonObject.getString("homework");
                                            items.add(new Homeworks(id, cls, sec, fac, sub, date, hw));
                                        } else {
                                            Integer id = jsonObject.getInt("id");
                                            String cls = jsonObject.getString("cls");
                                            String sec = jsonObject.getString("sec");
                                            String fac = jsonObject.getString("fac");
                                            String sub = jsonObject.getString("sub");
                                            String date = jsonObject.getString("dateStr");
                                            String hw = jsonObject.getString("homework");
                                            items.add(new Homeworks(id, cls, sec, fac, sub, date, hw));
                                        }
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                //Hide on screen loader
                                onScreenLoader.setVisibility(View.GONE);

                                mAdapter = new AdapterShowHomework(context, items, ItemAnimation.NONE, recyclerView);
                                recyclerView.setAdapter(mAdapter);

                            } catch (JSONException e) {
                                Log.e("data.fastech.pk", e.getLocalizedMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            finish();
                            Toast.makeText(context, "Error: " + volleyError.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String url = LoginSimpleGreen.domain + "/Homework/Populate?tID=" + localID + "&cID=" + cID + "&_db=" + dbName;
        LoadData(url, ShowHomework.this); // Changed here, if does not work put GetActivityContext
    }
}
