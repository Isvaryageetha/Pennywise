package com.example.pennywise.fragments;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.PennywiseContract;
import com.example.pennywise.R;
import com.example.pennywise.adapters.BillsAdapter;
import com.example.pennywise.models.Bill;

import java.util.ArrayList;
import java.util.List;

public class BillsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 101;

    private List<Bill> billsList = new ArrayList<>();
    private BillsAdapter adapter;

    private Uri selectedImageUri;
    private ImageView ivPickImage;

    public BillsFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bills, container, false);

        RecyclerView rv = view.findViewById(R.id.rvBills);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new BillsAdapter(billsList);
        rv.setAdapter(adapter);

        EditText etName = view.findViewById(R.id.etBillName);
        EditText etAmount = view.findViewById(R.id.etBillAmount);
        Button btnAdd = view.findViewById(R.id.btnAddBill);

        ivPickImage = new ImageView(getContext());
        ivPickImage.setId(View.generateViewId());
        ivPickImage.setLayoutParams(new ViewGroup.LayoutParams(200, 200));
        ivPickImage.setImageResource(R.drawable.ic_placeholder);
        ivPickImage.setOnClickListener(v -> openImagePicker());
        // You should add this ImageView in your layout above EditTexts if not already present

        loadBills();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String amtStr = etAmount.getText().toString().trim();

            if (name.isEmpty() || amtStr.isEmpty()) return;

            double amount = Double.parseDouble(amtStr);

            ContentValues v1 = new ContentValues();
            v1.put(PennywiseContract.BillEntry.COLUMN_NAME, name);
            v1.put(PennywiseContract.BillEntry.COLUMN_AMOUNT, amount);
            v1.put(PennywiseContract.BillEntry.COLUMN_IS_PAID, 0);
            v1.put(PennywiseContract.BillEntry.COLUMN_CREATED_AT, System.currentTimeMillis() + "");

            if (selectedImageUri != null)
                v1.put("image_uri", selectedImageUri.toString()); // optional, if storing in DB

            ContentResolver resolver = requireContext().getContentResolver();
            resolver.insert(PennywiseContract.BillEntry.CONTENT_URI, v1);

            billsList.add(0, new Bill(name, amount, false, selectedImageUri));
            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0);

            etName.setText("");
            etAmount.setText("");
            selectedImageUri = null;
            ivPickImage.setImageResource(R.drawable.ic_placeholder);
        });

        return view;
    }

    private void loadBills() {
        billsList.clear();
        ContentResolver resolver = requireContext().getContentResolver();

        // query the bills table
        try (android.database.Cursor c = resolver.query(
                PennywiseContract.BillEntry.CONTENT_URI,
                null, null, null,
                PennywiseContract.BillEntry._ID + " DESC"
        )) {
            if (c != null && c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_NAME));
                    double amount = c.getDouble(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_AMOUNT));
                    int isPaid = c.getInt(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_IS_PAID));

                    String imageUriStr = null;
                    int imgIndex = c.getColumnIndex("image_uri");
                    if (imgIndex != -1) imageUriStr = c.getString(imgIndex);

                    Uri imageUri = (imageUriStr != null) ? Uri.parse(imageUriStr) : null;
                    billsList.add(new Bill(name, amount, isPaid == 1, imageUri));
                } while (c.moveToNext());
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Bill Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            ivPickImage.setImageURI(selectedImageUri);
        }
    }
}
