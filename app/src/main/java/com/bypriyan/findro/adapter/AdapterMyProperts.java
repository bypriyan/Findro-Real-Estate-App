package com.bypriyan.findro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.activity.EditPropertyActivity;
import com.bypriyan.findro.activity.PropertyDetails;
import com.bypriyan.findro.filter.FilterRooms;
import com.bypriyan.findro.model.ModelRooms;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AdapterMyProperts extends RecyclerView.Adapter<AdapterMyProperts.HolderRoom> {

    public Context context;
    public ArrayList<ModelRooms> modelRoomsArrayList;
    public ArrayList<ModelRooms> filterList;
    private FilterRooms filter;

    public AdapterMyProperts(Context context, ArrayList<ModelRooms> modelRoomsArrayList) {
        this.context = context;
        this.modelRoomsArrayList = modelRoomsArrayList;
        this.filterList = modelRoomsArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public HolderRoom onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_my_property, parent, false);

        return new HolderRoom(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterMyProperts.HolderRoom holder, int position) {

        ModelRooms modelRooms = modelRoomsArrayList.get(position);

        String propertyType = modelRooms.getPropertyType();
        String id = modelRooms.getId();
        String address = modelRooms.getAddress();
        String monthlyRent= modelRooms.getMonthlyRent();
        String uid = modelRooms.getUid();

        holder.propertyTypeTv.setText(propertyType);
        holder.addressTv.setText(address);
        holder.priseTv.setText(monthlyRent+"/INR");

        final List<SlideModel> remoteImage = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child(Constant.KEY_PROPERTIES).child(id).child(Constant.KEY_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        for(DataSnapshot ds: snapshot.getChildren())
                            remoteImage.add(new SlideModel(ds.child("link").getValue().toString(), ScaleTypes.CENTER_CROP));

                        holder.mainSlider.setImageList(remoteImage, ScaleTypes.CENTER_CROP);

                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });


        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(id, propertyType);
            }
        });

        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditPropertyActivity.class);
                intent.putExtra(Constant.KEY_ID, id);
                intent.putExtra(Constant.KEY_UID , uid);
                context.startActivity(intent);
            }
        });

        holder.details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv(id, uid);
            }
        });

    }

    private void showDialog(String id, String propertyType) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_deleting, null);
        TextView textDialog = view.findViewById(R.id.textDialog);
        TextView cancel = view.findViewById(R.id.cancel);
        MaterialButton delete = view.findViewById(R.id.delete);

        textDialog.setText("Are you sure you want to delete this "+propertyType.toUpperCase()+"?");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
                reference.child(id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(context, "Property deleted successfully", Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(context, ""+e, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
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

        TextView propertyTypeTv, addressTv, priseTv, details;
        ImageView deleteBtn ,editBtn;
        ImageSlider mainSlider;

        public HolderRoom(@NonNull @NotNull View itemView) {
            super(itemView);
            propertyTypeTv = itemView.findViewById(R.id.propertyTypeTv);
            deleteBtn = itemView.findViewById(R.id.deleteBtn);
            editBtn = itemView.findViewById(R.id.editBtn);
            addressTv = itemView.findViewById(R.id.addressTv);
            mainSlider = itemView.findViewById(R.id.image_slider);
            priseTv = itemView.findViewById(R.id.priseTv);
            details = itemView.findViewById(R.id.details);

        }
    }

}
