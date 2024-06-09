package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityPostPropertyBinding;
import com.bypriyan.findro.register.OtpActivity;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class PostProperty extends AppCompatActivity implements LocationListener {

    private ActivityPostPropertyBinding binding;
    private FirebaseAuth firebaseAuth;
    private preferenceManager preferenceManager;

    private ArrayList<Uri> ImageList = new ArrayList<Uri>();
    private int uploads = 0;
    private DatabaseReference databaseReference;
    private static final int PICK_IMG = 1;
    androidx.appcompat.app.AlertDialog alertDialog;
    private String propertyType , iAm, sharingType, durationOfStay;
    //
    private static final int LOCATION_REQUST_CODE = 100;
    private String[] locationPermission;
    private LocationManager locationManager;
    private double latitude=0.0, longetude=0.0;
    //
    boolean[] selectedSeeking;
    ArrayList<Integer> seekingList = new ArrayList<>();
    boolean[] selectedMeals;
    ArrayList<Integer> mealsList = new ArrayList<>();
    boolean[] selectedRoomsInclude;
    ArrayList<Integer> roomsIncludeList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityPostPropertyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();

        locationPermission = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};
        selectedSeeking = new boolean[Constant.SEEING_A.length];
        selectedMeals = new boolean[Constant.MEALS_INCLUDED.length];
        selectedRoomsInclude = new boolean[Constant.MY_ROOM_INCLUDES.length];
        preferenceManager = new preferenceManager(getApplicationContext());

        binding.choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        binding.seeingATv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRoomIncAlertDialog(Constant.SEEING_A, selectedSeeking, seekingList, binding.seeingATv,"Seeking a");
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

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        binding.upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                propertyType = checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType);
               iAm = checkButton(binding.iAmGroup.getCheckedRadioButtonId(), iAm);
               sharingType = checkButton(binding.sharingTypeGroup.getCheckedRadioButtonId(), sharingType);
               durationOfStay = checkButton(binding.durationOfStayGroup.getCheckedRadioButtonId(), durationOfStay);

                loading(true);
                if(isValidation()){
                    showUploadingDialog();
                    upload();
                }else{
                    loading(false);
                }

            }
        });

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

        new DatePickerDialog(PostProperty.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
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
            binding.upload.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.upload.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    private boolean isValidation() {
        if(ImageList.isEmpty()){
            Toast.makeText(PostProperty.this, "Please select Property Images", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType)== null){
            Toast.makeText(this, "select Property Type", Toast.LENGTH_SHORT).show();
            binding.propertyTypeGroup.requestFocus();
            return false;
        } else if(checkButton(binding.iAmGroup.getCheckedRadioButtonId(), iAm)== null){
            Toast.makeText(this, "Select I Am Type..", Toast.LENGTH_SHORT).show();
            binding.iAmGroup.requestFocus();
            return false;
        }else if(seekingList.isEmpty()){
            binding.seeingATv.setError("Empty");
            binding.seeingATv.requestFocus();
            return false;
        }else if(binding.monthlyRent.getEditText().getText().toString().isEmpty()){
            binding.monthlyRent.setError("Empty");
            binding.monthlyRent.requestFocus();
            return false;
        }
        else if(checkButton(binding.sharingTypeGroup.getCheckedRadioButtonId(), sharingType)==null){
            Toast.makeText(this, "Select Sharing Type..", Toast.LENGTH_SHORT).show();
            binding.sharingTypeGroup.requestFocus();
            return false;
        }
        else if(checkButton(binding.durationOfStayGroup.getCheckedRadioButtonId(), durationOfStay)== null){
            Toast.makeText(this, "Select Duration Of Stay", Toast.LENGTH_SHORT).show();
            binding.durationOfStayGroup.requestFocus();
            return false;
        }
        else if(mealsList.isEmpty()){
            binding.mealsTv.setError("Empty");
            binding.mealsTv.requestFocus();
            return false;
        }
        else if(roomsIncludeList.isEmpty()){
            binding.roomsIncludeTv.setError("Empty");
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
        else if(latitude==0.0 || longetude == 0.0){
            Toast.makeText(this, "Please Click On Location Button On Screen's top right corner", Toast.LENGTH_LONG).show();
            return false;
        }

        else {
            return true;
        }
    }

    private void showRoomIncAlertDialog(String[] myRoomIncludes, boolean[] selectedRoomsInclude, ArrayList<Integer> roomsIncludeList, TextView roomsIncludeTv, String a) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PostProperty.this);
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

    private void selectImage(){
        Dexter.withContext(getApplicationContext())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("image/*");
                        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                        startActivityForResult(intent, PICK_IMG);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMG) {
            if (resultCode == RESULT_OK) {
                if (data.getClipData() != null) {
                    int count = data.getClipData().getItemCount();

                    int CurrentImageSelect = 0;

                    while (CurrentImageSelect < count) {
                        Uri imageuri = data.getClipData().getItemAt(CurrentImageSelect).getUri();
                        ImageList.add(imageuri);
                        CurrentImageSelect = CurrentImageSelect + 1;
                    }
                    binding.alert.setVisibility(View.VISIBLE);
                    binding.alert.setText("You Have Selected "+ ImageList.size() +" Pictures" );
                    binding.choose.setVisibility(View.GONE);
                }

            }

        }

    }

    public void upload() {
         String timeStamp = ""+System.currentTimeMillis();
        binding.alert.setText("Please Wait ...");
        final StorageReference ImageFolder =  FirebaseStorage.getInstance().getReference().child("RoomImage");
        for (uploads=0; uploads < ImageList.size(); uploads++) {
            Uri Image  = ImageList.get(uploads);
            final StorageReference imagename = ImageFolder.child("image/"+Image.getLastPathSegment());

            imagename.putFile(ImageList.get(uploads)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imagename.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String url = String.valueOf(uri);
                            SendLink(url, timeStamp);
                        }
                    });

                }
            });


        }


    }

    private void SendLink(String url, String timeStamp) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("link", url);
        databaseReference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
        databaseReference.child(timeStamp).child(Constant.KEY_IMAGES).push()
                .setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                binding.alert.setText("Image Uploaded Successfully");
                addData(timeStamp);
                ImageList.clear();
            }
        });


    }

    public void addData(String timeStamp){

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constant.KEY_UID, ""+firebaseAuth.getUid());
        hashMap.put(Constant.KEY_TIMESTAMP, ""+timeStamp);
        hashMap.put(Constant.KEY_EMAIL, ""+ preferenceManager.getString(Constant.KEY_EMAIL));
        hashMap.put(Constant.KEY_NAME, ""+preferenceManager.getString(Constant.KEY_NAME));
        hashMap.put(Constant.KEY_PHONE,""+preferenceManager.getString(Constant.KEY_PHONE));
        hashMap.put(Constant.KEY_PROPERTY_TYPE, ""+propertyType);
        hashMap.put(Constant.KEY_I_AM, ""+iAm);
        hashMap.put(Constant.KEY_SEEKING_A, ""+binding.seeingATv.getText().toString());
        hashMap.put(Constant.KEY_MONTHLY_RENT, ""+binding.monthlyRent.getEditText().getText().toString());
        hashMap.put(Constant.KEY_SHARING_TYPE, ""+sharingType);
        hashMap.put(Constant.KEY_DURATION_STAY ,""+durationOfStay);
        hashMap.put(Constant.KEY_MEALS_INCLUDE ,""+binding.mealsTv.getText().toString());
        hashMap.put(Constant.KEY_ROOM_INCLUDES,""+binding.roomsIncludeTv.getText().toString());
        hashMap.put(Constant.KEY_DESCRIPTION,""+binding.description.getEditText().getText().toString());
        hashMap.put(Constant.KEY_MOBILE_ALT_NUM ,""+binding.mobile.getEditText().getText().toString());
        hashMap.put(Constant.KEY_AREA,""+binding.propertyArea.getEditText().getText().toString());
        hashMap.put(Constant.KEY_ID,""+timeStamp);
        hashMap.put(Constant.KEY_AVALIBLE_FROM,""+binding.date.getText().toString());
        hashMap.put(Constant.KEY_STATE, ""+binding.state.getEditText().getText().toString());
        hashMap.put(Constant.KEY_CITY, ""+binding.city.getEditText().getText().toString());
        hashMap.put(Constant.KEY_ADDRESS, ""+binding.address.getEditText().getText().toString());
        hashMap.put(Constant.KEY_latitude, ""+latitude);
        hashMap.put(Constant.KEY_longitude ,""+longetude);
        hashMap.put(Constant.KEY_IS_AVALIBLE,"true");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
        reference.child(timeStamp)
                .updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {

                Toast.makeText(PostProperty.this, "Your Property Added Successfully", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                startActivity(new Intent(PostProperty.this, MainActivity.class));
                finish();
                loading(false);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                loading(false);
                Toast.makeText(PostProperty.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                loading(false);
            }
        });
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

    private void showUploadingDialog() {

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_uploading, null);
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setView(view);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

    }

}