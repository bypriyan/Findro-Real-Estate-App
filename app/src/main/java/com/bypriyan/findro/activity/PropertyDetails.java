package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterGalleryImage;
import com.bypriyan.findro.adapter.AdapterPropertyImages;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.databinding.ActivityPropertyDetailsBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.model.ModelSliderImg;
import com.bypriyan.findro.utilities.preferenceManager;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PropertyDetails extends AppCompatActivity {

    private ActivityPropertyDetailsBinding binding;
    private String uid, propertyId, propertyType;
    private FirebaseAuth firebaseAuth;
    private String mobileNumber1, mobileNumber2, emails, name, destinationLatitude, destinationLongitude;
    final ArrayList<ModelSliderImg> remoteImage = new ArrayList<>();
    static int PERMISSION_CODE= 100;

    private preferenceManager preferenceManager;
    private ArrayList<ModelRooms> roomsArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));

        binding = ActivityPropertyDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        uid = getIntent().getStringExtra(Constant.KEY_UID);
        propertyId = getIntent().getStringExtra(Constant.KEY_ID);
        firebaseAuth = FirebaseAuth.getInstance();
        preferenceManager = new preferenceManager(getApplicationContext());

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

//        loadPropertyImage(propertyId);
        propertyDetails(propertyId);
        loadGallery(propertyId);
        loadPropertys();

        binding.contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PropertyDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(PropertyDetails.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

                }else{
                    showDialog(mobileNumber1);
                }

            }
        });

        binding.location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMapDialog();
            }
        });

    }

        private void openMap(String destinationLatitude, String destinationLongitude) {
            String address = "https://maps.google.com/maps?saddr="+ "&daddr=" + destinationLatitude + "," + destinationLongitude;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address));
            startActivity(intent);

        }

        private void openMapDialog(){
            View view = LayoutInflater.from(this).inflate(R.layout.contect_dialog, null);
            TextView open = view.findViewById(R.id.open);
            TextView cancel = view.findViewById(R.id.cancel);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openMap(destinationLatitude, destinationLongitude);
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });
        }


    private void showDialog( String mobileNumber1) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_open_map, null);
        TextView contectWith = view.findViewById(R.id.contectWith);
        TextView call = view.findViewById(R.id.call);
        TextView whatsapp = view.findViewById(R.id.whatsapp);
        TextView gmail = view.findViewById(R.id.gmail);

        contectWith.setText("Contect to "+name+"?");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String nameFrom = preferenceManager.getString(Constant.KEY_NAME);
               String phoneFrom = preferenceManager.getString(Constant.KEY_PHONE);
                sendProcessData("call", mobileNumber1,name, nameFrom, phoneFrom);
                makePhoneCall(mobileNumber1);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWhatappInstalled()){
                    String nameFrom = preferenceManager.getString(Constant.KEY_NAME);
                    String phoneFrom = preferenceManager.getString(Constant.KEY_PHONE);
                    sendProcessData("Whatsapp", mobileNumber1,name, nameFrom, phoneFrom);

                    String number = "+91"+mobileNumber1;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+number+
                            "&text="+"I am interested in your property"));
                    startActivity(i);

                }else {

                    Toast.makeText(PropertyDetails.this,"Whatsapp is not installed",Toast.LENGTH_SHORT).show();

                }
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String nameFrom = preferenceManager.getString(Constant.KEY_NAME);
                String phoneFrom = preferenceManager.getString(Constant.KEY_PHONE);
                sendProcessData("Gmail", mobileNumber1,name, nameFrom, phoneFrom);

                String[] s = new String[2];
                s[0] = emails;
                s[1]= emails;
                sendMail(s);
            }
        });
    }

    private void sendProcessData(String contectType, String mobileNumber1, String nameOwner, String nameFrom, String phoneFrom) {

        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("contectType", contectType);
        hashMap.put("OwnerNumber", mobileNumber1);
        hashMap.put("OwnerName", nameOwner);
        hashMap.put("CallFromName", nameFrom);
        hashMap.put("CallFromNumber", phoneFrom);
        hashMap.put(Constant.KEY_TIMESTAMP, timeStamp);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Connect");
        reference.child(timeStamp).setValue(hashMap);

    }

    private void sendMail(String[] s) {
        String subject = "Findro";

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, s);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT,"I am interested in your property");

        intent.setType("message/rfc822");
        startActivity(Intent.createChooser(intent, "Choose an email client"));
    }

    private boolean isWhatappInstalled(){

        PackageManager packageManager = getPackageManager();
        boolean whatsappInstalled;

        try {

            packageManager.getPackageInfo("com.whatsapp",PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;


        }catch (PackageManager.NameNotFoundException e){

            whatsappInstalled = false;

        }

        return whatsappInstalled;

    }

    private void makePhoneCall(String mobileNumber1) {
        Intent i = new Intent(Intent.ACTION_CALL);
        i.setData(Uri.parse("tel:"+mobileNumber1));
        startActivity(i);
    }

    private void loadGallery(String propertyId) {
      DatabaseReference reference =  FirebaseDatabase.getInstance().getReference().child(Constant.KEY_PROPERTIES);
        reference.keepSynced(true);
        reference.child(propertyId).child(Constant.KEY_IMAGES)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        remoteImage.clear();
                        for(DataSnapshot ds: snapshot.getChildren()){
                            ModelSliderImg modelSliderImg = ds.getValue(ModelSliderImg.class);
                            remoteImage.add(modelSliderImg);
                        }
                        binding.recyclearGallery.setAdapter(new AdapterGalleryImage(PropertyDetails.this, remoteImage));
                        binding.recyclearPropImages.setAdapter(new AdapterPropertyImages(PropertyDetails.this, remoteImage));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
    }

    private void propertyDetails(String propertyId) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_PROPERTIES);
        reference.keepSynced(true);
        reference.child(propertyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                 propertyType= ""+snapshot.child(Constant.KEY_PROPERTY_TYPE).getValue();
                 String prise = ""+snapshot.child(Constant.KEY_MONTHLY_RENT).getValue();
                 String state = ""+snapshot.child(Constant.KEY_STATE).getValue();
                 String city = ""+snapshot.child(Constant.KEY_CITY).getValue();
                 String area = ""+snapshot.child(Constant.KEY_AREA).getValue();
                 String discription = ""+snapshot.child(Constant.KEY_DESCRIPTION).getValue();
                 String duration = ""+snapshot.child(Constant.KEY_DURATION_STAY).getValue();
                String mealsInclude = ""+snapshot.child(Constant.KEY_MEALS_INCLUDE).getValue();
                String roomInclude = ""+snapshot.child(Constant.KEY_ROOM_INCLUDES).getValue();
                String seekingA = ""+snapshot.child(Constant.KEY_SEEKING_A).getValue();
                String sharingType = ""+snapshot.child(Constant.KEY_SHARING_TYPE).getValue();
                String address = ""+snapshot.child(Constant.KEY_ADDRESS).getValue();
                String avalibleFrom = ""+snapshot.child(Constant.KEY_AVALIBLE_FROM).getValue();
                mobileNumber1 = ""+snapshot.child(Constant.KEY_MOBILE_ALT_NUM).getValue();
                mobileNumber2 = ""+snapshot.child(Constant.KEY_PHONE).getValue();
                emails = ""+snapshot.child(Constant.KEY_EMAIL).getValue();
                name = ""+snapshot.child(Constant.KEY_NAME).getValue();
                destinationLatitude = ""+snapshot.child(Constant.KEY_latitude).getValue();
                destinationLongitude = ""+snapshot.child(Constant.KEY_longitude).getValue();
                String avalible = ""+snapshot.child(Constant.KEY_IS_AVALIBLE).getValue();

                if(avalible.equals("true")){
                    binding.booked.setVisibility(View.GONE);
                }else if(avalible.equals("false")){
                    binding.booked.setVisibility(View.VISIBLE);
                }

                 binding.propertyTypeTv.setText(propertyType);
                 binding.priseTv.setText("₹ "+prise);
                 binding.cityState.setText(city+", "+state);
                 binding.propertyArea.setText(area+".sqft");
                 binding.discription.setText(discription);
                 binding.city.setText(city);
                 binding.state.setText(state);
                 binding.duration.setText(duration);
                 binding.propertyTypeD.setText(propertyType);
                 binding.areaTypeD.setText(area+".sqft");
                 binding.stateD.setText(state);
                 binding.cityD.setText(city);
                 binding.propertyType.setText(propertyType);
                 binding.avalibleFromD.setText(avalibleFrom);

                 if(duration.equals("Both Term")){
                     binding.durationOfD.setText("Long Term/Short Term");
                 }else{
                     binding.durationOfD.setText(duration);
                 }
                 binding.mealsIncludeD.setText(mealsInclude);
                 binding.roomIncludesD.setText(roomInclude);
                 binding.seekingAD.setText(seekingA);
                 binding.sharingTypeD.setText(sharingType);
                 binding.address.setText(address);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
            }
        });

    }

