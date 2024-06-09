package com.bypriyan.findro.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bypriyan.findro.Constant;
import com.bypriyan.findro.R;
import com.bypriyan.findro.adapter.AdapterRooms;
import com.bypriyan.findro.adapter.AdapterRoomsVertical;
import com.bypriyan.findro.databinding.ActivityMainBinding;
import com.bypriyan.findro.model.ModelRooms;
import com.bypriyan.findro.register.LogIn;
import com.bypriyan.findro.register.SelectActivity;
import com.bypriyan.findro.utilities.preferenceManager;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private long backPressed;
    private Toast backToast;

    private FirebaseAuth firebaseAuth;
    private preferenceManager preferenceManager;

    private ArrayList<ModelRooms> roomsArrayList;
    private ArrayList<ModelRooms> roomsArrayListHorizontal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(getResources().getColor(R.color.blue));
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseRemoteConfig config = FirebaseRemoteConfig.getInstance();
        preferenceManager = new preferenceManager(getApplicationContext());

        loadPropertys();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(3600)
                .build();
        config.setConfigSettingsAsync(configSettings);

        config.fetchAndActivate().addOnSuccessListener(new OnSuccessListener<Boolean>() {
            @Override
            public void onSuccess(Boolean aBoolean) {
                String supportNumber = config.getString("supportNumber");
                String findroServiceNumber = config.getString("findroService");
                String citys = config.getString(Constant.KEY_FINDRO_CITYS);
                preferenceManager.putString(Constant.KEY_SUPPORT_TEAM_NUMBER,supportNumber);
                preferenceManager.putString(Constant.KEY_FINDRO_SERVICE_NUMBER, findroServiceNumber);
                preferenceManager.putString(Constant.KEY_FINDRO_CITYS, citys);
            }
        });

        if (checkUserLogin()) loadUserData();

        binding.profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserLogin()) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    showDialog();
                }

            }
        });

        binding.searchEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserLogin()) {
                    Intent intent = new Intent(MainActivity.this, SearchRoomActivity.class);
                    intent.putExtra("search", "all");
                    startActivity(intent);
                } else {
                    showDialog();
                }
            }
        });

        binding.filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserLogin()) {
                    startActivity(new Intent(MainActivity.this, FilterActivity.class));
                } else {
                    showDialog();
                }
            }
        });

        binding.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.all2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Room");
            }
        });
        binding.flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Flat");
            }
        });

        binding.hostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Hostel");
            }
        });

        binding.pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("PG");
            }
        });

        binding.commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Commercial");
            }
        });


        binding.all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.all1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.all2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("all");
            }
        });

        binding.room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Room");
            }
        });
        binding.flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Flat");
            }
        });

        binding.hostel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Hostel");
            }
        });

        binding.pg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("PG");
            }
        });

        binding.commercial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActiv("Commercial");
            }
        });

        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkUserLogin()) {
                    logOut();
                } else {
                    showDialog();
                }
            }
        });

    }

    private void startActiv(String s) {
        Intent intent = new Intent(MainActivity.this, PropertyTypeSearchActivity.class);
        intent.putExtra("search", s);
        startActivity(intent);
    }

    private void logOut() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        String message = "Are you sure you want to LogOut?";
        builder.setMessage(Html.fromHtml("<font color='#1b1e28'>" + message + "</font>"));
        builder.setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(MainActivity.this, "Signing out", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        preferenceManager.putString(Constant.KEY_NAME, "");
                        preferenceManager.putString(Constant.KEY_EMAIL, "");
                        preferenceManager.putString(Constant.KEY_PHONE, "");
                        startActivity(new Intent(MainActivity.this, LogIn.class));
                        finish();

                    }

                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.color.white);
        alertDialog.show();

    }

    private void loadPropertys() {
        roomsArrayList = new ArrayList<>();
        roomsArrayListHorizontal = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);
        reference.child(Constant.KEY_PROPERTIES).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomsArrayList.clear();
                roomsArrayListHorizontal.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String avabilivle = "" + ds.child(Constant.KEY_IS_AVALIBLE).getValue();

                    ModelRooms modelRooms = ds.getValue(ModelRooms.class);
                    roomsArrayList.add(modelRooms);
//                    if(avabilivle.equals("true")){
                    ModelRooms modelRoomsHorizontal = ds.getValue(ModelRooms.class);
                    roomsArrayListHorizontal.add(modelRoomsHorizontal);
//                    }

                }
                binding.recyclearRoomsVertical.setAdapter(new AdapterRoomsVertical(MainActivity.this, roomsArrayList));
                Collections.shuffle(roomsArrayListHorizontal);
                binding.recyclearRooms.setAdapter(new AdapterRooms(MainActivity.this, roomsArrayListHorizontal));

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void loadUserData() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(Constant.KEY_USERS);
        reference.child(firebaseAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String name = "" + snapshot.child(Constant.KEY_NAME).getValue();
                String email = "" + snapshot.child(Constant.KEY_EMAIL).getValue();
                String PhoneNumber = "" + snapshot.child(Constant.KEY_PHONE).getValue();

                preferenceManager.putString(Constant.KEY_NAME, name);
                preferenceManager.putString(Constant.KEY_EMAIL, email);
                preferenceManager.putString(Constant.KEY_PHONE, PhoneNumber);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void showDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_oops, null);
        TextView okay = view.findViewById(R.id.okay);
        TextView later = view.findViewById(R.id.later);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SelectActivity.class));
                finish();
            }
        });

        later.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (backPressed + 2500 > System.currentTimeMillis()) {
            super.onBackPressed();
            backToast.cancel();
            return;
        } else {
            backToast = Toast.makeText(this, "press again to exit", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressed = System.currentTimeMillis();
    }

    private boolean checkUserLogin() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserLogin();
    }


}