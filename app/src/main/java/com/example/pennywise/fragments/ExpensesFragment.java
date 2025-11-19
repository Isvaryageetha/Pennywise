package com.example.pennywise.fragments;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
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
import com.example.pennywise.PennywiseContract;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        if (getActivity() instanceof OnDataPassListener)
            dataPassListener = (OnDataPassListener) getActivity();

        RecyclerView rvExpenses = view.findViewById(R.id.rvExpenses);
        rvExpenses.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExpenseAdapter(expenseList);
        rvExpenses.setAdapter(adapter);

        loadExpenses();

        setupInputFields(view);

        return view;
    }

    private void setupInputFields(View view) {
        EditText etPurpose = view.findViewById(R.id.etPurpose);
        EditText etAmount = view.findViewById(R.id.etAmount);
        Button btnAdd = view.findViewById(R.id.btnAddExpense);

        btnAdd.setOnClickListener(v -> handleAddExpense(etPurpose, etAmount));
    }

    private void handleAddExpense(EditText etPurpose, EditText etAmount) {
        String name = etPurpose.getText().toString().trim();
        String amountStr = etAmount.getText().toString().trim();

        if (name.isEmpty()) {
            etPurpose.setError("Enter purpose");
            return;
        }
        if (amountStr.isEmpty()) {
            etAmount.setError("Enter amount");
            return;
        }

        float amount;
        try {
            amount = Float.parseFloat(amountStr);
        } catch (Exception e) {
            etAmount.setError("Invalid amount");
            return;
        }

        String date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());

        ContentValues values = new ContentValues();
        values.put(PennywiseContract.ExpenseEntry.COLUMN_NAME, name);
        values.put(PennywiseContract.ExpenseEntry.COLUMN_AMOUNT, amount);
        values.put(PennywiseContract.ExpenseEntry.COLUMN_DATE, date);
        values.put(PennywiseContract.ExpenseEntry.COLUMN_CREATED_AT, date);

        Uri insertedUri = getContext().getContentResolver().insert(
                PennywiseContract.ExpenseEntry.CONTENT_URI, values
        );

        if (insertedUri != null) {
            expenseList.add(0, new Expense(name, amount, date));
            adapter.notifyItemInserted(0);

            if (dataPassListener != null)
                dataPassListener.onExpenseAdded(name, amount, date);

            Toast.makeText(getContext(), "Expense added", Toast.LENGTH_SHORT).show();
        }

        etPurpose.setText("");
        etAmount.setText("");
    }

    private void loadExpenses() {
        expenseList.clear();

        Cursor cursor = getContext().getContentResolver().query(
                PennywiseContract.ExpenseEntry.CONTENT_URI,
                null, null, null,
                PennywiseContract.ExpenseEntry._ID + " DESC"
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndexOrThrow(PennywiseContract.ExpenseEntry.COLUMN_NAME));
                float amount = cursor.getFloat(cursor.getColumnIndexOrThrow(PennywiseContract.ExpenseEntry.COLUMN_AMOUNT));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(PennywiseContract.ExpenseEntry.COLUMN_DATE));

                expenseList.add(new Expense(name, amount, date));
            }
            cursor.close();
        }

        if (adapter != null)
            adapter.notifyDataSetChanged();
    }
}
