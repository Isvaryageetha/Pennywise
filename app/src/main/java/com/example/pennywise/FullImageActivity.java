package com.example.pennywise;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class FullImageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);

        ImageView fullImage = findViewById(R.id.fullImageView);

        String imagePath = getIntent().getStringExtra("imageUrl");

        File imgFile = new File(imagePath);

        if (imgFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            fullImage.setImageBitmap(bitmap);
        } else {
            fullImage.setImageResource(android.R.drawable.ic_menu_report_image);
        }
    }
}
