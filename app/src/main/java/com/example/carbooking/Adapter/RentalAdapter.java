package com.example.carbooking.Adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.R;
import com.example.carbooking.databinding.ManageUserCarRentsBinding;
import com.example.carbooking.Model.Rental;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RentalAdapter extends RecyclerView.Adapter<RentalAdapter.ManageUserRentsCarViewHolder> {

    private final List<Rental> rentals;
    private final Map<String, AppCar> carMap;  // Maps carId to AppCar
    private FirebaseAuth auth;
    private FirebaseUser user;
    private final OnActionListener onApprove;
    private final OnActionListener onCancel;

    public interface OnActionListener {
        void onAction(Rental rental);
    }

    public RentalAdapter(List<Rental> rentals, List<AppCar> carList, OnActionListener onApprove, OnActionListener onCancel) {
        this.rentals = rentals;
        this.carMap = new HashMap<>();
        for (AppCar car : carList) {
            carMap.put(car.getId(), car);
        }
        this.onApprove = onApprove;
        this.onCancel = onCancel;
    }

    @NonNull
    @Override
    public ManageUserRentsCarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ManageUserCarRentsBinding binding = ManageUserCarRentsBinding.inflate(inflater, parent, false);
        return new ManageUserRentsCarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ManageUserRentsCarViewHolder holder, int position) {
        Rental rental = rentals.get(position);
        AppCar car = carMap.get(rental.getCarId());
        Log.d("RentalAdapter", "Binding rental for carId: " + rental.getCarId() + " at position " + position);
        holder.bind(rental, car);
    }

    @Override
    public int getItemCount() {
        Log.d("RentalAdapter", "getItemCount: " + rentals.size());
        return rentals.size();
    }

    public void setData(List<Rental> newRentals, List<AppCar> newCarList) {
        rentals.clear();
        rentals.addAll(newRentals);

        carMap.clear();
        for (AppCar car : newCarList) {
            carMap.put(car.getId(), car);
        }
        notifyDataSetChanged();
    }

    class ManageUserRentsCarViewHolder extends RecyclerView.ViewHolder {
        ManageUserCarRentsBinding binding;
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        public ManageUserRentsCarViewHolder(ManageUserCarRentsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Rental rental, AppCar car) {
            if (car == null || car.getId() == null) {
                // Hide the entire item view if car or car ID is null
                itemView.setVisibility(View.GONE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                return;
            } else {
                // Make sure it's visible if previously hidden
                itemView.setVisibility(View.VISIBLE);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));
            }
            FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(rental.getUserId())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            String userName = documentSnapshot.getString("firstName");
                            binding.userName.setText(userName != null ? userName : "Unknown User");
                        } else {
                            binding.userName.setText("Unknown User");
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.userName.setText("Failed to load user");
                        Log.e("RentalAdapter", "Error loading user info", e);
                    });




            binding.carModel.setText(car.getModel());
            Glide.with(binding.getRoot().getContext())
                    .load(car.getImage())
                    .into(binding.carImage);

            // Date formatting can be added here again if needed

            // Status display and button visibility
            String status = rental.getStatus();
            binding.tvStatus.setText(status);

            if ("Confirmed".equalsIgnoreCase(status)) {
                binding.tvStatus.setBackgroundResource(R.drawable.status_background_confirmed);
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            } else if ("Rejected".equalsIgnoreCase(status)) {
                binding.tvStatus.setBackgroundResource(R.drawable.status_background_pending);
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            } else {
                binding.tvStatus.setText("Pending");
                binding.tvStatus.setBackgroundResource(R.drawable.status_background_pending);
                binding.btnApprove.setVisibility(View.VISIBLE);
                binding.btnCancel.setVisibility(View.VISIBLE);
            }

            binding.btnApprove.setOnClickListener(v -> {
                onApprove.onAction(rental);
                rental.setStatus("Confirmed");

                DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars")
                        .child(rental.getCarId());

                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Confirmed");
                updates.put("available", false);
                carRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Log.d("RentalAdapter", "Car status updated to Confirmed"))
                        .addOnFailureListener(e -> Log.e("RentalAdapter", "Failed to update car status", e));

                binding.tvStatus.setText("Confirmed");
                binding.tvStatus.setBackgroundResource(R.drawable.status_background_confirmed);
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            });

            binding.btnCancel.setOnClickListener(v -> {
                onCancel.onAction(rental);
                rental.setStatus("Rejected");

                DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars")
                        .child(rental.getCarId());

                Map<String, Object> updates = new HashMap<>();
                updates.put("status", "Rejected");
                updates.put("available", true);
                carRef.updateChildren(updates)
                        .addOnSuccessListener(aVoid -> Log.d("RentalAdapter", "Car status updated to Rejected"))
                        .addOnFailureListener(e -> Log.e("RentalAdapter", "Failed to update car status", e));

                binding.tvStatus.setText("Rejected");
                binding.tvStatus.setBackgroundResource(R.drawable.status_background_pending);
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            });
        }

    }
}
