package com.material.components.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.Inbox;
import com.material.components.model.People;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterListBasic extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<People> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);

        //on item long clicked
        void onItemLongClick(View view, People obj, int pos);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterListBasic(Context context, List<People> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name, fatherName;
        public View lyt_parent;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.image);
            name = (TextView) v.findViewById(R.id.name);
            fatherName = (TextView) v.findViewById(R.id.name_of_father);
            lyt_parent = (View) v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_chat, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            People p = items.get(position);
            view.name.setText(p.name);
            view.fatherName.setText(p.email);
            Tools.displayImageRound(ctx, view.image, p.image);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                int counter = 0;
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null && counter == 0) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);

                    }
                }
            });

            view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(mOnItemClickListener != null){
                        mOnItemClickListener.onItemLongClick(view,items.get(position), position);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}