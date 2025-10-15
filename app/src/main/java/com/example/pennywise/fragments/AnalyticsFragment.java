package com.example.pennywise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.pennywise.R;
import com.example.pennywise.models.Expense;
import com.example.pennywise.models.SavingsGoal;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private List<Expense> expensesList = new ArrayList<>();
    private List<SavingsGoal> savingsList = new ArrayList<>();

    public AnalyticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        // Find chart views
        BarChart expenseChart = view.findViewById(R.id.barChartExpenses);
        BarChart savingsChart = view.findViewById(R.id.barChartSavings);

        // Sample data — explicit float literals
        expensesList.add(new Expense("Food", 150f, "2025-10-15"));
        expensesList.add(new Expense("Transport", 80f, "2025-10-15"));
        expensesList.add(new Expense("Books", 120f, "2025-10-15"));

        savingsList.add(new SavingsGoal("Phone", 500f, 200f));
        savingsList.add(new SavingsGoal("Laptop", 1000f, 100f));

        // Prepare expense entries
        List<BarEntry> expenseEntries = new ArrayList<>();
        for (int i = 0; i < expensesList.size(); i++) {
            expenseEntries.add(new BarEntry((float) i, expensesList.get(i).getAmount()));
        }

        // Prepare savings entries (use savedAmount)
        List<BarEntry> savingsEntries = new ArrayList<>();
        for (int i = 0; i < savingsList.size(); i++) {
            savingsEntries.add(new BarEntry((float) i, savingsList.get(i).getSavedAmount()));
        }

        // Expense chart
        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        BarData expenseData = new BarData(expenseDataSet);
        expenseChart.setData(expenseData);
        expenseChart.invalidate();

        // Savings chart
        BarDataSet savingsDataSet = new BarDataSet(savingsEntries, "Savings (saved)");
        BarData savingsData = new BarData(savingsDataSet);
        savingsChart.setData(savingsData);
        savingsChart.invalidate();

        return view;
    }
}
