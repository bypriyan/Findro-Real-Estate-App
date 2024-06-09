package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityEditPropertyBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class EditPropertyActivity extends AppCompatActivity implements LocationListener {

    private ActivityEditPropertyBinding binding;
    private String uid, propertyId;
    private String propertyType,iAm, prise, state,city, area,
            discription, duration, mealsInclude, roomInclude ,
            seekingA, sharingType, address, avalibleFrom, isAvalible;
    //
    boolean[] selectedSeeking;
    ArrayList<Integer> seekingList = new ArrayList<>();
    boolean[] selectedMeals;
    ArrayList<Integer> mealsList = new ArrayList<>();
    boolean[] selectedRoomsInclude;
    ArrayList<Integer> roomsIncludeList = new ArrayList<>();
    //
    private static final int LOCATION_REQUST_CODE = 100;
    private String[] locationPermission;
    private LocationManager locationManager;
    private double latitude=0.0, longetude=0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityEditPropertyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = getIntent().getStringExtra(Constant.KEY_UID);
        propertyId = getIntent().getStringExtra(Constant.KEY_ID);
        selectedSeeking = new boolean[Constant.SEEING_A.length];
        selectedMeals = new boolean[Constant.MEALS_INCLUDED.length];
        selectedRoomsInclude = new boolean[Constant.MY_ROOM_INCLUDES.length];
        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        propertyDetails(propertyId);

        binding.avability.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    isAvalible = "true";
                }else{
                    isAvalible = "false";
                }
            }
        });

        binding.seeingATv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomIncAlertDialog(Constant.SEEING_A, selectedSeeking, seekingList, binding.seeingATv,"Seeking a");
            }
        });

        binding.backBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.mealsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomIncAlertDialog(Constant.MEALS_INCLUDED, selectedMeals,
                        mealsList, binding.mealsTv,"Meals Included");
            }
        });

        binding.roomsIncludeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomIncAlertDialog(Constant.MY_ROOM_INCLUDES, selectedRoomsInclude,
                        roomsIncludeList, binding.roomsIncludeTv,"My Room Includes");
            }
        });

        binding.dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        binding.gpsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkLocationPermission()){
                    detectLoaction();
                }else{
                    requstLocationPermission();
                }
            }
        });

        binding.update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyType = checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType);
                iAm = checkButton(binding.iAmGroup.getCheckedRadioButtonId(), iAm);
                sharingType = checkButton(binding.sharingTypeGroup.getCheckedRadioButtonId(), sharingType);
                duration = checkButton(binding.durationOfStayGroup.getCheckedRadioButtonId(), duration);
                loading(true);
                 if(isValidation()){
                     addData(propertyId);
                }else{
                    loading(false);
                }
            }
        });
    }

    private void addData(String propertyId) {
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constant.KEY_PROPERTY_TYPE, ""+propertyType);
        hashMap.put(Constant.KEY_I_AM, ""+iAm);
        hashMap.put(Constant.KEY_SEEKING_A, ""+binding.seeingATv.getText().toString());
        hashMap.put(Constant.KEY_MONTHLY_RENT, ""+binding.monthlyRent.getEditText().getText().toString());
        hashMap.put(Constant.KEY_SHARING_TYPE, ""+sharingType);
        hashMap.put(Constant.KEY_DURATION_STAY ,""+duration);
        hashMap.put(Constant.KEY_MEALS_INCLUDE ,""+binding.mealsTv.getText().toString());
        hashMap.put(Constant.KEY_ROOM_INCLUDES,""+binding.roomsIncludeTv.getText().toString());
        hashMap.put(Constant.KEY_DESCRIPTION,""+binding.description.getEditText().getText().toString());
        hashMap.put(Constant.KEY_MOBILE_ALT_NUM ,""+binding.mobile.getEditText().getText().toString());
        hashMap.put(Constant.KEY_AREA,""+binding.propertyArea.getEditText().getText().toString());
        hashMap.put(Constant.KEY_AVALIBLE_FROM,""+binding.date.getText().toString());
        hashMap.put(Constant.KEY_STATE, ""+binding.state.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CITY, ""+binding.city.getEditText().getText().toString());
        hashMap.put(Constant.KEY_ADDRESS, ""+binding.address.getEditText().getText().toString());
        hashMap.put(Constant.KEY_latitude, ""+latitude);
        hashMap.put(Constant.KEY_longitude ,""+longetude);
        hashMap.put(Constant.KEY_IS_AVALIBLE, ""+isAvalible);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
        reference.child(propertyId)
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(EditPropertyActivity.this, "Your Property Updated Successfully", Toast.LENGTH_SHORT).show();

                loading(false);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                loading(false);
                Toast.makeText(EditPropertyActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                loading(false);
            }
        });
    }

    private void showRoomIncAlertDialog(String[] myRoomIncludes, boolean[] selectedRoomsInclude, ArrayList<Integer> roomsIncludeList, TextView roomsIncludeTv, String a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPropertyActivity.this);
        builder.setTitle(a);
        builder.setCancelable(false);
        builder.setMultiChoiceItems(myRoomIncludes, selectedRoomsInclude, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                if(b){
                    if(!roomsIncludeList.contains(i)){
                        roomsIncludeList.add(i);
                        Collections.sort(roomsIncludeList);
                    }
                }else{
                    try{
                        roomsIncludeList.remove(Integer.valueOf(i));
                    }catch (Exception e){
                    }

                }

            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                StringBuilder stringBuilder = new StringBuilder();
                for(int i=0;i<roomsIncludeList.size(); i++){
                    stringBuilder.append(myRoomIncludes[roomsIncludeList.get(i)]);

                    if(i != roomsIncludeList.size()-1 ){
                        stringBuilder.append(", ");
                    }
                }
                roomsIncludeTv.setText(stringBuilder.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for(int j=0; j< selectedRoomsInclude.length; j++){
                    selectedRoomsInclude[j]= false;
                    roomsIncludeList.clear();
                    roomsIncludeTv.setText("");
                }
            }
        });

        builder.show();
    }

    private boolean isValidation() {
        if(propertyType.isEmpty()){
            Toast.makeText(this, "select Property Type", Toast.LENGTH_SHORT).show();
            binding.propertyTypeGroup.requestFocus();
            return false;
        } else if(iAm.isEmpty()){
            Toast.makeText(this, "Select I Am Type..", Toast.LENGTH_SHORT).show();
            binding.iAmGroup.requestFocus();
            return false;
        }else if(seekingList.isEmpty()){
            binding.seeingATv.setError("Empty");
            Toast.makeText(this, "Select seeking a", Toast.LENGTH_SHORT).show();
            binding.seeingATv.requestFocus();
            return false;
        }else if(binding.monthlyRent.getEditText().getText().toString().isEmpty()){
            binding.monthlyRent.setError("Empty");
            binding.monthlyRent.requestFocus();
            return false;
        }
        else if(sharingType.isEmpty()){
            Toast.makeText(this, "Select Sharing Type..", Toast.LENGTH_SHORT).show();
            binding.sharingTypeGroup.requestFocus();
            return false;
        }
        else if(duration.isEmpty()){
            Toast.makeText(this, "Select Duration Of Stay", Toast.LENGTH_SHORT).show();
            binding.durationOfStayGroup.requestFocus();
            return false;
        }
        else if(mealsList.isEmpty()){
            binding.mealsTv.setError("Empty");
            Toast.makeText(this, "Select Meals Included", Toast.LENGTH_SHORT).show();
            binding.mealsTv.requestFocus();
            return false;
        }
        else if(roomsIncludeList.isEmpty()){
            binding.roomsIncludeTv.setError("Empty");
            Toast.makeText(this, "Select Room Includes", Toast.LENGTH_SHORT).show();
            binding.roomsIncludeTv.requestFocus();
            return false;
        }
        else if (binding.mobile.getEditText().getText().toString().isEmpty()||
                binding.mobile.getEditText().getText().toString().trim().length() != 10) {
            binding.mobile.setError("Please Check your Number");
            return false;
        }else if (binding.description.getEditText().getText().toString().isEmpty()) {
            binding.description.setError("Empty");
            return false;
        }
        else if(binding.propertyArea.getEditText().getText().toString().isEmpty()){
            binding.propertyArea.setError("Empty");
            binding.propertyArea.requestFocus();
            return false;
        }
        else if(binding.date.getText().toString().isEmpty()){
            binding.date.setError("Empty");
            binding.date.requestFocus();
            return false;
        }
        else if(binding.state.getEditText().getText().toString().isEmpty()){
            binding.state.setError("Empty");
            binding.state.requestFocus();
            return false;
        }
        else if(binding.city.getEditText().getText().toString().isEmpty()){
            binding.city.setError("Empty");
            binding.city.requestFocus();
            return false;
        }
        else if(binding.address.getEditText().getText().toString().isEmpty()){
            binding.address.setError("Empty");
            binding.address.requestFocus();
            return false;
        }
        else {
            return true;
        }
    }

    private void showDateDialog() {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yy");
                binding.date.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(EditPropertyActivity.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void propertyDetails(String propertyId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
        reference.keepSynced(true);
        reference.child(propertyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                propertyType= ""+snapshot.child(Constant.KEY_PROPERTY_TYPE).getValue();
                iAm= ""+snapshot.child(Constant.KEY_I_AM).getValue();
                prise = ""+snapshot.child(Constant.KEY_MONTHLY_RENT).getValue();
                state = ""+snapshot.child(Constant.KEY_STATE).getValue();
                 city = ""+snapshot.child(Constant.KEY_CITY).getValue();
                area = ""+snapshot.child(Constant.KEY_AREA).getValue();
                discription = ""+snapshot.child(Constant.KEY_DESCRIPTION).getValue();
                duration = ""+snapshot.child(Constant.KEY_DURATION_STAY).getValue();
                mealsInclude = ""+snapshot.child(Constant.KEY_MEALS_INCLUDE).getValue();
                roomInclude = ""+snapshot.child(Constant.KEY_ROOM_INCLUDES).getValue();
                 seekingA = ""+snapshot.child(Constant.KEY_SEEKING_A).getValue();
                 sharingType = ""+snapshot.child(Constant.KEY_SHARING_TYPE).getValue();
                 address = ""+snapshot.child(Constant.KEY_ADDRESS).getValue();
                 avalibleFrom = ""+snapshot.child(Constant.KEY_AVALIBLE_FROM).getValue();
                String mobileNumber1 = ""+snapshot.child(Constant.KEY_MOBILE_ALT_NUM).getValue();
                latitude = Double.parseDouble(""+snapshot.child(Constant.KEY_latitude).getValue());
                longetude = Double.parseDouble(""+snapshot.child(Constant.KEY_longitude).getValue());
                isAvalible = ""+snapshot.child(Constant.KEY_IS_AVALIBLE).getValue();

                if(isAvalible.equals("true")){
                    binding.avability.setChecked(true);
                }else if(isAvalible.equals("false")){
                    binding.avability.setChecked(false);
                }

                checkChecked(propertyType);
                checkiAm(iAm);
                binding.monthlyRent.getEditText().setText(prise);
                checkSharingType(sharingType);
                durationOfStayChecked(duration);

                binding.mobile.getEditText().setText(mobileNumber1);
                binding.description.getEditText().setText(discription);
                binding.propertyArea.getEditText().setText(area);
                binding.date.setText(avalibleFrom);
                binding.state.getEditText().setText(state);
                binding.city.getEditText().setText(city);
                binding.address.getEditText().setText(address);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }

    private void durationOfStayChecked(String duration) {
        if(duration.equals("Long Term")){
            binding.LongTerm.setChecked(true);
        }else if(duration.equals("Short Term")){
            binding.Shortterm.setChecked(true);
        }else if(duration.equals("Both Term")){
            binding.Bothterm.setChecked(true);
        }
    }

    private void checkSharingType(String sharingType) {
        if(sharingType.equals("Sharing")){
            binding.Sharing.setChecked(true);
        }else if(sharingType.equals("Non-Sharing")){
            binding.NonSharing.setChecked(true);
        }else if(sharingType.equals("Both")){
            binding.Both.setChecked(true);
        }
    }

    private void checkChecked(String propertyType) {
        if(propertyType.equals("Room")){
            binding.room.setChecked(true);
        } else if(propertyType.equals("Flat")){
            binding.flat.setChecked(true);
        }else if(propertyType.equals("Hostel")){
            binding.hostel.setChecked(true);
        }else if(propertyType.equals("PG")){
            binding.pg.setChecked(true);
        }else if(propertyType.equals("Commercial")){
            binding.commercial.setChecked(true);
        }
    }

    private void checkiAm(String person) {
        if(person.equals("Owner")){
            binding.Owner.setChecked(true);
        } else if(person.equals("Broker")){
            binding.Broker.setChecked(true);
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

    private void loading(boolean isloading){
        if(isloading){
            binding.update.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.update.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    private boolean checkLocationPermission(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)==(PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requstLocationPermission(){
        ActivityCompat.requestPermissions(this, locationPermission, LOCATION_REQUST_CODE);
    }

    private void detectLoaction() {
        Toast.makeText(this, "Please Wait...", Toast.LENGTH_LONG).show();
        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, (LocationListener) this);

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        latitude = location.getLatitude();
        longetude = location.getLongitude();
        findAdress();
    }

    private void findAdress() {
        Geocoder geocoder;
        List<Address> addresses;

        geocoder = new Geocoder(this, Locale.getDefault());
        try{
            addresses = geocoder.getFromLocation(latitude, longetude, 1);
            address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            binding.state.getEditText().setText(state);
            binding.city.getEditText().setText(city);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
        Toast.makeText(this, "Please turn on location...", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        switch (requestCode){
            case LOCATION_REQUST_CODE:{
                if(grantResults.length>0){
                    boolean loactionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if(loactionAccepted){
                        detectLoaction();
                    }else{
                        Toast.makeText(this, "Location permission is necessary...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}