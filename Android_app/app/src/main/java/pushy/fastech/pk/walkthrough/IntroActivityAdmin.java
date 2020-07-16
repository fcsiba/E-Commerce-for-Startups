package pushy.fastech.pk.walkthrough;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.admin.adminportal.AdminDashboard;

public class IntroActivityAdmin extends AppCompatActivity {

    private ViewPager screenPager;
    IntroViewPagerAdapter introViewPagerAdapter;
    TabLayout tabIndicator;
    TextView btnNext, tvSkip;
    int position = 0;
    Button btnGetStarted;
    Animation btnAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // When this activity is about to be launch we need to check if its opened before or not
        if (restorePrefData()) {
            Intent mainActivity = new Intent(getApplicationContext(), AdminDashboard.class);
            startActivity(mainActivity);
            finish();
        }

        setContentView(R.layout.activity_intro_admin);

        // Intro views
        btnNext = findViewById(R.id.btn_next);
        btnGetStarted = findViewById(R.id.btn_get_started);
        tabIndicator = findViewById(R.id.tab_indicator);
        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_animation);
        tvSkip = findViewById(R.id.tv_skip);

        // Fill list screen
        final List<ScreenItem> mList = new ArrayList<>();
        mList.add(new ScreenItem("Welcome!", "Before we begin, let's see what's in store for us with this short tour guide.", R.drawable.image_15, R.drawable.img_wizard_1,R.color.card_color_green));
        mList.add(new ScreenItem("Attendance", "Manage your student's attendance easily! Quickly track the attendance of your class, group or any gathering of people.",  R.drawable.image_16, R.drawable.img_wizard_2 ,R.color.card_color_red));
        mList.add(new ScreenItem("Marks", "Easily upload marks for any assignment, quizzes or exams for your class.", R.drawable.image_17, R.drawable.img_wizard_3 ,R.color.card_color_blue));
        mList.add(new ScreenItem("Homework", "Upload homework & assignments at any time anywhere.", R.drawable.image_18, R.drawable.img_wizard_4,R.color.card_color_purple));
        mList.add(new ScreenItem("Notifications", "Send instant notifications to your student(s) or group of people to keep them updated with the latest news.", R.drawable.image_19, R.drawable.img_wizard_1 ,R.color.card_color_orange));

        // Setup viewpager
        screenPager = findViewById(R.id.screen_viewpager);
        introViewPagerAdapter = new IntroViewPagerAdapter(this, mList);
        screenPager.setAdapter(introViewPagerAdapter);

        // Setup tabLayout with viewpager
        tabIndicator.setupWithViewPager(screenPager);

        // Next button click Listener
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                position = screenPager.getCurrentItem();
                if (position < mList.size()) {
                    position++;
                    screenPager.setCurrentItem(position);
                }

                if (position == mList.size() - 1) {
                    // When we reach to the last screen
                    loadLastScreen();
                }
            }
        });

        // TabLayout add change listener
        tabIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == mList.size() - 1) {
                    loadLastScreen();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Get Started button click listener
        btnGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), AdminDashboard.class);
                startActivity(mainActivity);
                savePrefsData();
                finish();
            }
        });

        // skip button click listener
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                screenPager.setCurrentItem(mList.size());
            }
        });
    }

    private boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        Boolean isIntroActivityOpnendBefore = pref.getBoolean("isIntroOpened", false);
        return isIntroActivityOpnendBefore;
    }

    private void savePrefsData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("isIntroOpened", true);
        editor.commit();
    }

    // Show the GET STARTED Button and hide the indicator and the next button
    private void loadLastScreen() {
        btnNext.setVisibility(View.INVISIBLE);
        btnGetStarted.setVisibility(View.VISIBLE);
        tvSkip.setVisibility(View.INVISIBLE);
        tabIndicator.setVisibility(View.INVISIBLE);
        // Setup animation
        btnGetStarted.setAnimation(btnAnim);
    }

    private void loadPreviousScreen(){
        btnNext.setVisibility(View.VISIBLE);
        btnGetStarted.setVisibility(View.INVISIBLE);
        tvSkip.setVisibility(View.VISIBLE);
        tabIndicator.setVisibility(View.VISIBLE);

        // Setup animation
        btnNext.setAnimation(btnAnim);
        tvSkip.setAnimation(btnAnim);
        tabIndicator.setAnimation(btnAnim);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Toast.makeText(this, "Press 'Next' to continue...", Toast.LENGTH_SHORT).show();
    }
}