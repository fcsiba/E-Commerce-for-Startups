package root.fastech.pk.adapters;

import android.content.Context;
import android.graphics.Color;
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

import root.fastech.pk.model.Student;

public class AdapterInquiry extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
    private List<Student> items;
    private OnClickListener onClickListener = null;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;
    private int animation_type = 0;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView from, email, message, date, image_letter;
        public ImageView image;
        public RelativeLayout lyt_checked, lyt_image;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            from = (TextView) view.findViewById(R.id.from);
            email = (TextView) view.findViewById(R.id.email);
            message = (TextView) view.findViewById(R.id.message);
            date = (TextView) view.findViewById(R.id.date);
            image_letter = (TextView) view.findViewById(R.id.image_letter);
            image = (ImageView) view.findViewById(R.id.image);
            lyt_checked = (RelativeLayout) view.findViewById(R.id.lyt_checked);
            lyt_image = (RelativeLayout) view.findViewById(R.id.lyt_image);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterInquiry(Context mContext, List<Student> items, int animation_type) {
        this.ctx = mContext;
        this.items = items;
        selected_items = new SparseBooleanArray();
        this.animation_type = animation_type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder view = (ViewHolder) holder;
            final Student inbox = items.get(position);

            // displaying text view data
            ((ViewHolder) holder).from.setText(inbox.getName());
            ((ViewHolder) holder).email.setText(inbox.getfName());
            ((ViewHolder) holder).message.setText("G.R: " + inbox.getGr() + " & Class: " + inbox.getCls() + "-" + inbox.getSec());
            ((ViewHolder) holder).image_letter.setText(inbox.getName().substring(0, 1));

            ((ViewHolder) holder).lyt_parent.setActivated(selected_items.get(position, false));

            ((ViewHolder) holder).lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener == null) return;
                    onClickListener.onItemClick(v, inbox, position);
                }
            });

            ((ViewHolder) holder).lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (onClickListener == null) return false;
                    onClickListener.onItemLongClick(v, inbox, position);
                    return true;
                }
            });

            toggleCheckedIcon(((ViewHolder) holder), position);
            displayImage(((ViewHolder) holder), inbox);
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

    private void displayImage(ViewHolder holder, Student inbox) {

        holder.image.setImageResource(R.drawable.shape_circle);
        holder.image.setColorFilter(R.color.blue_600);
        holder.image_letter.setVisibility(View.VISIBLE);

        //if (inbox.image != null) {
//            Tools.displayImageRound(ctx, holder.image, inbox.image);
//            holder.image.setColorFilter(null);
//            holder.image_letter.setVisibility(View.GONE);
//        } else {
//            holder.image.setImageResource(R.drawable.shape_circle);
//            holder.image.setColorFilter(inbox.color);
//            holder.image_letter.setVisibility(View.VISIBLE);
//        }
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.lyt_image.setVisibility(View.GONE);
            holder.lyt_checked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.lyt_checked.setVisibility(View.GONE);
            holder.lyt_image.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    public Student getItem(int position) {
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
        void onItemClick(View view, Student obj, int pos);

        void onItemLongClick(View view, Student obj, int pos);
    }
}