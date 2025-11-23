package com.example.pennywise.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.R;
import com.example.pennywise.models.Bill;

import java.util.List;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.ViewHolder> {

    private List<Bill> billsList;

    public BillsAdapter(List<Bill> billsList) {
        this.billsList = billsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = billsList.get(position);
        holder.tvName.setText(bill.getName());
        holder.tvAmount.setText("$" + bill.getAmount());
        holder.cbPaid.setChecked(bill.isPaid());
        holder.cbPaid.setOnCheckedChangeListener((buttonView, isChecked) -> bill.setPaid(isChecked));

        // Display bill image if available
        Uri imageUri = bill.getImageUri();
        if (imageUri != null) {
            holder.ivBillImage.setImageURI(imageUri);
        } else {
            holder.ivBillImage.setImageResource(R.drawable.ic_placeholder); // default placeholder
        }
    }

    @Override
    public int getItemCount() {
        return billsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        CheckBox cbPaid;
        ImageView ivBillImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBillName);
            tvAmount = itemView.findViewById(R.id.tvBillAmount);
            cbPaid = itemView.findViewById(R.id.cbPaid);
            ivBillImage = itemView.findViewById(R.id.ivBillImage);
        }
    }
}
