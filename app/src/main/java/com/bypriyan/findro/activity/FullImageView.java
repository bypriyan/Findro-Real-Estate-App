package com.bypriyan.findro.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bypriyan.findro.R;
import com.github.chrisbanes.photoview.PhotoView;

public class FullImageView extends AppCompatActivity {

    private PhotoView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_image_view);

        imageView = findViewById(R.id.imageViews);

        String image = getIntent().getStringExtra("image");

        try {
            Glide.with(FullImageView.this).load(image).centerInside()
                    .placeholder(R.drawable.home).into(imageView);

        }catch (Exception e){

        }
    }
}