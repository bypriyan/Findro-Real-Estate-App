package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivityPropertyTypeSearchBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class PropertyTypeSearchActivity extends AppCompatActivity {

    private ActivityPropertyTypeSearchBinding binding;
    FirebaseAuth firebaseAuth;
    private ArrayList<ModelRooms> roomsArrayList;
    private AdapterRoomsVertical adapterRooms;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityPropertyTypeSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        search = getIntent().getStringExtra("search");

        if (!search.equals("all")) {
            binding.collapsingToolbar.setTitle(search);
        }

        if(search.equals("all")){
            binding.img.setImageResource(R.drawable.prop_all_sketch);
        }else  if(search.equals("Room")){
            binding.img.setImageResource(R.drawable.room_sketch);
        }else  if(search.equals("Flat")){
            binding.img.setImageResource(R.drawable.flat_sketch);
        }else  if(search.equals("Hostel")){
            binding.img.setImageResource(R.drawable.hostel_sketch);
        } else  if(search.equals("PG")){
            binding.img.setImageResource(R.drawable.pg_sketch);
        }else  if(search.equals("Commercial")){
            binding.img.setImageResource(R.drawable.commer_sketch);
        }

        loadRooms();

        if (search.equals("all")) {
            loadRooms();
        } else {
            loadFilteredRooms(search);
        }


    }

    private void loadFilteredRooms(String search) {
        roomsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String propertyType = "" + ds.child(Constant.KEY_PROPERTY_TYPE).getValue();
                    if (propertyType.equals(search)) {
                        ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                        roomsArrayList.add(modelRooms);
                    }
                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterRoomsVertical(PropertyTypeSearchActivity.this, roomsArrayList));
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadRooms() {
        roomsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                    roomsArrayList.add(modelRooms);
                }
                adapterRooms = new AdapterRoomsVertical(PropertyTypeSearchActivity.this, roomsArrayList);
                binding.recyclearRoomsVertical.setAdapter(adapterRooms);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}