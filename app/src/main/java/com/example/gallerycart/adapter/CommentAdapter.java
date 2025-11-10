package com.example.gallerycart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.R;

import java.util.ArrayList;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holder> {

    public static class CommentDisplay {
        public final String header;  // "username · yyyy-MM-dd HH:mm"
        public final String content;
        public CommentDisplay(String header, String content) {
            this.header = header;
            this.content = content;
        }
    }

    private final List<CommentDisplay> items = new ArrayList<>();

    public void setItems(List<CommentDisplay> list) {
        items.clear();
        if (list != null) items.addAll(list);
        notifyDataSetChanged();
    }

    public void addItem(CommentDisplay c) {
        items.add(c);
        notifyItemInserted(items.size() - 1);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder h, int position) {
        CommentDisplay c = items.get(position);
        h.tvHeader.setText(c.header);
        h.tvContent.setText(c.content);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class Holder extends RecyclerView.ViewHolder {
        TextView tvHeader, tvContent;
        Holder(@NonNull View itemView) {
            super(itemView);
            tvHeader = itemView.findViewById(R.id.tvCommentHeader);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
        }
    }
}
