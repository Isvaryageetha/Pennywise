package com.example.pennywise.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.R;
import com.example.pennywise.adapters.ExpenseAdapter;
import com.example.pennywise.models.Expense;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

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

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();
        expensesRef = db.collection("Users").document(uid).collection("Expenses");

        RecyclerView rv = view.findViewById(R.id.rvExpenses);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ExpenseAdapter(expenseList);
        rv.setAdapter(adapter);

        EditText etTitle = view.findViewById(R.id.etPurpose);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAdd = view.findViewById(R.id.btnAddExpense);

        btnAdd.setOnClickListener(v -> {
            String title = etTitle.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (title.isEmpty() || amountStr.isEmpty()) {
                Toast.makeText(getContext(), "Enter title & amount", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Add expense to Firestore with server timestamp
            expensesRef.add(new java.util.HashMap<String, Object>() {{
                put("title", title);
                put("amount", amount);
                put("createdAt", FieldValue.serverTimestamp());
            }}).addOnSuccessListener(doc -> {
                Toast.makeText(getContext(), "Expense added", Toast.LENGTH_SHORT).show();
                etTitle.setText("");
                etAmount.setText("");
            }).addOnFailureListener(e ->
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
            );
        });

        loadExpenses();
        return view;
    }

    private void loadExpenses() {
        // Listen for real-time updates
        expensesRef.orderBy("createdAt").addSnapshotListener((snapshot, error) -> {
            if (error != null) return;

            List<Expense> tempList = new ArrayList<>();
            if (snapshot != null) {
                for (DocumentSnapshot doc : snapshot) {
                    Expense exp = Expense.fromFirestore(doc);
                    tempList.add(0, exp); // newest first
                }
            }

            adapter.updateList(tempList); // refresh RecyclerView
        });
    }
}
