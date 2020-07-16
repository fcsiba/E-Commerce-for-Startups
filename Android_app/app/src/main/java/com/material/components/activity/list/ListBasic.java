package com.material.components.activity.list;
//
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.adapter.AdapterListBasic;
import com.material.components.data.DataGenerator;
import com.material.components.model.Event;
import com.material.components.model.People;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import ir.mirrajabi.searchdialog.SimpleSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.BaseSearchDialogCompat;
import ir.mirrajabi.searchdialog.core.SearchResultListener;
import ir.mirrajabi.searchdialog.core.Searchable;
import pushy.fastech.pk.models.SearchModel;
//
public class ListBasic extends AppCompatActivity {
//
//    int dayOfMonth, month, year;
//    Calendar calendar;
//    DatePickerDialog datePickerDialog;
//
//    private View parent_view;
//
//    private RecyclerView recyclerView;
//    private AdapterListBasic mAdapter;
//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_basic);
//        parent_view = findViewById(android.R.id.content);
//
//        initToolbar();
//        initComponent();
    }
//
//    private void initToolbar() {
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        toolbar.setNavigationIcon(R.drawable.ic_menu);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setTitle("Class");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        Tools.setSystemBarColor(this);
//    }
//
//    private void initComponent() {
//        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setHasFixedSize(true);
//
//        final List<People> items = DataGenerator.getPeopleData(this);
//        items.addAll(DataGenerator.getPeopleData(this));
//        items.addAll(DataGenerator.getPeopleData(this));
//
//        //set data and list adapter
//        mAdapter = new AdapterListBasic(this, items);
//        recyclerView.setAdapter(mAdapter);
//
//
//        // on item list clicked
//        mAdapter.setOnItemClickListener(new AdapterListBasic.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(View view, People obj, int position) {
////                studentAbsent();
////                Snackbar.make(parent_view, "Item " + obj.name + " clicked", Snackbar.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onItemLongClick(View view, People obj, int pos) {
//                onLeaveCustomDialog();
//            }
//        });
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.student_present_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == android.R.id.home) {
//            Toast.makeText(this, "home", Toast.LENGTH_SHORT).show();
//        }
//        else if(item.getItemId() == R.id.ic_class_selection){
//            selectionDialog();
//        }
//        else if(item.getItemId() == R.id.ic_done_all_attendance) {
//            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show();
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//
//
//
//
//    private void selectionDialog(){
//
//        final Dialog dialogSelection = new Dialog(this);
//        dialogSelection.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialogSelection.setContentView(R.layout.class_selection_dialog);
//        dialogSelection.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//        dialogSelection.setCancelable(true);
//
//        WindowManager.LayoutParams lp2 = new WindowManager.LayoutParams();
//        lp2.copyFrom(dialogSelection.getWindow().getAttributes());
//        lp2.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp2.height = WindowManager.LayoutParams.WRAP_CONTENT;
//
//        ((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SimpleSearchDialogCompat(ListBasic.this, "SELECT CLASS", "Search...",
//                        null, initDataClass(), new SearchResultListener<Searchable>() {
//                    @Override
//                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
////                        Toast.makeText(ListBasic.this, "Selected: " + searchable.getTitle(), Toast.LENGTH_SHORT).show();
//                        ((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).setText(searchable.getTitle());
//                        baseSearchDialogCompat.dismiss();
//                    }
//                }).show();
//            }
//        });
//
//        ((Button) dialogSelection.findViewById(R.id.dialog_section_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SimpleSearchDialogCompat(ListBasic.this, "SELECT SECTION", "Search...",
//                        null, initDataSection(), new SearchResultListener<Searchable>() {
//                    @Override
//                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
////                        Toast.makeText(ListBasic.this, "Selected: " + searchable.getTitle(), Toast.LENGTH_SHORT).show();
//                        ((Button) dialogSelection.findViewById(R.id.dialog_section_btn)).setText(searchable.getTitle());
//                        baseSearchDialogCompat.dismiss();
//                    }
//                }).show();
//            }
//        });
//
//        ((Button) dialogSelection.findViewById(R.id.dialog_subject_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                new SimpleSearchDialogCompat(ListBasic.this, "SELECT SUBJECT", "Search...",
//                        null, initDataSubjects(), new SearchResultListener<Searchable>() {
//                    @Override
//                    public void onSelected(BaseSearchDialogCompat baseSearchDialogCompat, Searchable searchable, int i) {
////                        Toast.makeText(ListBasic.this, "Selected: " + searchable.getTitle(), Toast.LENGTH_SHORT).show();
//                        ((Button) dialogSelection.findViewById(R.id.dialog_subject_btn)).setText(searchable.getTitle());
//                        baseSearchDialogCompat.dismiss();
//                    }
//                }).show();
//            }
//        });
//
//        ((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                calendar = Calendar.getInstance();
//                year = calendar.get(Calendar.YEAR);
//                month = calendar.get(Calendar.MONTH);
//                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
//                datePickerDialog = new DatePickerDialog(ListBasic.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                        ((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).setText(day + "/" + (month + 1) + "/" + year);
//                    }
//                }, year, month, dayOfMonth);
//                datePickerDialog.show();
//            }
//        });
//
//        ((Button) dialogSelection.findViewById(R.id.dialog_done_btn)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(((Button) dialogSelection.findViewById(R.id.dialog_class_btn)).getText().toString().equals("CLASS")){
////                    snackBarIconError();
//                }
//                else if(((Button) dialogSelection.findViewById(R.id.dialog_section_btn)).getText().toString().equals("SECTION")){
////                    snackBarIconError2();
//                }
//                else if(((Button) dialogSelection.findViewById(R.id.dialog_subject_btn)).getText().toString().equals("SUBJECT")){
////                    snackBarIconError3();
//                }
//                else if(((Button) dialogSelection.findViewById(R.id.dialog_date_btn)).getText().toString().equals("DATE")){
////                    snackBarIconError4();
//                }
//                else
//                {
//                    Toast.makeText(ListBasic.this, "Done", Toast.LENGTH_SHORT).show();
//                    dialogSelection.hide();
//                }
//
//            }
//        });
//
//        dialogSelection.show();
//        dialogSelection.getWindow().setAttributes(lp2);
//    }
//
//    private ArrayList<SearchModel> initDataClass() {
//        ArrayList<SearchModel> items = new ArrayList<>();
//        items.add(new SearchModel("Class - 1"));
//        items.add(new SearchModel("Class - 2"));
//        items.add(new SearchModel("Class - 3"));
//        items.add(new SearchModel("Class - 4"));
//        items.add(new SearchModel("Class - 5"));
//        items.add(new SearchModel("Class - 6"));
//        items.add(new SearchModel("Class - 7"));
//        items.add(new SearchModel("Class - 8"));
//        items.add(new SearchModel("Class - 9"));
//        items.add(new SearchModel("Class - 10"));
//
//        return items;
//    }
//
//    private ArrayList<SearchModel> initDataSection() {
//        ArrayList<SearchModel> items = new ArrayList<>();
//        items.add(new SearchModel("Section - A"));
//        items.add(new SearchModel("Section - B"));
//        items.add(new SearchModel("Section - C"));
//        items.add(new SearchModel("Section - D"));
//        items.add(new SearchModel("Section - E"));
//        items.add(new SearchModel("Section - F"));
//        items.add(new SearchModel("Section - G"));
//        items.add(new SearchModel("Section - H"));
//        items.add(new SearchModel("Section - I"));
//        items.add(new SearchModel("Section - J"));
//
//        return items;
//    }
//
//    private ArrayList<SearchModel> initDataSubjects() {
//        ArrayList<SearchModel> items = new ArrayList<>();
//        items.add(new SearchModel("Mathematics"));
//        items.add(new SearchModel("English"));
//        items.add(new SearchModel("Urdu"));
//        items.add(new SearchModel("Islamiat"));
//        items.add(new SearchModel("Biology"));
//        items.add(new SearchModel("Chemistry"));
//        items.add(new SearchModel("Physics"));
//
//        return items;
//    }
//
////    private void snackBarIconError() {
////        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
////        //inflate view
////        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);
////
////        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
////        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
////        snackBarView.setPadding(0, 0, 0, 0);
////
////        ((TextView) custom_view.findViewById(R.id.message)).setText("Select Class");
////        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
////        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
////        snackBarView.addView(custom_view, 0);
////        snackbar.show();
////    }
////
////    private void snackBarIconError2() {
////        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
////        //inflate view
////        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);
////
////        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
////        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
////        snackBarView.setPadding(0, 0, 0, 0);
////
////        ((TextView) custom_view.findViewById(R.id.message)).setText("Select Section");
////        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
////        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
////        snackBarView.addView(custom_view, 0);
////        snackbar.show();
////    }
////
////    private void snackBarIconError3() {
////        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
////        //inflate view
////        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);
////
////        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
////        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
////        snackBarView.setPadding(0, 0, 0, 0);
////
////        ((TextView) custom_view.findViewById(R.id.message)).setText("Select Subject");
////        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
////        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
////        snackBarView.addView(custom_view, 0);
////        snackbar.show();
////    }
////
////    private void snackBarIconError4() {
////        final Snackbar snackbar = Snackbar.make(parent_view, "", Snackbar.LENGTH_SHORT);
////        //inflate view
////        View custom_view = getLayoutInflater().inflate(R.layout.snackbar_icon_text, null);
////
////        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
////        Snackbar.SnackbarLayout snackBarView = (Snackbar.SnackbarLayout) snackbar.getView();
////        snackBarView.setPadding(0, 0, 0, 0);
////
////        ((TextView) custom_view.findViewById(R.id.message)).setText("Select date");
////        ((ImageView) custom_view.findViewById(R.id.icon)).setImageResource(R.drawable.ic_close);
////        (custom_view.findViewById(R.id.parent_view)).setBackgroundColor(getResources().getColor(R.color.red_600));
////        snackBarView.addView(custom_view, 0);
////        snackbar.show();
////    }
}