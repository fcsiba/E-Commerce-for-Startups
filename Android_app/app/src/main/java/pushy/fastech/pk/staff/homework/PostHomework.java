package pushy.fastech.pk.staff.homework;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import pushy.fastech.pk.Helper.Classes;
import pushy.fastech.pk.Helper.Faculty;
import pushy.fastech.pk.Helper.Populate;
import pushy.fastech.pk.Helper.Sections;
import pushy.fastech.pk.Helper.Subjects;
import pushy.fastech.pk.Storage.Upload;
import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.adapters.AdapterPostHomework;
import pushy.fastech.pk.models.SearchModel;

public class PostHomework extends AppCompatActivity {

    private ProgressBar pClass, pSection, pFaculty, pSubject;
    private CardView class_cv, subject_cv, faculty_cv, section_cv;
    private TextView class_txt, subject_txt, faculty_txt, section_txt;
    private Button date_btn, time_btn, date_of_homework_btn, post_btn;
    private EditText homeWork;
    private TextView deadlineLabel, clearParagraph;
    private CheckBox checkBox;
    private Populate populate;
    private String username = "", dbName = "";
    private String cmbClass = "", cmbSec = "", cmbFaculty = "", cmbSubject = "", dtDate = "";
    private String selectedClass = "", selectedSec = "", selectedFaculty = "", selectedSubject = "";
    private Integer cID = 0, localID = 0, clsID = 0, subjectID = 0, tID = 0;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private RequestQueue queue;
    private int SELECT_IMAGE = 1;
    private int SELECT_PDF = 1;
    private static final int CAMERA_REQUEST = 1337; //onActivityResult request
    private RecyclerView mUploadList;
    private AdapterPostHomework mAdapterPostHomework;
    private TimePickerDialog timePickerDialog;
    private String amPm;
    private int dayOfMonth, month, year;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private View parent_view;
    private ProgressDialog progressDialog;
    private RelativeLayout changeAccount;
    String cls= "", sec= "", fac= "", sub= "", date= "", hw = "";
    Integer hID = 0;
    Boolean edit = false;
    private StorageReference mStorageRef;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_homework);

        parent_view = findViewById(android.R.id.content);
        initToolbar();

        // files code
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1001);
        }

        //Upload
        FirebaseStorage storage = FirebaseStorage.getInstance();
        mStorageRef = storage.getReferenceFromUrl(LoginSimpleGreen.firebaseApp);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        username = pref.getString("username", "");
        dbName = pref.getString("dbName", "");
        cID = pref.getInt("campusID",0);
        clsID = pref.getInt("classID", 0);
        localID = pref.getInt("localID", 0);
        editor = pref.edit();
        populate = new Populate(getApplicationContext());

        cmbClass = pref.getString("cmbClassPost", "");
        cmbSec = pref.getString("cmbSecPost", "");
        cmbFaculty = pref.getString("cmbFacultyPost", "");
        cmbSubject = pref.getString("cmbSubjectPost", "");
        dtDate = pref.getString("dtDatePost", "");

        selectedClass = cmbClass;
        selectedSec = cmbSec;
        selectedFaculty = cmbFaculty;
        selectedSubject = cmbSubject;

        mUploadList = findViewById(R.id.upload_list);

        pClass = findViewById(R.id.progressBar1);
        pSection = findViewById(R.id.progressBar2);
        pFaculty = findViewById(R.id.progressBar3);
        pSubject = findViewById(R.id.progressBar4);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Posting Homework");
        progressDialog.setMessage("Please wait...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        class_cv = findViewById(R.id.class_cv_hw);
        subject_cv = findViewById(R.id.subject_cv_hw);
        faculty_cv = findViewById(R.id.faculty_cv_hw);
        section_cv = findViewById(R.id.section_cv_hw);

        subject_cv.setEnabled(false);
        faculty_cv.setEnabled(false);
        section_cv.setEnabled(false);

        class_txt = findViewById(R.id.class_txt_homework_changetxt);
        section_txt = findViewById(R.id.section_txt_homework_changetxt);
        faculty_txt = findViewById(R.id.faculty_txt_homework_changetxt);
        subject_txt = findViewById(R.id.subject_txt_homework_changetxt);

        date_of_homework_btn = findViewById(R.id.date_btn);

        date_btn = findViewById(R.id.date_btn_hw);
        time_btn = findViewById(R.id.time_btn_hw);
        post_btn = findViewById(R.id.post_btn);

        homeWork = findViewById(R.id.homework_ed_txt);
        //notification = findViewById(R.id.notification);
        clearParagraph = findViewById(R.id.clear_paragraph);
        deadlineLabel = findViewById(R.id.deadline_label);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        checkBox = findViewById(R.id.deadline_validate);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                Drawable buttonDrawable = date_btn.getBackground();
                buttonDrawable = DrawableCompat.wrap(buttonDrawable);

                Drawable buttonDrawable2 = time_btn.getBackground();
                buttonDrawable2 = DrawableCompat.wrap(buttonDrawable2);

                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable, Color.parseColor("#1E88E5"));
                date_btn.setBackground(buttonDrawable);

                //the color is a direct color int and not a color resource
                DrawableCompat.setTint(buttonDrawable2, Color.parseColor("#1E88E5"));
                time_btn.setBackground(buttonDrawable2);

                if (checkBox.isChecked()) {
                    deadlineLabel.setTextColor(Color.parseColor("#F44336"));
                    date_btn.setEnabled(true);
                    time_btn.setEnabled(true);

                    //the color is a direct color int and not a color resource
                    DrawableCompat.setTint(buttonDrawable, Color.parseColor("#1E88E5"));
                    date_btn.setBackground(buttonDrawable);

                    //the color is a direct color int and not a color resource
                    DrawableCompat.setTint(buttonDrawable2, Color.parseColor("#1E88E5"));
                    time_btn.setBackground(buttonDrawable2);
                } else if (!checkBox.isChecked()) {
                    deadlineLabel.setTextColor(Color.parseColor("#666666"));
                    date_btn.setEnabled(false);
                    time_btn.setEnabled(false);

                    //the color is a direct color int and not a color resource
                    DrawableCompat.setTint(buttonDrawable, Color.parseColor("#666666"));
                    date_btn.setBackground(buttonDrawable);

                    //the color is a direct color int and not a color resource
                    DrawableCompat.setTint(buttonDrawable2, Color.parseColor("#666666"));
                    time_btn.setBackground(buttonDrawable2);
                }
            }
        });

        Bundle extras = getIntent().getExtras();
        try{
            hID = extras.getInt("id");
            cls = extras.getString("cls");
            sec = extras.getString("sec");
            fac = extras.getString("fac");
            sub = extras.getString("sub");
            date = extras.getString("date");
            hw = extras.getString("hw");

            String[] dt = date.split("/");
            dtDate = dt[0] + "-" + dt[1] + "-" + dt[2];
            date_of_homework_btn.setText(dtDate);
            homeWork.setText(hw);
            edit = true;
        }
        catch(Exception x){
            edit = false;
            Log.e("data.fastech.pk", x.getLocalizedMessage());
        }

        LoadClasses(section_cv, faculty_cv, subject_cv);

        if(!cmbClass.equals("")){
            class_txt.setText(cmbClass);
        }

        if(!dtDate.equals("")){
            date_of_homework_btn.setText(dtDate);
        }
        else {
            // Date now
            calendar = Calendar.getInstance();
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH);
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            dtDate = calendar.get(Calendar.DAY_OF_MONTH) + "-" + (month + 1) + "-" + year;
            editor.putString("dtDate", dtDate);
            editor.apply();
            Log.d("data.fastech.pk", "dt: " + dtDate);
            date_of_homework_btn.setText(dtDate);
        }

        Log.d("data.fastech.pk", "D2: " + date_btn.getText().toString());

        // Select Class, Section and Subject code
        class_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostHomework.this, "Select Class", "Search...",
                        null, initDataClass(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        class_txt.setText(searchable.getTitle());
                        selectedClass = searchable.getTitle();
                        cmbClass = searchable.getTitle();
                        editor.putString("cmbClassPost", class_txt.getText().toString());
                        editor.apply();
                        LoadSections(populate.getSelectedClass(selectedClass), section_cv, faculty_cv, subject_cv);
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        section_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostHomework.this, "Select Section", "Search...",
                        null, initDataSection(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        section_txt.setText(searchable.getTitle());
                        selectedSec = searchable.getTitle();
                        cmbSec = searchable.getTitle();
                        editor.putString("cmbSecPost", section_txt.getText().toString());
                        editor.apply();
                        LoadFaculty(populate.getSelectedClass(selectedClass), faculty_cv, subject_cv);
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        faculty_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostHomework.this, "Select Faculty", "Search...",
                        null, initDataFaculty(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        faculty_txt.setText(searchable.getTitle());
                        selectedFaculty = searchable.getTitle();
                        cmbFaculty = searchable.getTitle();
                        editor.putString("cmbFacultyPost", faculty_txt.getText().toString());
                        editor.apply();
                        LoadSubjects(populate.getSelectedClass(selectedClass), cmbSec, populate.getFacultyID(selectedFaculty), subject_cv);
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        subject_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(PostHomework.this, "Select Subject", "Search...",
                        null, initDataSubject(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        subject_txt.setText(searchable.getTitle());
                        selectedSubject = searchable.getTitle();
                        cmbSubject = searchable.getTitle();
                        editor.putString("cmbSubjectPost", selectedSubject);
                        editor.apply();
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();
            }
        });

        // date of homework posting
        date_of_homework_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("data.fastech.pk", "Calender:" + dtDate);
                Integer day = Integer.parseInt(dtDate.split("-")[0]);
                Integer mn = Integer.parseInt(dtDate.split("-")[1]);
                mn = mn - 1;
                Integer year = Integer.parseInt(dtDate.split("-")[2]);

                datePickerDialog = new DatePickerDialog(PostHomework.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_of_homework_btn.setText(day + "/" + (month + 1) + "/" + year);
                        dtDate = day + "-" + (month + 1) + "-" + year;
                    }
                }, year, mn, day);
                datePickerDialog.show();
            }
        });

        // date and time code
        time_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timePickerDialog = new TimePickerDialog(PostHomework.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hours, int minutes) {
                        if (hours == 0) {
                            hours += 12;
                            amPm = "AM";
                        } else if (hours == 12) {
                            amPm = "PM";
                        } else if (hours > 12) {
                            hours -= 12;
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        time_btn.setText(String.format("%2d:%02d", hours, minutes) + " " + amPm);
                    }
                }, 0, 0, false);
                timePickerDialog.show();
            }
        });

        date_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("data.fastech.pk", "Updating date...");
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(PostHomework.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        date_btn.setText(day + "/" + (month + 1) + "/" + year);
                        dtDate = day + "-" + (month + 1) + "-" + year;
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        clearParagraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                homeWork.setText("");
            }
        });

        //post button
        post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PostHomework.this, SelectAccount.class);
                startActivity(intent);
            }
        });
    }

    public void postHomework() {
        if (checkBox.isChecked()) {
            if (class_txt.getText().toString().equals("Class")) {
                snackBarIconError("Please select class");
            } else if (section_txt.getText().toString().equals("Section")) {
                snackBarIconError("Please select section");
            } else if (faculty_txt.getText().toString().equals("Faculty")) {
                snackBarIconError("Please select faculty");
            } else if (subject_txt.getText().toString().equals("Subject")) {
                snackBarIconError("Please select subject");
            } else if (date_of_homework_btn.getText().toString().equals("Date")) {
                snackBarIconError("Please select date");
            } else if (date_btn.getText().toString().equals("Due Date")) {
                snackBarIconError("Select due date");
            } else if (time_btn.getText().toString().equals("Due Time")) {
                snackBarIconError("Select due time");
            } else if (homeWork.getText().toString().equals("")) {
                snackBarIconError("Please define homework");
            } else {
                progressDialog.show();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetFields();
                        progressDialog.dismiss();
                        snackBarIconSuccessful();
                    }
                }, 5000);
            }
        }

        if (!checkBox.isChecked()) {

            if (class_txt.getText().toString().equals("Class")) {
                snackBarIconError("Please select class");
            } else if (section_txt.getText().toString().equals("Section")) {
                snackBarIconError("Please select section");
            } else if (faculty_txt.getText().toString().equals("Faculty")) {
                snackBarIconError("Please select faculty");
            } else if (subject_txt.getText().toString().equals("Subject")) {
                snackBarIconError("Please select subject");
            } else if (date_of_homework_btn.getText().toString().equals("Date")) {
                snackBarIconError("Please select date");
            } else if (homeWork.getText().toString().equals("")) {
                snackBarIconError("Please define homework");
            } else {
                progressDialog.show();
                Log.d("data.fastech.pk", "dtDate: " + dtDate);
                String day = dtDate.split("-")[0];
                String mn = dtDate.split("-")[1];
                String _year = dtDate.split("-")[2];
                String url = "http://app.fastech.pk/Homework/Post";
                JSONObject js = new JSONObject();
                try {
                    if (!edit) //Add
                    {
                        Log.d("data.fastech.pk", "Adding hw..");
                        js.put("tID", localID);
                        js.put("clsID", populate.getSelectedClass(class_txt.getText().toString()).getId());
                        js.put("sec", section_txt.getText().toString());
                        js.put("facID", populate.getFacultyID(faculty_txt.getText().toString()).getId());
                        js.put("sub", populate.getSubjectID(subject_txt.getText().toString()).getId());
                        js.put("hw", homeWork.getText().toString());
                        js.put("day", day);
                        js.put("mn", mn);
                        js.put("year", _year);
                        js.put("cID", cID);
                        js.put("_db", dbName);
                        Log.d("data.fastech.pk", js.toString());
                    }
                    else{ //Edit
                        url = "http://app.fastech.pk/Homework/Edit";
                        Log.d("data.fastech.pk", "Updating hw..");
                        js.put("id", hID);
                        js.put("tID", localID);
                        js.put("clsID", populate.getSelectedClass(class_txt.getText().toString()).getId());
                        js.put("sec", section_txt.getText().toString());
                        js.put("facID", populate.getFacultyID(faculty_txt.getText().toString()).getId());
                        js.put("sub", populate.getSubjectID(subject_txt.getText().toString()).getId());
                        js.put("hw", homeWork.getText().toString());
                        js.put("day", day);
                        js.put("mn", mn);
                        js.put("year", _year);
                        js.put("cID", cID);
                        js.put("_db", dbName);
                        Log.d("data.fastech.pk", js.toString());
                    }
                } catch (JSONException e) {
                    Log.e("data.fastech.pk", e.getLocalizedMessage());
                }

                JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                        url, js, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("data.fastech.pk", "Post Response: " + response);
                        //Saved
                        progressDialog.show();
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                resetFields();
                                snackBarIconSuccessful();
                            }
                        }, 5000);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError e) {
                        Log.e("data.fastech.pk", "Error on request:" + e.getLocalizedMessage());
                        progressDialog.dismiss();
                        Toast.makeText(PostHomework.this, "Error! Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };

                queue.add(jsonObjReq);
            }
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Post Homework");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_posthomework, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_save) {
            postHomework();
        }
        else if (item.getItemId() == R.id.action_attach) {
            showBottomSheetDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 1001:

                    // Checking whether data is null or not
                    if (data != null) {

                        // Checking for selection multiple files or single.
                        if (data.getClipData() != null){

                            // Getting the length of data and logging up the logs using index
                            for (int index = 0; index < data.getClipData().getItemCount(); index++) {
                                // Getting the URIs of the selected files and logging them into logcat at debug level
                                Uri uri = data.getClipData().getItemAt(index).getUri();
                                Upload up = new Upload();
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    up.UploadFile(getApplication(), uri ,mStorageRef, dbName);
                                }
                                else{
                                    Toast.makeText(this, "You need android version KITKAT or later for this feature.", Toast.LENGTH_LONG).show();
                                }
                            }
                        }else{

                            // Getting the URI of the selected file and logging into logcat at debug level
                            Uri uri = data.getData();

                            Upload up = new Upload();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                up.UploadFile(getApplication(), uri ,mStorageRef, dbName);
                            }
                            else{
                                Toast.makeText(this, "You need android version KITKAT or later for this feature.", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1001: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    //=========================Loading Classes=========================//
    public void LoadClasses(final CardView section_cv, final CardView faculty_cv, final  CardView subject_cv) {
        // Class progress show here
        pClass.setVisibility(View.VISIBLE);
        try {
            final String url = LoginSimpleGreen.domain + "/Fill/TClasses?cID=" + cID + "&_db=" + dbName + "&localID=" + localID;
            Log.d("data.fastech.pk", "Loading classes for:" + url);
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
                            try {
                                // Get the JSON array
                                Log.d("data.fastech.pk", "Getting classes");
                                JSONArray array = response.getJSONArray("data");
                                Log.d("data.fastech.pk", "Got classes");

                                // Loop through the array elements
                                for (int i = 0; i < array.length(); i++) {
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

                                // Class progress hide here
                                pClass.setVisibility(View.GONE);
                                if (!cls.equals("")) {
                                    class_txt.setText(cls);
                                    selectedClass = cls;
                                    cmbClass = cls;
                                }

                                // Class loaded
                                LoadSections(populate.getSelectedClass(cmbClass), section_cv, faculty_cv, subject_cv);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Do something when error occurred
                            //Log.e("data.fastech.pk", error.getLocalizedMessage());
                        }
                    }
            );
            queue.add(request);
        } catch (Exception x) {
        }
    }

    //=========================Loading Sections=========================//
    public void LoadSections(Classes cls, final CardView section_cv, final CardView faculty_cv, final CardView subject_cv) {
        if(cls != null) {
            // Section progress show here
            pSection.setVisibility(View.VISIBLE);
            Log.d("data.fastech.pk", "Loading sections for: " + cls.getCls());
            try {
                populate.secList.clear();
                section_txt.setText("SECTION");
                section_cv.setEnabled(false);
                final String url = LoginSimpleGreen.domain + "/Fill/TSections?cID=" + cID + "&_db=" + dbName + "&localID=" + localID + "&clsID=" + cls.getId();
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
                                try {
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    // Loop through the array elements
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Sections c = new Sections();
                                        try {
                                            String jsonObject = jsonArray.getString(i);
                                            Log.d("data.fastech.pk", "Sec: " + jsonObject);
                                            c.setSection(jsonObject);
                                            populate.secList.add(c);
                                            section_cv.setEnabled(true);
                                            if (jsonObject.equals(cmbSec)) {
                                                found = true;
                                            }
                                        } catch (JSONException e) {
                                            Log.e("data.fastech.pk", "Error parsing data [" + e.getMessage() + "] ");
                                        }
                                    }


                                    if (!sec.equals(""))
                                    {
                                        section_txt.setText(sec);
                                        selectedSec = sec;
                                        cmbSec = sec;
                                    }
                                    else {
                                        if (found){
                                            section_txt.setText(cmbSec);
                                        }
                                    }

                                    // Section progress hide here
                                    pSection.setVisibility(View.GONE);
                                    // Load Faculty
                                    LoadFaculty(populate.getSelectedClass(cmbClass), faculty_cv, subject_cv);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Do something when error occurred
                                //Log.e("data.fastech.pk", error.getLocalizedMessage());
                            }
                        }
                );

                queue.add(request);
            } catch (Exception x) {
            }
        }
    }

    //=========================Loading Faculty=========================//
    public void LoadFaculty(Classes cls, final CardView faculty_cv, final CardView subject_cv) {
        // Section progress show here
        pFaculty.setVisibility(View.VISIBLE);
        try {
            populate.facList.clear();
            faculty_txt.setText("FACULTY");
            faculty_cv.setEnabled(false);
            Log.d("data.fastech.pk", "Loading fac for: " + cls.getCls());
            final String url = LoginSimpleGreen.domain + "/Fill/TFaculty?cID=" + cID + "&_db=" + dbName + "&localID=" + localID + "&clsID=" + cls.getId();
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
                            try {
                                JSONArray jsonArray = response.getJSONArray("data");
                                // Loop through the array elements
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Faculty c = new Faculty();
                                    try {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        c.setId(jsonObject.getInt("id"));
                                        c.setName(jsonObject.getString("name"));
                                        populate.facList.add(c);
                                        faculty_cv.setEnabled(true);
                                        if(c.getName().equals(cmbFaculty)){
                                            found = true;
                                        }

                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                Log.d("data.fastech.pk", "cmbFac: " + cmbFaculty);

                                if (!fac.equals(""))
                                {
                                    faculty_txt.setText(fac);
                                    selectedFaculty = fac;
                                    cmbFaculty = fac;
                                }
                                else
                                {
                                    if(found){
                                        faculty_txt.setText(cmbFaculty);
                                    }
                                }

                                // Faculty progress hide here
                                pFaculty.setVisibility(View.GONE);
                                // Load subjects here
                                LoadSubjects(populate.getSelectedClass(cmbClass), cmbSec, populate.getFacultyID(cmbFaculty), subject_cv);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
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

    //=========================Loading Subjects=========================//
    public void LoadSubjects(Classes cls, String sec, Faculty fac, final CardView subject_cv) {
        if(fac != null) {
            // Show subject
            pSubject.setVisibility(View.VISIBLE);
            try {
                populate.subsList.clear();
                subject_txt.setText("SUBJECT");
                subject_cv.setEnabled(false);
                Log.d("data.fastech.pk", "Loading subjects for: " + cls.getCls() + " - " + sec);
                final String url = LoginSimpleGreen.domain + "/Fill/TSubjects?cID=" + cID + "&_db=" + dbName + "&localID=" + localID + "&clsID=" + cls.getId() + "&sec=" + sec + "&fac=" + fac.getId();
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
                                try {
                                    JSONArray jsonArray = response.getJSONArray("data");
                                    // Loop through the array elements
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        Subjects c = new Subjects();
                                        try {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            String subject = jsonObject.getString("name");
                                            Integer subjectID = jsonObject.getInt("id");
                                            Log.d("data.fastech.pk", "Subject: " + subject);
                                            c.setId(subjectID);
                                            c.setSubject(subject);
                                            populate.subsList.add(c);


                                            if (!sub.equals(""))
                                            {
                                                subject_txt.setText(sub);
                                                selectedSubject = sub;
                                                cmbSubject = sub;
                                            }
                                            else{
                                                if(subject.equals(cmbSubject)){
                                                    found = true;
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Log.e("data.fastech.pk", "Error parsing data [" + e.getMessage() + "] ");
                                        }
                                    }

                                    // Hide subjects progress here
                                    pSubject.setVisibility(View.GONE);
                                    subject_cv.setEnabled(true);

                                    if(found){
                                        subject_txt.setText(cmbSubject);
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                            }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
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


    private ArrayList<SearchModel> initDataClass() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Classes c : populate.clsList) {
            items.add(new SearchModel(c.getCls(), c.getCls()));
        }
        return items;
    }

    private ArrayList<SearchModel> initDataSection() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Sections c : populate.secList) {
            items.add(new SearchModel(c.getSection(), c.getSection()));
        }
        return items;
    }

    private ArrayList<SearchModel> initDataFaculty() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Faculty c : populate.facList) {
            items.add(new SearchModel(c.getName(), c.getName()));
        }
        return items;
    }

    private ArrayList<SearchModel> initDataSubject() {
        ArrayList<SearchModel> items = new ArrayList<>();
        for (Subjects c : populate.subsList) {
            items.add(new SearchModel(c.getSubject(), c.getSubject()));
        }
        return items;
    }

    private void snackBarIconError(String name) {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
        //inflate view
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

    private void snackBarIconSuccessful() {
        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_LONG);
        //inflate view
        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
        snackBarView.setPadding(0, 0, 0, 0);

        ((TextView) custom_view.findViewById(R.id.message)).setText("Homework Posted Successfully");
        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_done);
        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.green_400));
        snackBarView.addView(custom_view, 0);
        snackbar.show();
    }

    private void showBottomSheetDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_floating, null);
        (view.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });

        (view.findViewById(R.id.image_cv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
                mBottomSheetDialog.dismiss();
            }
        });

        (view.findViewById(R.id.pdf_cv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a PDF "), SELECT_PDF);
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

    public void resetFields() {
//        class_txt.setText("Class");
//        section_txt.setText("Section");
//        faculty_txt.setText("Faculty");
//        subject_txt.setText("Subject");
//        date_of_homework_btn.setText("Date");
//        date_btn.setText("Due Date");
//        time_btn.setText("Due Time");
        homeWork.setText("");
    }
}