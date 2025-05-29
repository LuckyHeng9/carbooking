package com.example.carbooking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carbooking.Adapter.AdminCarAdapter;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.Adapter.CarAdapter;
import com.example.carbooking.databinding.FragmentDashboardBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private AdminCarAdapter carAdapter;
    private List<AppCar> carList;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout using ViewBinding
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        loadBookingStats();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));


//        // Initialize Adapter and set it to RecyclerView
//        carAdapter = new CarAdapter(carList);
//        binding.recyclerView.setAdapter(carAdapter);
    }


    private void loadCarStats() {
        FirebaseFirestore.getInstance()
                .collection("cars") // or your collection name
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int totalCars = querySnapshot.size();
                    binding.textView1.setText(String.valueOf(totalCars));
                })
                .addOnFailureListener(e -> {
                    binding.textView1.setText("0");
                });
    }

    private void loadBookingStats() {
        DatabaseReference rentalsRef = FirebaseDatabase.getInstance().getReference("cars");

        rentalsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int total = 0;
                int active = 0;
                int confirmed = 0;
                int pending = 0;

                for (DataSnapshot rentalSnapshot : snapshot.getChildren()) {
                    total++;
                    String status = rentalSnapshot.child("status").getValue(String.class);
                    if (status == null) continue;

                    switch (status.toLowerCase()) {
                        case "active":
                            active++;
                            break;
                        case "confirmed":
                            confirmed++;
                            break;
                        case "pending":
                            pending++;
                            break;
                    }
                }

                binding.textView1.setText(String.valueOf(total));     // Total Bookings
                binding.textView.setText(String.valueOf(active));     // Active
                binding.textView2.setText(String.valueOf(confirmed)); // Confirmed
                binding.textView4.setText(String.valueOf(pending));   // Pending
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
