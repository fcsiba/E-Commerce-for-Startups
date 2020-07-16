package com.material.components.activity.profile;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.chat.ChatWhatsapp;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.parent.NotificationSideBar;
import pushy.fastech.pk.parent.ViewStudentAttendance;

public class ProfileCardHeader extends AppCompatActivity {

//    private ShimmerTextView shimmer;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    FloatingActionButton btnAttendance, btnHw, btnChat, btnComplaint, btnResult, btnTimetable, btnFeeDetails, btnPayment;
    String username;
    Integer type = 3;

    // Progress when logging out
    private ProgressDialog dialogLogout;

    // Change account activity
    private RelativeLayout changeAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_card_header);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        initToolbar();
        initComponent();

        // Progress logout
        dialogLogout = new ProgressDialog(ProfileCardHeader.this);
        dialogLogout.setMessage("Logging out...");

        changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileCardHeader.this, SelectAccount.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, R.color.blue_700);
    }

    private void initComponent() {
        btnAttendance = findViewById(R.id.btnAttd);
        btnHw = findViewById(R.id.btnHw);

        btnChat = findViewById(R.id.btnChat);
        btnComplaint = findViewById(R.id.btnComplaint);
        btnResult = findViewById(R.id.btnTestReport);

        btnTimetable = findViewById(R.id.btnTimeTable);
        btnFeeDetails = findViewById(R.id.btnFeeDetails);
        btnPayment = findViewById(R.id.btnFeePayment);

        //Load Variables
        Bundle extras = getIntent().getExtras();
        try {
            username = pref.getString("username", "");
        } catch (Exception x) {

        }

        //Chat
        btnChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ChatWhatsapp.class);
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putInt("typeN", type);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        btnHw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileCardHeader.this, pushy.fastech.pk.homework.ShowHomework.class);
                startActivity(intent);
            }
        });

        btnAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileCardHeader.this, ViewStudentAttendance.class);
                startActivity(intent);
            }
        });

        btnComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnTimetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnFeeDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });

        btnPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Coming soon", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.finishAffinity();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
        }else if (item.getItemId() == R.id.action_search) {
            startActivity(new Intent(this, NotificationSideBar.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }else if (item.getItemId() == R.id.action_logout) {
            logoutDialog();
        } else {
            Intent intent = new Intent(getApplicationContext(), ChatWhatsapp.class);
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            bundle.putInt("typeN", type);
            intent.putExtras(bundle);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void logoutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_logout_confrim);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        (dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogLogout.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLogout.dismiss();
                        Intent intent = new Intent(ProfileCardHeader.this, LoginSimpleGreen.class);
                        startActivity(intent);
                        editor.putBoolean("login", false);
                        editor.commit();
                        finish();
                    }
                }, 3000);
            }
        });

        (dialog.findViewById(R.id.bt_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}

//        try {
//            shimmer = findViewById(R.id.shimmer_tv);
//            Shimmer myShimmer = new Shimmer();
//            myShimmer.setRepeatCount(Animation.INFINITE)
//                    .setDuration(2000)
//                    .setStartDelay(4000)
//                    .setDirection(Shimmer.ANIMATION_DIRECTION_LTR);
//            myShimmer.start(shimmer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

