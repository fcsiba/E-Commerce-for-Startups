package com.material.components.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.ShopProduct;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.saaj.Products;

public class AdapterGridShopProductCard extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Products> items = new ArrayList<>();
    private List<Products> items_filtered = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }



    public AdapterGridShopProductCard(Context context, List<Products> items) {
        this.items = items;
        this.items_filtered = new ArrayList<>(items);
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView price;
        public ImageButton more;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            title = (TextView) v.findViewById(R.id.title);
            price = (TextView) v.findViewById(R.id.price);
            more = (ImageButton) v.findViewById(R.id.more);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_product_card, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            final Products p = items.get(position);
            view.title.setText(p.getItemName());
            view.price.setText(p.getItemCode());
            Tools.displayImageOriginal(ctx, view.image, p.getCoverImage());
            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });

        }
    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, Products obj, int pos);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            List<Products> filteredList = new ArrayList<>();
            if(charSequence == null || charSequence.length() == 0){
                filteredList.addAll(items_filtered);
            } else{
                String filterPattern = charSequence.toString().toLowerCase().trim();
                for (Products item : items){
                    if(item.getDescription().toLowerCase().contains(filterPattern) || item.getItemCode().toLowerCase().contains(filterPattern))
                    {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            items.clear();
            items.addAll((List) filterResults.values);
            notifyDataSetChanged();
        }
    };

}