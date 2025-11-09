package com.example.gallerycart.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gallerycart.R;
import com.example.gallerycart.data.entity.Commission;
import java.util.List;

public class CommissionAdapter extends RecyclerView.Adapter<CommissionAdapter.CommissionViewHolder> {

    private Context context;
    private List<Commission> commissions;

    public CommissionAdapter(Context context) {
        this.context = context;
    }

    public void setCommissions(List<Commission> commissions) {
        this.commissions = commissions;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CommissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.commission_item, parent, false);
        return new CommissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionViewHolder holder, int position) {
        Commission commission = commissions.get(position);
        holder.tvDescription.setText(commission.getDescription());
        holder.tvStatus.setText(commission.getStatus());
        holder.tvPrice.setText(String.format("%.2f VND", commission.getPrice()));

        if (commission.getFilePath() != null && !commission.getFilePath().isEmpty()) {
            holder.btnDownload.setVisibility(View.VISIBLE);
            holder.btnDownload.setOnClickListener(v -> {
                // TODO: Implement download logic
                Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show();
            });
        } else {
            holder.btnDownload.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return commissions != null ? commissions.size() : 0;
    }

    public static class CommissionViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescription, tvStatus, tvPrice;
        Button btnDownload;

        public CommissionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnDownload = itemView.findViewById(R.id.btnDownload);
        }
    }
}
