package com.teh.fakelocationimage.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.teh.fakelocationimage.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.openCamera.setOnClickListener(v -> startActivity(new Intent(this, CameraActivity.class)));
        verifyStoragePermissions(this);
        binding.settings.setOnClickListener(v -> openSettings());
        binding.newProject.setOnClickListener(v -> startActivity(new Intent(this, EditImageActivity.class)));
        binding.premiumBtn.setOnClickListener(v -> openPremium());
        binding.backgroundLocationImage.setOnClickListener(v -> startActivity(new Intent(this, PickLocationImageActivity.class)));

    }
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
    private void openSettings() {
        //open app settings
//        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//        intent.setData(android.net.Uri.parse("package:" + getPackageName()));
//        startActivity(intent);
        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
        //transition right to left
        startActivity(intent);
    }


    private void openPremium() {
        startActivity(new Intent(MainActivity.this, PremiumActivity.class));
    }
}