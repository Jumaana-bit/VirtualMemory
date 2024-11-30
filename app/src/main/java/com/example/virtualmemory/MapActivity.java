package com.example.virtualmemory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import java.lang.ref.WeakReference;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private EditText etHome, etWork, etFriendHouse;
    private static final String PREFS_NAME = "LocationPrefs";
    private static final String KEY_HOME = "home";
    private static final String KEY_WORK = "work";
    private static final String KEY_FRIENDHOUSE = "friendHouse";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        // Initialize map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            Log.e("MAPS_API", "Failed to initialize map fragment.");
        }

        // Initialize UI elements
        etHome = findViewById(R.id.et_home);
        etWork = findViewById(R.id.et_work);
        etFriendHouse = findViewById(R.id.et_friendHouse);

        // Load saved locations asynchronously
        loadSavedLocations();

        // Set up buttons
        Button btnSaveHome = findViewById(R.id.btn_save_home);
        Button btnSaveWork = findViewById(R.id.btn_save_work);
        Button btnSaveFriendHouse = findViewById(R.id.btn_save_friendHouse);
        Button btnNavigateHome = findViewById(R.id.btn_navigate_home);
        Button btnNavigateWork = findViewById(R.id.btn_navigate_work);
        Button btnNavigateFriendHouse = findViewById(R.id.btn_navigate_friendHouse);

        // Button actions
        btnSaveHome.setOnClickListener(v -> saveLocation(KEY_HOME, etHome.getText().toString()));
        btnSaveWork.setOnClickListener(v -> saveLocation(KEY_WORK, etWork.getText().toString()));
        btnSaveFriendHouse.setOnClickListener(v -> saveLocation(KEY_FRIENDHOUSE, etFriendHouse.getText().toString()));

        btnNavigateHome.setOnClickListener(v -> navigateTo(etHome.getText().toString()));
        btnNavigateWork.setOnClickListener(v -> navigateTo(etWork.getText().toString()));
        btnNavigateFriendHouse.setOnClickListener(v -> navigateTo(etFriendHouse.getText().toString()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MAPS_API", "Google Map is ready!");
    }

    private void saveLocation(String key, String address) {
        if (address.isEmpty()) {
            Log.w("MAPS_API", key + " address is empty. Not saved.");
            return;
        }

        // Offload saving to background thread using AsyncTask
        new SaveLocationTask(this).execute(key, address);
    }

    // AsyncTask for saving location
    private static class SaveLocationTask extends AsyncTask<String, Void, Void> {
        private WeakReference<Context> contextWeakRef;

        SaveLocationTask(Context context) {
            this.contextWeakRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... params) {
            String key = params[0];
            String address = params[1];
            Context context = contextWeakRef.get();

            if (context != null) {
                try {
                    // Save location in SharedPreferences on background thread
                    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(key, address);
                    editor.apply();
                    Log.d("MAPS_API", "Saved " + key + ": " + address);
                } catch (Exception e) {
                    Log.e("MAPS_API", "Error saving location for " + key + ": " + e.getMessage());
                }
            }

            return null;
        }
    }

    private void loadSavedLocations() {
        // Offload to background thread using AsyncTask
        new LoadLocationTask(this).execute();
    }

    // AsyncTask for loading saved locations
    private static class LoadLocationTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Context> contextWeakRef;

        LoadLocationTask(Context context) {
            this.contextWeakRef = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Context context = contextWeakRef.get();

            if (context != null) {
                try {
                    // Load locations from SharedPreferences on background thread
                    SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                    String home = sharedPreferences.getString(KEY_HOME, "");
                    String work = sharedPreferences.getString(KEY_WORK, "");
                    String friendHouse = sharedPreferences.getString(KEY_FRIENDHOUSE, "");

                    // Update UI elements with loaded values on the main thread
                    if (context instanceof MapActivity) {
                        ((MapActivity) context).runOnUiThread(() -> {
                            EditText etHome = ((MapActivity) context).etHome;
                            EditText etWork = ((MapActivity) context).etWork;
                            EditText etFriendHouse = ((MapActivity) context).etFriendHouse;

                            etHome.setText(home);
                            etWork.setText(work);
                            etFriendHouse.setText(friendHouse);
                        });
                    }

                    Log.d("MAPS_API", "Loaded saved locations.");
                } catch (Exception e) {
                    Log.e("MAPS_API", "Error loading saved locations: " + e.getMessage());
                }
            }

            return null;
        }
    }

    private void navigateTo(String address) {
        if (!address.isEmpty()) {
            try {
                String uri = "google.navigation:q=" + Uri.encode(address);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } catch (Exception e) {
                Log.e("MAPS_API", "Error starting navigation to " + address + ": " + e.getMessage());
            }
        }
    }
}