//    private void loadPropertyImage(String propertyId) {
//        final List<SlideModel> remoteImage = new ArrayList<>();
//        FirebaseDatabase.getInstance().getReference().child(Constant.KEY_PROPERTIES).child(propertyId).child(Constant.KEY_IMAGES)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
//                        for(DataSnapshot ds: snapshot.getChildren())
//                            remoteImage.add(new SlideModel(ds.child("link").getValue().toString(), ScaleTypes.CENTER_CROP));
//
//                        binding.imageSlider.setImageList(remoteImage, ScaleTypes.CENTER_INSIDE);
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull @NotNull DatabaseError error) {
//
//                    }
//                });
//    }

    private void loadPropertys() {
        binding.similarListing.setVisibility(View.GONE);
        roomsArrayList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){

                    String propertyTyp = ""+ds.child(Constant.KEY_PROPERTY_TYPE).getValue();
                    String propertyIds = ""+ds.child(Constant.KEY_ID).getValue();
                    if(propertyTyp.equals(propertyType) && !propertyId.equals(propertyIds)){
                        ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                        roomsArrayList.add(modelRooms);
                    }
                }
                binding.recyclearRooms.setAdapter(new AdapterRooms(PropertyDetails.this, roomsArrayList));
                if(roomsArrayList.isEmpty()){
                    binding.similarListing.setVisibility(View.GONE);
                }else{
                    binding.similarListing.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

}