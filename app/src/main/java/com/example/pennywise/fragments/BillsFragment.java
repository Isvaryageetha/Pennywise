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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.PennywiseContract;
import com.example.pennywise.R;
import com.example.pennywise.adapters.BillsAdapter;
import com.example.pennywise.models.Bill;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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

        adapter = new BillsAdapter(requireContext(), billsList);
        rv.setAdapter(adapter);

        EditText etName = view.findViewById(R.id.etBillName);
        EditText etAmount = view.findViewById(R.id.etBillAmount);
        Button btnAdd = view.findViewById(R.id.btnAddBill);
        ivPickImage = view.findViewById(R.id.ivPickImage);

        ivPickImage.setOnClickListener(v -> openImagePicker());

        loadBills();

        btnAdd.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String amtStr = etAmount.getText().toString().trim();

            if (name.isEmpty() || amtStr.isEmpty()) return;

            double amount = Double.parseDouble(amtStr);

            ContentValues values = new ContentValues();
            values.put(PennywiseContract.BillEntry.COLUMN_NAME, name);
            values.put(PennywiseContract.BillEntry.COLUMN_AMOUNT, amount);
            values.put(PennywiseContract.BillEntry.COLUMN_IS_PAID, 0);
            values.put(PennywiseContract.BillEntry.COLUMN_DUE_DATE, System.currentTimeMillis());
            values.put(PennywiseContract.BillEntry.COLUMN_CREATED_AT, System.currentTimeMillis() + "");

            String savedImagePath = null;

            if (selectedImageUri != null) {
                savedImagePath = saveImageToInternalStorage(selectedImageUri);
                values.put("image_uri", savedImagePath);
            }

            ContentResolver resolver = requireContext().getContentResolver();
            resolver.insert(PennywiseContract.BillEntry.CONTENT_URI, values);

            billsList.add(0, new Bill(name, amount, false,
                    savedImagePath != null ? Uri.parse(savedImagePath) : null));

            adapter.notifyItemInserted(0);
            rv.scrollToPosition(0);

            etName.setText("");
            etAmount.setText("");
            selectedImageUri = null;
            ivPickImage.setImageResource(android.R.drawable.ic_menu_report_image);
        });

        return view;
    }


    // ⬇⬇ SAVE IMAGE TO INTERNAL STORAGE HERE ⬇⬇
    private String saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(imageUri);

            File directory = new File(requireContext().getFilesDir(), "bill_images");
            if (!directory.exists()) directory.mkdir();

            File file = new File(directory, System.currentTimeMillis() + ".jpg");
            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            inputStream.close();
            outputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // ⬆⬆ END OF SAVE FUNCTION ⬆⬆


    private void loadBills() {
        billsList.clear();
        ContentResolver resolver = requireContext().getContentResolver();

        try (android.database.Cursor c = resolver.query(
                PennywiseContract.BillEntry.CONTENT_URI,
                null, null, null,
                PennywiseContract.BillEntry._ID + " DESC"
        )) {
            if (c != null && c.moveToFirst()) {
                do {
                    String name = c.getString(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_NAME));
                    double amount = c.getDouble(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_AMOUNT));
                    boolean isPaid = c.getInt(c.getColumnIndexOrThrow(PennywiseContract.BillEntry.COLUMN_IS_PAID)) == 1;

                    String imagePath = c.getString(c.getColumnIndexOrThrow("image_uri"));

                    Uri imgUri = imagePath != null ? Uri.parse(imagePath) : null;

                    billsList.add(new Bill(name, amount, isPaid, imgUri));
                } while (c.moveToNext());
            }
        }

        adapter.notifyDataSetChanged();
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
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
