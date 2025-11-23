package com.example.pennywise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.pennywise.R;
import com.example.pennywise.models.Expense;
import com.google.firebase.auth.FirebaseAuth;
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
        tvTotalExpenses = view.findViewById(R.id.tvBalance);

        db = FirebaseFirestore.getInstance();

        loadData();

        return view;
    }

    private void loadData() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Users")
                .document(uid)
                .collection("Expenses")
                .addSnapshotListener((snapshot, e) -> {

                    if (snapshot == null || e != null) return;

                    totalExpenses = 0;

                    for (QueryDocumentSnapshot doc : snapshot) {
                        Expense exp = doc.toObject(Expense.class);
                        totalExpenses += exp.getAmount();
                    }

                    updateUI();
                });
    }

    private void updateUI() {

        tvThreshold.setText("Threshold: ₹" + threshold);
        tvTotalExpenses.setText("Total Expenses: ₹" + totalExpenses);

        double remaining = threshold - totalExpenses;

        tvRemaining.setText("Remaining: ₹" + remaining);

        if (remaining < 0) {
            tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvRemaining.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }
    }
}
