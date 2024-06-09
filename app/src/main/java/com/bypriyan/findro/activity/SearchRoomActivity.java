package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivitySearchRoomBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class SearchRoomActivity extends AppCompatActivity {

    private ActivitySearchRoomBinding binding;
    FirebaseAuth firebaseAuth;

    private ArrayList<ModelRooms> roomsArrayList;
    private AdapterRoomsVertical adapterRooms;
    private String search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivitySearchRoomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        search = getIntent().getStringExtra("search");

        if(!search.equals("all")){
            binding.searchEt.setText(search);
        }
        loadRooms();

        if(search.equals("all")){
            loadRooms();
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.searchEt.setText(null);
            }
        });

        binding.searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0){
                    binding.backBtn.setVisibility(View.GONE);
                    binding.clear.setVisibility(View.VISIBLE);
                    binding.filter.setVisibility(View.GONE);
                }else{
                    binding.backBtn.setVisibility(View.VISIBLE);
                    binding.clear.setVisibility(View.GONE);
                    binding.filter.setVisibility(View.VISIBLE);
                }
                try{
                    adapterRooms.getFilter().filter(s);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    startActivity(new Intent(SearchRoomActivity.this, FilterActivity.class));
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
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                    roomsArrayList.add(modelRooms);
                }
                adapterRooms = new AdapterRoomsVertical(SearchRoomActivity.this, roomsArrayList);
                binding.recyclearRoomsVertical.setAdapter(adapterRooms);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}