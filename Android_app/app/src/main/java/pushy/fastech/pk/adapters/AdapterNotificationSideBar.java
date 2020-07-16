package pushy.fastech.pk.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.People;

import java.util.List;

public class AdapterNotificationSideBar extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<People> items;
    private Context context;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, People obj, int Position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterNotificationSideBar(Context context, List<People> items){
        this.items = items;
        this.context = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public TextView title, description, time;
        public View lyt_parent;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title_notification_side_bar);
            description = itemView.findViewById(R.id.description_notification_side_bar);
            time = itemView.findViewById(R.id.time_notification_side_bar);
            lyt_parent = itemView.findViewById(R.id.lyt_parent_notification_side_bar);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_side_bar, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder,final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            People p = items.get(position);
            view.title.setText(p.name);
            view.description.setText(R.string.long_lorem_ipsum);
            view.time.setText(p.email);

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

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
