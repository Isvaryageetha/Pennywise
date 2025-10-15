package com.example.pennywise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.pennywise.R;
import com.example.pennywise.models.Bill;
import com.example.pennywise.models.Expense;
import com.example.pennywise.models.SavingsGoal;
import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private double threshold = 1000.0;
    private double balance = 1200.0;

    private List<Expense> expensesList = new ArrayList<>();
    private List<SavingsGoal> savingsList = new ArrayList<>();
    private List<Bill> billsList = new ArrayList<>();

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        TextView tvThreshold = view.findViewById(R.id.tvThreshold);
        TextView tvRemaining = view.findViewById(R.id.tvRemaining);

        tvThreshold.setText("Threshold: $" + threshold);
        double remaining = calculateBalance();
        tvRemaining.setText("Remaining: $" + remaining);

        return view;
    }

    private double calculateBalance() {
        double totalExpenses = 0;
        for(Expense e : expensesList) totalExpenses += e.getAmount();

        double totalSavings = 0;
        for(SavingsGoal s : savingsList) totalSavings += s.getSavedAmount();

        double totalBills = 0;
        for(Bill b : billsList) if(!b.isPaid()) totalBills += b.getAmount();

        return balance - (totalExpenses + totalSavings + totalBills);
    }
}
