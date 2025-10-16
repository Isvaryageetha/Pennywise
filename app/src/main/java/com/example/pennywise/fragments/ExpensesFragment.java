package com.example.pennywise.fragments;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import com.example.pennywise.R;
import com.example.pennywise.adapters.ExpenseAdapter;
import com.example.pennywise.interfaces.OnDataPassListener;
import com.example.pennywise.models.Expense;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ExpensesFragment extends Fragment {

    private List<Expense> expenseList = new ArrayList<>();
    private ExpenseAdapter adapter;
    private OnDataPassListener dataPassListener;
    private static final String TAG = "ExpensesFragment";

    public ExpensesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        Log.d(TAG, "ExpensesFragment onCreateView started");

        try {
            // Safe way to get the dataPassListener from Activity
            if (getActivity() instanceof OnDataPassListener) {
                dataPassListener = (OnDataPassListener) getActivity();
                Log.d(TAG, "DataPassListener connected successfully");
            } else {
                Log.w(TAG, "Activity does not implement OnDataPassListener");
                // Don't throw exception, just show warning and continue
                Toast.makeText(getContext(), "Communication with main app limited", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up dataPassListener: " + e.getMessage());
        }

        // RecyclerView setup with error handling
        try {
            RecyclerView rvExpenses = view.findViewById(R.id.rvExpenses);
            if (rvExpenses == null) {
                throw new RuntimeException("RecyclerView not found with ID: rvExpenses");
            }

            rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ExpenseAdapter(expenseList);
            rvExpenses.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up RecyclerView: " + e.getMessage());
            Toast.makeText(getContext(), "Error setting up expenses list", Toast.LENGTH_LONG).show();
        }

        // Input fields setup
        setupInputFields(view);

        return view;
    }

    private void setupInputFields(View view) {
        try {
            EditText etPurpose = view.findViewById(R.id.etPurpose);
            EditText etAmount = view.findViewById(R.id.etAmount);
            Button btnAdd = view.findViewById(R.id.btnAddExpense);

            if (etPurpose == null || etAmount == null || btnAdd == null) {
                throw new RuntimeException("One or more input fields not found");
            }

            btnAdd.setOnClickListener(v -> {
                handleAddExpense(etPurpose, etAmount);
            });

            Log.d(TAG, "Input fields setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up input fields: " + e.getMessage());
            Toast.makeText(getContext(), "Error setting up input fields", Toast.LENGTH_LONG).show();
        }
    }
    private void handleAddExpense(EditText etPurpose, EditText etAmount) {
        try {
            String name = etPurpose.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();

            if (name.isEmpty()) {
                etPurpose.setError("Please enter purpose");
                return;
            }
            if (amountStr.isEmpty()) {
                etAmount.setError("Please enter amount");
                return;
            }
            float amount;
            try {
                amount = Float.parseFloat(amountStr);
            } catch (NumberFormatException e) {
                etAmount.setError("Please enter a valid amount");
                return;
            }

            if (amount <= 0) {
                etAmount.setError("Amount must be greater than 0");
                return;
            }

            String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

            // Add to local list
            expenseList.add(0, new Expense(name, amount, date));
            if (adapter != null) {
                adapter.notifyItemInserted(0);
            }

            // Scroll to top if RecyclerView exists
            View view = getView();
            if (view != null) {
                RecyclerView rvExpenses = view.findViewById(R.id.rvExpenses);
                if (rvExpenses != null) {
                    rvExpenses.scrollToPosition(0);
                }
            }

            // Communicate with Activity
            if (dataPassListener != null) {
                dataPassListener.onExpenseAdded(name, amount, date);
                Toast.makeText(getContext(), "Expense added: " + name, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), "Expense added locally", Toast.LENGTH_SHORT).show();
            }

            // Clear input fields
            etPurpose.setText("");
            etAmount.setText("");

        } catch (Exception e) {
            Log.e(TAG, "Error adding expense: " + e.getMessage());
            Toast.makeText(getContext(), "Error adding expense", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "ExpensesFragment resumed");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "ExpensesFragment paused");
    }
}