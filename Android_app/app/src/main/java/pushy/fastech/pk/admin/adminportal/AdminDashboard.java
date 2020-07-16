package pushy.fastech.pk.admin.adminportal;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.material.components.R;
import com.material.components.utils.ViewAnimation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import pushy.fastech.pk.admin.adminattendanceview.StaffAttendanceAdminView;
import pushy.fastech.pk.models.SearchModel;
import pushy.fastech.pk.receivers.NetworkChangeReceiver;

class CampusDetails{
    private String campus_name;
    private String db_name;

    public String getCampus_name() {
        return campus_name;
    }

    public void setCampus_name(String campus_name) {
        this.campus_name = campus_name;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }
}

class Department{

    private Integer id;

    private String department;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

public class AdminDashboard extends AppCompatActivity {

    static Context context;
    static Activity activity;
    static Boolean state = true;

    private int dayOfMonth, month, year;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat df, dfShort;
    private String formattedDate, formattedDateShort;
    private SwipeRefreshLayout swipe_refresh;
    private BroadcastReceiver mNetworkReceiver;
    static TextView tv_check_connection;
    private View parent_view;
    private CardView campusSelect, departmentSelect, dateSelect;
    private TextView campusSelect_txt, departmentSelect_txt, dateSelect_txt, lblStdTotal, lblStdPresent, lblStdAbsent, lblStdLeave;
    private TextView lblStaffTotal, lblStafPresent, lblStaffAbsent, lblStaffLeave, lblStaffLate;
    private TextView lblCollection_t, lblCollection_w, lblCollection_m, lblCollection_tf, lblCollection_other;
    private TextView lblDues_t, lblDues_current, lblDues_arrears, lblDues_tf, lblDues_other;
    private TextView lblRcvable, lblRcvd, lblBalance, lblBadDebts, lblSummmary;
    private TextView lblExpenses, lblOtherIncome, lblProfit;
    private CardView studentCard, staffCard, collectionCard, receivableCard, bankBalanceCard;
    private TextView campusTextChanged;
    private BarChart graphMarketSummary, graphDCR, graphReceivable;
    private PieChart graphStudentAttendance, graphStaffAttendance;
    private TextView dcrToday, dcrThisWeek, dcrThisMonth, receivableTotalStudents;
    private TextView studentPresentPercentage, studentAbsentPercentage, studentOnLeavePercentage;
    private TextView staffPresentPercentage, staffAbsentPercentage, staffOnLeavePercentage, staffLatePercentage;
    private TextView summaryDate, dcrDate, defaultersDate, studentDate, staffDate;
    private LinearLayout staffAbsentLyt, staffLeaveLyt, staffLateLyt;
    private LinearLayout studentAbsentLyt, studentLeaveLyt;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RequestQueue queue;
    String username, pw, selectedDB, firstCampus = "";
    List<CampusDetails> list = new ArrayList<>();
    List<Department> departmentList = new ArrayList<>();
    Integer dptID = 0;
    static LinearLayout lyt_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        pw = pref.getString("pw", "");
        editor = pref.edit();

        context = getApplicationContext();
        activity = this;

        lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);

        // Refresh layout
        swipe_refresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        // Date
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time: " + c);
        df = new SimpleDateFormat("EEE, d MMM, ''yy");
        dfShort = new SimpleDateFormat("EEEE, d MMM");
        formattedDate = df.format(c);
        formattedDateShort = dfShort.format(c);

        // Card dates
        summaryDate = findViewById(R.id.summary_date_txt);
        dcrDate = findViewById(R.id.dcr_date_txt);
        defaultersDate = findViewById(R.id.defaulter_date_txt);
        staffDate = findViewById(R.id.staff_date_txt);
        studentDate = findViewById(R.id.student_date_txt);

        // Graphs
        graphMarketSummary = findViewById(R.id.graph_market_summary);
        graphDCR = findViewById(R.id.graph_dcr);
        graphReceivable = findViewById(R.id.graph_receivable);
        graphStudentAttendance = findViewById(R.id.graph_student_attendance);
        graphStaffAttendance = findViewById(R.id.graph_staff_attendance);

