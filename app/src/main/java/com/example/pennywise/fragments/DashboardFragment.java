package com.example.pennywise.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.pennywise.R;
public class DashboardFragment extends Fragment {
    private TextView tvThreshold, tvRemaining, tvBalance;
    private double threshold = 1000.0;
    private double balance = 1200.0;
    public DashboardFragment() {}
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // Initialize views
        tvThreshold = view.findViewById(R.id.tvThreshold);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        tvBalance = view.findViewById(R.id.tvBalance);

        // Get initial data from MainActivity
        if (getActivity() instanceof com.example.pennywise.MainActivity) {
            com.example.pennywise.MainActivity mainActivity = (com.example.pennywise.MainActivity) getActivity();
            threshold = mainActivity.getBalanceThreshold();
            balance = mainActivity.getCurrentBalance();
        }

        updateUI();

        return view;
    }

    // This method will be called by MainActivity when data changes
    public void updateDashboardData(double newBalance, double newThreshold) {
        this.balance = newBalance;
        this.threshold = newThreshold;
        updateUI();
    }

    private void updateUI() {
        if (getView() != null) {
            tvThreshold.setText("Threshold: $" + threshold);
            tvBalance.setText("Current Balance: $" + balance);

            double remaining = balance - threshold;
            tvRemaining.setText("Remaining: $" + remaining);

            // Change color if below threshold
            if (remaining < 0) {
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
            } else {
                tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh data when fragment becomes visible
        if (getActivity() instanceof com.example.pennywise.MainActivity) {
            com.example.pennywise.MainActivity mainActivity = (com.example.pennywise.MainActivity) getActivity();
            updateDashboardData(mainActivity.getCurrentBalance(), mainActivity.getBalanceThreshold());
        }
    }
}