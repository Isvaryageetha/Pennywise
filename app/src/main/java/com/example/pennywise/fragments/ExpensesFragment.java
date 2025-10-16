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

    public ExpensesFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        // Get the dataPassListener from Activity
        try {
            dataPassListener = (OnDataPassListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement OnDataPassListener");
        }

        // RecyclerView setup
        RecyclerView rvExpenses = view.findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(expenseList);
        rvExpenses.setAdapter(adapter);

        // Input fields
        EditText etPurpose = view.findViewById(R.id.etPurpose);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAdd = view.findViewById(R.id.btnAddExpense);

        btnAdd.setOnClickListener(v -> {
            String name = etPurpose.getText().toString();
            String amountStr = etAmount.getText().toString();

            if (!name.isEmpty() && !amountStr.isEmpty()) {
                float amount = Float.parseFloat(amountStr);
                String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

                // Add to local list
                expenseList.add(0, new Expense(name, amount, date));
                adapter.notifyItemInserted(0);
                rvExpenses.scrollToPosition(0);

                // Communicate with Activity
                if (dataPassListener != null) {
                    dataPassListener.onExpenseAdded(name, amount, date);
                }

                // Clear input fields
                etPurpose.setText("");
                etAmount.setText("");
            }
        });

        return view;
    }
}