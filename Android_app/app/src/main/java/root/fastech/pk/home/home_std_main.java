package root.fastech.pk.home;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.activity.list.ListMultiSelection;
import com.material.components.adapter.AdapterListInbox;
import com.material.components.data.DataGenerator;
import com.material.components.model.Inbox;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;
import com.material.components.utils.ViewAnimation;
import com.material.components.widget.LineItemDecoration;

import java.util.List;

public class home_std_main extends AppCompatActivity {
    private View parent_view;


    //Data related
    private RecyclerView recyclerView;
    private AdapterListInbox mAdapter;
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;


    private Toolbar toolbar;

    //Fab More
    private View back_drop;
    private boolean rotate = false;

    private View lay_inquiry;
    private View lay_registration;
    private View lay_add;

    //List Animation
    private int animation_type = ItemAnimation.FADE_IN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_std_main);
        parent_view = findViewById(R.id.lyt_parent);



        initToolbar();
        initComponent();
        Toast.makeText(this, "Long press for multi selection", Toast.LENGTH_SHORT).show();

        iniFabMoreText();

    }


    //Fab more text -- START
    private void iniFabMoreText()
    {
        //Fab More
        back_drop = findViewById(R.id.back_drop);
        final FloatingActionButton fab_inquery = (FloatingActionButton) findViewById(R.id.fab_inquiry);
        final FloatingActionButton fab_registration = (FloatingActionButton) findViewById(R.id.fab_registration);
        final FloatingActionButton fab_admission = (FloatingActionButton) findViewById(R.id.fab_addmission);
        final FloatingActionButton fab_add = (FloatingActionButton) findViewById(R.id.fab_add);

        lay_inquiry = findViewById(R.id.lay_inquiry);
        lay_registration = findViewById(R.id.lay_registration);
        lay_add = findViewById(R.id.lay_add);
        ViewAnimation.initShowOut(lay_add);
        ViewAnimation.initShowOut(lay_registration);
        ViewAnimation.initShowOut(lay_inquiry);
        back_drop.setVisibility(View.GONE);

        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(v);
            }
        });

        back_drop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleFabMode(fab_add);
            }
        });

        fab_inquery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Inquiry clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fab_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Registration clicked", Toast.LENGTH_SHORT).show();
            }
        });

        fab_admission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Admission clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleFabMode(View v) {
        rotate = ViewAnimation.rotateFab(v, !rotate);
        if (rotate) {
            ViewAnimation.showIn(lay_inquiry);
            ViewAnimation.showIn(lay_registration);
            ViewAnimation.showIn(lay_add);
            back_drop.setVisibility(View.VISIBLE);
        } else {
            ViewAnimation.showOut(lay_inquiry);
            ViewAnimation.showOut(lay_registration);
            ViewAnimation.showOut(lay_add);
            back_drop.setVisibility(View.GONE);
        }
    }

    //Fab more text -- END


    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Students");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this, R.color.blue_600);
    }


    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new LineItemDecoration(this, LinearLayout.VERTICAL));
        recyclerView.setHasFixedSize(true);

        List<Inbox> items = DataGenerator.getInboxData(this);

        //set data and list adapter
        mAdapter = new AdapterListInbox(this, items, animation_type);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new AdapterListInbox.OnClickListener() {
            @Override
            public void onItemClick(View view, Inbox obj, int pos) {
                if (mAdapter.getSelectedItemCount() > 0) {
                    enableActionMode(pos);
                } else {
                    // read the inbox which removes bold from the row
                    Inbox inbox = mAdapter.getItem(pos);
                    Toast.makeText(getApplicationContext(), "Read: " + inbox.from, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, Inbox obj, int pos) {
                enableActionMode(pos);
            }
        });

        actionModeCallback = new ActionModeCallback();

    }

    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            Tools.setSystemBarColor(home_std_main.this, R.color.red_600); //Color on delete bar
            mode.getMenuInflater().inflate(R.menu.menu_delete, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete) {
                deleteInboxes();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
            Tools.setSystemBarColor(home_std_main.this, R.color.blue_600);
        }
    }

    private void deleteInboxes() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { //Open drawer
            //finish();
            Toast.makeText(getApplicationContext(), "Open drawer", Toast.LENGTH_SHORT).show();
        } else { //Search
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}
