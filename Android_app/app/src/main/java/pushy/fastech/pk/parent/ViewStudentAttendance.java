package pushy.fastech.pk.parent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.material.components.R;
import com.material.components.utils.Tools;

import java.util.ArrayList;

public class ViewStudentAttendance extends AppCompatActivity {

    private LineChart chart;
    private TextView mAttendance, wAttendance, dAttendance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_student_attendance);

        initToolbar();

        mAttendance = findViewById(R.id.monthly_attendance);
        wAttendance = findViewById(R.id.weekly_attendance);
        dAttendance = findViewById(R.id.daily_attendance);

        chart = findViewById(R.id.chart_attendance);
        LineDataSet lineDataSet1 = new LineDataSet(dataValues1(), "Data Set 1");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet1);

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Attendance");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Tools.setSystemBarColor(this);
    }

    private ArrayList<Entry> dataValues1(){
        ArrayList<Entry> dataValues = new ArrayList<Entry>();
        dataValues.add(new Entry(0, 20));
        dataValues.add(new Entry(1, 22));
        dataValues.add(new Entry(2, 43));
        dataValues.add(new Entry(3, 19));
        dataValues.add(new Entry(4, 32));

        return dataValues;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {

        }
        return super.onOptionsItemSelected(item);
    }
}
