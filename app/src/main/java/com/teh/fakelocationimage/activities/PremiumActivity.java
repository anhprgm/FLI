package com.teh.fakelocationimage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.teh.fakelocationimage.databinding.ActivityPremiumBinding;

public class PremiumActivity extends AppCompatActivity {
    private ActivityPremiumBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPremiumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.backBtn.setOnClickListener(v -> onBackPressed());
    }
}