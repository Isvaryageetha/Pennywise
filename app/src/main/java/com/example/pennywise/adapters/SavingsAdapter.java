package com.example.pennywise.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.pennywise.R;
import com.example.pennywise.models.SavingsGoal;
import java.util.List;

public class SavingsAdapter extends RecyclerView.Adapter<SavingsAdapter.ViewHolder> {
    private List<SavingsGoal> savingsList;
    public SavingsAdapter(List<SavingsGoal> savingsList) { this.savingsList = savingsList; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_saving, parent, false); // ensure file name is item_saving.xml
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SavingsGoal goal = savingsList.get(position);

        holder.tvPurpose.setText(goal.getPurpose());
        holder.tvAmount.setText("$" + goal.getSavedAmount() + " / $" + goal.getTargetAmount());

        // Safe progress calculation (avoid division by zero and NaN)
        int progress = 0;
        float target = goal.getTargetAmount();
        float saved = goal.getSavedAmount();

        if (target > 0f) {
            float frac = saved / target;
            if (!Float.isNaN(frac) && !Float.isInfinite(frac)) {
                if (frac < 0f) frac = 0f;
                if (frac > 1f) frac = 1f;
                progress = (int) (frac * 100f);
            }
        }

        holder.progressBar.setMax(100);
        holder.progressBar.setProgress(progress);
    }

    @Override
    public int getItemCount() { return savingsList.size(); }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPurpose, tvAmount;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPurpose = itemView.findViewById(R.id.tvPurpose);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            progressBar = itemView.findViewById(R.id.progressBar);
        }
    }
}
