package pushy.fastech.pk.staff.staffportal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.chat.ChatWhatsapp;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;
import com.romainpiel.shimmer.ShimmerTextView;

import pushy.fastech.pk.accounts.SelectAccount;
import pushy.fastech.pk.admin.adminportal.NotificationView;
import pushy.fastech.pk.receivers.NetworkChangeReceiver;

public class StaffDashboard extends AppCompatActivity {

    private static LinearLayout mainScreen;
    private static RelativeLayout internetGone;
    private BroadcastReceiver mNetworkReceiver;
    private static TextView tv_check_connection;

    private ShimmerTextView shimmer;
    private CardView cv_attendance, cv_homework, cv_marks, cv_notification, cv_something;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    //Animation
    private AnimationSet set;
    private Animation animation;
    private static LayoutAnimationController controller;

    // Progress when logging out
    private ProgressDialog dialogLogout;

    // Change account activity
    private RelativeLayout changeAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_dashboard);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0);
        editor = pref.edit();

        initToolbar();

        // CardView options
        cv_attendance = findViewById(R.id.attendance_cv_staff);
        cv_homework = findViewById(R.id.homework_cv_staff);
        cv_marks = findViewById(R.id.marks_cv_staff);
        cv_notification = findViewById(R.id.notification_cv_staff);
        cv_something = findViewById(R.id.cv_something_staff);

        // Internet Checking
        internetGone = findViewById(R.id.cloud_off_rl);
        tv_check_connection = (TextView) findViewById(R.id.tv_check_connection);
        mNetworkReceiver = new NetworkChangeReceiver();
        registerNetworkBroadcast();

        // Main screen]
        mainScreen = findViewById(R.id.main_layout);

        // OnClick for cards
        cv_attendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDashboard.this, StudentAttendanceList.class);
                startActivity(intent);
            }
        });

        cv_homework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDashboard.this, pushy.fastech.pk.homework.ShowHomework.class);
                startActivity(intent);
            }
        });

        cv_marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDashboard.this, StudentMarksList.class);
                startActivity(intent);
            }
        });

        cv_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putBoolean("editMode", false);
                editor.commit();
                Intent intent = new Intent(StaffDashboard.this, NotificationView.class);
                startActivity(intent);
            }
        });

        cv_something.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(StaffDashboard.this, "Under development", Toast.LENGTH_SHORT).show();
            }
        });

        // Animation Fade
        set = new AnimationSet(true);

        animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(500);
        set.addAnimation(animation);

        animation = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f
        );
        animation.setDuration(1000);
        set.addAnimation(animation);

        controller = new LayoutAnimationController(set, 0.5f);
//        mainScreen.setLayoutAnimation(controller);

        // Progress logout
        dialogLogout = new ProgressDialog(StaffDashboard.this);
        dialogLogout.setMessage("Logging out...");

        changeAccount = findViewById(R.id.open_dialog_change_account);
        changeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StaffDashboard.this, SelectAccount.class);
                startActivity(intent);
            }
        });
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.staff_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        Tools.setSystemBarColor(this, R.color.blue_700);
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
        if (item.getItemId() == R.id.action_logout) {
            logoutDialog();
        } else if (item.getItemId() == R.id.action_faculty) {

        } else {
            Intent intent = new Intent(getApplicationContext(), ChatWhatsapp.class);
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
                        Intent intent = new Intent(StaffDashboard.this, LoginSimpleGreen.class);
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

    public static void dialogStaff(boolean value) {
        if (value) {
            try {
                tv_check_connection.setVisibility(View.VISIBLE);
                tv_check_connection.setText("Internet Connected");
                tv_check_connection.setBackgroundColor(Color.parseColor("#00b300"));
                tv_check_connection.setTextColor(Color.WHITE);
                internetGone.setVisibility(View.GONE);
                mainScreen.setVisibility(View.VISIBLE);
//                mainScreen.setLayoutAnimation(controller);

            } catch (Exception e) {
                e.printStackTrace();
            }

            Handler handler = new Handler();
            Runnable delay = new Runnable() {
                @Override
                public void run() {
                    try {
                        tv_check_connection.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(delay, 3000);
        } else {
            try {
                tv_check_connection.setVisibility(View.VISIBLE);
                tv_check_connection.setText("No Internet Connection");
                tv_check_connection.setBackgroundColor(Color.parseColor("#cc0000"));
                tv_check_connection.setTextColor(Color.WHITE);
                internetGone.setVisibility(View.VISIBLE);
                mainScreen.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Handler handler = new Handler();
            Runnable delay = new Runnable() {
                @Override
                public void run() {
                    try {
                        tv_check_connection.setVisibility(View.GONE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            handler.postDelayed(delay, 3000);
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
}