package com.bypriyan.findro.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
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
import com.bypriyan.findro.filter.FilterRooms;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.register.SelectActivity;
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

public class AdapterRoomsVertical extends RecyclerView.Adapter<AdapterRoomsVertical.HolderRoom> implements Filterable {

    public Context context;
    public ArrayList<ModelRooms> modelRoomsArrayList;
    public ArrayList<ModelRooms> filterList;
    private FilterRooms filter;
    private FirebaseAuth firebaseAuth;

    public AdapterRoomsVertical(Context context, ArrayList<ModelRooms> modelRoomsArrayList) {
        this.context = context;
        this.modelRoomsArrayList = modelRoomsArrayList;
        this.filterList = modelRoomsArrayList;
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @NotNull
    @Override
    public HolderRoom onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_rooms_vertical, parent, false);

        return new HolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterRoomsVertical.HolderRoom holder, int position) {

        ModelRooms modelRooms = modelRoomsArrayList.get(position);
        String propertyType = modelRooms.getPropertyType();
        String address = modelRooms.getAddress();
        String monthlyRent= modelRooms.getMonthlyRent();
        String id = modelRooms.getId();
        String latitude= modelRooms.getLatitude();
        String area = modelRooms.getArea();
        String uid = modelRooms.getUid();
        String longitude = modelRooms.getLongitude();

        holder.propertyTypeTv.setText(propertyType);
        holder.addressTv.setText(address);
        holder.priseTv.setText("₹ "+monthlyRent);
        holder.sqftTv.setText(area+"sq.ft");

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



        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUserLogin()){
                    startActiv(id, uid);
                }else{
                    showDialog();
                }

            }
        });

        holder.contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkUserLogin()){
                    openMap(latitude, longitude);
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

    private void openMap(String destinationLatitude, String destinationLongitude) {
        String address = "https://maps.google.com/maps?saddr=" + "&daddr=" + destinationLatitude + "," + destinationLongitude;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
        context.startActivity(intent);

    }

    private void startActiv(String id, String uid) {
        Intent intent = new Intent(context, PropertyDetails.class);
        intent.putExtra(Constant.KEY_ID, id);
        intent.putExtra(Constant.KEY_UID, uid);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return modelRoomsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new FilterRooms(this, filterList);
        }
        return filter;
    }


    class HolderRoom extends RecyclerView.ViewHolder {

        TextView propertyTypeTv, addressTv, priseTv, details, sqftTv;
        ImageView mainSlider;
        FrameLayout frameLayout;
        ImageView contect;

        public HolderRoom(@NonNull @NotNull View itemView) {
            super(itemView);
            propertyTypeTv = itemView.findViewById(R.id.propertyTypeTv);
            addressTv = itemView.findViewById(R.id.addressTv);
            priseTv = itemView.findViewById(R.id.priseTv);
            details = itemView.findViewById(R.id.details);
            contect = itemView.findViewById(R.id.contect);
            sqftTv = itemView.findViewById(R.id.sqftTv);
            frameLayout = itemView.findViewById(R.id.frame);
            mainSlider = itemView.findViewById(R.id.img_prop);

        }
    }

}