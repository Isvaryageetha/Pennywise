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
import com.example.pennywise.adapters.SavingsAdapter;
import com.example.pennywise.models.SavingsGoal;

import java.util.ArrayList;
import java.util.List;

public class SavingsFragment extends Fragment {

    private List<SavingsGoal> savingsList = new ArrayList<>();
    private SavingsAdapter adapter;

    public SavingsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_savings, container, false);

        // RecyclerView setup
        RecyclerView rvSavings = view.findViewById(R.id.rvSavings);
        rvSavings.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavingsAdapter(savingsList);
        rvSavings.setAdapter(adapter);

        // Input fields
        EditText etName = view.findViewById(R.id.etPurpose); // Goal name, e.g., "New Phone"
        EditText etTarget = view.findViewById(R.id.etTarget); // Target amount
        Button btnAdd = view.findViewById(R.id.btnAddSaving);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String targetStr = etTarget.getText().toString();

            if (!name.isEmpty() && !targetStr.isEmpty()) {
                float target = Float.parseFloat(targetStr);

                // Add new savings goal at the top
                savingsList.add(0, new SavingsGoal(name, target, 0f));
                adapter.notifyItemInserted(0);
                rvSavings.scrollToPosition(0);

                // Clear input fields
                etName.setText("");
                etTarget.setText("");
            }
        });

        return view;
    }
}
