package root.fastech.pk.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.material.components.activity.chat.ChatWhatsapp;
import com.material.components.activity.profile.ProfileImageAppbar;
import com.material.components.data.DataGenerator;
import com.material.components.model.Inbox;
import com.material.components.model.Message;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;
import com.material.components.widget.LineItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import root.fastech.pk.adapters.AdapterInquiry;
import root.fastech.pk.model.Student;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ActivityStaff extends AppCompatActivity {


    private ViewPager view_pager;
    //Search
    private AppBarLayout appbarSearch;
    private EditText et_search;
    private ImageButton bt_clear, bt_searchby;
    //List Animation
    private int animation_type = ItemAnimation.FADE_IN;

    //Bottom Sheet
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    //Data
    public RecyclerView recyclerView;
    public Toolbar toolbar;
    public AdapterInquiry mAdapter;
    LinearLayout lyt_progress;
    RequestQueue queue;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_main);

        queue = Volley.newRequestQueue(getApplicationContext());

        //Hide Keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Students");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.blue_600);
    }

    private void initComponent() {


        RecyclerView rv = (RecyclerView) findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_add);
        rv.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);



        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setVisibility(View.VISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Add student clicked.", Toast.LENGTH_SHORT).show(); //Search
            }
        });


        //Searchbar
        appbarSearch = (AppBarLayout) findViewById(R.id.appbarSearch);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_searchby = (ImageButton) findViewById(R.id.bt_search_by);
        appbarSearch.setVisibility(View.INVISIBLE);
        et_search = (EditText) findViewById(R.id.et_search);
        lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);


        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                et_search.setText("");
                et_search.clearFocus();
                appbarSearch.setVisibility(View.INVISIBLE);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
                    et_search.clearFocus();
                    return true;
                }
                return false;
            }
        });


        bt_searchby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSearchByDialog();
            }
        });


        //Bottom Sheet
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        //Load Data
        initData();

    }

    private void searchAction() {

        recyclerView.setVisibility(View.GONE);
        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            //showLoading(1500);

        } else {
            Toast.makeText(this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }


    private void initData() {


        Log.d("data.fastech.pk", "Loading Data...");

        //Loading
        lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        lyt_progress.setVisibility(View.VISIBLE);
        lyt_progress.setAlpha(1.0f);



        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        //List<Student> items = DataGenerator.getInboxData(getApplicationContext());
        final List<Student> items = new ArrayList<>();

        //Load Students
        JsonArrayRequest request = new JsonArrayRequest("http://pushy.fastech.pk/Student/GetAll",
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        for(int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);

                                String id = jsonObject.getString("id");
                                String gr = jsonObject.getString("gr");
                                String name = jsonObject.getString("name");
                                String fName = jsonObject.getString("fNmae");
                                String cls = jsonObject.getString("cls");
                                String sec = jsonObject.getString("sec");
                                String fCode = jsonObject.getString("fCode");
                                String address = jsonObject.getString("address");
                                String mob = jsonObject.getString("mob");
                                String homeMob = jsonObject.getString("homeMob");
                                String tutorMob = jsonObject.getString("tutorMob");

                                Student n = new Student();
                                n.setId(id);
                                n.setGr(gr);
                                n.setName(name);
                                n.setfName(fName);
                                n.setfCode(fCode);
                                n.setAddress(address);
                                n.setMob(mob);
                                n.setHomeMob(homeMob);
                                n.setTutorMob(tutorMob);
                                n.setCls(cls);
                                n.setSec(sec);

                                //Log.d("data.fastech.pk", "ID: " + id + " Name: " + name);

                                //Add to List
                                items.add(n);
                            }
                            catch(JSONException e) {
                                Log.d("data.fastech.pk", e.getLocalizedMessage());
                            }
                        }

                        //Hide Loading...
                        ViewAnimation.fadeOut(lyt_progress);

                        //set data and list adapter
                        mAdapter = new AdapterInquiry(getApplicationContext(), items, animation_type);
                        recyclerView.setAdapter(mAdapter);

                        //Multi Select
                        mAdapter.setOnClickListener(new AdapterInquiry.OnClickListener() {


                            @Override
                            public void onItemClick(View view, Student obj, int pos) {
                                if (mAdapter.getSelectedItemCount() > 0) {
                                    //enableActionMode(pos);
                                } else {
                                    // read the inbox which removes bold from the row
                                    //Inbox inbox = mAdapter.getItem(pos);
                                    //Toast.makeText(getContext(), "Read: " + inbox.from, Toast.LENGTH_SHORT).show();
                                    showBottomSheetDialog(obj);
                                }
                            }

                            @Override
                            public void onItemLongClick(View view, Student obj, int pos)
                            {
                                //enableActionMode(pos);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(ActivityStaff.this, "Unable to load: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        queue.add(request);





    }

    //Actionbar Meny
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Menu in acion bar
        getMenuInflater().inflate(R.menu.menu_refresh_setting, menu);
        //getMenuInflater().inflate(R.menu.menu_search, menu);
        getMenuInflater().inflate(R.menu.menu_people_more, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Action bar icons
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if (item.getItemId() == R.id.btnSearchby) {
            showSearchByDialog();
        }
        else if (item.getItemId() == R.id.btnSearch) { //Search
            appbarSearch.setVisibility(View.VISIBLE);
            //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        return super.onOptionsItemSelected(item);
    }

    //Dialogs

    private void showSearchByDialog() {
        et_search.clearFocus();
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_staff_searchby, null);

        ((View) view.findViewById(R.id.sb_id)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by G.R No.", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();

            }
        });

        ((View) view.findViewById(R.id.sb_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();
            }
        });

        ((View) view.findViewById(R.id.sb_father)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by father's name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();
            }
        });

        ((View) view.findViewById(R.id.sb_designation)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by Class-Sec", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();
            }
        });

        ((View) view.findViewById(R.id.sb_conatct)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by Contact", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();
            }
        });




        mBottomSheetDialog = new BottomSheetDialog(ActivityStaff.this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //Landscape
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mBehavior.setPeekHeight(view.getHeight());//get the height dynamically
            }
        });

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }



    private void showBottomSheetDialog(final Student people) {
        et_search.clearFocus();
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_student_menu, null);



        ((View) view.findViewById(R.id.lyt_preview)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Preview '" + people.getName() + "' clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ActivityStaff.this, ProfileImageAppbar.class);
                String clicked_id = people.getId() + "";
                intent.putExtra(EXTRA_MESSAGE, clicked_id);
                startActivity(intent);
                mBottomSheetDialog.dismiss();


            }
        });

        ((View) view.findViewById(R.id.lyt_push)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "Push Notification '" + people.getName() + "' clicked", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), ChatWhatsapp.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", people.getMob());
                bundle.putString("name", people.getName());
                bundle.putInt("typeN", 1);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


        ((View) view.findViewById(R.id.lyt_sms)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(getApplicationContext(), "SMS '" + people.getName() + "' clicked", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
                hideKeyboard();

                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", people.getMob());
                smsIntent.putExtra("sms_body","");
                startActivity(smsIntent);
            }
        });

        ((View) view.findViewById(R.id.lyt_block)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog(people);
                mBottomSheetDialog.dismiss();
                hideKeyboard();
            }
        });

        ((View) view.findViewById(R.id.lyt_unblock)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActiveDeactive(people.getMob(), false);
                ActiveDeactive(people.getMob(), false);
                ActiveDeactive(people.getMob(), false);
                Toast.makeText(getApplicationContext(), "Unblocked.", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();

            }
        });



        mBottomSheetDialog = new BottomSheetDialog(ActivityStaff.this);
        mBottomSheetDialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        //Landscape
        final BottomSheetBehavior mBehavior = BottomSheetBehavior.from((View) view.getParent());
        mBottomSheetDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                mBehavior.setPeekHeight(view.getHeight());//get the height dynamically
            }
        });



        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void showRemoveDialog(final Student obj) {
        et_search.clearFocus();
        final Dialog dialog = new Dialog(ActivityStaff.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_remove);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((AppCompatButton) dialog.findViewById(R.id.bt_remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActiveDeactive(obj.getMob(), true);
                ActiveDeactive(obj.getMob(), true);
                ActiveDeactive(obj.getMob(), true);
                Toast.makeText(getApplicationContext(), "Blocked.", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void ActiveDeactive(String username, final boolean block)
    {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url;


        if (block)
        {
            url =  "http://pushy.fastech.pk/Home/Block?sID=" + username;
        }
        else
        {
            url =  "http://pushy.fastech.pk/Home/Unblock?sID=" + username;
        }

        Log.d("data.fastech.pk", url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String title = "Blocked";
                        if (!block)
                            title = "Unblocked";

                        // Convert String to json object
                        JSONObject json = null;
                        try {
                            json = new JSONObject(response.toString());
                            Log.d("data.fastech.pk", response);
                        } catch (JSONException e) {
                            Log.d("data.fastech.pk", e.getLocalizedMessage());
                            e.printStackTrace();
                        }
                        // get value from  Json Object
                        try {
                            String status = json.getString("status");
                            if (status.equals("Success"))                            {

                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Unable to perform action.", Toast.LENGTH_SHORT).show();
                                Log.d("data.fastech.pk", "Status: " + status);
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Unable to perform action.", Toast.LENGTH_SHORT).show();
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





}
