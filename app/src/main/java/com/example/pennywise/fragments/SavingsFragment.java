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

        RecyclerView rv = view.findViewById(R.id.rvSavings);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SavingsAdapter(savingsList);
        rv.setAdapter(adapter);

        loadSavings();

        EditText etName = view.findViewById(R.id.etPurpose);
        EditText etTarget = view.findViewById(R.id.etTarget);
        Button btnAdd = view.findViewById(R.id.btnAddSaving);

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString();
            String targetStr = etTarget.getText().toString();
            if (name.isEmpty() || targetStr.isEmpty()) return;

            float target = Float.parseFloat(targetStr);

            ContentValues val = new ContentValues();
            val.put(PennywiseContract.SavingsEntry.COLUMN_PURPOSE, name);
            val.put(PennywiseContract.SavingsEntry.COLUMN_TARGET, target);
            val.put(PennywiseContract.SavingsEntry.COLUMN_SAVED, 0f);
            val.put(PennywiseContract.SavingsEntry.COLUMN_CREATED_AT, System.currentTimeMillis() + "");

            ContentResolver resolver = requireContext().getContentResolver();
            resolver.insert(PennywiseContract.SavingsEntry.CONTENT_URI, val);

            savingsList.add(0, new SavingsGoal(name, target, 0f));
            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0);

            etName.setText("");
            etTarget.setText("");
        });

        return view;
    }

    private void loadSavings() {
        savingsList.clear();
        ContentResolver resolver = requireContext().getContentResolver();

        Cursor c = resolver.query(
                PennywiseContract.SavingsEntry.CONTENT_URI,
                null, null, null,
                PennywiseContract.SavingsEntry._ID + " DESC"
        );

        if (c != null && c.moveToFirst()) {
            do {
                String name = c.getString(c.getColumnIndexOrThrow(PennywiseContract.SavingsEntry.COLUMN_PURPOSE));
                float target = c.getFloat(c.getColumnIndexOrThrow(PennywiseContract.SavingsEntry.COLUMN_TARGET));
                float saved = c.getFloat(c.getColumnIndexOrThrow(PennywiseContract.SavingsEntry.COLUMN_SAVED));
                savingsList.add(new SavingsGoal(name, target, saved));
            } while (c.moveToNext());
            c.close();
        }
    }
}
