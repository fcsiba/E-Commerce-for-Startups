package root.fastech.pk.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.Inbox;
import com.material.components.utils.ItemAnimation;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterAllowances extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Inbox> items;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private int animation_type = 0;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView lblTitle, lblDesc;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            lblTitle = (TextView) view.findViewById(R.id.lblTitle);
            lblDesc = (TextView) view.findViewById(R.id.lblDescription);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterAllowances(Context mContext, List<Inbox> items, int animation_type) {
        this.ctx = mContext;
        this.items = items;
        selected_items = new SparseBooleanArray();
        this.animation_type = animation_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_allowances, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final Inbox inbox = items.get(position);

            // displaying text view data
            ((ViewHolder) holder).lblTitle.setText(inbox.from);
            ((ViewHolder) holder).lblDesc.setText(inbox.email);


            ((AdapterAllowances.ViewHolder) holder).lyt_parent.setActivated(selected_items.get(position, false));

            ((AdapterAllowances.ViewHolder) holder).lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener == null) return;
                    onClickListener.onItemClick(v, inbox, position);
                }
            });

            ((AdapterAllowances.ViewHolder) holder).lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) return false;
                    onClickListener.onItemLongClick(v, inbox, position);
                    return true;
                }
            });

            setAnimation(view.itemView, position);

        }



    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                on_attach = false;
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        super.onAttachedToRecyclerView(recyclerView);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }

    public Inbox getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, Inbox obj, int pos);

        void onItemLongClick(View view, Inbox obj, int pos);
    }

}