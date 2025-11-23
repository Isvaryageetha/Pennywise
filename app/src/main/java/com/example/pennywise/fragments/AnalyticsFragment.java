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
import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private List<Expense> expensesList = new ArrayList<>();
    private List<SavingsGoal> savingsList = new ArrayList<>();

    public AnalyticsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_analytics, container, false);

        BarChart expenseChart = view.findViewById(R.id.barChartExpenses);
        BarChart savingsChart = view.findViewById(R.id.barChartSavings);

        // ----------------------------
        // Add Dummy Expense Data
        // ----------------------------
        expensesList.add(new Expense("Food", 150f, toTimestamp("2025-10-15")));
        expensesList.add(new Expense("Transport", 80f, toTimestamp("2025-10-15")));
        expensesList.add(new Expense("Books", 120f, toTimestamp("2025-10-15")));

        // ----------------------------
        // Add Dummy Savings Data
        // ----------------------------
        savingsList.add(new SavingsGoal("Phone", 500f, 200f));
        savingsList.add(new SavingsGoal("Laptop", 1000f, 100f));

        // ----------------------------
        // Prepare Expense Chart Entries
        // ----------------------------
        List<BarEntry> expenseEntries = new ArrayList<>();
        for (int i = 0; i < expensesList.size(); i++) {
            expenseEntries.add(new BarEntry(i, (float) expensesList.get(i).getAmount()));
        }

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        BarData expenseData = new BarData(expenseDataSet);
        expenseChart.setData(expenseData);
        expenseChart.invalidate();

        // ----------------------------
        // Prepare Savings Chart Entries
        // ----------------------------
        List<BarEntry> savingsEntries = new ArrayList<>();
        for (int i = 0; i < savingsList.size(); i++) {
            savingsEntries.add(new BarEntry(i, savingsList.get(i).getSavedAmount()));
        }

        BarDataSet savingsDataSet = new BarDataSet(savingsEntries, "Savings (Saved)");
        BarData savingsData = new BarData(savingsDataSet);
        savingsChart.setData(savingsData);
        savingsChart.invalidate();

        return view;
    }

    // ------------------------------------
    // Convert "2025-10-15" → Timestamp
    // ------------------------------------
    private Timestamp toTimestamp(String dateString) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateString);
            return new Timestamp(date);
        } catch (Exception e) {
            return Timestamp.now(); // fallback
        }
    }
}
