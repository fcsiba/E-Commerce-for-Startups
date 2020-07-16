package pushy.fastech.pk.adapters;

import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.Image;
import com.material.components.model.Message;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.Helper.Thread_Messages;

public class AdapterChatAdminNotification extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CHAT_ME = 100;
    private final int CHAT_YOU = 200;

    private List<Thread_Messages> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Thread_Messages obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterChatAdminNotification(Context context, List<Thread_Messages> _msgs) {
        ctx = context;
        items = _msgs;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text_content;
        public TextView text_time;
        public View lyt_parent;
        public ImageView imgTick, imgNotSend;

        public ItemViewHolder(View v) {
            super(v);
            text_content = v.findViewById(R.id.text_content);
            text_time = v.findViewById(R.id.text_time);
            lyt_parent = v.findViewById(R.id.lyt_parent);
            imgTick = v.findViewById(R.id.imgTick);
            imgNotSend = v.findViewById(R.id.imgNotSend);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == CHAT_ME) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_telegram_me, parent, false);
            vh = new ItemViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_whatsapp_telegram_you, parent, false);
            vh = new ItemViewHolder(v);
        }
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Thread_Messages m = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.text_content.setText(m.getBody());
            vItem.text_time.setText(m.getRequestTime());
            if (m.getApproved())
            {
                vItem.imgNotSend.setVisibility(View.GONE);
                vItem.imgTick.setVisibility(View.VISIBLE);
            }
            else{
                vItem.imgNotSend.setVisibility(View.VISIBLE);
                vItem.imgTick.setVisibility(View.GONE);
            }

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, m, position);
                    }
                }
            });
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return CHAT_ME;
    }

    public void insertItem(Thread_Messages item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void setItems(List<Thread_Messages> items) {
        this.items = items;
    }
}