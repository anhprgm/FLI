package com.teh.fakelocationimage.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.teh.fakelocationimage.CallApiService.ApiServices;
import com.teh.fakelocationimage.Constants.Constants;
import com.teh.fakelocationimage.adapters.LocationImageAdapter;
import com.teh.fakelocationimage.databinding.ActivityEditImageBinding;
import com.teh.fakelocationimage.databinding.ActivityPickLocationImageBinding;
import com.teh.fakelocationimage.listeners.LocationImageListener;
import com.teh.fakelocationimage.models.Location;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PickLocationImageActivity extends AppCompatActivity implements LocationImageListener {
    private ActivityPickLocationImageBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPickLocationImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getLocationImage();

    }

    private void getLocationImage() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.API_LINK_BG)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiServices services = retrofit.create(ApiServices.class);
        Call<List<Location>> call = services.getListLocation();
        call.enqueue(new Callback<List<Location>>() {
            @Override
            public void onResponse(@NonNull Call<List<Location>> call,@NonNull Response<List<Location>> response) {
                List<Location> locationLists = response.body();
                LocationImageAdapter locationImageAdapter = new LocationImageAdapter(locationLists, PickLocationImageActivity.this);
                binding.locationRecycleView.setAdapter(locationImageAdapter);
            }

            @Override
            public void onFailure(@NonNull Call<List<Location>> call,@NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onClickLocationImageItem(Location location) {
        Log.d("AAAA", location.getName());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Glide.with(this).clear(binding.locationRecycleView);
    }
}