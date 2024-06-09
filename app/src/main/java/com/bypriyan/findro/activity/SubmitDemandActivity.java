package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityProfileBinding;
import com.bypriyan.findro.databinding.ActivitySubmitDemandBinding;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class SubmitDemandActivity extends AppCompatActivity {

    private ActivitySubmitDemandBinding binding;
    private String propertyType;
    private preferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivitySubmitDemandBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        preferenceManager = new preferenceManager(getApplicationContext());
        firebaseAuth = FirebaseAuth.getInstance();

        binding.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                propertyType = checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType);
                if(isValidation()){
                    uploadData();
                }else{
                    loading(false);
                }
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void uploadData() {

        String budget = binding.budget.getText().toString();

        String timeStamps = ""+System.currentTimeMillis();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_PROPERTY_TYPE, ""+propertyType);
        hashMap.put(Constant.KEY_PHONE, ""+preferenceManager.getString(Constant.KEY_PHONE));
        hashMap.put(Constant.KEY_NAME, ""+preferenceManager.getString(Constant.KEY_NAME));
        hashMap.put("location", ""+binding.location.getText().toString());
        hashMap.put(Constant.KEY_DESCRIPTION , "my budget is"+budget+" "+binding.description.getText().toString());
        hashMap.put(Constant.KEY_UID, ""+firebaseAuth.getUid());
        hashMap.put(Constant.KEY_TIMESTAMP, ""+timeStamps);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Demand");
        reference.child(timeStamps).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                sucessDialog();
                loading(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                loading(false);
                Toast.makeText(SubmitDemandActivity.this, ""+e, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void loading(boolean isloading){
        if(isloading){
            binding.send.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.send.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
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

    private boolean isValidation(){
        if(checkButton(binding.propertyTypeGroup.getCheckedRadioButtonId(), propertyType)== null){
            Toast.makeText(this, "select Property Type", Toast.LENGTH_SHORT).show();
            binding.propertyTypeGroup.requestFocus();
            return false;
        }else if(binding.location.getText().toString().isEmpty()){
            binding.location.setError("Empty");
            binding.location.requestFocus();
            return false;
        }else if(binding.budget.getText().toString().isEmpty()){
            binding.budget.setError("Empty");
            binding.budget.requestFocus();
            return false;
        }else if(binding.description.getText().toString().isEmpty()){
            binding.description.setError("Empty");
            binding.description.requestFocus();
            return false;
        }else{
            return true;
        }
    }

    private void sucessDialog(){
        View view = LayoutInflater.from(this).inflate(R.layout.sucessful_request_submit_dialog, null);
        MaterialButton Okey = view.findViewById(R.id.Okey);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        Okey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

}