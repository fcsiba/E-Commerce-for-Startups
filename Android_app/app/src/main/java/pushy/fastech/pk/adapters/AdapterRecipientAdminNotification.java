package pushy.fastech.pk.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.material.components.R;
import com.material.components.model.People;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.Helper.Reciept;
import pushy.fastech.pk.Helper.Thread_Messages;

public class AdapterRecipientAdminNotification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Reciept> items;
    private OnItemClickListener mOnItemClickListener;
    private Context ctx;
    RecyclerView rvContacts;

    public interface OnItemClickListener {
        void onItemClick(View view, Reciept obj, int pos);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterRecipientAdminNotification(Context context, List<Reciept> items, RecyclerView _rv) {
        this.items = items;
        ctx = context;
        rvContacts = _rv;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView cancelSelection;
        public TextView selectedText;
        public View lyt_parent_notifications;

        public OriginalViewHolder(View v) {
            super(v);
            selectedText = v.findViewById(R.id.text_content_admin_notification);
            cancelSelection = v.findViewById(R.id.cancel_btn_admin_notification);
            lyt_parent_notifications = v.findViewById(R.id.lyt_parent_admin_notification);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_notification, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Glide.with(ctx);

            Reciept p = items.get(position);
            view.selectedText.setText(p.getTitle());

            view.lyt_parent_notifications.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                        visibility(holder);
                    }
                }
            });

            view.cancelSelection.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteRecipient(holder, position);
                }
            });
        }
    }

    public void visibility(RecyclerView.ViewHolder holder) {
        OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

        if (viewHolder.cancelSelection.getVisibility() == View.GONE) {
            viewHolder.cancelSelection.setVisibility(View.VISIBLE);
        } else if(viewHolder.cancelSelection.getVisibility() == View.VISIBLE){
            viewHolder.cancelSelection.setVisibility(View.GONE);
        }
    }

    private void deleteRecipient(RecyclerView.ViewHolder holder, final int position) {
        OriginalViewHolder viewHolder = (OriginalViewHolder) holder;

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(300);
        rvContacts.setItemAnimator(itemAnimator);

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());
        viewHolder.cancelSelection.setVisibility(View.GONE);

        rvContacts.requestLayout();
    }

    @Override
    public int getItemCount() {
        try{
            return items.size();
        }
        catch (Exception x)
        {
            return  0;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List<Reciept> GetReciept()
    {
       return  items;
    }

    public void insertItem(Reciept item) {
        items.add(item);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(300);
        rvContacts.setItemAnimator(itemAnimator);
        notifyItemInserted(getItemCount());
    }
}