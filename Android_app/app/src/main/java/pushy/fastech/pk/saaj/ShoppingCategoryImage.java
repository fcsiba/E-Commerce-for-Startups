package pushy.fastech.pk.saaj;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.adapter.AdapterListShopCategoryImg;
import com.material.components.data.DataGenerator;
import com.material.components.model.ShopCategory;
import com.material.components.utils.Tools;

import java.util.List;

public class ShoppingCategoryImage extends AppCompatActivity {

    private View parent_view;

    private RecyclerView recyclerView;
    private AdapterListShopCategoryImg mAdapter;
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_category_image);
        parent_view = findViewById(R.id.parent_view);

        initToolbar();
        initComponent();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Categories");
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        Tools.setSystemBarColor(this);
    }

    private void initComponent() {
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        pref = getApplicationContext().getSharedPreferences("pushy.fastech.pk", 0); // 0 - for private mode
        editor = pref.edit();

        List<ShopCategory> items = DataGenerator.getShoppingCategory(this);

        //set data and list adapter
        mAdapter = new AdapterListShopCategoryImg(this, items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterListShopCategoryImg.OnItemClickListener() {
            @Override
            public void onItemClick(View view, ShopCategory obj, int position) {
                if (position == 0) //All
                {
                    editor.putInt("mainCategory", 0);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), ShoppingProductGrid.class);
                    startActivity(intent);
                }
                else if (position == 1) //Women
                {
                    editor.putInt("mainCategory", 2);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), ShoppingProductGrid.class);
                    startActivity(intent);
                }
                else if (position == 2) //Men
                {
                    editor.putInt("mainCategory", 1);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), ShoppingProductGrid.class);
                    startActivity(intent);
                }
                if (position == 3) //Kids
                {
                    editor.putInt("mainCategory", 3);
                    editor.commit();
                    Intent intent = new Intent(getApplicationContext(), ShoppingProductGrid.class);
                    startActivity(intent);
                }

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_cart_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        else if (item.getTitle().equals("Cart")) {
            Intent intent = new Intent(getApplicationContext(), ShoppingCartSimple.class);
            startActivity(intent);
        }
        else {
            Toast.makeText(getApplicationContext(), item.getItemId(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
