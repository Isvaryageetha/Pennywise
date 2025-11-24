package com.example.pennywise.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pennywise.FullImageActivity;
import com.example.pennywise.PennywiseContract;
import com.example.pennywise.R;
import com.example.pennywise.models.Bill;

import java.io.File;
import java.util.List;

public class BillsAdapter extends RecyclerView.Adapter<BillsAdapter.ViewHolder> {

    private final Context context;
    private final List<Bill> billsList;

    public BillsAdapter(Context context, List<Bill> billsList) {
        this.context = context;
        this.billsList = billsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bill, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Bill bill = billsList.get(position);

        holder.tvName.setText(bill.getName());
        holder.tvAmount.setText("₹" + bill.getAmount());
        holder.cbPaid.setChecked(bill.isPaid());

        Uri imageUri = bill.getImageUri();

        // Load image from internal storage
        if (imageUri != null) {
            File imgFile = new File(imageUri.getPath());
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.ivBillImage.setImageBitmap(bitmap);
            } else {
                holder.ivBillImage.setImageResource(android.R.drawable.ic_menu_report_image);
            }
        } else {
            holder.ivBillImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }

        // Open full image
        holder.ivBillImage.setOnClickListener(v -> {
            if (imageUri != null) {
                Intent intent = new Intent(context, FullImageActivity.class);
                intent.putExtra("imageUrl", imageUri.toString());
                context.startActivity(intent);
            }
        });

        // Delete on long press
        holder.itemView.setOnLongClickListener(v -> {
            deleteBill(bill, holder.getAdapterPosition());
            return true;
        });
    }

    private void deleteBill(Bill bill, int position) {
        try {
            if (bill.getId() > 0) {
                Uri deleteUri = Uri.withAppendedPath(
                        PennywiseContract.BillEntry.CONTENT_URI,
                        String.valueOf(bill.getId())
                );

                int rows = context.getContentResolver().delete(deleteUri, null, null);

                if (rows > 0) {
                    billsList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Bill deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Delete failed: ID not found", Toast.LENGTH_SHORT).show();
                }
            } else {
                // Dummy bill (not in DB)
                billsList.remove(position);
                notifyItemRemoved(position);
                Toast.makeText(context, "Bill removed", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return billsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvAmount;
        CheckBox cbPaid;
        ImageView ivBillImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvBillName);
            tvAmount = itemView.findViewById(R.id.tvBillAmount);
            cbPaid = itemView.findViewById(R.id.cbPaid);
            ivBillImage = itemView.findViewById(R.id.ivBillImage);
        }
    }
}
