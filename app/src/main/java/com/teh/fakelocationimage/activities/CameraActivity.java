package com.teh.fakelocationimage.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.teh.fakelocationimage.databinding.ActivityCameraBinding;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;

public class CameraActivity extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ActivityCameraBinding binding;
    private ImageCapture imageCapture;
    private final AtomicReference<CameraSelector> cameraSelector = new AtomicReference<>(new CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build());
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCameraBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
        checkPermission();
//        getUriRecentImage();
        binding.backBtn.setOnClickListener(view -> {
            onBackPressed();
        });
    }
    @SuppressLint("RestrictedApi")
    private void bindPreview(ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
        binding.btnSwitchCamera.setOnClickListener(v -> {
            try {
                cameraProvider.unbindAll();
                if (cameraSelector.get().getLensFacing() == CameraSelector.LENS_FACING_BACK) {
                    cameraSelector.set(new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                            .build());
                } else {
                    cameraSelector.set(new CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                            .build());
                }
                bindPreview(cameraProvider);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        imageCapture = new ImageCapture.Builder()
                                .setTargetRotation(binding.previewView.getDisplay().getRotation())
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                                .build();
        binding.btnShot.setOnClickListener(v -> {
            shotPhoto();
        });
        cameraProvider.bindToLifecycle(this, cameraSelector.get(), preview, imageCapture);

    }

    private void checkPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }
    private void shotPhoto() {

        long timeStamps = System.currentTimeMillis();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "Image_" + timeStamps);
        contentValues.put(android.provider.MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                ).build(),
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        getUriRecentImage();
                        Uri uri = outputFileResults.getSavedUri();

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {

                    }
                }
        );
    }
    public static void setCameraDisplayOrientation() {
    }
    private void SetImageResult(Uri uri) {
        binding.imageResult.setImageURI(uri);
        binding.imageResult.setVisibility(View.VISIBLE);

    }
    private void getUriRecentImage() {
        int position = 0;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Images.Media._ID};
        String sortOrder = MediaStore.Images.Media._ID + " DESC";
        Cursor cursor = getContentResolver().query(uri, projection, null, null, sortOrder);
        if (cursor != null && cursor.moveToFirst()) {
            position = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            cursor.close();
        }
        Uri uriRecentImage = Uri.withAppendedPath(uri, "" + position);
        binding.recentImage.setImageURI(uriRecentImage);
    }
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
//    }
}