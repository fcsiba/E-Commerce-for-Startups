package pushy.fastech.pk.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.utils.ItemAnimation;

import java.util.List;

import pushy.fastech.pk.Helper.Homeworks;
import pushy.fastech.pk.staff.homework.PostHomework;

public class AdapterShowHomework extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_ITEM = 1;
    private final int VIEW_SECTION = 0;
    private RecyclerView rvHomework;
    private List<Homeworks> items;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;
    private int animation_type = 0;

    public interface OnItemClickListener {
        void onItemClick(View view, Homeworks obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterShowHomework(Context context, List<Homeworks> items, int animation_type, RecyclerView rv) {
        this.items = items;
        ctx = context;
        this.animation_type = animation_type;
        rvHomework = rv;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView backgroundTintHomework, deleteItem;
        public TextView courseFirstLetter, classSection, subjects, dueDateTime, descriptionHomework;
        public View lyt_parentShowHomework;


        public OriginalViewHolder(View v) {
            super(v);
            backgroundTintHomework = v.findViewById(R.id.course_first_letter_image_tint);
            courseFirstLetter = v.findViewById(R.id.course_first_letter_image);
            deleteItem = v.findViewById(R.id.delete_show_hw);
            classSection = v.findViewById(R.id.class_section_show_hw);
            subjects = v.findViewById(R.id.subject_show_hw);
            dueDateTime = v.findViewById(R.id.due_date_time_show_hw);
            descriptionHomework = v.findViewById(R.id.description_show_hw);
            lyt_parentShowHomework = v.findViewById(R.id.lyt_parent_show_hw);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        public TextView dateOfPostingHomework;

        public SectionViewHolder(View v) {
            super(v);
            dateOfPostingHomework = v.findViewById(R.id.date_show_hw);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_show, parent, false);
            vh = new OriginalViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_homework_show_secondviewholder, parent, false);
            vh = new SectionViewHolder(v);
        }
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        final Homeworks p = items.get(position);
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;
            view.classSection.setText(p.getCls() + "(" + p.getSec() + ") - " + p.getFac());
            view.subjects.setText(p.getSub());
            view.dueDateTime.setText(p.getDate());
            view.descriptionHomework.setText(p.getHomework());
            if (p.getSub().toUpperCase().startsWith("A")) {
                view.courseFirstLetter.setText("A");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E88E5")));
            } else if (p.getSub().toUpperCase().startsWith("B")) {
                view.courseFirstLetter.setText("B");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
            } else if (p.getSub().toUpperCase().startsWith("C")) {
                view.courseFirstLetter.setText("C");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
            } else if (p.getSub().toUpperCase().startsWith("D")) {
                view.courseFirstLetter.setText("D");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF5350")));
            } else if (p.getSub().toUpperCase().startsWith("E")) {
                view.courseFirstLetter.setText("E");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA68C8")));
            } else if (p.getSub().toUpperCase().startsWith("F")) {
                view.courseFirstLetter.setText("F");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F48FB1")));
            } else if (p.getSub().toUpperCase().startsWith("G")) {
                view.courseFirstLetter.setText("G");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E88E5")));
            } else if (p.getSub().toUpperCase().startsWith("H")) {
                view.courseFirstLetter.setText("H");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
            } else if (p.getSub().toUpperCase().startsWith("I")) {
                view.courseFirstLetter.setText("I");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
            } else if (p.getSub().toUpperCase().startsWith("J")) {
                view.courseFirstLetter.setText("J");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF5350")));
            } else if (p.getSub().toUpperCase().startsWith("K")) {
                view.courseFirstLetter.setText("K");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA68C8")));
            } else if (p.getSub().toUpperCase().startsWith("L")) {
                view.courseFirstLetter.setText("L");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F48FB1")));
            } else if (p.getSub().toUpperCase().startsWith("M")) {
                view.courseFirstLetter.setText("M");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E88E5")));
            } else if (p.getSub().toUpperCase().startsWith("N")) {
                view.courseFirstLetter.setText("N");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
            } else if (p.getSub().toUpperCase().startsWith("O")) {
                view.courseFirstLetter.setText("O");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
            } else if (p.getSub().toUpperCase().startsWith("P")) {
                view.courseFirstLetter.setText("P");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF5350")));
            } else if (p.getSub().toUpperCase().startsWith("Q")) {
                view.courseFirstLetter.setText("Q");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA68C8")));
            } else if (p.getSub().toUpperCase().startsWith("R")) {
                view.courseFirstLetter.setText("R");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F48FB1")));
            } else if (p.getSub().toUpperCase().startsWith("S")) {
                view.courseFirstLetter.setText("S");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E88E5")));
            } else if (p.getSub().toUpperCase().startsWith("T")) {
                view.courseFirstLetter.setText("T");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
            } else if (p.getSub().toUpperCase().startsWith("U")) {
                view.courseFirstLetter.setText("U");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#43A047")));
            } else if (p.getSub().toUpperCase().startsWith("V")) {
                view.courseFirstLetter.setText("V");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EF5350")));
            } else if (p.getSub().toUpperCase().startsWith("W")) {
                view.courseFirstLetter.setText("W");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BA68C8")));
            } else if (p.getSub().toUpperCase().startsWith("X")) {
                view.courseFirstLetter.setText("X");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F48FB1")));
            } else if (p.getSub().toUpperCase().startsWith("Y")) {
                view.courseFirstLetter.setText("Y");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#1E88E5")));
            } else if (p.getSub().toUpperCase().startsWith("Z")) {
                view.courseFirstLetter.setText("Z");
                view.backgroundTintHomework.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#607d8b")));
            }

            view.lyt_parentShowHomework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(view, items.get(position), position);
                    }
                }
            });

            view.lyt_parentShowHomework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("data.fastech.pk", "Clicked: " + p.getId());
                        Intent intent = new Intent(ctx, PostHomework.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("id", p.getId());
                        bundle.putString("cls", p.getCls());
                        bundle.putString("sec", p.getSec());
                        bundle.putString("fac", p.getFac());
                        bundle.putString("sub", p.getSub());
                        bundle.putString("date", p.getDateStr());
                        bundle.putString("hw", p.getHomework());
                        intent.putExtras(bundle);
                        ctx.startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            view.deleteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   // show dialog to delete
                    confirmDeleteDialog(position);
                }
            });

        } else {
            SectionViewHolder viewSection = (SectionViewHolder) holder;
            viewSection.dateOfPostingHomework.setText(p.getDate());
        }

        setAnimation(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).sectionSeparate ? VIEW_SECTION : VIEW_ITEM;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
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

    private void deleteRecipient(final int position) {
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setRemoveDuration(300);
        rvHomework.setItemAnimator(itemAnimator);

        items.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, items.size());

        rvHomework.requestLayout();
    }

    private void confirmDeleteDialog(final int position) {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_delete_homework_item);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        (dialog.findViewById(R.id.bt_yes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteRecipient(position);
                dialog.dismiss();
            }
        });

        (dialog.findViewById(R.id.bt_no)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}