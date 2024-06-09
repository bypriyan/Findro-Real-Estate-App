package com.bypriyan.findro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.activity.PropertyDetails;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.model.ModelSliderImg;
import com.bypriyan.findro.register.SelectActivity;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterRooms extends RecyclerView.Adapter<AdapterRooms.HolderRoom> {

    public Context context;
    public ArrayList<ModelRooms> modelRoomsArrayList;
    private FirebaseAuth firebaseAuth;

    public AdapterRooms(Context context, ArrayList<ModelRooms> modelRoomsArrayList) {
        this.context = context;
        this.modelRoomsArrayList = modelRoomsArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public HolderRoom onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_rooms, parent, false);

        return new HolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRooms.HolderRoom holder, int position) {

        ModelRooms modelRooms = modelRoomsArrayList.get(position);
        String propertyType = modelRooms.getPropertyType();
        String address = modelRooms.getAddress();
        String monthlyRent= modelRooms.getMonthlyRent();
        String id = modelRooms.getId();
        String uid = modelRooms.getUid();
        String city= modelRooms.getCity();
        String state = modelRooms.getState();
        String seekingA = modelRooms.getSeekingA();
        String by = modelRooms.getiAm();

        holder.propertyTypeTv.setText(propertyType);
        holder.addressTv.setText(address);
        holder.priseTv.setText(String.format("₹%s", monthlyRent));


        final String[] link = {""};
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).child(id).child(Constant.KEY_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren()){
                            link[0] = ds.child("link").getValue().toString();
                            break;
                        }

                        try {
                            Glide.with(context).load(link[0]).centerCrop()
                                    .placeholder(R.drawable.prop).into(holder.mainSlider);

                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUserLogin()){
                    startActiv(id, uid);
                }else{
                    showDialog();
                }

            }
        });

        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUserLogin()){
                    startActiv(id, uid);
                }else{
                    showDialog();
                }
            }
        });

    }

    private void showDialog( ) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_oops, null);
        TextView okay = view.findViewById(R.id.okay);
        TextView later = view.findViewById(R.id.later);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, SelectActivity.class));
                ((Activity)context).finish();
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private boolean checkUserLogin(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null){
            return true;
        }else{
            return false;
        }
    }

    private void startActiv(String id, String uid){
        Intent intent = new Intent(context, PropertyDetails.class);
        intent.putExtra(Constant.KEY_ID, id);
        intent.putExtra(Constant.KEY_UID , uid);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return modelRoomsArrayList.size();
    }

    class HolderRoom extends RecyclerView.ViewHolder {

        TextView propertyTypeTv, addressTv, priseTv;
        ImageView mainSlider;
        FrameLayout frameLayout;

        public HolderRoom(@NonNull @NotNull View itemView) {
            super(itemView);
            propertyTypeTv = itemView.findViewById(R.id.propertyTypeTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            priseTv = itemView.findViewById(R.id.priseTv);
            mainSlider = itemView.findViewById(R.id.image_slider);
            frameLayout = itemView.findViewById(R.id.frame);

        }
    }

}
