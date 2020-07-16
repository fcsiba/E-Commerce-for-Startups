package pushy.fastech.pk.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Dialog;

import com.material.components.R;

import java.util.List;
import java.util.Random;

import pushy.fastech.pk.Helper.Thread_Messages;
import pushy.fastech.pk.admin.adminportal.AdminNotification;
import pushy.fastech.pk.admin.adminportal.AdminNotificationView;

public class AdminAdapterNotificationView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Thread_Messages> items;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    AdminNotificationView ntView;
    RecyclerView recyclerView;

    public interface OnItemClickListener {
        void onItemClick(View view, Thread_Messages obj, int position);

        void onItemLongClick(View view, Thread_Messages obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of DataSet)
    public AdminAdapterNotificationView(Context context, List<Thread_Messages> _msgs, AdminNotificationView _ntView, RecyclerView _rv) {
        ctx = context;
        items = _msgs;
        ntView = _ntView;
        recyclerView = _rv;
    }

    // Original view holder
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView image;
        public TextView name, fatherName;
        public View lyt_parent;
        public ImageView stdLetterTint;
        public TextView stdFirstLetter;

        public ItemViewHolder(View v) {
            super(v);

            image = v.findViewById(R.id.image);
            name = v.findViewById(R.id.name);
            fatherName = v.findViewById(R.id.name_of_father);
            stdFirstLetter = v.findViewById(R.id.std_first_letter);
            stdLetterTint = v.findViewById(R.id.std_first_letter_tint);
            lyt_parent = v.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification_view, parent, false);
        vh = new ItemViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            final Thread_Messages m = items.get(position);
            ItemViewHolder vItem = (ItemViewHolder) holder;
            vItem.name.setText(m.getTitle());
            vItem.fatherName.setText(m.getDesc());

            symbol(holder, position);

            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);

                        try {
                            Intent intent = new Intent(ctx, AdminNotification.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("recipient", m.getReciept());
                            intent.putExtras(bundle);
                            ntView.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            vItem.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemLongClick(view, items.get(position), position);
                        dialogVerification(holder, position, view);
                    }
                    return true;
                }
            });
        }
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void insertItem(Thread_Messages item) {
        this.items.add(item);
        notifyItemInserted(getItemCount());
    }

    public void setItems(List<Thread_Messages> items) {
        this.items = items;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void symbol(final RecyclerView.ViewHolder holder, final int position) {

        ItemViewHolder view = (ItemViewHolder) holder;
        final Thread_Messages p = items.get(position);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
        view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(color));

        if (p.getTitle().startsWith("A")) {
            view.stdFirstLetter.setText("A");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e91e63")));
        } else if (p.getTitle().startsWith("B")) {
            view.stdFirstLetter.setText("B");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9c27b0")));
        } else if (p.getTitle().startsWith("C")) {
            view.stdFirstLetter.setText("C");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#673ab7")));
        } else if (p.getTitle().startsWith("D")) {
            view.stdFirstLetter.setText("D");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
        } else if (p.getTitle().startsWith("E")) {
            view.stdFirstLetter.setText("E");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5677fc")));
        } else if (p.getTitle().startsWith("F")) {
            view.stdFirstLetter.setText("F");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#689F38")));
        } else if (p.getTitle().startsWith("G")) {
            view.stdFirstLetter.setText("G");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#03a9f4")));
        } else if (p.getTitle().startsWith("H")) {
            view.stdFirstLetter.setText("H");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00bcd4")));
        } else if (p.getTitle().startsWith("I")) {
            view.stdFirstLetter.setText("I");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
        } else if (p.getTitle().startsWith("J")) {
            view.stdFirstLetter.setText("J");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#259b24")));
        } else if (p.getTitle().startsWith("K")) {
            view.stdFirstLetter.setText("K");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff5722")));
        } else if (p.getTitle().startsWith("L")) {
            view.stdFirstLetter.setText("L");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#795548")));
        } else if (p.getTitle().startsWith("M")) {
            view.stdFirstLetter.setText("M");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
        } else if (p.getTitle().startsWith("N")) {
            view.stdFirstLetter.setText("N");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff9800")));
        } else if (p.getTitle().startsWith("O")) {
            view.stdFirstLetter.setText("O");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#e91e63")));
        } else if (p.getTitle().startsWith("P")) {
            view.stdFirstLetter.setText("P");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#9c27b0")));
        } else if (p.getTitle().startsWith("Q")) {
            view.stdFirstLetter.setText("Q");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#673ab7")));
        } else if (p.getTitle().startsWith("R")) {
            view.stdFirstLetter.setText("R");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E53935")));
        } else if (p.getTitle().startsWith("S")) {
            view.stdFirstLetter.setText("S");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#5677fc")));
        } else if (p.getTitle().startsWith("T")) {
            view.stdFirstLetter.setText("T");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#689F38")));
        } else if (p.getTitle().startsWith("U")) {
            view.stdFirstLetter.setText("U");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#03a9f4")));
        } else if (p.getTitle().startsWith("V")) {
            view.stdFirstLetter.setText("V");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00bcd4")));
        } else if (p.getTitle().startsWith("W")) {
            view.stdFirstLetter.setText("W");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#009688")));
        } else if (p.getTitle().startsWith("X")) {
            view.stdFirstLetter.setText("X");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#259b24")));
        } else if (p.getTitle().startsWith("Y")) {
            view.stdFirstLetter.setText("Y");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff5722")));
        } else if (p.getTitle().startsWith("Z")) {
            view.stdFirstLetter.setText("Z");
//            view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff9800")));
        }
    }

    // Dialog selection Verification
    private void dialogVerification(RecyclerView.ViewHolder holder, final int position, View view) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_verify_notifiaction_view);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final Thread_Messages m = items.get(position);

        ((TextView) dialog.findViewById(R.id.recipient_name_to_authenticate)).setText("Authenticate " + "'" + m.getTitle() + "'" + "?");

        ((AppCompatButton) dialog.findViewById(R.id.bt_verify)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ctx, "Verified", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });

        ((AppCompatButton) dialog.findViewById(R.id.bt_delete)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                deleteRecipient(position);
                Toast.makeText(ctx, "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    // Delete request
    private void deleteRecipient(final int position) {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(300);
        recyclerView.setItemAnimator(itemAnimator);

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());

        recyclerView.requestLayout();
    }
}