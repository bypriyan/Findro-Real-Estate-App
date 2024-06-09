package com.bypriyan.findro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.activity.FullImageView;
import com.bypriyan.findro.activity.PropertyDetails;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.model.ModelSliderImg;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterGalleryImage extends RecyclerView.Adapter<AdapterGalleryImage.HolderRoom> {

    public Context context;
    public ArrayList<ModelSliderImg> modelSliderImgArrayList;

    public AdapterGalleryImage(Context context, ArrayList<ModelSliderImg> modelSliderImgArrayList) {
        this.context = context;
        this.modelSliderImgArrayList = modelSliderImgArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderRoom onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_gallery_image, parent, false);

        return new HolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterGalleryImage.HolderRoom holder, int position) {

        ModelSliderImg modelSliderImg = modelSliderImgArrayList.get(position);
        String link = modelSliderImg.getLink();

        try {
            Glide.with(context).load(link).centerInside()
                    .placeholder(R.drawable.prop).into(holder.imageView);

        }catch (Exception e){

        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullImageView.class);
                intent.putExtra("image", link);
                context.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return modelSliderImgArrayList.size();
    }

    class HolderRoom extends RecyclerView.ViewHolder {

        private RoundedImageView imageView;

        public HolderRoom(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageProperty);
        }
    }

}
