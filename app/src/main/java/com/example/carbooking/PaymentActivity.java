package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carbooking.databinding.ActivityPaymentBinding;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        btnBooking();
    }

    private void btnBooking(){
       binding.btnPayNow.setOnClickListener(v -> {
           Intent intent = new Intent(this, BookingStatusActivity.class);
           startActivity(intent);
           finish();

       });
    }
}