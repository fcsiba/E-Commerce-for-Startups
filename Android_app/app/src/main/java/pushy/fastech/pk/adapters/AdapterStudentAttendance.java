package pushy.fastech.pk.adapters;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.utils.ItemAnimation;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import pushy.fastech.pk.Helper.Attendance;

public class AdapterStudentAttendance extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Attendance> items;
    private Context ctx, app_context;
    private int animation_type = 0;
    private int dayOfMonth, month, year;
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TextView txtPresent, txtAbsent, txtLeave;

    public AdapterStudentAttendance(Context context, Context _app_context, List<Attendance> items, TextView tvP, TextView tvA, TextView tvL, int animation_type) {
        this.items = items;
        ctx = context;
        txtPresent = tvP;
        txtAbsent = tvA;
        txtLeave = tvL;
        app_context = _app_context;
        this.animation_type = animation_type;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView name, fatherName;
        public View lyt_parent;
        public ImageView stdLetterTint;
        public TextView stdFirstLetter;

        public OriginalViewHolder(View v) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_attendance, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            final Attendance p = items.get(position);
            view.name.setText(p.getName());
            view.fatherName.setText(p.getfName());

            symbol(holder, position);

            //Dialog Here
            Log.d("data.fastech.pk", p.getName() + " - " + p.getStatus());
            if (p.getAbsent() == true) {
                studentAbsent(holder, position);
            }
            if(p.getStatus().equals("L")) {
                studentOnLeave(holder, position);
            }

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Log.d("data.fastech.pk", "isAbsent: " + p.getAbsent());
                        if (p.getAbsent() == true) {
                            studentPresent(holder, position);
                        } else {
                            studentAbsent(holder, position);
                        }

                        txtPresent.setText(p.getTotal(items, "P"));
                        txtAbsent.setText(p.getTotal(items, "A"));
                        txtLeave.setText(p.getTotal(items, "L"));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            view.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    try {
                        if (!p.getStatus().equals("L")) {
                            onLeaveCustomDialog(p, app_context, holder, position);
                        } else {
                            studentPresent(holder, position);
                        }
                    } catch (Exception e) {
                        Log.e("data.fastech.pk", e.getLocalizedMessage());
                    }
                    return true;
                }
            });
        }

        setAnimation(holder.itemView, position);
    }

    public void studentAbsent(RecyclerView.ViewHolder holder, final int position) {
        //Log.d("data.fastech.pk", "marking as absent");
        OriginalViewHolder viewHolder = (OriginalViewHolder) holder;
        Attendance people = items.get(position);
        people.setAbsent(true);
        people.setStatus("A");
        viewHolder.name.setTextColor(Color.WHITE);
        viewHolder.fatherName.setTextColor(Color.WHITE);
        viewHolder.lyt_parent.setBackgroundColor(Color.parseColor("#E57373"));
    }

    public void studentPresent(RecyclerView.ViewHolder holder, final int position) {
        //Log.d("data.fastech.pk", "marking as present");
        OriginalViewHolder viewHolder = (OriginalViewHolder) holder;
        Attendance people = items.get(position);
        people.setAbsent(false);
        people.setStatus("P");
        viewHolder.name.setTextColor(Color.BLACK);
        viewHolder.fatherName.setTextColor(Color.GRAY);
        viewHolder.lyt_parent.setBackgroundColor(Color.WHITE);
    }

    public void studentOnLeave(RecyclerView.ViewHolder holder, final int position) {
        Log.d("data.fastech.pk", "marking as on leave");
        OriginalViewHolder viewHolder = (OriginalViewHolder) holder;
        Attendance people = items.get(position);
        people.setStatus("L");
        viewHolder.name.setTextColor(Color.WHITE);
        viewHolder.fatherName.setTextColor(Color.WHITE);
        viewHolder.lyt_parent.setBackgroundColor(Color.parseColor("#4FC3F7"));
        // Counter values
        txtPresent.setText(people.getTotal(items, "P"));
        txtAbsent.setText(people.getTotal(items, "A"));
        txtLeave.setText(people.getTotal(items, "L"));
    }

    public List<Attendance> GetList() {
        return items;
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
        final Attendance p = items.get(position);

        Random rnd = new Random();
        int color = Color.argb(255, rnd.nextInt(200), rnd.nextInt(200), rnd.nextInt(200));
        view.stdLetterTint.setBackgroundTintList(ColorStateList.valueOf(color));

        if (p.getName().startsWith("A")) {
            view.stdFirstLetter.setText("A");
        } else if (p.getName().startsWith("B")) {
            view.stdFirstLetter.setText("B");
        } else if (p.getName().startsWith("C")) {
            view.stdFirstLetter.setText("C");
        } else if (p.getName().startsWith("D")) {
            view.stdFirstLetter.setText("D");
        } else if (p.getName().startsWith("E")) {
            view.stdFirstLetter.setText("E");
        } else if (p.getName().startsWith("F")) {
            view.stdFirstLetter.setText("F");
        } else if (p.getName().startsWith("G")) {
            view.stdFirstLetter.setText("G");
        } else if (p.getName().startsWith("H")) {
            view.stdFirstLetter.setText("H");
        } else if (p.getName().startsWith("I")) {
            view.stdFirstLetter.setText("I");
        } else if (p.getName().startsWith("J")) {
            view.stdFirstLetter.setText("J");
        } else if (p.getName().startsWith("K")) {
            view.stdFirstLetter.setText("K");
        } else if (p.getName().startsWith("L")) {
            view.stdFirstLetter.setText("L");
        } else if (p.getName().startsWith("M")) {
            view.stdFirstLetter.setText("M");
        } else if (p.getName().startsWith("N")) {
            view.stdFirstLetter.setText("N");
        } else if (p.getName().startsWith("O")) {
            view.stdFirstLetter.setText("O");
        } else if (p.getName().startsWith("P")) {
            view.stdFirstLetter.setText("P");
        } else if (p.getName().startsWith("Q")) {
            view.stdFirstLetter.setText("Q");
        } else if (p.getName().startsWith("R")) {
            view.stdFirstLetter.setText("R");
        } else if (p.getName().startsWith("S")) {
            view.stdFirstLetter.setText("S");
        } else if (p.getName().startsWith("T")) {
            view.stdFirstLetter.setText("T");
        } else if (p.getName().startsWith("U")) {
            view.stdFirstLetter.setText("U");
        } else if (p.getName().startsWith("V")) {
            view.stdFirstLetter.setText("V");
        } else if (p.getName().startsWith("W")) {
            view.stdFirstLetter.setText("W");
        } else if (p.getName().startsWith("X")) {
            view.stdFirstLetter.setText("X");
        } else if (p.getName().startsWith("Y")) {
            view.stdFirstLetter.setText("Y");
        } else if (p.getName().startsWith("Z")) {
            view.stdFirstLetter.setText("Z");
        }
    }

    private void onLeaveCustomDialog(Attendance p, final Context app_ctx, final RecyclerView.ViewHolder holder, final int position) {
        final Dialog dialog = new Dialog(app_ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.student_on_leave_dailog);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time: " + c);
        final SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate = df.format(c);

        ((TextView) dialog.findViewById(R.id.student_name_onleave_tv)).setText(p.getName());
        ((TextView) dialog.findViewById(R.id.student_fname_onleave_tv)).setText(p.getfName());

        ((Button) dialog.findViewById(R.id.spn_from_date)).setText(formattedDate);
        ((Button) dialog.findViewById(R.id.spn_to_date)).setText(formattedDate);

        (dialog.findViewById(R.id.spn_from_date)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(app_ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        ((Button) dialog.findViewById(R.id.spn_from_date)).setText(day + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        (dialog.findViewById(R.id.spn_to_date)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(app_ctx, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        calendar.set(year, month, day);
                        ((Button) dialog.findViewById(R.id.spn_to_date)).setText(day + "-" + (month + 1) + "-" + year);
                    }
                }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        (dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        final EditText textOnLeave = dialog.findViewById(R.id.reason_onleave_edtxt);
        (dialog.findViewById(R.id.bt_save)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (textOnLeave.getText().toString().trim().length() == 0) {
                    textOnLeave.setError("Reason for leave");
                } else {
                    studentOnLeave(holder, position);
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    private int lastPosition = -1;
    private boolean on_attach = true;

    private void setAnimation(View view, int position) {
        if (position > lastPosition) {
            ItemAnimation.animate(view, on_attach ? position : -1, animation_type);
            lastPosition = position;
        }
    }
}