package pushy.fastech.pk.admin.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.profile.ProfileCardHeader;
import com.material.components.utils.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import pushy.fastech.pk.admin.adminportal.AdminDashboard;
import pushy.fastech.pk.models.SearchModel;
import pushy.fastech.pk.staff.staffportal.StudentAttendanceList;
import pushy.fastech.pk.staff.staffportal.StudentMarksList;

public class CampusReportPanel extends AppCompatActivity {

    TextView date_from, date_to, campus_gettxt, department_gettxt, report_gettxt;
    CardView campus_dialog,department_dialog,report_dialog,from_dialog,to_dialog;

    int dayOfMonth, month, year;
    Calendar calendar;
    DatePickerDialog datePickerDialog;
    SimpleDateFormat df;
    String formattedDate;


    private View parent_view;

    private NestedScrollView nested_scroll_view;
    private ImageButton report_bt_toggle_text, from_bt_toggle_text, to_bt_toggle_text;
    private Button report_bt_hide_text, from_bt_hide_text, to_bt_hide_text;
    private View report_lyt_expand_text, from_lyt_expand_text, to_lyt_expand_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_report_panel);
        parent_view = findViewById(android.R.id.content);

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time: " + c);
        df = new SimpleDateFormat("EEEE, d MMM, yyyy");
        formattedDate = df.format(c);

        date_from = findViewById(R.id.from_getDate);
        date_from.setText(formattedDate);
        date_to = findViewById(R.id.to_getDate);
        date_to.setText(formattedDate);
        SharedPreferences sharedPreferences = getSharedPreferences("myKey", MODE_PRIVATE);

        campus_gettxt = findViewById(R.id.campus_txt_getcampus);
        String valueCamp = sharedPreferences.getString("campus", "");
        campus_gettxt.setText(valueCamp);

        department_gettxt = findViewById(R.id.department_txt_getdept);
        String valueDept = sharedPreferences.getString("department", "");
        department_gettxt.setText(valueDept);

        report_gettxt = findViewById(R.id.report_txt_getreport);

        campus_dialog = findViewById(R.id.campus_dialog);
        department_dialog = findViewById(R.id.department_dialog);
        report_dialog = findViewById(R.id.report_dialog);

        from_dialog = findViewById(R.id.from_dialog);
        to_dialog = findViewById(R.id.to_dialog);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Campus Report");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {

        campus_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(CampusReportPanel.this, "Select Campus", "search campus...",
                        null, initData(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        Toast.makeText(CampusReportPanel.this, "" + searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        campus_gettxt.setText(searchable.getTitle());
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();

            }
        });

        department_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SimpleSearchDialogCompat(CampusReportPanel.this, "Select Department", "search department...",
                        null, initData2(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        Toast.makeText(CampusReportPanel.this, "" + searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        department_gettxt.setText(searchable.getTitle());
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();

            }
        });

        report_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SimpleSearchDialogCompat(CampusReportPanel.this, "Select Report", "search report...",
                        null, initData3(), new SearchResultListener<Searchable>() {
                    @Override
                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
                        Toast.makeText(CampusReportPanel.this, "" + searchable.getTitle(), Toast.LENGTH_SHORT).show();
                        report_gettxt.setText(searchable.getTitle());
                        baseSearchDialogCompat.dismiss();
                    }
                }).show();

            }
        });

        from_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog  = new DatePickerDialog(CampusReportPanel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        formattedDate = df.format(calendar.getTime());
                        date_from.setText(formattedDate);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });


        to_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog  = new DatePickerDialog(CampusReportPanel.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        formattedDate = df.format(calendar.getTime());
                        date_to.setText(formattedDate);
//                        date_to.setText(day + "/" + (month + 1) + "/" + year);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);
    }

    //Campuses list
    private ArrayList<SearchModel> initData() {
        ArrayList<SearchModel> items = new ArrayList<>();
        items.add(new SearchModel("Campus1"));
        items.add(new SearchModel("Campus2"));
        items.add(new SearchModel("Campus3"));
        items.add(new SearchModel("Campus4"));
        items.add(new SearchModel("Campus5"));
        items.add(new SearchModel("Campus6"));
        items.add(new SearchModel("Campus7"));
        items.add(new SearchModel("Campus8"));
        items.add(new SearchModel("Campus9"));
        items.add(new SearchModel("Campus10"));
        items.add(new SearchModel("Campus11"));
        items.add(new SearchModel("Campus12"));
        items.add(new SearchModel("Campus13"));
        items.add(new SearchModel("Campus14"));

        return items;
    }

    //Department list
    private ArrayList<SearchModel> initData2() {
        ArrayList<SearchModel> items = new ArrayList<>();
        items.add(new SearchModel("department1"));
        items.add(new SearchModel("department2"));
        items.add(new SearchModel("department3"));
        items.add(new SearchModel("department4"));

        return items;
    }

    //Reports list
    private ArrayList<SearchModel> initData3() {
        ArrayList<SearchModel> items = new ArrayList<>();
        items.add(new SearchModel("reports1"));
        items.add(new SearchModel("reports2"));
        items.add(new SearchModel("reports3"));
        items.add(new SearchModel("reports4"));

        return items;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            showDialogPolygon();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDialogPolygon() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_menu_adminportal);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((LinearLayout) dialog.findViewById(R.id.goto_adminpanel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Intent intent = new Intent(CampusReportPanel.this, AdminDashboard.class);
                startActivity(intent);
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.goto_campusreport)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.goto_attendance)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Intent intent = new Intent(CampusReportPanel.this, StudentAttendanceList.class);
                startActivity(intent);
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.goto_marks)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Intent intent = new Intent(CampusReportPanel.this, StudentMarksList.class);
                startActivity(intent);
            }
        });

        ((LinearLayout) dialog.findViewById(R.id.goto_parentportal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.hide();
                Intent intent = new Intent(CampusReportPanel.this, ProfileCardHeader.class);
                startActivity(intent);
            }
        });

        dialog.show();
    }
}
