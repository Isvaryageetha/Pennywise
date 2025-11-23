package com.example.pennywise.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pennywise.R;
import com.example.pennywise.adapters.ExpenseAdapter;
import com.example.pennywise.models.Expense;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExpenseFragment extends Fragment {

    private FirebaseFirestore db;
    private CollectionReference expensesRef;

    private List<Expense> expenseList = new ArrayList<>();
    private ExpenseAdapter adapter;

    public ExpenseFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        db = FirebaseFirestore.getInstance();
        expensesRef = db.collection("Expenses");

        RecyclerView rv = view.findViewById(R.id.rvExpenses);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter(expenseList);
        rv.setAdapter(adapter);

        // Matching your XML
        EditText etPurpose = view.findViewById(R.id.etPurpose);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAdd = view.findViewById(R.id.btnAddExpense);

        btnAdd.setOnClickListener(v -> {
            String purpose = etPurpose.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (purpose.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Please enter purpose and amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Firestore document
            Map<String, Object> expense = new HashMap<>();
            expense.put("purpose", purpose);
            expense.put("amount", amount);
            expense.put("createdAt", FieldValue.serverTimestamp());  // auto timestamp

            expensesRef.add(expense)
                    .addOnSuccessListener(doc -> {
                        Toast.makeText(getContext(), "Expense added", Toast.LENGTH_SHORT).show();
                        etPurpose.setText("");
                        etAmount.setText("");
                        loadExpenses();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        loadExpenses();
        return view;
    }

    private void loadExpenses() {
        expensesRef.orderBy("createdAt").addSnapshotListener((snapshot, error) -> {
            if (error != null) return;

            expenseList.clear();

            if (snapshot != null) {
                for (DocumentSnapshot doc : snapshot) {
                    Expense exp = Expense.fromFirestore(doc);
                    expenseList.add(0, exp); // newest first
                }
            }

            adapter.notifyDataSetChanged();
        });
    }
}
