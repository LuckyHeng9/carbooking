package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
        String carPrice = getIntent().getStringExtra("carPrice");


        if (carPrice != null) {
            // For example, set the price to a TextView (make sure you have one in your layout)
            carPrice = carPrice.replace("$", "");
            binding.tvAmount.setText("Amount: " + carPrice + " USD");
            Log.d("bookingstatus", carPrice);
        }


        btnDone();
    }


    private void btnDone(){
        binding.btnContinue.setOnClickListener(v -> {
            finish();
        });
    }
}