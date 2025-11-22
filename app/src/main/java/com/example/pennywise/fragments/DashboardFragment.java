package com.example.pennywise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pennywise.R;
import com.example.pennywise.models.Expense;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class DashboardFragment extends Fragment {

    private TextView tvThreshold, tvRemaining, tvTotalExpenses;
    private double threshold = 1000.0;
    private double totalExpenses = 0.0;

    private FirebaseFirestore db;

    public DashboardFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        tvThreshold = view.findViewById(R.id.tvThreshold);
        tvRemaining = view.findViewById(R.id.tvRemaining);
        tvTotalExpenses = view.findViewById(R.id.tvBalance); // rename in XML if needed

        db = FirebaseFirestore.getInstance();

        loadDataFromFirestore();

        return view;
    }

    // =============== LOAD EXPENSES FROM FIRESTORE ===============
    private void loadDataFromFirestore() {

        db.collection("Expenses")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    totalExpenses = 0.0;

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Expense exp = doc.toObject(Expense.class);
                        totalExpenses += exp.getAmount();
                    }

                    updateUI();
                })
                .addOnFailureListener(e -> {
                    tvTotalExpenses.setText("Failed to load");
                });
    }

    // =============== UPDATE DASHBOARD UI ==========================
    private void updateUI() {

        tvTotalExpenses.setText("Total Expenses: ₹" + totalExpenses);
        tvThreshold.setText("Threshold: ₹" + threshold);

        double remaining = threshold - totalExpenses;
        tvRemaining.setText("Remaining: ₹" + remaining);

        if (remaining < 0) {
            tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataFromFirestore(); // refresh
    }
}
