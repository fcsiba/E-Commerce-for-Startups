package com.material.components.fragment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.material.components.R;
import com.material.components.adapter.AdapterGridShopProductCard;
import com.material.components.data.DataGenerator;
import com.material.components.model.ShopProduct;
import com.material.components.utils.Tools;
import com.material.components.widget.SpacingItemDecoration;

import java.util.Collections;
import java.util.List;

import pushy.fastech.pk.saaj.Products;

public class FragmentProductGrid extends Fragment {

    public FragmentProductGrid() {
    }

    public static FragmentProductGrid newInstance() {
        FragmentProductGrid fragment = new FragmentProductGrid();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragment_product_grid, container, false);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(getActivity(), 8), true));
        recyclerView.setHasFixedSize(true);

        List<Products> items = DataGenerator.GetProducts(getActivity(), 1,0);
        Collections.shuffle(items);

        //set data and list adapter
        AdapterGridShopProductCard mAdapter = new AdapterGridShopProductCard(getActivity(), items);
        recyclerView.setAdapter(mAdapter);

        // on item list clicked
        mAdapter.setOnItemClickListener(new AdapterGridShopProductCard.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Products obj, int position) {
                Snackbar.make(root, "Item " + obj.getItemName() + " clicked", Snackbar.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}