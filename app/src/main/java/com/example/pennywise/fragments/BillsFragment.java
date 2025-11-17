package com.example.pennywise.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
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
import com.example.pennywise.adapters.BillsAdapter;
import com.example.pennywise.interfaces.OnDataPassListener;
import com.example.pennywise.models.Bill;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private List<Bill> billsList = new ArrayList<>();
    private BillsAdapter adapter;
    private OnDataPassListener dataPassListener;

    public BillsFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Get the listener from the parent activity
        if (context instanceof OnDataPassListener) {
            dataPassListener = (OnDataPassListener) context;
        } else {
            throw new RuntimeException(context + " must implement OnDataPassListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        RecyclerView rvBills = view.findViewById(R.id.rvBills);
        rvBills.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BillsAdapter(billsList);
        rvBills.setAdapter(adapter);

        EditText etBillName = view.findViewById(R.id.etBillName);
        EditText etBillAmount = view.findViewById(R.id.etBillAmount);
        Button btnAdd = view.findViewById(R.id.btnAddBill);

        btnAdd.setOnClickListener(v -> {
            String name = etBillName.getText().toString();
            String amountStr = etBillAmount.getText().toString();

            if(!name.isEmpty() && !amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);

                    // Create new bill
                    Bill newBill = new Bill(name, amount, false);

                    // Add to local list
                    billsList.add(0, newBill);
                    adapter.notifyItemInserted(0);

                    // ✅ COMMUNICATION: Notify Activity about new bill
                    if (dataPassListener != null) {
                        dataPassListener.onBillAdded(name, amount, false);
                        dataPassListener.onDataUpdated(); // General update notification
                    }

                    rvBills.scrollToPosition(0);
                    etBillName.setText("");
                    etBillAmount.setText("");

                } catch (NumberFormatException e) {
                    Toast.makeText(getContext(), "Please enter a valid amount", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // Clean up reference to avoid memory leaks
        dataPassListener = null;
    }
}