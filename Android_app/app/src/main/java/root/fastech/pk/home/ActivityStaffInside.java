package root.fastech.pk.home;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.profile.ProfileImageAppbar;
import com.material.components.data.DataGenerator;
import com.material.components.model.Inbox;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;
import com.material.components.widget.LineItemDecoration;

import java.util.List;

import root.fastech.pk.adapters.AdapterAllowances;
import root.fastech.pk.adapters.AdapterInquiry;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class ActivityStaffInside extends AppCompatActivity {

    private ImageButton bt_toggle_info, bt_toggle_aapointment, bt_toggle_salary, bt_toggle_allowances, bt_add_allowance, bt_add_last_increment, bt_toggle_last_increment, bt_toggle_hours, bt_add_hours, bt_add_sub, bt_toggle_sub,  bt_toggle_family;
    private View lyt_expand_info, lyt_expand_appointment, lyt_expand_salary, lyt_expand_allowances,lyt_expand_last_increment, lyt_expand_hours, lyt_expand_sub, lyt_expand_family;
    private NestedScrollView nested_scroll_view;

    //Allowances
    public RecyclerView recyclerView;
    public AdapterAllowances mAdapter;

    //Last Increment
    public RecyclerView rvLastIncre;
    public AdapterAllowances mAdapterLastIncre;

    //Wokring Hours
    public RecyclerView rvHours;
    public AdapterAllowances mAdapterHours;

    //Subjects
    public RecyclerView rvSubs;
    public AdapterAllowances mAdapterSub;

    //List Animation
    private int animation_type = ItemAnimation.FADE_IN;

    //Bottom Sheet
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_inside);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initComponent() {


        //RV - Allowances
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.addItemDecoration(new LineItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        List<Inbox> items = DataGenerator.getInboxData(getApplicationContext());

        //set data and list adapter
        mAdapter = new AdapterAllowances(getApplicationContext(), items, animation_type);
        recyclerView.setAdapter(mAdapter);

        //Multi Select
        mAdapter.setOnClickListener(new AdapterAllowances.OnClickListener() {

            @Override
            public void onItemClick(View view, Inbox obj, int pos) {

                showBottomSheetDialog(obj, 1);
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos)
            {
                showRemoveDialog();
            }
        });


        //RV - Last Increment
        rvLastIncre = (RecyclerView) findViewById(R.id.rvLastIncrement);
        rvLastIncre.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvLastIncre.addItemDecoration(new LineItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        rvLastIncre.setHasFixedSize(true);

        List<Inbox> itemsLastIncre = DataGenerator.getInboxData(getApplicationContext());

        //set data and list adapter
        mAdapterLastIncre = new AdapterAllowances(getApplicationContext(), itemsLastIncre, animation_type);
        rvLastIncre.setAdapter(mAdapterLastIncre);

        //Multi Select
        mAdapterLastIncre.setOnClickListener(new AdapterAllowances.OnClickListener() {

            @Override
            public void onItemClick(View view, Inbox obj, int pos) {
                showBottomSheetDialog(obj, 2);
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos)
            {
                showRemoveDialog();
            }
        });

        //RV - Hours
        rvHours = (RecyclerView) findViewById(R.id.rvWorkingHours);
        rvHours.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvHours.addItemDecoration(new LineItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        rvHours.setHasFixedSize(true);

        List<Inbox> itemsHours = DataGenerator.getInboxData(getApplicationContext());

        //set data and list adapter
        mAdapterHours = new AdapterAllowances(getApplicationContext(), itemsHours, animation_type);
        rvHours.setAdapter(mAdapterHours);

        //Multi Select
        mAdapterHours.setOnClickListener(new AdapterAllowances.OnClickListener() {

            @Override
            public void onItemClick(View view, Inbox obj, int pos) {
                showBottomSheetDialog(obj, 3);
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos)
            {
                showRemoveDialog();
            }
        });


        //RV - Subjects
        rvSubs = (RecyclerView) findViewById(R.id.rvSub);
        rvSubs.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvSubs.addItemDecoration(new LineItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        rvSubs.setHasFixedSize(true);

        List<Inbox> itemsSubs = DataGenerator.getInboxData(getApplicationContext());

        //set data and list adapter
        mAdapterSub = new AdapterAllowances(getApplicationContext(), itemsSubs, animation_type);
        rvSubs.setAdapter(mAdapterSub);

        //Multi Select
        mAdapterSub.setOnClickListener(new AdapterAllowances.OnClickListener() {

            @Override
            public void onItemClick(View view, Inbox obj, int pos) {
                showBottomSheetDialog(obj, 4);
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos)
            {
                showRemoveDialog();
            }
        });


        //Bottom Sheet
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);


        //Toggle
        // nested scrollview
        nested_scroll_view = (NestedScrollView) findViewById(R.id.nested_scroll_view);

        // personal
        bt_toggle_info = (ImageButton) findViewById(R.id.bt_toggle_info);
        lyt_expand_info = (View) findViewById(R.id.lyt_expand_info);

        bt_toggle_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_info);
            }
        });

        //Auto Toggle first tab
        toggleSection(bt_toggle_info, lyt_expand_info);

        // appointment
        bt_toggle_aapointment = (ImageButton) findViewById(R.id.bt_toggle_appointment);
        lyt_expand_appointment = (View) findViewById(R.id.lyt_expand_appointment);

        bt_toggle_aapointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_appointment);
            }
        });

        // salary
        bt_toggle_salary = (ImageButton) findViewById(R.id.bt_toggle_salary);
        lyt_expand_salary = (View) findViewById(R.id.lyt_expand_salary);

        bt_toggle_salary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_salary);
            }
        });

        // allowances
        bt_toggle_allowances = (ImageButton) findViewById(R.id.bt_toggle_Allowances);
        lyt_expand_allowances = (View) findViewById(R.id.lyt_expand_allowances);

        bt_toggle_allowances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_allowances);
            }
        });

        //Add Allowance
        bt_add_allowance = (ImageButton) findViewById(R.id.bt_add_allowance);
        bt_add_allowance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddAllowanceDialog();
            }
        });


        // Last Increment
        bt_toggle_last_increment = (ImageButton) findViewById(R.id.bt_toggle_last_incremnet);
        lyt_expand_last_increment = (View) findViewById(R.id.lyt_expand_last_increment);

        bt_toggle_last_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_last_increment);
            }
        });

        //Add Last Increment
        bt_add_last_increment = (ImageButton) findViewById(R.id.bt_add_last_increment);
        bt_add_last_increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddLastIncrementDialog();
            }
        });


        // Working Hours
        bt_toggle_hours = (ImageButton) findViewById(R.id.bt_toggle_wh);
        lyt_expand_hours = (View) findViewById(R.id.lyt_expand_wh);

        bt_toggle_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_hours);
            }
        });

        //Add Working Hour
        bt_add_hours = (ImageButton) findViewById(R.id.bt_add_wh);
        bt_add_hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWorkingHoursDialog();
            }
        });


        // Subjects
        bt_toggle_sub = (ImageButton) findViewById(R.id.bt_toggle_sub);
        lyt_expand_sub = (View) findViewById(R.id.lyt_expand_sub);

        bt_toggle_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleSection(view, lyt_expand_sub);
            }
        });

        //Add Subject
        bt_add_sub = (ImageButton) findViewById(R.id.bt_add_sub);
        bt_add_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSubjectDialog();
            }
        });


    }

    private void toggleSection(View bt, final View lyt) {
        boolean show = toggleArrow(bt);
        if (show) {
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    //Tools.nestedScrollTo(nested_scroll_view, lyt);
                }
            });
        } else {
            ViewAnimation.collapse(lyt);
        }
    }

    public boolean toggleArrow(View view) {
        if (view.getRotation() == 0) {
            view.animate().setDuration(200).rotation(180);
            return true;
        } else {
            view.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //Menu in acion bar
        getMenuInflater().inflate(R.menu.menu_staff_inside, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //Action bar icons
        if (item.getItemId() == R.id.btnCamera) {
            Toast.makeText(getApplicationContext(), "Camera clicked", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.btnReport) {
            Toast.makeText(getApplicationContext(), "Report clicked", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.btnSave) {
            Toast.makeText(getApplicationContext(), "Save clicked", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    private void showBottomSheetDialog(final Inbox people, final Integer type) {

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_allowance_menu, null);



        ((View) view.findViewById(R.id.lyt_share)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type == 1){ //Allowance
                    showAddAllowanceDialog();
                }
                else if (type == 2) //Last Increment
                {
                    showAddLastIncrementDialog();
                }
                else if (type == 3) //Working Hours
                {
                    showWorkingHoursDialog();
                }
                else if (type == 4) //Subjects
                {
                    showSubjectDialog();
                }
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.lyt_get_link)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRemoveDialog();
                mBottomSheetDialog.dismiss();
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(ActivityStaffInside.this);
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

    private void showRemoveDialog() {

        final Dialog dialog = new Dialog(ActivityStaffInside.this);
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
                Toast.makeText(getApplicationContext(), "Removed...", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showAddAllowanceDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_allowances);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;





        ((ImageButton) dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save_allowance)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText type = (EditText) dialog.findViewById(R.id.txtAllowanceType);
                EditText amount = (EditText) dialog.findViewById(R.id.txtAllowanceAmount);
                Toast.makeText(getApplicationContext(), "Save Allowance: " + type.getText() + " - Rs." + amount.getText(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showAddLastIncrementDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_last_increment);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;





        ((ImageButton) dialog.findViewById(R.id.bt_close_last_increment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save_last_increment)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText date = (EditText) dialog.findViewById(R.id.txtLastIncreDate);
                EditText amount = (EditText) dialog.findViewById(R.id.txtLastIncreAmount);
                Toast.makeText(getApplicationContext(), "Save Increment: " + date.getText() + " - Rs." + amount.getText(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showWorkingHoursDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_working_hours);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;





        ((ImageButton) dialog.findViewById(R.id.bt_close_wh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save_wh)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText date = (EditText) dialog.findViewById(R.id.txtWhDate);
                EditText in = (EditText) dialog.findViewById(R.id.txtWhTimeIn);
                EditText out = (EditText) dialog.findViewById(R.id.txtWhTimeOut);
                Toast.makeText(getApplicationContext(), "Save WH: " + date.getText() + ": " + in.getText() + " - " + out.getText(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private void showSubjectDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_teacher_subject);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ((ImageButton) dialog.findViewById(R.id.bt_close_sub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        ((Button) dialog.findViewById(R.id.bt_save_sub)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText cls = (EditText) dialog.findViewById(R.id.txtClass);
                EditText sec = (EditText) dialog.findViewById(R.id.txtSec);
                EditText sub = (EditText) dialog.findViewById(R.id.txtSubject);
                EditText share = (EditText) dialog.findViewById(R.id.txtShare);
                CheckBox per = (CheckBox) dialog.findViewById(R.id.chkPercentage);
                Toast.makeText(getApplicationContext(), "Subject: " + cls.getText() + "-" + sec.getText() + "  :  " + sub.getText() + " - " + share.getText() + " - " + per.isChecked(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}
