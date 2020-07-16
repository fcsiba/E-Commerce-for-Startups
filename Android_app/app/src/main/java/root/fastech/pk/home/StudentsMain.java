package root.fastech.pk.home;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.adapter.AdapterListInbox;
import com.material.components.model.Event;
import com.material.components.model.Inbox;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;

import java.util.ArrayList;
import java.util.List;

import root.fastech.pk.fragments.FragmentInquiry;
import root.fastech.pk.fragments.FragmentRegistration;
import root.fastech.pk.fragments.FragmentStudentsEnrolled;
import root.fastech.pk.fragments.FragmentStudentsLeft;

public class StudentsMain extends AppCompatActivity {

    private ViewPager view_pager;
    private SectionsPagerAdapter viewPagerAdapter;
    private TabLayout tab_layout;

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

    private void showLoading(long LOADING_DURATION) {

        //view_pager.setVisibility(View.GONE);
//        final LinearLayout lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
//        lyt_progress.setVisibility(View.VISIBLE);
//        lyt_progress.setAlpha(1.0f);
//
//
//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                ViewAnimation.fadeOut(lyt_progress);
//            }
//        }, LOADING_DURATION);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                lyt_progress.setVisibility(View.GONE);
//                view_pager.setVisibility(View.VISIBLE);
//            }
//        }, LOADING_DURATION + 400);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs_icon_stack);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inquiry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.blue_600);
    }

    private void initComponent() {
        view_pager = (ViewPager) findViewById(R.id.view_pager);
        tab_layout = (TabLayout) findViewById(R.id.tab_layout);
        setupViewPager(view_pager);

        tab_layout.setupWithViewPager(view_pager);

        tab_layout.getTabAt(0).setIcon(R.drawable.ic_music);
        tab_layout.getTabAt(1).setIcon(R.drawable.ic_movie);
        tab_layout.getTabAt(2).setIcon(R.drawable.ic_book);
        tab_layout.getTabAt(3).setIcon(R.drawable.ic_games);

        //Searchbar
        appbarSearch = (AppBarLayout) findViewById(R.id.appbarSearch);
        bt_clear = (ImageButton) findViewById(R.id.bt_clear);
        bt_searchby = (ImageButton) findViewById(R.id.bt_search_by);
        appbarSearch.setVisibility(View.INVISIBLE);
        et_search = (EditText) findViewById(R.id.et_search);
        final LinearLayout lyt_progress = (LinearLayout) findViewById(R.id.lyt_progress);
        //lyt_progress.setVisibility(View.GONE);

        bt_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_search.setText("");
                appbarSearch.setVisibility(View.INVISIBLE);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    hideKeyboard();
                    searchAction();
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

        // set icon color pre-selected
        tab_layout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.blue_500), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
        tab_layout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);

        tab_layout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                getSupportActionBar().setTitle(viewPagerAdapter.getTitle(tab.getPosition()));
                tab.getIcon().setColorFilter(getResources().getColor(R.color.blue_500), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(getResources().getColor(R.color.grey_20), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        //Bottom Sheet
        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

    }

    private void searchAction() {

        final String query = et_search.getText().toString().trim();
        if (!query.equals("")) {
            showLoading(1500);

        } else {
            Toast.makeText(this, "Please fill search input", Toast.LENGTH_SHORT).show();
        }
    }


    private void setupViewPager(ViewPager viewPager) { //Initialize Tab Menu
        viewPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(FragmentInquiry.newInstance(), "Inquiry");    //Inquiry
        viewPagerAdapter.addFragment(FragmentRegistration.newInstance(), "Waiting List");   // Registration
        viewPagerAdapter.addFragment(FragmentRegistration.newInstance(), "Enrolled");    // Enrolled
        viewPagerAdapter.addFragment(FragmentRegistration.newInstance(), "Left");    //
        viewPager.setAdapter(viewPagerAdapter);
    }


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
        else if (item.getItemId() == R.id.btnSearch) { //Search
            appbarSearch.setVisibility(View.VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }


        return super.onOptionsItemSelected(item);
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public String getTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }

    private void showOrderByDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_student_orderby, null);

        ((View) view.findViewById(R.id.or_gr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by GR", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();

            }
        });

        ((View) view.findViewById(R.id.or_family)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by F.Code", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.or_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.or_fName)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by father's name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.or_class)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by Class", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.or_house)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by house", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.or_area)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Order by area", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(StudentsMain.this);
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

    private void showSearchByDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_student_searchby, null);

        ((View) view.findViewById(R.id.sb_gr)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by GR", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();

            }
        });

        ((View) view.findViewById(R.id.sb_family)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by F.Code", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.sb_name)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.sb_father)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by father's name", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.sb_class)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by Class", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });

        ((View) view.findViewById(R.id.sb_conatct)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Search by Conatact", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });




        mBottomSheetDialog = new BottomSheetDialog(StudentsMain.this);
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

    private void showOrderSequenceDialog() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        final View view = getLayoutInflater().inflate(R.layout.sheet_order_sequence, null);

        ((View) view.findViewById(R.id.os_asc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Asc order", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();

            }
        });

        ((View) view.findViewById(R.id.os_dsc)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Dsc order", Toast.LENGTH_SHORT).show();
                mBottomSheetDialog.dismiss();
            }
        });


        mBottomSheetDialog = new BottomSheetDialog(StudentsMain.this);
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

    private void showReportsDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_student_reports);
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
        ((Button) dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Show report", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

}