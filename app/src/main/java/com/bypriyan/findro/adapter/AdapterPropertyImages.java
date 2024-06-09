package com.bypriyan.findro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.findro.R;
import com.bypriyan.findro.activity.FullImageView;
import com.bypriyan.findro.model.ModelSliderImg;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterPropertyImages extends RecyclerView.Adapter<AdapterPropertyImages.HolderRoom>  {

    public Context context;
    public ArrayList<ModelSliderImg> modelSliderImgArrayList;

    public AdapterPropertyImages(Context context, ArrayList<ModelSliderImg> modelSliderImgArrayList) {
        this.context = context;
        this.modelSliderImgArrayList = modelSliderImgArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public AdapterPropertyImages.HolderRoom onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_property_images, parent, false);

        return new HolderRoom(view);
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterPropertyImages.HolderRoom holder, int position) {

        ModelSliderImg modelSliderImg = modelSliderImgArrayList.get(position);
        String link = modelSliderImg.getLink();

        try {
            Glide.with(context).load(link).centerInside()
                    .placeholder(R.drawable.prop).into(holder.imageView);

        }catch (Exception e){

        }

    }

    @Override
    public int getItemCount() {
        return modelSliderImgArrayList.size();
    }

    class HolderRoom extends RecyclerView.ViewHolder {

        private ImageView imageView;

        public HolderRoom(@NonNull @NotNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.propImages);
        }
    }

}

