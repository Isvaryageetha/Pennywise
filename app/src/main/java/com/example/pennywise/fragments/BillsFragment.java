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
                double amount = Double.parseDouble(amountStr);
                billsList.add(0,new Bill(name, amount, false));
                adapter.notifyItemInserted(0);
                rvBills.scrollToPosition(0);
                etBillName.setText("");
                etBillAmount.setText("");
            }
        });

        return view;
    }
}
