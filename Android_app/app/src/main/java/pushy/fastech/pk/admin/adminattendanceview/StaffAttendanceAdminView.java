package pushy.fastech.pk.admin.adminattendanceview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.data.DataGenerator;
import com.material.components.model.People;
import com.material.components.utils.Tools;

import java.util.List;

import pushy.fastech.pk.adapters.AdapterStaffAttendanceAdminView;

public class StaffAttendanceAdminView extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterStaffAttendanceAdminView mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_attendance_admin_view);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Absent");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponent(){
        recyclerView = findViewById(R.id.recyclerViewStaffAttendanceAdminView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final List<People> items = DataGenerator.getPeopleData(this);
        items.addAll(DataGenerator.getPeopleData(this));

        mAdapter = new AdapterStaffAttendanceAdminView(this, items);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterStaffAttendanceAdminView.OnItemClickListener() {
            @Override
            public void onItemClick(View view, People obj, int Position) {
                Toast.makeText(StaffAttendanceAdminView.this, obj.name, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
