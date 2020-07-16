package pushy.fastech.pk.admin.adminportal;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

public class NotificationSettings extends AppCompatActivity {

    private LinearLayout logout;
    private SwitchCompat smsAbsent, smsPresent, smsLeave;
    private SwitchCompat notifyFather, notifyMother, notifyTutor;

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    // Progress when logging out
    private ProgressDialog dialogLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings);

        initToolbar();
        initComponents();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    public void initComponents(){
        smsAbsent = findViewById(R.id.sms_absent_switch_btn);
        smsPresent = findViewById(R.id.sms_present_switch_btn);
        smsLeave = findViewById(R.id.sms_leave_switch_btn);

        notifyFather = findViewById(R.id.notification_father_switch_btn);
        notifyMother = findViewById(R.id.notification_mother_switch_btn);
        notifyTutor = findViewById(R.id.notification_tutor_switch_btn);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk",0);
        editor = pref.edit();

        // Progress logout
        dialogLogout = new ProgressDialog(NotificationSettings.this);
        dialogLogout.setMessage("Logging out...");

        logout = findViewById(R.id.logout_admin);
        logout.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                logoutDialog();
            }
        });
    }

    // Logout dialog
    private void logoutDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_logout_confrim);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((AppCompatButton) dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                dialogLogout.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialogLogout.dismiss();
                        Intent intent = new Intent(NotificationSettings.this, LoginSimpleGreen.class);
                        startActivity(intent);
                        editor.putBoolean("login", false);
                        editor.commit();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            finishAffinity();
                        }
                    }
                }, 3000);
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
