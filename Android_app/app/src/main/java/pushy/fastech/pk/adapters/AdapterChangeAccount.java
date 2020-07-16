package pushy.fastech.pk.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.material.components.R;

import java.util.List;

import pushy.fastech.pk.Helper.GetAll;

public class AdapterChangeAccount extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<GetAll> items;

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener{
        void onItemClick(View view, GetAll obj, int Position);
    }



    public void setOnItemClickListener(final OnItemClickListener mItemClickListener){
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterChangeAccount(Context context, List<GetAll> items){
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder{
        public TextView firstLetter, name, section, school;
        public View tint;
        public CardView lyt_parent;

        public OriginalViewHolder(final View itemView) {
            super(itemView);

            firstLetter = itemView.findViewById(R.id.symbol_activity_select_account);
            name = itemView.findViewById(R.id.name_activity_select_account);
            section = itemView.findViewById(R.id.section_activity_select_account);
            school = itemView.findViewById(R.id.school_activity_select_account);
            tint = itemView.findViewById(R.id.tint_activity_select_account);
            lyt_parent = itemView.findViewById(R.id.lyt_parent_activity_select_account);

            // bind focus listener
            itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger
                        Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.scale_in);
                        itemView.startAnimation(anim);
                        Toast.makeText(ctx, "Selected", Toast.LENGTH_SHORT).show();
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(ctx, R.anim.scale_out);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_change_account, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            Glide.with(ctx);

            GetAll c = items.get(position);
            view.name.setText(c.getSMSTitle());

            symbol(holder, position);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
//                        Toast.makeText(ctx, "Clicked", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void symbol(final RecyclerView.ViewHolder holder, final int position) {
        OriginalViewHolder view = (OriginalViewHolder) holder;
        final GetAll p = items.get(position);

        if (p.getSMSTitle().startsWith("A")) {
            view.firstLetter.setText("A");
            view.tint.setBackgroundColor(Color.parseColor("#B38BC34A"));
        } else if (p.getSMSTitle().startsWith("B")) {
            view.firstLetter.setText("B");
            view.tint.setBackgroundColor(Color.parseColor("#B3E53935"));
        } else if (p.getSMSTitle().startsWith("C")) {
            view.firstLetter.setText("C");
            view.tint.setBackgroundColor(Color.parseColor("#B35677fc"));
        } else if (p.getSMSTitle().startsWith("D")) {
            view.firstLetter.setText("D");
            view.tint.setBackgroundColor(Color.parseColor("#B39c27b0"));
        } else if (p.getSMSTitle().startsWith("E")) {
            view.firstLetter.setText("E");
            view.tint.setBackgroundColor(Color.parseColor("#B3e91e63"));
        } else if (p.getSMSTitle().startsWith("F")) {
            view.firstLetter.setText("F");
            view.tint.setBackgroundColor(Color.parseColor("#B3ff9800"));
        } else if (p.getSMSTitle().startsWith("G")) {
            view.firstLetter.setText("G");
            view.tint.setBackgroundColor(Color.parseColor("#B38BC34A"));
        } else if (p.getSMSTitle().startsWith("H")) {
            view.firstLetter.setText("H");
            view.tint.setBackgroundColor(Color.parseColor("#B3E53935"));
        } else if (p.getSMSTitle().startsWith("I")) {
            view.firstLetter.setText("I");
            view.tint.setBackgroundColor(Color.parseColor("#B35677fc"));
        } else if (p.getSMSTitle().startsWith("J")) {
            view.firstLetter.setText("J");
            view.tint.setBackgroundColor(Color.parseColor("#B39c27b0"));
        } else if (p.getSMSTitle().startsWith("K")) {
            view.firstLetter.setText("K");
            view.tint.setBackgroundColor(Color.parseColor("#B3e91e63"));
        } else if (p.getSMSTitle().startsWith("L")) {
            view.firstLetter.setText("L");
            view.tint.setBackgroundColor(Color.parseColor("#B3ff9800"));
        } else if (p.getSMSTitle().startsWith("M")) {
            view.firstLetter.setText("M");
            view.tint.setBackgroundColor(Color.parseColor("#B3e91e63"));
        } else if (p.getSMSTitle().startsWith("N")) {
            view.firstLetter.setText("N");
            view.tint.setBackgroundColor(Color.parseColor("#B38BC34A"));
        } else if (p.getSMSTitle().startsWith("O")) {
            view.firstLetter.setText("O");
            view.tint.setBackgroundColor(Color.parseColor("#B3E53935"));
        } else if (p.getSMSTitle().startsWith("P")) {
            view.firstLetter.setText("P");
            view.tint.setBackgroundColor(Color.parseColor("#B35677fc"));
        } else if (p.getSMSTitle().startsWith("Q")) {
            view.firstLetter.setText("Q");
            view.tint.setBackgroundColor(Color.parseColor("#B39c27b0"));
        } else if (p.getSMSTitle().startsWith("R")) {
            view.firstLetter.setText("R");
            view.tint.setBackgroundColor(Color.parseColor("#B3e91e63"));
        } else if (p.getSMSTitle().startsWith("S")) {
            view.firstLetter.setText("S");
            view.tint.setBackgroundColor(Color.parseColor("#B3ff9800"));
        } else if (p.getSMSTitle().startsWith("T")) {
            view.firstLetter.setText("T");
            view.tint.setBackgroundColor(Color.parseColor("#B38BC34A"));
        } else if (p.getSMSTitle().startsWith("U")) {
            view.firstLetter.setText("U");
            view.tint.setBackgroundColor(Color.parseColor("#B3E53935"));
        } else if (p.getSMSTitle().startsWith("V")) {
            view.firstLetter.setText("V");
            view.tint.setBackgroundColor(Color.parseColor("#B35677fc"));
        } else if (p.getSMSTitle().startsWith("W")) {
            view.firstLetter.setText("W");
            view.tint.setBackgroundColor(Color.parseColor("#B39c27b0"));
        } else if (p.getSMSTitle().startsWith("X")) {
            view.firstLetter.setText("X");
            view.tint.setBackgroundColor(Color.parseColor("#B3e91e63"));
        } else if (p.getSMSTitle().startsWith("Y")) {
            view.firstLetter.setText("Y");
            view.tint.setBackgroundColor(Color.parseColor("#B3e9B3ff98001e63"));
        } else if (p.getSMSTitle().startsWith("Z")) {
            view.firstLetter.setText("Z");
            view.tint.setBackgroundColor(Color.parseColor("#B38BC34A"));
        }
    }
}