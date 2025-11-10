package com.example.gallerycart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gallerycart.R;
import com.example.gallerycart.data.entity.Commission;

import java.util.List;

public class AdminCommissionAdapter extends RecyclerView.Adapter<AdminCommissionAdapter.CommissionViewHolder> {

    private List<Commission> commissions;

    public AdminCommissionAdapter(List<Commission> commissions) {
        this.commissions = commissions;
    }

    @NonNull
    @Override
    public CommissionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_commission, parent, false);
        return new CommissionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommissionViewHolder holder, int position) {
        Commission commission = commissions.get(position);
        holder.commissionId.setText("Commission ID: #" + commission.getCommissionId());
        holder.clientName.setText("Client ID: " + commission.getClientId());
        holder.artistName.setText("Artist ID: " + commission.getArtistId());
        holder.commissionStatus.setText("Status: " + commission.getStatus());
        holder.commissionPrice.setText("Price: $" + commission.getPrice());
    }

    @Override
    public int getItemCount() {
        return commissions.size();
    }

    static class CommissionViewHolder extends RecyclerView.ViewHolder {
        TextView commissionId, clientName, artistName, commissionStatus, commissionPrice;

        public CommissionViewHolder(@NonNull View itemView) {
            super(itemView);
            commissionId = itemView.findViewById(R.id.tvCommissionId);
            clientName = itemView.findViewById(R.id.tvClientName);
            artistName = itemView.findViewById(R.id.tvArtistName);
            commissionStatus = itemView.findViewById(R.id.tvCommissionStatus);
            commissionPrice = itemView.findViewById(R.id.tvCommissionPrice);
        }
    }
}
