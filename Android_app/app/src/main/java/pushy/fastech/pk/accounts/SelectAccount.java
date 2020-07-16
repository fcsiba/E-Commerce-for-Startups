package pushy.fastech.pk.accounts;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.Helper.GetAll;
import pushy.fastech.pk.adapters.AdapterChangeAccount;

public class SelectAccount extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AdapterChangeAccount mAdapter;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private String dbName = "";
    private List<GetAll> cItems;
    private RequestQueue queue;
    private LinearLayout loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_account);

        loader = findViewById(R.id.on_screen_loader);

        queue = Volley.newRequestQueue(getApplicationContext());
        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        dbName = pref.getString("dbName", "");
        editor = pref.edit();

        Tools.setSystemBarColor(this, R.color.blue_700);
        initComponent();
    }

    private void initComponent() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(SelectAccount.this);
        mLayoutManager.setOrientation(mLayoutManager.HORIZONTAL);
        recyclerView = findViewById(R.id.recyclerViewSelectAccount);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        cItems = new ArrayList<>();
        LoadCampus();
    }

    // Load Classes
    public void LoadCampus() {
        loader.setVisibility(View.VISIBLE);
        try {
            final String url = LoginSimpleGreen.domain + "Fill/TFaculty?cID=1&_db=fusion-argon&localID=20&clsID=74";
            Log.d("data.fastech.pk", "Loading campus: " + LoginSimpleGreen.domain + "Fill/TFaculty?cID=1&_db=fusion-argon&localID=20&clsID=74");
            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Do something with response
                            try {
                                JSONArray array = response.getJSONArray("data");
                                // Loop through the array elements
                                for (int i = 0; i < array.length(); i++) {
                                    GetAll c = new GetAll();
                                    try {
                                        JSONObject jsonObject = array.getJSONObject(i);
                                        c.setSMSTitle(jsonObject.getString("name"));
                                        cItems.add(c);
                                        cItems.add(c);
                                        cItems.add(c);
                                        cItems.add(c);
                                        Log.d("app.fastech.pk", "onResponse: " + cItems) ;
                                    } catch (JSONException e) {
                                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                                    }
                                }

                                loader.setVisibility(View.GONE);
                                mAdapter = new AdapterChangeAccount(getApplicationContext(), cItems);
                                recyclerView.setAdapter(mAdapter);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(SelectAccount.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
            );
            queue.add(request);
        } catch (Exception x) {
        }
    }
}