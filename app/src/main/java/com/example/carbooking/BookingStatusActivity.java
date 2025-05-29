package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carbooking.Fragment.HomeFragment;
import com.example.carbooking.databinding.ActivityBookingBinding;

public class BookingStatusActivity extends AppCompatActivity {

    private ActivityBookingBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBookingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        btnDone();
    }

    private void btnDone(){
        binding.btnContinue.setOnClickListener(v -> {
            finish();
        });
    }
}