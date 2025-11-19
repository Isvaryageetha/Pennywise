package com.example.pennywise.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.pennywise.PennywiseContract;
import com.example.pennywise.R;
import com.example.pennywise.adapters.BillsAdapter;
import com.example.pennywise.models.Bill;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private List<Bill> billsList = new ArrayList<>();
    private BillsAdapter adapter;

    public BillsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        RecyclerView rv = view.findViewById(R.id.rvBills);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BillsAdapter(billsList);
        rv.setAdapter(adapter);

        loadBills();

        EditText etName = view.findViewById(R.id.etBillName);
        EditText etAmount = view.findViewById(R.id.etBillAmount);
        Button btnAdd = view.findViewById(R.id.btnAddBill);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String amtStr = etAmount.getText().toString();
            if (name.isEmpty() || amtStr.isEmpty()) return;

            double amount = Double.parseDouble(amtStr);

            ContentValues v1 = new ContentValues();
            v1.put(PennywiseContract.BillEntry.COLUMN_NAME, name);
            v1.put(PennywiseContract.BillEntry.COLUMN_AMOUNT, amount);
            v1.put(PennywiseContract.BillEntry.COLUMN_IS_PAID, 0);
            v1.put(PennywiseContract.BillEntry.COLUMN_CREATED_AT, System.currentTimeMillis() + "");

            ContentResolver resolver = requireContext().getContentResolver();
            resolver.insert(PennywiseContract.BillEntry.CONTENT_URI, v1);

            billsList.add(0, new Bill(name, amount, false));
            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0);

            etName.setText("");
            etAmount.setText("");
        });

        return view;
    }

    private void loadBills() {
        billsList.clear();
        ContentResolver resolver = requireContext().getContentResolver();

        Cursor c = resolver.query(
                PennywiseContract.BillEntry.CONTENT_URI,
                null, null, null,
                PennywiseContract.BillEntry._ID + " DESC"
        );

        if (c != null && c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_NAME));
                double amount = c.getDouble(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_AMOUNT));
                int isPaid = c.getInt(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_IS_PAID));
                billsList.add(new Bill(name, amount, isPaid == 1));
            } while (c.moveToNext());
            c.close();
        }
    }
}
