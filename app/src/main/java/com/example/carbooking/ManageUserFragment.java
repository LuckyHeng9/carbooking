package com.example.carbooking;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.carbooking.Adapter.RentalAdapter;
import com.example.carbooking.databinding.FragmentManageUserBinding;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.Model.Rental;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ManageUserFragment extends Fragment {

    private FragmentManageUserBinding binding;
    private RentalAdapter rentalAdapter;
    private final List<Rental> rentalList = new ArrayList<>();
    private final List<AppCar> carlist = new ArrayList<>();
    private static final String TAG = "ManageUserFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManageUserBinding.inflate(inflater, container, false);
        binding.rcManageUserRents.setLayoutManager(new LinearLayoutManager(getContext()));
        loadCarsAndRentals();
        return binding.getRoot();
    }

    private void loadCarsAndRentals() {
        DatabaseReference carsRef = FirebaseDatabase.getInstance().getReference("cars");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        carlist.clear();
        rentalList.clear();

        carsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot carSnap : snapshot.getChildren()) {
                    AppCar car = carSnap.getValue(AppCar.class);
                    if (car != null) {
                        car.setId(carSnap.getKey());
                        carlist.add(car);
                        Log.d(TAG, "Loaded " + car.getId() );
                        Log.d(TAG, "Loaded " + car.getModel() );
                        Log.d(TAG, "Loaded " + car.getImage() );
                    }
                }

                // Now fetch rentals
                db.collection("users").get().addOnSuccessListener(userSnapshots -> {
                    if (userSnapshots.isEmpty()) return;

                    AtomicInteger remaining = new AtomicInteger(userSnapshots.size());

                    for (DocumentSnapshot userDoc : userSnapshots) {
                        String userId = userDoc.getId();
                        db.collection("users")
                                .document(userId)
                                .collection("rentals")
                                .get()
                                .addOnSuccessListener(rentalSnapshots -> {
                                    for (DocumentSnapshot rentalDoc : rentalSnapshots) {
                                        Rental rental = rentalDoc.toObject(Rental.class);
                                        if (rental != null) {
                                            rental.setRentalId(rentalDoc.getId());
                                            rental.setUserId(userId);
                                            rentalList.add(rental);

                                        }
                                    }

                                    if (remaining.decrementAndGet() == 0) {
                                        showRentals();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    if (remaining.decrementAndGet() == 0) {
                                        showRentals();
                                    }
                                });

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load cars: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRentals() {
        if (rentalAdapter == null) {
            rentalAdapter = new RentalAdapter(rentalList, carlist,
                    rental -> updateRentalStatus(rental.getUserId(), rental.getRentalId(), "Confirmed"),
                    rental -> updateRentalStatus(rental.getUserId(), rental.getRentalId(), "Rejected"));

            binding.rcManageUserRents.setAdapter(rentalAdapter);
        }
    }

    private void updateRentalStatus(String userId, String rentalId, String newStatus) {
        FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("rentals")
                .document(rentalId)
                .update("status", newStatus)
                .addOnSuccessListener(aVoid -> Log.d(TAG, "Rental status updated"))
                .addOnFailureListener(e -> Log.e(TAG, "Failed to update status", e));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
