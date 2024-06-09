package com.bypriyan.findro.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.databinding.ActivityServiceBinding;
import com.bypriyan.findro.utilities.preferenceManager;

public class ServiceActivity extends AppCompatActivity {

    private ActivityServiceBinding binding;
    private String serviceType;
    static int PERMISSION_CODE= 100;
    private String service;
    private preferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityServiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        serviceType = getIntent().getStringExtra("ServiceType");
        preferenceManager = new preferenceManager(getApplicationContext());

        if(serviceType.equals("tiffin")){

            binding.tiffin.setVisibility(View.VISIBLE);
            binding.drawIcon.setImageResource(R.drawable.tiffin);
            binding.servicTxt.setText("Best-quality foods");
            binding.serviceDet.setText(R.string.food_servie);
            service = "Tiffin Service";

        } else if(serviceType.equals("painting")){

            binding.painting.setVisibility(View.VISIBLE);
            binding.drawIcon.setImageResource(R.drawable.painting_roller);
            binding.servicTxt.setText("Hassle-free Service");
            binding.serviceDet.setText(R.string.painting);
            service = "Painting and Cleaning service";

        }else if(serviceType.equals("moverpacker")){

            binding.packers.setVisibility(View.VISIBLE);
            binding.drawIcon.setImageResource(R.drawable.mover_packer_icon);
            binding.servicTxt.setText("Hassle-free Service");
            binding.serviceDet.setText(R.string.painting);
            service = "Movers and Packers Service";
        }
        else if(serviceType.equals("repairService")){

            binding.repair.setVisibility(View.VISIBLE);
            binding.drawIcon.setImageResource(R.drawable.repair_tool);
            binding.servicTxt.setText("Hassle-free Service");
            binding.serviceDet.setText(R.string.painting);
            service = "Repair Service";
        }else if(serviceType.equals("Milk")){

            binding.milk.setVisibility(View.VISIBLE);
            binding.drawIcon.setImageResource(R.drawable.cow_icon);
            binding.servicTxt.setText("Best-quality Milk");
            binding.serviceDet.setText(R.string.Milk_servie);
            service = "Milk Delivery Service";
        }

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        binding.contect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(ServiceActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){

                    ActivityCompat.requestPermissions(ServiceActivity.this,new String[]{Manifest.permission.CALL_PHONE},PERMISSION_CODE);

                }else{
                    String number = preferenceManager.getString(Constant.KEY_FINDRO_SERVICE_NUMBER);
                    showDialog(number);
                }

            }
        });

    }

    private void showDialog( String mobileNumber1) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_open_map, null);
        TextView contectWith = view.findViewById(R.id.contectWith);
        TextView call = view.findViewById(R.id.call);
        TextView whatsapp = view.findViewById(R.id.whatsapp);
        TextView gmail = view.findViewById(R.id.gmail);

        contectWith.setText("Contect to "+"Findro"+"?");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall(mobileNumber1);
            }
        });

        whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isWhatappInstalled()){
                    String number = "+91"+mobileNumber1;
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone="+number+
                            "&text="+"I am interested in your "+service));
                    startActivity(i);

                }else {

                    Toast.makeText(ServiceActivity.this,"Whatsapp is not installed",Toast.LENGTH_SHORT).show();

                }
            }
        });

        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = "suppoert.findro@gmail.com";
                String[] s = new String[2];
                s[0] = emails;
                s[1]= emails;
                sendMail(s);
            }
        });
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
}