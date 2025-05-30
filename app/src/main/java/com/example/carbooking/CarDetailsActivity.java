package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.carbooking.Fragment.HomeFragment;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.Model.Car;
import com.example.carbooking.databinding.ActivityCarDetailBinding;
import com.example.carbooking.Adapter.CarAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class CarDetailsActivity extends AppCompatActivity {

    private ActivityCarDetailBinding binding;
    private AppCar selectedCar; // Store the loaded car


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize ViewBinding
        binding = ActivityCarDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadCarDetail();
        // Call the method to handle
        btnBack();
        btnBooking();
    }




    private void loadCarDetail() {
        // Get carId from Intent extras
        String carId = getIntent().getStringExtra("carId");
        if (carId == null) {
            Log.e("car_detail", "No carId passed to activity");
            return;
        }

        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars").child(carId);
        carRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AppCar car = snapshot.getValue(AppCar.class);
                    if (car != null) {
                        selectedCar = car; // Store for later use
                        binding.carName.setText(car.getName());
                        binding.carPrice.setText("$" + car.getPrice());
                        binding.carDescription.setText(car.getDescription());

                        Glide.with(CarDetailsActivity.this)
                                .load(car.getImage())
                                .into(binding.carImage);

                        Log.d("car_detail", "model: " + car.getModel());
                        Log.d("car_detail", "price: " + car.getPrice());
                        Log.d("car_detail", "imageUrl: " + car.getImage());
                        Log.d("car_detail", "description: " + car.getDescription());
                    }
                } else {
                    Log.e("car_detail", "Car data not found for id: " + carId);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.e("car_detail", "Failed to load car data: " + error.getMessage());
            }
        });
    }




    private void btnBack() {
        binding.backIcon.setOnClickListener(v -> {
            finish();
        });
    }

    private void btnBooking() {
        binding.btnBookNow.setOnClickListener(v -> {
            if (selectedCar != null) {
                Intent intent = new Intent(this, PaymentActivity.class);
                intent.putExtra("carName", selectedCar.getName());
                intent.putExtra("carPrice", String.valueOf(selectedCar.getPrice()));
                intent.putExtra("carDescription", selectedCar.getDescription());
                intent.putExtra("carImage", selectedCar.getImage());
                Booking();
                startActivity(intent);
            } else {
                Toast.makeText(this, "Car data not loaded yet", Toast.LENGTH_SHORT).show();
            }
        });
    }




    private void Booking() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String carId = getIntent().getStringExtra("carId");

        if (carId == null) {
            Toast.makeText(this, "Error: Car ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create booking data
        HashMap<String, Object> bookingData = new HashMap<>();
        bookingData.put("carId", carId);
        bookingData.put("timestamp", System.currentTimeMillis()); // Optional: add timestamp
        bookingData.put("status", "pending"); // Optional: add booking status

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("rentals")
                .add(bookingData)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Booking successful!", Toast.LENGTH_SHORT).show();
                    Log.d("booking", "Booking saved with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Booking failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("booking", "Error saving booking", e);
                });
    }


}
