package pushy.fastech.pk.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.model.People;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Random;

import pushy.fastech.pk.Helper.Marks;
import pushy.fastech.pk.staff.staffportal.StudentMarksList;

public class AdapterStudentMarks extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Marks> items;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, People obj, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterStudentMarks(Context context, List<Marks> items) {
        this.items = items;
        ctx = context;

    }

    public List<Marks> GetList() {
        return items;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView stdLetterTint;
        public TextView stdFirstLetter, name, name_of_father;
        public EditText marks_edittxt;
        public View lyt_parent;

        public OriginalViewHolder(View itemView) {
            super(itemView);
            stdLetterTint = itemView.findViewById(R.id.first_letter_tint);
            stdFirstLetter = itemView.findViewById(R.id.first_letter);
            name = itemView.findViewById(R.id.name);
            name_of_father = itemView.findViewById(R.id.name_of_father);
            marks_edittxt = itemView.findViewById(R.id.marks_edittxt);
            lyt_parent = itemView.findViewById(R.id.lyt_parent);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_marks, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            final Marks p = items.get(position);
            view.name.setText(p.name);
            view.name_of_father.setText(p.fName);
            view.marks_edittxt.setText(p.getObtainedMarks());

            view.marks_edittxt.setSelection(view.marks_edittxt.getText().length());
            if (view.marks_edittxt.getText().toString().equals("0")) {
                view.marks_edittxt.setText(null);
            }

//            InputMethodManager imm = (InputMethodManager)   ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
//            view.marks_edittxt.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);

            p.setTotalMarks(StudentMarksList.finalValue);

            symbol(holder, position);

            view.lyt_parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickListener != null) {

                    }
                }
            });

            view.marks_edittxt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void afterTextChanged(Editable editable) {

                    Drawable buttonDrawable = view.marks_edittxt.getBackground();
                    buttonDrawable = DrawableCompat.wrap(buttonDrawable);
                    String data = "";
                    Float n = 0f;
                    DecimalFormat df = new DecimalFormat("0.00");
                    df.setMaximumFractionDigits(2);
                    Boolean number;

                    try {
                        data = editable.toString();
                    } catch (Exception x) {
                        Log.d("data.fastech.pk", x.getLocalizedMessage());
                    }

                    //Number check
                    try {
                        BigDecimal d = new BigDecimal(data);
                        n = Float.parseFloat(d.toString());
                        number = true;
                    } catch (Exception x) {
                        Log.e("data.fastech.pk", "Error:" + x.getLocalizedMessage());
                        number = false;
                    }

                    Log.d("data.fastech.pk", "Data:" + data);

                    try {
                        if (n > StudentMarksList.finalValue) {
                            DrawableCompat.setTint(buttonDrawable, Color.RED);
                            view.marks_edittxt.setBackground(buttonDrawable);
                            view.marks_edittxt.setError("Not more than " + StudentMarksList.finalValue);
                        } else if (n <= StudentMarksList.finalValue) {
                            DrawableCompat.setTint(buttonDrawable, Color.parseColor("#1E88E5"));
                            view.marks_edittxt.setBackground(buttonDrawable);
                            view.marks_edittxt.setError(null);
                            if (number) {
                                p.setObtainedMarks(data);
                                p.setNumericMarks(n);
                            } else {
                                Log.d("data.fastech.pk", "Not numbers..");
                                p.setObtainedMarks(data);
                                p.setNumericMarks(0);
                            }
                            if (TextUtils.isEmpty(data)) {
                                Log.d("data.fastech.pk", "Empty numbers..");
                                p.setNumericMarks(0);
                                p.setObtainedMarks("-");
                            }
                        }
                    } catch (Exception e) {
                        if (TextUtils.isEmpty(data)) {
                            Log.d("data.fastech.pk", "Empty numbers..");
                            p.setNumericMarks(0);
                            p.setObtainedMarks("-");
                        } else {
                            p.setObtainedMarks(data);
                            p.setNumericMarks(0);
                        }
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
        AdapterStudentMarks.OriginalViewHolder view = (AdapterStudentMarks.OriginalViewHolder) holder;
        final Marks p = items.get(position);

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
}
