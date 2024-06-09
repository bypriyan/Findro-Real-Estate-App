package com.bypriyan.findro.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.activity.MainActivity;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityOtpBinding;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class OtpActivity extends AppCompatActivity {

    private ActivityOtpBinding binding;
    private EditText t2;
    private TextView num;
    private Button b2;

    private String PhoneNumber, otpId;
    private String email, name, password;
    private String verificationId;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private preferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityOtpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        firebaseAuth = FirebaseAuth.getInstance();
        verificationId = getIntent().getStringExtra("verificationId").toString();
        PhoneNumber = getIntent().getStringExtra("phoneNumber");
        email = getIntent().getStringExtra(Constant.KEY_EMAIL);
        name = getIntent().getStringExtra(Constant.KEY_NAME);
        password = getIntent().getStringExtra(Constant.KEY_PASSWORD);
        num = findViewById(R.id.phoneLbl);
        preferenceManager = new preferenceManager(getApplicationContext());

        dialog = new ProgressDialog(this);
        dialog.setMessage("Verifying OTP....");
        dialog.setCancelable(false);

        t2 = findViewById(R.id.otp);
        b2 = findViewById(R.id.submitBtn);

        num.setText("Verify "+"+91 "+PhoneNumber);

        initiateOtp();

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();

                if(t2.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Blank Field Cannot Be Processed", Toast.LENGTH_SHORT).show();
                }else if(t2.getText().toString().length()!= 6){
                    t2.setError("Invalid OTP");
                }else{
                    if(verificationId != null) {
                        loading(true);
                        String code = t2.getText().toString();
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                        FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    dialog.dismiss();
                                    registerUser(email, password);

                                }else{
                                    dialog.dismiss();
                                    loading(false);
                                    Toast.makeText(getApplicationContext(), "Invalid OTP", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void requestPermissions() {
        if (ContextCompat.checkSelfPermission(OtpActivity.this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(OtpActivity.this,new String[]{
                    Manifest.permission.RECEIVE_SMS
            },100);
        }

    }

    private void initiateOtp() {

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                        .setPhoneNumber(PhoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                            @Override
                            public void onCodeSent(@NonNull @NotNull String s, @NonNull @NotNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                otpId = s;

                            }

                            @Override
                            public void onVerificationCompleted(@NonNull @NotNull PhoneAuthCredential phoneAuthCredential) {

                                signInWithPhoneAuthCredential(phoneAuthCredential);

                            }

                            @Override
                            public void onVerificationFailed(@NonNull @NotNull FirebaseException e) {
                                Toast.makeText(OtpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();


                            }
                        })          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void registerUser(String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    loading(true);
                    addData();
                } else{
                    loading(false);
                    FirebaseAuth.getInstance().signOut();
                    preferenceManager.putString(Constant.KEY_NAME, "");
                    preferenceManager.putString(Constant.KEY_EMAIL, "");
                    preferenceManager.putString(Constant.KEY_PHONE, "");
                    startActivity(new Intent(OtpActivity.this, LogIn.class));
                    finish();
                    Toast.makeText(OtpActivity.this, "Unable to create account", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    //signIn

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            registerUser(email, password);
                        } else {
                            Toast.makeText(OtpActivity.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }

    private void loading(boolean isloading){
        if(isloading){
            binding.submitBtn.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.submitBtn.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    public void addData(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String timeStamp = ""+System.currentTimeMillis();

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(Constant.KEY_UID, ""+firebaseAuth.getUid());
        hashMap.put(Constant.KEY_EMAIL, ""+ email);
        hashMap.put(Constant.KEY_NAME, ""+name);
        hashMap.put(Constant.KEY_PHONE,""+PhoneNumber);
        hashMap.put(Constant.KEY_PASSWORD,""+password);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(firebaseAuth.getUid())
                .setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                preferenceManager.putString(Constant.KEY_NAME, name);
                preferenceManager.putString(Constant.KEY_EMAIL, email);
                preferenceManager.putString(Constant.KEY_PHONE, PhoneNumber);
                Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("MobNum",PhoneNumber);
                startActivity(intent);
                loading(false);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                loading(false);
                Toast.makeText(OtpActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}