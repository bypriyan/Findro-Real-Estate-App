package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivityFilterBinding;
import com.bypriyan.findro.databinding.ActivityFilterPropertyBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class FilterPropertyActivity extends AppCompatActivity {

    private ActivityFilterPropertyBinding binding;
    private String city, propertyType, rentRange;
    private ArrayList<ModelRooms> roomsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityFilterPropertyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        city = getIntent().getStringExtra(Constant.KEY_CITY).trim();
        propertyType = getIntent().getStringExtra(Constant.KEY_PROPERTY_TYPE).trim();
        rentRange = getIntent().getStringExtra(Constant.KEY_MONTHLY_RENT).trim();


        binding.city.setText(city);
        loadPropertys(city, propertyType, rentRange);

        binding.filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(FilterPropertyActivity.this, FilterActivity.class));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onBackPressed();
            }
        });

    }

    private void loadPropertys(String city, String propertyType, String rentRange) {
        roomsArrayList = new ArrayList<>();
        int rent = Integer.parseInt(rentRange);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {

                    String cit = "" + ds.child(Constant.KEY_CITY).getValue();
                    String propTyp = "" + ds.child(Constant.KEY_PROPERTY_TYPE).getValue();
                    int monthlyRent = Integer.parseInt(""+ds.child(Constant.KEY_MONTHLY_RENT).getValue().toString());

                    ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                    if(city.equals(cit) && propertyType.equals(propTyp) && monthlyRent<=rent ){
                    roomsArrayList.add(modelRooms);
                    }

                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterRoomsVertical(FilterPropertyActivity.this, roomsArrayList));
                if(roomsArrayList.size()>0){
                    binding.resultFound.setText("Yay! "+roomsArrayList.size()+" results found");
                    binding.resultLottie.setVisibility(View.GONE);
                }else {
                    binding.resultFound.setVisibility(View.GONE);
                    binding.resultLottie.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}