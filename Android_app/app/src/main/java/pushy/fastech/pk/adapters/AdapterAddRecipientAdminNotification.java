package pushy.fastech.pk.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;

import java.util.ArrayList;
import java.util.List;

import pushy.fastech.pk.Helper.Reciept;

public class AdapterAddRecipientAdminNotification extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private List<Reciept> nameRecipient;
    private List<Reciept> nameRecipientFull;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Reciept obj, int position);
    }


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterAddRecipientAdminNotification(Context context, List<Reciept> nameRecipient) {
        this.nameRecipient = nameRecipient;
        nameRecipientFull = new ArrayList<>(nameRecipient);
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView circleColor;
        public TextView recipientFirstLetter;
        public TextView recipientName, recipientID;
        public View lyt_parent_recipient;

        public OriginalViewHolder(View v) {
            super(v);
            circleColor = v.findViewById(R.id.recipient_shape_circular);
            recipientFirstLetter = v.findViewById(R.id.recipient_person_first_letter);
            recipientName = v.findViewById(R.id.recipient_name);
            recipientID = v.findViewById(R.id.recipient_id);
            lyt_parent_recipient = v.findViewById(R.id.lyt_parent_recipient);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipients_admin_notification, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Reciept p = nameRecipient.get(position);
            view.recipientName.setText(p.getTitle());
            view.recipientID.setText(p.getDesc());

            symbol(holder, position);

            view.lyt_parent_recipient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, nameRecipient.get(position), position);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return nameRecipient.size();
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    private Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Reciept> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(nameRecipientFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Reciept item : nameRecipientFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            nameRecipient.clear();
            nameRecipient.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void symbol(final RecyclerView.ViewHolder holder, final int position) {
        AdapterAddRecipientAdminNotification.OriginalViewHolder view = (AdapterAddRecipientAdminNotification.OriginalViewHolder) holder;
        final Reciept p = nameRecipient.get(position);

        if (p.getTitle().startsWith("A")) {
            view.recipientFirstLetter.setText("A");
        } else if (p.getTitle().startsWith("B")) {
            view.recipientFirstLetter.setText("B");
        } else if (p.getTitle().startsWith("C")) {
            view.recipientFirstLetter.setText("C");
        } else if (p.getTitle().startsWith("D")) {
            view.recipientFirstLetter.setText("D");
        } else if (p.getTitle().startsWith("E")) {
            view.recipientFirstLetter.setText("E");
        } else if (p.getTitle().startsWith("F")) {
            view.recipientFirstLetter.setText("F");
        } else if (p.getTitle().startsWith("G")) {
            view.recipientFirstLetter.setText("G");
        } else if (p.getTitle().startsWith("H")) {
            view.recipientFirstLetter.setText("H");
        } else if (p.getTitle().startsWith("I")) {
            view.recipientFirstLetter.setText("I");
        } else if (p.getTitle().startsWith("J")) {
            view.recipientFirstLetter.setText("J");
        } else if (p.getTitle().startsWith("K")) {
            view.recipientFirstLetter.setText("K");
        } else if (p.getTitle().startsWith("L")) {
            view.recipientFirstLetter.setText("L");
        } else if (p.getTitle().startsWith("M")) {
            view.recipientFirstLetter.setText("M");
        } else if (p.getTitle().startsWith("N")) {
            view.recipientFirstLetter.setText("N");
        } else if (p.getTitle().startsWith("O")) {
            view.recipientFirstLetter.setText("O");
        } else if (p.getTitle().startsWith("P")) {
            view.recipientFirstLetter.setText("P");
        } else if (p.getTitle().startsWith("Q")) {
            view.recipientFirstLetter.setText("Q");
        } else if (p.getTitle().startsWith("R")) {
            view.recipientFirstLetter.setText("R");
        } else if (p.getTitle().startsWith("S")) {
            view.recipientFirstLetter.setText("S");
        } else if (p.getTitle().startsWith("T")) {
            view.recipientFirstLetter.setText("T");
        } else if (p.getTitle().startsWith("U")) {
            view.recipientFirstLetter.setText("U");
        } else if (p.getTitle().startsWith("V")) {
            view.recipientFirstLetter.setText("V");
        } else if (p.getTitle().startsWith("W")) {
            view.recipientFirstLetter.setText("W");
        } else if (p.getTitle().startsWith("X")) {
            view.recipientFirstLetter.setText("X");
        } else if (p.getTitle().startsWith("Y")) {
            view.recipientFirstLetter.setText("Y");
        } else if (p.getTitle().startsWith("Z")) {
            view.recipientFirstLetter.setText("Z");
        }
    }
}
