package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.carbooking.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fetchData();   // Load car info from Intent
        btnBooking();  // Set up Pay Now button
    }

    private void fetchData() {
        Intent intent = getIntent();

        String carName = intent.getStringExtra("carName");
        String carDescription = intent.getStringExtra("carDescription");
        String carPrice = intent.getStringExtra("carPrice");
        String carImage = intent.getStringExtra("carImage");

        // Display
        binding.tvCarPrice.setText("$" + carPrice );
        binding.tvCheckout5.setText("$" + carPrice + "/day");


        // Log for debug
        Log.d("payment_activity", "Car Name: " + carName);
        Log.d("payment_activity", "Car Price: " + carPrice);
        Log.d("payment_activity", "Car Description: " + carDescription);
        Log.d("payment_activity", "Car Image: " + carImage);
    }



    private void btnBooking() {
        binding.btnPayNow.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingStatusActivity.class);
            String carPrice = binding.tvCarPrice.getText().toString();
            intent.putExtra("carPrice", carPrice);
            startActivity(intent);
            finish(); // Optional: close PaymentActivity
        });
    }
}