        //Attendance - Student
        lblStdTotal = (TextView) findViewById(R.id.lblStdTotal);
        lblStdPresent = (TextView) findViewById(R.id.lblStdPresent);
        lblStdAbsent = (TextView) findViewById(R.id.lblStdAbsent);
        lblStdLeave = (TextView) findViewById(R.id.lblStdLeave);
        studentPresentPercentage = findViewById(R.id.students_present_percentage);
        studentAbsentPercentage = findViewById(R.id.students_absents_percentage);
        studentOnLeavePercentage = findViewById(R.id.students_on_leave_percentage);

        //Attendance - Staff
        lblStaffTotal = (TextView) findViewById(R.id.lblStaffTotal);
        lblStafPresent = (TextView) findViewById(R.id.lblStaffPresent);
        lblStaffAbsent = (TextView) findViewById(R.id.lblStaffAbsent);
        lblStaffLeave = (TextView) findViewById(R.id.lblStaffLeave);
        lblStaffLate = (TextView) findViewById(R.id.lblStaffLate);
        staffPresentPercentage = findViewById(R.id.staff_present_percentage);
        staffAbsentPercentage = findViewById(R.id.staff_absent_percentage);
        staffOnLeavePercentage = findViewById(R.id.staff_on_leave_percentage);
        staffLatePercentage = findViewById(R.id.staff_late_percentage);

        //Collection
        lblCollection_t = (TextView) findViewById(R.id.lblCollection_t);
        lblCollection_w = (TextView) findViewById(R.id.lblCollection_w);
        lblCollection_m = (TextView) findViewById(R.id.lblCollection_m);
        lblCollection_tf = (TextView) findViewById(R.id.lblCollection_tf);
        lblCollection_other = (TextView) findViewById(R.id.lblCollection_other);
        dcrToday = findViewById(R.id.dcr_today_student);
        dcrThisWeek = findViewById(R.id.dcr_this_week_student);
        dcrThisMonth = findViewById(R.id.dcr_this_month_student);

        //Dues
        lblDues_t = (TextView) findViewById(R.id.lblDues_t);
        lblDues_current = (TextView) findViewById(R.id.lblDues_current);
        lblDues_arrears = (TextView) findViewById(R.id.lblDues_arrears);
        lblDues_tf = (TextView) findViewById(R.id.lblDues_tf);
        lblDues_other = (TextView) findViewById(R.id.lblDues_others);
        receivableTotalStudents = findViewById(R.id.receivable_total_students);

        //Summary
        lblRcvable = (TextView) findViewById(R.id.lblRcvable);
        lblRcvd = (TextView) findViewById(R.id.lblRcvd);
        lblBalance = (TextView) findViewById(R.id.lblBalance);
        lblBadDebts = (TextView) findViewById(R.id.lblBadDebts);
        lblSummmary = (TextView) findViewById(R.id.lblSummary);
        lblExpenses = (TextView) findViewById(R.id.lblExpenses);
        lblOtherIncome = (TextView) findViewById(R.id.lblOtherIncome);
        lblProfit = (TextView) findViewById(R.id.lblProfit);

        //CardViews
        studentCard = (CardView) findViewById(R.id.student_card);
        staffCard = (CardView) findViewById(R.id.staff_card);
        collectionCard = (CardView) findViewById(R.id.collection_card);
        receivableCard = (CardView) findViewById(R.id.receive_card);
        bankBalanceCard = (CardView) findViewById(R.id.bank_card);

        //Internet Checking
        tv_check_connection = (TextView) findViewById(R.id.tv_check_connection);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();

        //Selection CardView
        campusSelect = findViewById(R.id.select_campus_cv);
        departmentSelect = findViewById(R.id.select_department_cv);
        dateSelect = findViewById(R.id.select_date_cv);

