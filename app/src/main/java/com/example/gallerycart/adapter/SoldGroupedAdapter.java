package com.example.gallerycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.gallerycart.R;
import com.example.gallerycart.data.model.CartItemWithPost;
import com.example.gallerycart.data.model.SectionItem;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import de.hdodenhof.circleimageview.CircleImageView;

public class SoldGroupedAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context ctx;
    private final List<SectionItem> sections = new ArrayList<>();
    private final SimpleDateFormat dateFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public SoldGroupedAdapter(Context ctx) {
        this.ctx = ctx;
    }

    public void setSections(List<SectionItem> list) {
        sections.clear();
        if (list != null) sections.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sections.get(position).type;
    }

    @Override
    public int getItemCount() {
        return sections.size();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(ctx);
        if (viewType == SectionItem.TYPE_HEADER) {
            View v = inf.inflate(R.layout.item_sold_header, parent, false);
            return new HeaderVH(v);
        } else {
            View v = inf.inflate(R.layout.item_sold, parent, false);
            return new ItemVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder vh, int position) {
        SectionItem si = sections.get(position);
        if (si.type == SectionItem.TYPE_HEADER) {
            HeaderVH h = (HeaderVH) vh;
            h.title.setText("Order #" + si.cartId);
            String dateText = si.purchaseDateMillis > 0 ? dateFmt.format(new Date(si.purchaseDateMillis)) : "—";
            h.meta.setText(dateText + " • Total: " + String.format("%.0f VND", si.cartTotal));
        } else {
            ItemVH h = (ItemVH) vh;
            CartItemWithPost it = si.item;
            h.title.setText(it.getTitle() != null ? it.getTitle() : "—");
            Double unitPrice = it.getPrice();
            if (unitPrice == null) unitPrice = 0.0;
            h.unitPrice.setText(String.format("Price: %.0f VND", unitPrice));
            Glide.with(ctx).load(it.getImagePath()).into(h.image);
        }
    }

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView title, meta;
        HeaderVH(@NonNull View v) {
            super(v);
            title = v.findViewById(R.id.tvCartHeaderTitle);
            meta = v.findViewById(R.id.tvCartHeaderMeta);
        }
    }

    static class ItemVH extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView title, unitPrice, qty, lineTotal;
        ItemVH(@NonNull View v) {
            super(v);
            image = v.findViewById(R.id.sold_item_image);
            title = v.findViewById(R.id.sold_item_title);
            unitPrice = v.findViewById(R.id.sold_item_unit_price);
        }
    }
}

