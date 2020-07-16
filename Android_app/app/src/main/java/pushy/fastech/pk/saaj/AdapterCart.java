package pushy.fastech.pk.saaj;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.material.components.R;
import com.material.components.activity.login.LoginSimpleGreen;
import com.material.components.utils.Tools;

import java.util.ArrayList;
import java.util.List;

public class AdapterCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static List<Cart> items = new ArrayList<>();

    private Context ctx;
    private OnItemClickListener mOnItemClickListener;


    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }



    public AdapterCart(Context context, List<Cart> items) {
        this.items = items;
        ctx = context;
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView title;
        public TextView desc;
        public TextView price;
        public ImageButton btnAdd, btnSub;

        public OriginalViewHolder(View v) {
            super(v);
            image = (ImageView) v.findViewById(R.id.imgMain);
            title = (TextView) v.findViewById(R.id.title);
            desc = (TextView) v.findViewById(R.id.desc);
            price = (TextView) v.findViewById(R.id.priceTotal);

            btnAdd = (ImageButton) v.findViewById(R.id.btnAdd);
            btnSub = (ImageButton) v.findViewById(R.id.btnSub);

        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_shopping_cart_simple, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof OriginalViewHolder) {
            OriginalViewHolder view = (OriginalViewHolder) holder;

            final Cart p = items.get(position);
            view.title.setText(p.getItemName());
            view.desc.setText(p.getSize() + " x " + p.getItemQty());
            view.price.setText("Rs." + p.getPr());
            Tools.displayImageOriginal(ctx, view.image, LoginSimpleGreen.picPath + p.getPic());

            view.btnAdd.setTag(p.getId().toString() + "~" + p.getSize() + "~" + position);
            view.btnSub.setTag(p.getId().toString() + "~" + p.getSize() + "~" + position);

            view.image.setOnClickListener(new View.OnClickListener() {
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

    public interface OnItemClickListener {
        void onItemClick(View view, Cart obj, int pos);
    }

    public Cart GetItem(int pos){
         return items.get(pos);
    }

}