package com.bypriyan.findro.register;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityMobileNumberBinding;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MobileNumber extends AppCompatActivity {

    private ActivityMobileNumberBinding binding;

    private TextInputLayout t1;
    private Button b1;
    private FirebaseAuth mAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private String PhoneNumber;
    private TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMobileNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mAuth = FirebaseAuth.getInstance();

        t1 = findViewById(R.id.mobile);
        b1 = findViewById(R.id.next);

        PhoneNumber = t1.getEditText().getText().toString();

        binding.terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://104findro.blogspot.com/2022/03/terms-conditions-body-font-family.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://104findro.blogspot.com/2022/03/privacy-policy-body-font-family.html");
                startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loading(true);
                if(isValidation()){
                    otpSend();
                }else{
                    loading(false);
                }

            }
        });
    }

    private boolean isValidation() {

         if(binding.name.getEditText().getText().toString().isEmpty()){
            binding.name.setError("Empty");
            binding.name.requestFocus();
            return false;
        }else if (binding.email.getEditText().getText().toString().isEmpty()) {
            binding.email.setError("Empty");
            binding.email.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.email.getEditText().getText().toString()).matches()) {
            binding.email.setError("Enter valid email");
            binding.email.requestFocus();
            return false;
        }else if (binding.mobile.getEditText().getText().toString().isEmpty()) {
             t1.setError("Empty");
             loading(false);
             return false;
         } else if (binding.mobile.getEditText().getText().toString().trim().length() != 10) {
             t1.setError("please enter valid number");
             loading(false);
             return false;
         }else if (binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setError("Empty");
            binding.password.requestFocus();
            return false;
        } else if (binding.password.getEditText().getText().toString().length() < 6 ||
         binding.password.getEditText().getText().toString().isEmpty()) {
            binding.password.setError("Enter at least 6 character ");
            binding.password.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isloading){
        if(isloading){
            binding.next.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        }else{
            binding.next.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }

    private void otpSend() {

        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                loading(false);
                Toast.makeText(MobileNumber.this, "successfully OTP send to your number", Toast.LENGTH_SHORT).show();

            }


            @Override
            public void onVerificationFailed (FirebaseException e){
                loading(false);

                Toast.makeText(MobileNumber.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent (@NonNull String verificationId,
                                    @NonNull PhoneAuthProvider.ForceResendingToken token){

                loading(false);
                Intent intent = new Intent(getApplicationContext(), OtpActivity.class);
                intent.putExtra("phoneNumber", t1.getEditText().getText().toString() );
                intent.putExtra("verificationId",verificationId);
                intent.putExtra(Constant.KEY_NAME, binding.name.getEditText().getText().toString());
                intent.putExtra(Constant.KEY_EMAIL, binding.email.getEditText().getText().toString());
                intent.putExtra(Constant.KEY_PASSWORD, binding.password.getEditText().getText().toString());
                startActivity(intent);

            }
        };

        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+91"+t1.getEditText().getText().toString().trim())       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }

}