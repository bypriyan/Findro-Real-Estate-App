package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityProfileBinding;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Objects;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private preferenceManager preferenceManager;
    private FirebaseAuth firebaseAuth;
    private boolean profile = false;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        preferenceManager = new preferenceManager(getApplicationContext());
        imageView = findViewById(R.id.shareImg);
        firebaseAuth = FirebaseAuth.getInstance();

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!profile) {
                    binding.editProfile.setVisibility(View.VISIBLE);
                    profile = true;
                } else if (profile) {
                    binding.editProfile.setVisibility(View.GONE);
                    profile = false;
                }
            }
        });

        binding.posProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PostProperty.class));
            }
        });

        binding.demand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SubmitDemandActivity.class));
            }
        });

        binding.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareImage(bitmap);
//                Intent intent = new Intent();
//                intent.setAction(Intent.ACTION_SEND);
//                intent.setType("text/plain");
//                intent.putExtra(Intent.EXTRA_TEXT, "Findro helps you to find Rooms, Hostel, Flat, PG and commercial property on rent. You can post your property on rent and grow your rental business.  \n\n https://play.google.com/store/apps/details?id=com.bypriyan.findro&hl=en");
//                startActivity(Intent.createChooser(intent,"Choose One"));
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

        binding.customerCare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SupportActivity.class));
            }
        });

        binding.yourProperties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MyPropertys.class));
            }
        });

        binding.privecyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://104findro.blogspot.com/2022/03/privacy-policy-body-font-family.html");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        binding.termsAndCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("https://104findro.blogspot.com/2022/03/terms-conditions-body-font-family.html");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });

        binding.tiffinService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivit("tiffin");
            }
        });

        binding.paintingService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivit("painting");
            }
        });

        binding.packerMover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivit("moverpacker");
            }
        });

        binding.milkService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivit("Milk");
            }
        });


        binding.repairService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivit("repairService");
            }
        });

        binding.name.getEditText().setText(preferenceManager.getString(Constant.KEY_NAME));

        binding.next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading(true);
                if (isValidation()) {
                    updateData();
                } else {
                    loading(false);
                }
            }
        });


    }

    private void shareImage(Bitmap bitmap) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/jpeg");
        Uri bmpUri;
        String Text = "Hi! I have found so many amazing Rooms, Hostel, Flat, PG and commercial property options relevant to my needs on Findro App. Download the app to search for your next Stay.\n\n https://play.google.com/store/apps/details?id=com.bypriyan.findro&hl=en";
        bmpUri = saveImg(bitmap, getApplicationContext());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        intent.putExtra(Intent.EXTRA_SUBJECT, "NEW APP");
        intent.putExtra(Intent.EXTRA_TEXT, Text);
        startActivity(Intent.createChooser(intent, "invite to Findro App"));

    }

    private Uri saveImg(Bitmap image, Context context) {

        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdirs();
            File file = new File(imageFolder, "shared_images.jpeg");
            FileOutputStream stream = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                    "com.bypriyan.findro"+".provider",file);
        } catch (Exception e) {
            Log.d("check", "saveImg: "+e);
        }
        return uri;

    }

    private void updateData() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(Constant.KEY_NAME, binding.name.getEditText().getText().toString().trim());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(firebaseAuth.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loading(false);
                preferenceManager.putString(Constant.KEY_NAME, binding.name.getEditText().getText().toString().trim());
                Toast.makeText(ProfileActivity.this, "Profile Updated successfully", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                loading(false);
                Toast.makeText(ProfileActivity.this, "" + e, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isValidation() {

        if (binding.name.getEditText().getText().toString().isEmpty()) {
            binding.name.setError("Empty");
            binding.name.requestFocus();
            return false;
        } else {
            return true;
        }
    }

    private void loading(boolean isloading) {
        if (isloading) {
            binding.next.setVisibility(View.INVISIBLE);
            binding.progressbar.setVisibility(View.VISIBLE);
        } else {
            binding.next.setVisibility(View.VISIBLE);
            binding.progressbar.setVisibility(View.INVISIBLE);
        }

    }


    private void startActivit(String s) {
        Intent intent = new Intent(ProfileActivity.this, ServiceActivity.class);
        intent.putExtra("ServiceType", s);
        startActivity(intent);
    }

}