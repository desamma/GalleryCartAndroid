package com.example.gallerycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gallerycart.R;
import com.example.gallerycart.data.model.CartItemWithPost;
import java.util.ArrayList;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.VH> {

    public interface Listener {
        void onRemove(CartItemWithPost item);
    }

    private final Context ctx;
    private final Listener listener;
    private List<CartItemWithPost> data = new ArrayList<>();

    public CartAdapter(Context ctx, Listener listener) {
        this.ctx = ctx;
        this.listener = listener;
    }

    public void setData(List<CartItemWithPost> items) {
        data = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx).inflate(R.layout.item_cart, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        CartItemWithPost it = data.get(position);

        holder.title.setText(it.getTitle());

        int qty = 0;
        if (it.getCartItem() != null) {
            qty = it.getCartItem().getQuantity();
        }
        holder.qty.setText("Qty: " + qty);

        double linePrice = (it.getPrice()) * qty;
        holder.price.setText(String.format("%.0f VND", linePrice));

        Glide.with(ctx).load(it.getImagePath()).into(holder.image);

        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) listener.onRemove(it);
        });
    }

    @Override
    public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView title, qty, price;
        ImageButton btnRemove;
        VH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.cart_item_image);
            title = v.findViewById(R.id.cart_item_title);
            price = v.findViewById(R.id.cart_item_price);
            btnRemove = v.findViewById(R.id.btn_remove);
        }
    }
}

