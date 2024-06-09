package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivityFilterBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.material.slider.RangeSlider;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;

public class FilterActivity extends AppCompatActivity {

    private ActivityFilterBinding binding;
    private String propertyType, city, rentRange="10000";
    private String[] citys;
    private preferenceManager preferenceManager;

    private ArrayAdapter<String> adapterCitys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityFilterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new preferenceManager(getApplicationContext());
       String FirebaseCitys = preferenceManager.getString(Constant.KEY_FINDRO_CITYS);

        citys = FirebaseCitys.split(",");
        adapterCitys = new ArrayAdapter<String>(FilterActivity.this, R.layout.list_item_layout, citys);

        binding.cityAutoComplete.setAdapter(adapterCitys);
        binding.cityAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city = parent.getItemAtPosition(position).toString();

            }
        });

        binding.slider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull @NotNull Slider slider, float value, boolean fromUser) {
                rentRange = ""+(int)slider.getValue();
                binding.rentRange.setText("₹"+rentRange);
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyType = checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType);
                if(isValidation()){
                    Intent intent = new Intent(FilterActivity.this, FilterPropertyActivity.class);
                    intent.putExtra(Constant.KEY_CITY, city);
                    intent.putExtra(Constant.KEY_PROPERTY_TYPE, propertyType);
                    intent.putExtra(Constant.KEY_MONTHLY_RENT, rentRange);
                    startActivity(intent);
                }
            }
        });

    }

    private boolean isValidation(){
        if(city==null){
            Toast.makeText(this, "please select a location", Toast.LENGTH_SHORT).show();
            return false;
        } else if(checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType)== null){
            Toast.makeText(this, "select Property Type", Toast.LENGTH_SHORT).show();
            binding.propertyTypeGroup.requestFocus();
            return false;
        }else{
            return true;
        }
    }
    
    public String checkButton(int a, String propertyType) {
        int radioId = a;
        RadioButton radioButton = findViewById(radioId);
        try {
            propertyType = radioButton.getText().toString();
        } catch (Exception e) { }
        return propertyType;

    }

}