        //To see absent, late and on leave members
        staffAbsentLyt = findViewById(R.id.staff_absent_admin_dashboard);
        staffLateLyt = findViewById(R.id.staff_late_admin_dashboard);
        staffLeaveLyt = findViewById(R.id.staff_leave_admin_dashboard);
        studentAbsentLyt = findViewById(R.id.student_absent_admin_dashboard);
        studentLeaveLyt = findViewById(R.id.student_leave_admin_dashboard);

        //Toolbar
        final Toolbar myToolbar = (Toolbar) findViewById(R.id.admindashboard_toolbar);
        setSupportActionBar(myToolbar);
//        myToolbar.setNavigationIcon(R.drawable.ic_menu);
        getSupportActionBar().setTitle("Fusion");

        campusTextChanged = findViewById(R.id.txt_campuschange);
        campusSelect_txt = findViewById(R.id.campus_select_txt);
        departmentSelect_txt = findViewById(R.id.department_select_txt);
        dateSelect_txt = findViewById(R.id.date_select_txt);

        //cmbCampus
        campusSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(AdminDashboard.this, "SELECT CAMPUS", "Search...",
                        null, initData(), new SearchResultListener<Searchable>() {

                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        selectedDB = "";
                        campusSelect_txt.setText(searchable.getTitle());
                        campusTextChanged.setText(searchable.getTitle());

                        for (CampusDetails c : list) {
                            if (c.getCampus_name().equals(searchable.getTitle())) {
                                selectedDB = c.getDb_name();
                                LoadDepartments(selectedDB);
                                break;
                            }
                        }

                        baseSearchDialogCompat.dismiss();
                        LoadData(selectedDB, dptID);
                    }
                }).show();
            }
        });

        //cmbDepartment
        departmentSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(AdminDashboard.this, "SELECT DEPARTMENT", "Search...",
                        null, initData2(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        departmentSelect_txt.setText(searchable.getTitle());

                        for (Department c : departmentList) {
                            if (c.getDepartment().equals(searchable.getTitle())) {
                                dptID = c.getId();
                                break;
                            }
                        }

                        baseSearchDialogCompat.dismiss();
                        LoadData(selectedDB, dptID);
                    }
                }).show();
            }
        });

        //cmbDate
        dateSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(AdminDashboard.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        formattedDate = df.format(calendar.getTime());
                        dateSelect_txt.setText(formattedDate);

                        formattedDateShort = dfShort.format(calendar.getTime());
                        summaryDate.setText(formattedDateShort);
                        dcrDate.setText(formattedDateShort);
                        defaultersDate.setText(formattedDateShort);
                        staffDate.setText(formattedDateShort);
                        studentDate.setText(formattedDateShort);

                        LoadData(selectedDB, dptID);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        //Load Data
        loadingAndDisplayContent(username, pw, true);

        // on swipe
        swipe_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullAndRefresh();
            }
        });

        // View members who are absent or on Leave
        viewMemberStatus();
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_dashboard_toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        //Log.d("data.fastech.pk", "Menu clicked: " +item.getItemId());

        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.btnMail) {
            editor.putBoolean("editMode", false);
            editor.commit();
            Intent intent = new Intent(AdminDashboard.this, NotificationView.class);
            startActivity(intent);
//            SharedPreferences sharedPref = getSharedPreferences("myKey", MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedPref.edit();
//            valueCampus = campusSelect_txt.getText().toString().trim();
//            valueDepartment = departmentSelect_txt.getText().toString().trim();
//            editor.putString("campus", valueCampus);
//            editor.putString("department", valueDepartment);
//            editor.apply();
//            Intent intent = new Intent(AdminDashboard.this, CampusReportPanel.class);
//            startActivity(intent);
        } else if (item.getItemId() == R.id.action_notifications_settings) {
            Intent intent2 = new Intent(AdminDashboard.this, NotificationSettings.class);
            startActivity(intent2);
        } else if (item.getItemId() == R.id.btnNotifications) {
            editor.putBoolean("editMode", true);
            editor.commit();
            Intent intent2 = new Intent(AdminDashboard.this, AdminNotificationView.class);
            startActivity(intent2);
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList<SearchModel> initData() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (CampusDetails c : list) {
            items.add(new SearchModel(c.getCampus_name(), c.getDb_name()));
        }
        return items;
    }

    private ArrayList<SearchModel> initData2() {
        ArrayList<SearchModel> items2 = new ArrayList<>();
        for (Department c : departmentList) {
            items2.add(new SearchModel(c.getDepartment()));
        }
        return items2;
    }

    // Network receiver =================Start===================
    public static void dialogAdmin(boolean value) {
        if (value) {
            if (!state.equals(value)) {
                try {
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
                            context.startActivity(activity.getIntent());
                            activity.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                handler.postDelayed(delay, 3000);
                state = value;
            }

        } else {
            if (!state.equals(value)) {
                try {
                    tv_check_connection.setVisibility(View.VISIBLE);
                    tv_check_connection.setText("No Internet Connection");
                    tv_check_connection.setBackgroundColor(Color.parseColor("#cc0000"));
                    tv_check_connection.setTextColor(Color.WHITE);
                    lyt_progress.setVisibility(View.GONE);

                } catch (Exception e) {
                    e.printStackTrace();
                }

                state = value;
            }
        }
    }

    private void registerNetworkBroadcast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            registerReceiver(mNetworkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
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
    //==================End===================


    private void loadingAndDisplayContent(String user, String pw, boolean dropdown) {
        //Show Loader
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        studentCard.setVisibility(View.GONE);
        staffCard.setVisibility(View.GONE);
        collectionCard.setVisibility(View.GONE);
        receivableCard.setVisibility(View.GONE);
        bankBalanceCard.setVisibility(View.GONE);

        if (dropdown) {
            LoadCampuses(user, pw);
        }
    }

    private void LoadCampuses(final String user, final String pw) {

        Log.d("data.fastech.pk", "Loading campus for: " + user + "-" + pw);
        JsonArrayRequest request = new JsonArrayRequest("http://pushy.fastech.pk/Login/Login?username=" + user + "&pw=" + pw,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        list.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            CampusDetails c = new CampusDetails();
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                c.setCampus_name(jsonObject.getString("campusName"));
                                c.setDb_name(jsonObject.getString("db_name"));
                                if (firstCampus.equals("")) {
                                    firstCampus = c.getCampus_name();
                                    selectedDB = c.getDb_name();
                                    campusSelect_txt.setText(c.getCampus_name());
                                    campusTextChanged.setText(c.getCampus_name());
                                    LoadDepartments(selectedDB);
                                }

                                //Log.d("data.fastech.pk", "db_name: " + jsonObject.getString("db_name"));
                                list.add(c);
                            } catch (JSONException e) {
                                Log.d("data.fastech.pk", e.getLocalizedMessage());
                                //mEntries.add("Error: " + e.getLocalizedMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //snackBarIconError("Invalid Login.");
                    }
                });

        queue.add(request);
    }

    private void LoadDepartments(final String db) {

        Log.d("data.fastech.pk", "Loading departments for: " + db);
        JsonArrayRequest request = new JsonArrayRequest("http://pushy.fastech.pk/Fill/GetDepartments?_db=" + db,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        departmentList.clear();
                        //Default All Department
                        Department d = new Department();
                        d.setId(0);
                        d.setDepartment("All Departments");
                        departmentList.add(d);
                        departmentSelect_txt.setText("All Departments");
                        dateSelect_txt.setText(formattedDate);
                        LoadData(selectedDB, dptID);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Department c = new Department();
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                c.setId(jsonObject.getInt("id"));
                                c.setDepartment(jsonObject.getString("name"));
                                Log.d("data.fastech.pk", "Dpt: " + jsonObject.getString("name"));
                                departmentList.add(c);
                            } catch (JSONException e) {
                                Log.d("data.fastech.pk", e.getLocalizedMessage());
                                //mEntries.add("Error: " + e.getLocalizedMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //snackBarIconError("Invalid Login.");
                    }
                });

        queue.add(request);
    }

    private void LoadData(final String db, Integer dpt) {

        //Show Loader
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);
        studentCard.setVisibility(View.GONE);
        staffCard.setVisibility(View.GONE);
        collectionCard.setVisibility(View.GONE);
        receivableCard.setVisibility(View.GONE);
        bankBalanceCard.setVisibility(View.GONE);

        Log.d("data.fastech.pk", "Loading Std Attd for: " + db + " and dpt " + dpt);

        //Std Attd
        String url = "http://pushy.fastech.pk/Attendance/GetStudentsAttd?_db=" + db + "&dpt=" + dpt;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Convert String to json object
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // get value from  Json Object
                        try {
                            lblStdTotal.setText(json.getString("total"));
                            lblStdPresent.setText(json.getString("present"));
                            studentPresentPercentage.setText(String.format("%.2f", json.getDouble("presentPer")) + "%");
                            lblStdAbsent.setText(json.getString("absent"));
                            studentAbsentPercentage.setText(String.format("%.2f", json.getDouble("absentPer")) + "%");
                            lblStdLeave.setText(json.getString("onLeave"));
                            studentOnLeavePercentage.setText(String.format("%.2f", json.getDouble("onLeavePer")) + "%");

                            //Graph Student============Start
                            ArrayList<PieEntry> pieEntriesStudent = new ArrayList<>();

                            pieEntriesStudent.add(new PieEntry(json.getInt("present"), 0));
                            pieEntriesStudent.add(new PieEntry(json.getInt("absent"), 1));
                            pieEntriesStudent.add(new PieEntry(json.getInt("onLeave"), 2));
                            pieEntriesStudent.add(new PieEntry(json.getInt("total"), 3));

                            PieDataSet dataSetStudent = new PieDataSet(pieEntriesStudent, "");

                            ArrayList titlesStudent = new ArrayList();
                            titlesStudent.add("Present");
                            titlesStudent.add("Absent");
                            titlesStudent.add("On Leave");
                            titlesStudent.add("Total");

                            PieData dataStudent = new PieData(dataSetStudent);
                            graphStudentAttendance.setData(dataStudent);
                            dataSetStudent.setColors(new int[]{
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.green_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.blue_grey_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.purple_600),
                            });

                            Description description = new Description();
                            description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]); description.setText("Student Attendance");
                            graphStudentAttendance.setDescription(description);

                            graphStudentAttendance.animateXY(5000, 5000);

                            // Date
                            studentDate.setText(formattedDateShort);
                            //==============End

                        } catch (JSONException e) {

                            Log.d("data.fastech.pk", "Error on getting student attendance.");
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //snackBarIconError("Invalid Login.");
                            }
                        });


        //excecute your request
        queue.add(jsonObjectRequest);

        //Staff Attd
        String urlStaff = "http://pushy.fastech.pk/Attendance/GetStaffAttd?_db=" + db + "&dpt=" + dpt;
        JsonObjectRequest jsonObjectRequestStaff = new JsonObjectRequest
                (Request.Method.GET, urlStaff, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Convert String to json object
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // get value from  Json Object
                        try {
                            lblStaffTotal.setText(json.getString("total"));
                            lblStafPresent.setText(json.getString("present"));
                            staffPresentPercentage.setText(String.format("%.2f", json.getDouble("presentPer")) + "%");
                            lblStaffAbsent.setText(json.getString("absent"));
                            staffAbsentPercentage.setText(String.format("%.2f", json.getDouble("absentPer")) + "%");
                            lblStaffLeave.setText(json.getString("onLeave"));
                            staffOnLeavePercentage.setText(String.format("%.2f", json.getDouble("onLeavePer")) + "%");
                            lblStaffLate.setText(json.getString("late"));
                            staffLatePercentage.setText(String.format("%.2f", json.getDouble("latePer")) + "%");

                            // Graph Staff=========Start
                            ArrayList<PieEntry> pieEntriesStaff = new ArrayList<>();

                            pieEntriesStaff.add(new PieEntry(json.getInt("present"), 0));
                            pieEntriesStaff.add(new PieEntry(json.getInt("absent"), 1));
                            pieEntriesStaff.add(new PieEntry(json.getInt("onLeave"), 2));
                            pieEntriesStaff.add(new PieEntry(json.getInt("late"), 3));
                            pieEntriesStaff.add(new PieEntry(json.getInt("total"), 4));

                            PieDataSet dataSetStaff = new PieDataSet(pieEntriesStaff, "");

                            ArrayList titlesStaff = new ArrayList();
                            titlesStaff.add("Present");
                            titlesStaff.add("Absent");
                            titlesStaff.add("On Leave");
                            titlesStaff.add("Late");
                            titlesStaff.add("Total");

                            PieData dataStaff = new PieData(dataSetStaff);
                            graphStaffAttendance.setData(dataStaff);
                            dataSetStaff.setColors(new int[]{
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.green_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.blue_grey_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.blue_600),
                            });

                            Description description = new Description();
                            description.setTextColor(ColorTemplate.VORDIPLOM_COLORS[2]); description.setText("Staff Attendance");
                            graphStaffAttendance.setDescription(description);

                            graphStaffAttendance.animateXY(5000, 5000);

                            // Date
                            staffDate.setText(formattedDateShort);
                            //===============End


                        } catch (JSONException e) {

                            Log.d("data.fastech.pk", "Error on getting student attendance.");
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //snackBarIconError("Invalid Login.");
                            }
                        });


        //excecute your request
        queue.add(jsonObjectRequestStaff);

        //Collection
        String urlCollection = "http://pushy.fastech.pk/Fee/GetData?_db=" + db + "&dpt=" + dpt;
        JsonObjectRequest jsonObjectRequestCollection = new JsonObjectRequest
                (Request.Method.GET, urlCollection, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Convert String to json object
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        // get value from  Json Object
                        try {
                            lblCollection_t.setText("Rs." + String.format("%,d", json.getInt("collection_t")));
                            lblCollection_w.setText("Rs." + String.format("%,d", json.getInt("collection_w")));
                            lblCollection_m.setText("Rs." + String.format("%,d", json.getInt("collection_m")));
                            lblCollection_tf.setText("Rs." + String.format("%,d", json.getInt("total_tf")));
                            lblCollection_other.setText("Rs." + String.format("%,d", json.getInt("total_others")));
                            dcrToday.setText(json.getString("collection_t_count") + " STUDENT(S)");
                            dcrThisWeek.setText(json.getString("collection_w_count") + " STUDENT(S)");
                            dcrThisMonth.setText(json.getString("collection_m_count") + " STUDENT(S)");

                            // Graph DCR===========Start
                            ArrayList<BarEntry> barEntriesDCR = new ArrayList<>();
                            barEntriesDCR.add(new BarEntry(json.getInt("collection_t"), 0));
                            barEntriesDCR.add(new BarEntry(json.getInt("collection_w"), 1));
                            barEntriesDCR.add(new BarEntry(json.getInt("collection_m"), 2));
                            barEntriesDCR.add(new BarEntry(json.getInt("total_tf"), 3));
                            barEntriesDCR.add(new BarEntry(json.getInt("total_others"), 4));

                            BarDataSet barDataSet2 = new BarDataSet(barEntriesDCR, "DCR");

                            ArrayList<String> titlesDCR = new ArrayList<>();
                            titlesDCR.add("TD");
                            titlesDCR.add("TW");
                            titlesDCR.add("TM");
                            titlesDCR.add("TF");
                            titlesDCR.add("OF");

                            BarData dataDCR = new BarData(barDataSet2);
                            graphDCR.setData(dataDCR);
                            graphDCR.setTouchEnabled(true);
                            graphDCR.setDragEnabled(true);
                            graphDCR.setScaleEnabled(true);

                            barDataSet2.setColors(new int[]{
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.pink_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.orange_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.blue_600),
                                    ContextCompat.getColor(graphDCR.getContext(), R.color.brown_600),
                            });

                            graphDCR.animateY(5000);
                            graphDCR.getAxisLeft().setDrawLabels(false);
                            graphDCR.getAxisRight().setDrawLabels(false);

                            // Date
                            dcrDate.setText(formattedDateShort);
                            //================End

                            //Dues
                            lblDues_t.setText("Rs." + String.format("%,d", json.getInt("defaultersTotal")));
                            receivableTotalStudents.setText(json.getString("defaultersCount") + " STUDENT(S)");
                            lblDues_current.setText("Rs." + String.format("%,d", json.getInt("currentMonthDefaulters")));
                            lblDues_arrears.setText("Rs." + String.format("%,d", json.getInt("arrears")));
                            lblDues_tf.setText("Rs." + String.format("%,d", json.getInt("defaultersTF")));
                            lblDues_other.setText("Rs." + String.format("%,d", json.getInt("defaultersOthers")));

                            // Graph Dues(Receivable)==========Start
                            ArrayList<BarEntry> barEntriesDues = new ArrayList<>();
                            barEntriesDues.add(new BarEntry(json.getInt("defaultersTotal"), 0));
                            barEntriesDues.add(new BarEntry(json.getInt("currentMonthDefaulters"), 1));
                            barEntriesDues.add(new BarEntry(json.getInt("arrears"), 2));
                            barEntriesDues.add(new BarEntry(json.getInt("defaultersTF"), 3));
                            barEntriesDues.add(new BarEntry(json.getInt("defaultersOthers"), 4));
                            BarDataSet barDataSet3 = new BarDataSet(barEntriesDues, "Dues");

                            ArrayList<String> titlesDues = new ArrayList<>();
                            titlesDues.add("TL");
                            titlesDues.add("CM");
                            titlesDues.add("AR");
                            titlesDues.add("TF");
                            titlesDues.add("OF");

                            BarData dataDues = new BarData(barDataSet3);
                            graphReceivable.setData(dataDues);
                            graphReceivable.setTouchEnabled(true);
                            graphReceivable.setDragEnabled(true);
                            graphReceivable.setScaleEnabled(true);

                            barDataSet3.setColors(new int[]{
                                    ContextCompat.getColor(graphReceivable.getContext(), R.color.green_600),
                                    ContextCompat.getColor(graphReceivable.getContext(), R.color.pink_600),
                                    ContextCompat.getColor(graphReceivable.getContext(), R.color.brown_600),
                                    ContextCompat.getColor(graphReceivable.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphReceivable.getContext(), R.color.blue_600),
                            });

                            graphReceivable.animateY(5000);
                            graphReceivable.getAxisLeft().setDrawLabels(false);
                            graphReceivable.getAxisRight().setDrawLabels(false);

                            // Date
                            defaultersDate.setText(formattedDateShort);
                            //===============End

                            //Summary
                            lblRcvable.setText("Rs." + String.format("%,d", json.getInt("currentMonthRcvdAble")));
                            lblRcvd.setText("Rs." + String.format("%,d", json.getInt("currentMonthRcvd")));
                            lblBalance.setText("Rs." + String.format("%,d", json.getInt("currentMonthBalance")));
                            lblBadDebts.setText("Rs." + String.format("%,d", json.getInt("currentMonthBadDebts")));
                            lblSummmary.setText(String.format("%.2f", json.getDouble("collectionPer")) + "%");
                            lblExpenses.setText("Rs." + String.format("%,d", json.getInt("expenses_m")));
                            lblOtherIncome.setText("Rs." + String.format("%,d", json.getInt("income_m")));
                            double profit = json.getDouble("collection_m") + json.getDouble("income_m") - json.getDouble("expenses_m");

                            DecimalFormat formatter = new DecimalFormat("#,###");
                            lblProfit.setText("Rs." + formatter.format(profit));
//                            lblProfit.setText("Rs." + String.format("%.0f", profit));

                            // Summary Graph==========Start
                            ArrayList<BarEntry> barEntries = new ArrayList<>();
                            barEntries.add(new BarEntry(json.getInt("currentMonthRcvdAble"), 0));
                            barEntries.add(new BarEntry(json.getInt("currentMonthRcvd"), 1));
                            barEntries.add(new BarEntry(json.getInt("currentMonthBadDebts"), 2));
                            barEntries.add(new BarEntry(json.getInt("currentMonthBalance"), 3));
                            barEntries.add(new BarEntry(json.getInt("collectionPer"), 4));
                            barEntries.add(new BarEntry(json.getInt("expenses_m"), 5));
                            barEntries.add(new BarEntry(json.getInt("income_m"), 6));
                            barEntries.add(new BarEntry(Math.round(profit), 7));

                            BarDataSet barDataSet = new BarDataSet(barEntries, "Summary");

                            ArrayList<String> titles = new ArrayList<>();
                            titles.add("RV");
                            titles.add("RD");
                            titles.add("BD");
                            titles.add("BL");
                            titles.add("SM");
                            titles.add("E");
                            titles.add("OI");
                            titles.add("PNL");

                            BarData data = new BarData(barDataSet);
                            graphMarketSummary.setData(data);
                            graphMarketSummary.setTouchEnabled(true);
                            graphMarketSummary.setDragEnabled(true);
                            graphMarketSummary.setScaleEnabled(true);

                            barDataSet.setColors(new int[]{
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.green_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.blue_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.purple_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.red_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.orange_600),
                                    ContextCompat.getColor(graphMarketSummary.getContext(), R.color.pink_600)
                            });

                            graphMarketSummary.animateY(5000);
                            graphMarketSummary.getAxisLeft().setDrawLabels(false);
                            graphMarketSummary.getAxisRight().setDrawLabels(false);

                            // Date
                            summaryDate.setText(formattedDateShort);
                            //===============End

                            HideLoader();

                        } catch (JSONException e) {
                            HideLoader();
                            Log.d("data.fastech.pk", "Error on getting student attendance.");
                            e.printStackTrace();
                        }
                    }
                },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //snackBarIconError("Invalid Login.");
                            }
                        });
        //execute your request
        queue.add(jsonObjectRequestCollection);
    }

    public void HideLoader() {
        ViewAnimation.fadeOut(lyt_progress);
        lyt_progress.setVisibility(View.GONE);
        studentCard.setVisibility(View.VISIBLE);
        staffCard.setVisibility(View.VISIBLE);
        collectionCard.setVisibility(View.VISIBLE);
        receivableCard.setVisibility(View.VISIBLE);
        bankBalanceCard.setVisibility(View.VISIBLE);
    }

    private void pullAndRefresh() {
        swipeProgress(true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(getIntent());
                overridePendingTransition(0, 0);
                finish();
                overridePendingTransition(0, 0);
                swipeProgress(false);
            }
        }, 3000);
    }

    private void swipeProgress(final boolean show) {
        if (!show) {
            swipe_refresh.setRefreshing(show);
            return;
        }
        swipe_refresh.post(new Runnable() {
            @Override
            public void run() {
                swipe_refresh.setRefreshing(show);
            }
        });
    }

    // List of Members who are absent/leave etc.
    private void viewMemberStatus() {
        staffAbsentLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, StaffAttendanceAdminView.class);
                startActivity(intent);
            }
        });

        staffLeaveLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, StaffAttendanceAdminView.class);
                startActivity(intent);
            }
        });

        staffLateLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, StaffAttendanceAdminView.class);
                startActivity(intent);
            }
        });

        studentAbsentLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, StaffAttendanceAdminView.class);
                startActivity(intent);
            }
        });

        studentLeaveLyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminDashboard.this, StaffAttendanceAdminView.class);
                startActivity(intent);
            }
        });
    }
}