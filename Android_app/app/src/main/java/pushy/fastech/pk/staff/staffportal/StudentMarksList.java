package pushy.fastech.pk.staff.staffportal;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cdflynn.android.library.checkview.CheckView;
import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import pushy.fastech.pk.Helper.Classes;
import pushy.fastech.pk.Helper.Dropdown;
import pushy.fastech.pk.Helper.Faculty;
import pushy.fastech.pk.Helper.Marks;
import pushy.fastech.pk.Helper.Populate;
import pushy.fastech.pk.Helper.Sections;
import pushy.fastech.pk.Helper.Subjects;
import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.adapters.AdapterStudentMarks;
import pushy.fastech.pk.models.SearchModel;
import pushy.fastech.pk.walkthrough.IntroActivityMarks;

public class StudentMarksList extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterStudentMarks mAdapter;
    private String totalMarksToSet = "100";
    private View parent_view;
    public static float finalValue = 100;
    private RequestQueue queue;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Populate populate;
    private String username = "", dbName = "";
    private Integer clsID = 0, subjectID = 0;
    private String day, mn , _year;
    private List<Marks> items;
    private ProgressBar pClass, pSection, pFaculty, pSubject, pTerm, pSession ;
    private String cmbClass = "", cmbSec = "", cmbFaculty = "", cmbSubject = "", dtDate = "", cmbTerm = "", cmbSession = "";
    private int dayOfMonth, month, year;
    private Calendar calendar;
    private String selectedClass = "", selectedSec = "", selectedFaculty = "", selectedSubject = "", selectedTerm = "", selectedSession = "";
    private DatePickerDialog datePickerDialog;
    private Integer cID = 0, localID = 0;
    private ProgressDialog progressDialog;
    private LinearLayout progressAttendanceActivity;
    private LinearLayout checkLyt;
    private CheckView checkViewAnim;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private LinearLayout selection, help;
    private FloatingActionButton done;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_marks_list);
        parent_view = findViewById(android.R.id.content);
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0);
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        cID = pref.getInt("campusID",0);
        clsID = pref.getInt("classID", 0);
        localID = pref.getInt("localID", 0);
        editor = pref.edit();
        populate = new Populate(getApplicationContext());
        queue = Volley.newRequestQueue(getApplicationContext());

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        selection = findViewById(R.id.selection);
        help = findViewById(R.id.help);
        done = findViewById(R.id.done_upload_marks);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Saving marks");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        progressAttendanceActivity = findViewById(R.id.progress_bar_attendance);
        checkViewAnim = findViewById(R.id.checkAnim);
        checkLyt = findViewById(R.id.lyt_check_animation);

        initToolbar();
        initComponent();

        RelativeLayout changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudentMarksList.this, SelectAccount.class);
                startActivity(intent);
            }
        });

        selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectionDialog();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showCustomDialogHelp();
                startActivity(new Intent(StudentMarksList.this, IntroActivityMarks.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://app.fastech.pk/Exams/Post";
                if (clsID > 0) {
                    progressDialog.show();
                    Integer facID = populate.getFacultyID(cmbFaculty).getId();
                    Integer examID = populate.getTerm(cmbTerm).getId();
                    Integer sessionID = populate.getSession(cmbSession).getId();
                    Integer subjID = populate.getSubjectID(cmbSubject).getId();
                    SaveMarks(url, mAdapter.GetList(), localID, clsID, cmbSec, facID, subjID, examID, sessionID, day, mn, _year, cID, dbName);
                } else
                    selectionDialog();
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Marks");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        cmbClass = pref.getString("ecmbClass", "");
        cmbSec = pref.getString("ecmbSec", "");
        cmbFaculty = pref.getString("ecmbFaculty", "");
        cmbSubject = pref.getString("ecmbSubject", "");
        cmbTerm = pref.getString("ecmbTerm", "");
        cmbSession = pref.getString("ecmbSession", "");
        dtDate = pref.getString("edtDate", "");

        selectedClass = cmbClass;
        selectedSec = cmbSec;
        selectedFaculty = cmbFaculty;
        selectedSubject = cmbSubject;
        selectedTerm = cmbTerm;
        selectedSession =  cmbSession;

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        items = new ArrayList<>();
        selectionDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_marks_list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_sort_bottom_sheet) {
            sortBottomSheetDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectionDialog() {
        final Dialog dialogSelection = new Dialog(this);
        dialogSelection.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSelection.setContentView(R.layout.exam_selection_dialog);
        dialogSelection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogSelection.setCancelable(true);

        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
        lp2.copyFrom(dialogSelection.getWindow().getAttributes());
        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Button btnSection = dialogSelection.findViewById(R.id.dialog_section_btn);
        final Button btnFaculty = dialogSelection.findViewById(R.id.dialog_faculty_btn);
        final Button btnSubject = dialogSelection.findViewById(R.id.dialog_subject_btn);
        final Button btnTerm = dialogSelection.findViewById(R.id.dialog_term_btn);
        final Button btnSession = dialogSelection.findViewById(R.id.dialog_session_btn);

        btnSection.setEnabled(false);
        btnFaculty.setEnabled(false);
        btnSubject.setEnabled(false);

        pClass = dialogSelection.findViewById(R.id.progressBar);
        pSection = dialogSelection.findViewById(R.id.progressBar2);
        pFaculty = dialogSelection.findViewById(R.id.progressBar3);
        pSubject = dialogSelection.findViewById(R.id.progressBar4);
        pTerm = dialogSelection.findViewById(R.id.progressBar5);
        pSession = dialogSelection.findViewById(R.id.progressBar6);

        LoadClasses(btnSection, btnFaculty, btnSubject);
        LoadTerms(btnTerm);
        LoadSession(btnSession);

        if (!cmbClass.equals("")) {
            ((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).setText(cmbClass);
        }
        if (!cmbTerm.equals("")) {
            ((Button) dialogSelection.findViewById(R.id.dialog_term_btn)).setText(cmbTerm);
        }
        if (!cmbSession.equals("")) {
            ((Button) dialogSelection.findViewById(R.id.dialog_session_btn)).setText(cmbSession);
        }
        if (!dtDate.equals("")) {
            ((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).setText(dtDate);
        } else {
            //Current
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dtDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (month + 1) + "-" + year;
            editor.putString("dtDate", dtDate);
            editor.apply();
            Log.d("data.fastech.pk", "dt: " + dtDate);
            ((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).setText(calendar.get(Calendar.DAY_OF_MONTH) + "-" + (month + 1) + "-" + year);
        }

        (dialogSelection.findViewById(R.id.dialog_class_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Class", "Search...",
                        null, initDataClass(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).setText(searchable.getTitle());
                        selectedClass = searchable.getTitle();
                        cmbClass = selectedClass;
                        editor.putString("ecmbClass", selectedClass);
                        editor.apply();
                        LoadSections(populate.getSelectedClass(selectedClass), btnSection, btnFaculty, btnSubject);
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_section_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Section", "Search...",
                        null, initDataSection(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_section_btn)).setText(searchable.getTitle());
                        selectedSec = searchable.getTitle();
                        cmbSec = selectedSec;
                        editor.putString("ecmbSec", selectedSec);
                        editor.apply();
                        Log.d("data.fastech.pk", "Selected Class: " + selectedClass);
                        LoadFaculty(populate.getSelectedClass(selectedClass), btnFaculty, btnSubject);
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_faculty_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Faculty", "Search...",
                        null, initDataFaculty(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_faculty_btn)).setText(searchable.getTitle());
                        selectedFaculty = searchable.getTitle();
                        cmbFaculty = selectedFaculty;
                        editor.putString("ecmbFaculty", selectedFaculty);
                        editor.apply();
                        Log.d("data.fastech.pk", "Selected Fac:" + selectedFaculty);
                        LoadSubjects(populate.getSelectedClass(selectedClass), cmbSec, populate.getFacultyID(selectedFaculty), (Button) dialogSelection.findViewById(R.id.dialog_subject_btn));
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_subject_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Subject", "Search...",
                        null, initDataSubjects(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_subject_btn)).setText(searchable.getTitle());
                        selectedSubject = searchable.getTitle();
                        cmbSubject = selectedSubject;
                        editor.putString("ecmbSubject", selectedSubject);
                        editor.apply();
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_term_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Term", "Search...",
                        null, initDataTerms(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_term_btn)).setText(searchable.getTitle());
                        selectedTerm = searchable.getTitle();
                        cmbTerm = selectedTerm;
                        editor.putString("ecmbTerm", selectedTerm);
                        editor.apply();
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_session_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(StudentMarksList.this, "Select Session", "Search...",
                        null, initDataSession(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_session_btn)).setText(searchable.getTitle());
                        selectedSession = searchable.getTitle();
                        cmbSession = selectedSession;
                        editor.putString("ecmbSession", selectedSession);
                        editor.apply();
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_date_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(StudentMarksList.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        ((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).setText(day + "-" + (month + 1) + "-" + year);
                        dtDate = day + "-" + (month + 1) + "-" + year;
                        editor.putString("dtDate", dtDate);
                        editor.apply();
                        Log.d("data.fastech.pk", "dt: " + dtDate);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        (dialogSelection.findViewById(R.id.dialog_done_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).getText().toString().equals("CLASS")) {
                    snackBarIconError("Please select class");
                } else if (((Button) dialogSelection.findViewById(R.id.dialog_section_btn)).getText().toString().equals("SECTION")) {
                    snackBarIconError("Please select section");
                } else if (((Button) dialogSelection.findViewById(R.id.dialog_faculty_btn)).getText().toString().equals("FACULTY")) {
                    snackBarIconError("Please select faculty");
                } else if (((Button) dialogSelection.findViewById(R.id.dialog_subject_btn)).getText().toString().equals("SUBJECT")) {
                    snackBarIconError("Please select subject");
                } else if (((Button) dialogSelection.findViewById(R.id.dialog_term_btn)).getText().toString().equals("TERM")) {
                    snackBarIconError("Please select term");
                }else if (((Button) dialogSelection.findViewById(R.id.dialog_session_btn)).getText().toString().equals("SESSION")) {
                    snackBarIconError("Please select session");
                }else if (((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).getText().toString().equals("DATE")) {
                    snackBarIconError("Please select date");
                }else if (((EditText) dialogSelection.findViewById(R.id.dialog_total_text)).getText().toString().equals("")) {
                    snackBarIconError("Fill Total Marks Field");
                }  else {
                    //Check total marks
                    totalMarksToSet = ((EditText) dialogSelection.findViewById(R.id.dialog_total_text)).getText().toString();
                    finalValue = Integer.parseInt(totalMarksToSet);

                    //Get IDs
                    clsID = populate.getSelectedClass(cmbClass).getId();
                    subjectID = populate.getSubjectID(cmbSubject).getId();
                    Faculty fac = populate.getFacultyID(cmbFaculty);

                    day = dtDate.split("-")[0];
                    mn = dtDate.split("-")[1];
                    _year = dtDate.split("-")[2];

                    Integer examID = populate.getTerm(cmbTerm).getId();
                    Integer sessionID = populate.getSession(cmbSession).getId();

                    String url = LoginSimpleGreen.domain+ "/Exams/Populate?tID=" + localID + "&clsID=" + clsID + "&sec=" + cmbSec + "&facID=" + fac.getId() + "&subID=" + subjectID + "&examID=" + examID + "&sessionID=" + sessionID + "&day=" + day + "&mn=" + mn + "&year=" + _year + "&cID=" + cID + "&_db=" + dbName;

                    //Load Data
                    LoadData(url, getApplicationContext());


                    dialogSelection.hide();
                    items.clear();
                }
            }
        });

        dialogSelection.show();
        dialogSelection.getWindow().setAttributes(lp2);
    }

    public void LoadData(String url, final Context context) {
        //On screen show loader
        progressAttendanceActivity.setVisibility(View.VISIBLE);

        try {
            Log.d("data.fastech.pk", "Loading exams data: " + url);

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
                                Log.d("data.fastech.pk", "Loading students marks...");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Marks c = new Marks();
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setsID(jsonObject.getInt("sID"));
                                        c.setGr(jsonObject.getString("gr"));
                                        c.setName(capitalize(jsonObject.getString("name").toLowerCase()));
                                        c.setfName(capitalize(jsonObject.getString("fName").toLowerCase()));
                                        c.setObtainedMarks(jsonObject.getString("obtainedMarks"));
                                        c.setTotalMarks(Float.parseFloat(jsonObject.getDouble("TotalMarks") + ""));
                                        c.setNumericMarks(Float.parseFloat(jsonObject.getDouble("numericMarks") + ""));
                                        //Log.d("data.fastech.pk", c.getName() + " - " + c.getStatus());
                                        items.add(c);
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                //Hide on screen loader
                                progressAttendanceActivity.setVisibility(View.GONE);

                                mAdapter = new AdapterStudentMarks(context,  items);
                                recyclerView.setAdapter(mAdapter);



                            } catch (JSONException e) {
                                Log.e("data.fastech.pk", e.getLocalizedMessage());
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            Log.e("data.fastech.pk", volleyError.getLocalizedMessage());
                        }
                    });

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    private String capitalize(String capString) {
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()) {
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }

        return capMatcher.appendTail(capBuffer).toString();
    }

    //========================= Loading Classes =========================//
    public void LoadClasses(final Button btnSec, final Button btnFaculty, final Button btnSubject) {
        //Class progress show here
        pClass.setVisibility(View.VISIBLE);

        try {
            Log.d("data.fastech.pk", "Loading classes for tID: " + username);
            final String url = LoginSimpleGreen.domain+"/Fill/TClasses?cID="+cID+"&_db="+dbName+"&localID="+localID;
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            populate.clsList.clear();
                            // Process the JSON
                            try{
                                // Get the JSON array
                                Log.d("data.fastech.pk", "Getting classes");
                                JSONArray array = response.getJSONArray("data");
                                Log.d("data.fastech.pk", "Got classes");

                                // Loop through the array elements
                                for(int i=0;i<array.length();i++){
                                    Classes c = new Classes();
                                    try {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setCls(jsonObject.getString("name"));
                                        populate.clsList.add(c);
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                //Class progress hide here
                                pClass.setVisibility(View.GONE);

                                //Class Loaded
                                LoadSections(populate.getSelectedClass(cmbClass), btnSec, btnFaculty, btnSubject);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e("data.fastech.pk", error.getLocalizedMessage());
                        }
                    }
            );

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    //========================= Loading Sections =========================//
    public void LoadSections(Classes cls, final Button btnSec, final Button btnFaculty, final Button btnSubject) {
        if(cls != null)
        {
            //Section progress show here
            pSection.setVisibility(View.VISIBLE);
            Log.d("data.fastech.pk", "Loading sections for: " + cls.getCls());
            try {
                populate.secList.clear();
                btnSec.setText("SECTION");
                btnSec.setEnabled(false);
                final  String url = LoginSimpleGreen.domain + "/Fill/TSections?cID=" + cID + "&_db=" + dbName + "&localID=" + localID + "&clsID=" + cls.getId();
                Log.d("data.fastech.pk", "Loading sections url: " + url);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Do something with response
                                Boolean found = false;
                                // Process the JSON
                                try{
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    // Loop through the array elements
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Sections c = new Sections();
                                        try {
                                            String jsonObject = jsonArray.getString(i);
                                            Log.d("data.fastech.pk", "Sec: " + jsonObject);
                                            c.setSection(jsonObject);
                                            populate.secList.add(c);
                                            btnSec.setEnabled(true);
                                            if (jsonObject.equals(cmbSec))
                                                found = true;

                                        } catch (JSONException e) {
                                            Log.e("data.fastech.pk", "Error parsing data [" + e.getMessage() + "] ");
                                        }
                                    }
                                    if (found)
                                        btnSec.setText(cmbSec);

                                    //Section progress hide here
                                    pSection.setVisibility(View.GONE);

                                    LoadFaculty(populate.getSelectedClass(cmbClass), btnFaculty, btnSubject);

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                // Do something when error occurred
                                Log.e("data.fastech.pk", error.getLocalizedMessage());
                            }
                        }
                );

                queue.add(request);
            } catch (Exception x) {
            }
        }
    }

    //========================= Loading Faculty =========================//
    public void LoadFaculty(Classes cls, final Button btnFaculty, final Button btnSubject) {

        //Log.d("data.fastech.pk", "Loading faculty..");
        //Section progress show here
        pFaculty.setVisibility(View.VISIBLE);

        try {
            populate.facList.clear();
            btnFaculty.setText("FACULTY");
            btnFaculty.setEnabled(false);
            Log.d("data.fastech.pk", "Loading fac for: " + cls.getCls());

            final  String url = LoginSimpleGreen.domain+"/Fill/TFaculty?cID="+cID+"&_db="+dbName+"&localID="+localID+"&clsID="+cls.getId();
            Log.d("data.fastech.pk", "Loading faculty url: " + url);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            Boolean found = false;
                            // Process the JSON
                            try{
                                JSONArray jsonArray = response.getJSONArray("data");
                                // Loop through the array elements
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Faculty c = new Faculty();
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setName(jsonObject.getString("name"));
                                        //Log.d("data.fastech.pk", "Fac: " + c.getName());
                                        populate.facList.add(c);
                                        btnFaculty.setEnabled(true);
                                        if (c.getName().equals(cmbFaculty))
                                            found = true;


                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                Log.d("data.fastech.pk", "cmbFac: " + cmbFaculty);


                                if (found)
                                    btnFaculty.setText(cmbFaculty);

                                //Section progress hide here
                                pFaculty.setVisibility(View.GONE);

                                //Log.d("data.fastech.pk", "cmbFaculty: " + cmbFaculty);
                                LoadSubjects(populate.getSelectedClass(cmbClass), cmbSec, populate.getFacultyID(cmbFaculty), btnSubject);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e("data.fastech.pk", error.getLocalizedMessage());
                        }
                    }
            );

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    //========================= Loading Subjects =========================//
    public void LoadSubjects(Classes cls, String sec, Faculty fac, final Button btnSubject) {

        if(fac != null)
        {
            //Show subject progress here
            pSubject.setVisibility(View.VISIBLE);

            try {
                populate.subsList.clear();
                btnSubject.setText("SUBJECT");
                btnSubject.setEnabled(false);
                Log.d("data.fastech.pk", "Loading subjects for: " + cls.getCls() + " - " + sec);

                final  String url = LoginSimpleGreen.domain + "/Fill/TSubjects?cID=" + cID + "&_db=" + dbName + "&localID=" + localID + "&clsID=" + cls.getId() + "&sec=" + sec + "&fac=" + fac.getId();
                Log.d("data.fastech.pk", "Loading subjects url: " + url);
                JsonObjectRequest request = new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Do something with response
                                Boolean found = false;
                                // Process the JSON
                                try{
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    // Loop through the array elements
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Subjects c = new Subjects();
                                        try {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String subject = jsonObject.getString("name");
                                            Integer subjectID = jsonObject.getInt("id");
                                            //Log.d("data.fastech.pk", "Subject: " + subject);
                                            c.setId(subjectID);
                                            c.setSubject(subject);
                                            populate.subsList.add(c);

                                            if (subject.equals(cmbSubject))
                                                found = true;

                                        } catch (JSONException e) {
                                            Log.e("data.fastech.pk", "Error parsing data [" + e.getMessage() + "] ");
                                        }
                                    }
                                    //Hide subject progress here
                                    pSubject.setVisibility(View.GONE);
                                    btnSubject.setEnabled(true);

                                    if (found)
                                        btnSubject.setText(cmbSubject);

                                }catch (JSONException e){
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                // Do something when error occurred
                                //Log.e("data.fastech.pk", error.getLocalizedMessage());
                            }
                        }
                );

                queue.add(request);
            } catch (Exception x) {
                Log.e("data.fastech.pk", x.getLocalizedMessage());
            }
        }
    }

    //========================= Loading Term =========================//
    public void LoadTerms(final Button btnTerm) {
        //Class progress show here
        pTerm.setVisibility(View.VISIBLE);
        btnTerm.setEnabled(false);

        try {
            //Log.d("data.fastech.pk", "Loading term");
            final String url = LoginSimpleGreen.domain+"/Fill/TTerms?cID="+cID+"&_db="+dbName+"&localID="+localID;
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            populate.termList.clear();
                            btnTerm.setEnabled(true);
                            // Process the JSON
                            try{
                                // Get the JSON array
                                Log.d("data.fastech.pk", "Getting terms");
                                JSONArray array = response.getJSONArray("data");
                                Log.d("data.fastech.pk", "Got terms");

                                // Loop through the array elements
                                for(int i=0;i<array.length();i++){
                                    Dropdown c = new Dropdown();
                                    try {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setName(jsonObject.getString("name"));
                                        populate.termList.add(c);
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                //Class progress hide here
                                pTerm.setVisibility(View.GONE);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e("data.fastech.pk", error.getLocalizedMessage());
                        }
                    }
            );

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    //========================= Loading Session =========================//
    public void LoadSession(final Button btnSession) {
        //Class progress show here
        pSession.setVisibility(View.VISIBLE);
        btnSession.setEnabled(false);

        try {

            final String url = LoginSimpleGreen.domain+"/Session?cID="+cID+"&_db="+dbName;
            Log.d("data.fastech.pk", "Loading session: " + url);
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            populate.sessionList.clear();
                            btnSession.setEnabled(true);
                            // Process the JSON
                            try{
                                // Get the JSON array
                                //Log.d("data.fastech.pk", "Getting terms");
                                JSONArray array = response.getJSONArray("data");
                                Log.d("data.fastech.pk", "Got session");

                                // Loop through the array elements
                                for(int i=0;i<array.length();i++){
                                    Dropdown c = new Dropdown();
                                    try {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setName(jsonObject.getString("session"));
                                        populate.sessionList.add(c);
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                //Class progress hide here
                                pSession.setVisibility(View.GONE);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            // Do something when error occurred
                            Log.e("data.fastech.pk", error.getLocalizedMessage());
                        }
                    }
            );

            queue.add(request);
        } catch (Exception x) {
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }
    }

    //========================= Save Marks =========================//
    public void SaveMarks(String url, List<Marks> list, Integer tID, Integer clsID, String sec, Integer facID, Integer subID, Integer examID, Integer sessionID, String day, String mn, String year, Integer cID, String _db) {
        Log.d("data.fastech.pk", "List: " + list.size());
        Log.d("data.fastech.pk", "tID: " + tID + " cID: " + cID + " sec: " + sec + " sub: " + subID + "examID: " + examID + "sessionID: " + sessionID + " day: " + day + " mn: " + mn + " year: " + year + " _db: " + _db);
        Log.d("data.fastech.pk", "URL: " + url);

        JSONArray array = new JSONArray();
        for (Marks items : list) {
            Integer i = 0;
            JSONObject jObj = new JSONObject();
            try {
                jObj.put("id", items.getId());
                jObj.put("sID", items.getsID());
                jObj.put("gr", items.getGr());
                jObj.put("name", items.getName());
                jObj.put("fName", items.getfName());
                Log.d("data.fastech.pk", "Obt: " + items.getObtainedMarks());
                jObj.put("obtainedMarks", items.getObtainedMarks());
                Log.d("data.fastech.pk", "Post Numeric: " + items.getNumericMarks());
                jObj.put("numericMarks", items.getNumericMarks());
                jObj.put("TotalMarks", items.getTotalMarks());
                array.put(jObj);
                i++;
                Log.e("data.fastech.pk", i + " obj added..");
            } catch (JSONException e) {
                Log.e("data.fastech.pk", e.getLocalizedMessage());
            }
        }

        JSONObject js = new JSONObject();
        try {
            Log.d("data.fastech.pk", "Putting marks obj..");
            js.put("list", array);
            js.put("tID", tID);
            js.put("clsID", clsID);
            js.put("sec", sec);
            js.put("facID", facID);
            js.put("subID", subID);
            js.put("examID", examID);
            js.put("sessionID", sessionID);
            js.put("day", day);
            js.put("mn", mn);
            js.put("year", year);
            js.put("cID", cID);
            js.put("_db", _db);
            Log.d("data.fastech.pk", js.toString());

        } catch (JSONException e) {
            Log.e("data.fastech.pk", e.getLocalizedMessage());
        }

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, js, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d("data.fastech.pk", "Post Response: " + response);
                progressDialog.dismiss();
                recyclerView.setVisibility(View.INVISIBLE);
                checkLyt.setVisibility(View.VISIBLE);
                checkViewAnim.check();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        checkLyt.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                }, 2000);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                Log.e("data.fastech.pk", "Error on request:" + e.getLocalizedMessage());
                progressDialog.dismiss();
                Toast.makeText(StudentMarksList.this, "Error! Something went wrong", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(jsonObjReq);
    }

    //========================= Fill List with classes =========================//
    private ArrayList<SearchModel> initDataClass() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Classes c : populate.clsList) {
            items.add(new SearchModel(c.getCls(), c.getCls()));
        }
        return items;
    }

    //========================= Fill List with section =========================//
    private ArrayList<SearchModel> initDataSection() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Sections c : populate.secList) {
            items.add(new SearchModel(c.getSection(), c.getSection()));
        }
        return items;
    }

    //========================= Fill List with faculty =========================//
    private ArrayList<SearchModel> initDataFaculty() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Faculty c : populate.facList) {
            items.add(new SearchModel(c.getName(), c.getName()));
        }
        return items;
    }

    //========================= Fill Lists with subjects =========================//
    private ArrayList<SearchModel> initDataSubjects() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Subjects c : populate.subsList) {
            items.add(new SearchModel(c.getSubject(), c.getSubject()));
        }
        return items;
    }

    private ArrayList<SearchModel> initDataTerms() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Dropdown c : populate.termList) {
            items.add(new SearchModel(c.getName(), c.getName()));
        }
        return items;
    }

    private ArrayList<SearchModel> initDataSession() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Dropdown c : populate.sessionList) {
            items.add(new SearchModel(c.getName(), c.getName()));
        }
        return items;
    }

    private void snackBarIconError(String name) {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText(name);
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    //========================= BottomSheet Sort ==============================//
    private void sortBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_basic, null);
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        (view.findViewById(R.id.sort_gr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(items, new Comparator<Marks>() {
                    @Override
                    public int compare(Marks lhs, Marks rhs) {
                        return lhs.gr.compareTo(rhs.gr);
                    }
                });
                mBottomSheetDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Sorted by GR", Toast.LENGTH_SHORT).show();
            }
        });

        (view.findViewById(R.id.sort_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Collections.sort(items, new Comparator<Marks>() {
                    @Override
                    public int compare(Marks lhs, Marks rhs) {
                        return lhs.name.compareTo(rhs.name);
                    }
                });
                mBottomSheetDialog.dismiss();
                mAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Sorted by NAME", Toast.LENGTH_SHORT).show();
            }
        });

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        // set background transparent
        ((View) view.getParent()).setBackgroundColor(getResources().getColor(android.R.color.transparent));

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }
}
