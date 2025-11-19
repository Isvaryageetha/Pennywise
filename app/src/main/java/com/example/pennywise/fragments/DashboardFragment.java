package com.example.pennywise.fragments;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.pennywise.PennywiseContract;
import com.example.pennywise.R;

public class DashboardFragment extends Fragment {

    private TextView tvThreshold, tvRemaining, tvBalance;
    private double threshold = 1000.0;
    private double balance = 0.0;

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvThreshold = view.findViewById(R.id.tvThreshold);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        tvBalance = view.findViewById(R.id.tvBalance);

        loadDashboardData();
        updateUI();

        return view;
    }

    private void loadDashboardData() {

        double totalExpenses = 0.0;

        // ====== GET TOTAL EXPENSES FROM PROVIDER ======
        Uri expenseUri = PennywiseContract.ExpenseEntry.CONTENT_URI;
        Cursor cursor = getActivity().getContentResolver().query(
                expenseUri,
                new String[]{"SUM(" + PennywiseContract.ExpenseEntry.COLUMN_AMOUNT + ") AS total"},
                null, null, null
        );

        if (cursor != null && cursor.moveToFirst()) {
            totalExpenses = cursor.getDouble(cursor.getColumnIndexOrThrow("total"));
            cursor.close();
        }

        // ====== CALCULATE BALANCE ======
        balance = 0 - totalExpenses;
    }

    private void updateUI() {
        if (getView() != null) {

            tvBalance.setText("Current Balance: ₹" + balance);
            tvThreshold.setText("Threshold: ₹" + threshold);

            double remaining = balance - threshold;
            tvRemaining.setText("Remaining: ₹" + remaining);

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
        loadDashboardData();
        updateUI();
    }

    // For MainActivity compatibility
    public void updateDashboardData(double newBalance, double newThreshold) {
        loadDashboardData();
        updateUI();
    }
}