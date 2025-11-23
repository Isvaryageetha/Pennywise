package com.example.pennywise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.R;
import com.example.pennywise.models.Expense;
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ViewHolder> {

    private List<Expense> expenseList;

    public ExpenseAdapter(List<Expense> expenseList) {
        this.expenseList = expenseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Expense e = expenseList.get(position);

        Timestamp ts = e.getCreatedAt();
        if (ts != null) {
            String date = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                    .format(ts.toDate());
            holder.tvDate.setText(date);
        } else {
            holder.tvDate.setText("—");
        }

        holder.tvPurpose.setText(e.getTitle());
        holder.tvAmount.setText("₹" + e.getAmount());
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    // ✅ Update the list and refresh RecyclerView
    public void updateList(List<Expense> newList) {
        expenseList.clear();
        expenseList.addAll(newList);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvPurpose, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }
    }
}
