package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterMyProperts;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivityMyPropertysBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class MyPropertys extends AppCompatActivity {

    private ActivityMyPropertysBinding binding;
    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelRooms> roomsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMyPropertysBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        loadPropertys();

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void loadPropertys() {
        roomsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).orderByChild(Constant.KEY_UID).equalTo(firebaseAuth.getUid())
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                    roomsArrayList.add(modelRooms);
                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterMyProperts(MyPropertys.this, roomsArrayList));

                if(roomsArrayList.isEmpty()){
                    binding.resultLottie.setVisibility(View.VISIBLE);
                }else{
                    binding.resultLottie.setVisibility(View.GONE);
                }

            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}