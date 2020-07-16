package pushy.fastech.pk.parent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.data.DataGenerator;
import com.material.components.model.People;
import com.material.components.utils.Tools;

import java.util.List;

import pushy.fastech.pk.adapters.AdapterNotificationSideBar;

public class NotificationSideBar extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterNotificationSideBar mAdapter;
    private ImageButton btBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_side_bar);

        Tools.setSystemBarColor(this, R.color.blue_700);

        initComponent();
    }

    private void initComponent() {
        btBack = findViewById(R.id.bt_back);

        recyclerView = findViewById(R.id.recyclerViewNotificationParent);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final List<People> items = DataGenerator.getPeopleData(this);
        items.addAll(DataGenerator.getPeopleData(this));

        mAdapter = new AdapterNotificationSideBar(this, items);
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new AdapterNotificationSideBar.OnItemClickListener() {
            @Override
            public void onItemClick(View view, People obj, int Position) {
                Toast.makeText(NotificationSideBar.this, obj.name, Toast.LENGTH_SHORT).show();
            }
        });

        btBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


}
