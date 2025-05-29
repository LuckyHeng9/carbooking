package com.example.carbooking.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.carbooking.Adapter.HistoryAdapter;
import com.example.carbooking.Login;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.R;
import com.example.carbooking.UserAccountSetting;
import com.example.carbooking.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.*;
import com.google.firebase.firestore.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private HistoryAdapter historyAdapter;
    private final List<AppCar> rentalHistoryList = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();
        recyclerView = view.findViewById(R.id.rvRentalHistory);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        historyAdapter = new HistoryAdapter();
        recyclerView.setAdapter(historyAdapter);


        // Redirect to LoginActivity if user is not logged in
        if (user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return view;
        } else {
            String uid = user.getUid();

            // Load user profile info
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String userName = getString(R.string.username, firstName, lastName);
                            binding.tvUsername.setText(userName);
                        } else {
                            Log.w("Profile", "User data not found.");
                        }
                    })
                    .addOnFailureListener(e -> Log.e("Login", "Failed to fetch user data", e));

            long creationMillis = user.getMetadata().getCreationTimestamp();
            Date creationDate = new Date(creationMillis);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedCreationDate = sdf.format(creationDate);
            String joined = getString(R.string.joined, formattedCreationDate);
            binding.tvCreatedAt.setText(joined);

            // 🔥 Load rental history
            loadRentalHistoryFromFirestore(uid);
        }

        binding.btnLogout.setOnClickListener(v -> signOut());
        binding.btnGoSetting.setOnClickListener(v -> goSetting());

        return view;
    }

    private void loadRentalHistoryFromFirestore(String uid) {
        db.collection("users").document(uid).collection("rentals")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                            String carId = doc.getString("carId");
                            String rentedDate = doc.getString("rentedDate");

                            if (carId != null) {
                                loadCarFromRealtimeDB(carId);
                            }
                        }
                    } else {
                        Log.d("rental_history", "No rentals found.");
                    }
                })
                .addOnFailureListener(e -> Log.e("rental_history", "Failed to load rentals", e));
    }

    private void loadCarFromRealtimeDB(String carId) {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars").child(carId);
        carRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    AppCar car = snapshot.getValue(AppCar.class);
                    if (car != null) {
                       // car.setRentedDate(rentedDate); // Add rental date
                        rentalHistoryList.add(car);
                        historyAdapter.setHistoryList(rentalHistoryList);
                        Log.d("history",car.getModel());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("history", "Failed to fetch car", error.toException());
            }
        });
    }


    private void goSetting() {
        Intent intent = new Intent(getActivity(), UserAccountSetting.class);
        startActivity(intent);
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
