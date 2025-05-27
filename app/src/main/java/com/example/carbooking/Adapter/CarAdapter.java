package com.example.carbooking.Adapter;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.content.res.ColorStateList;
import android.graphics.Color;

import com.bumptech.glide.Glide;
import com.example.carbooking.CarDetailsActivity;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.R;
import com.example.carbooking.databinding.ItemCarsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private final List<AppCar> cars;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;
    public CarAdapter() {
        this.cars = new ArrayList<>();
    }
    private boolean isFavorite = false;
    // Optional: Constructor with a list
    public CarAdapter(List<AppCar> carList) {
        this.cars = new ArrayList<>(carList);
    }
    @NonNull
    @Override
    public CarAdapter.CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ItemCarsBinding binding = ItemCarsBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false);
        return new CarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarAdapter.CarViewHolder holder, int position) {
        AppCar car = cars.get(position);
        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .centerCrop()
                .into(holder.binding.ivCar);

        holder.binding.tvCarName.setText(car.getName());
        holder.binding.tvCarPrice.setText(car.getPrice());




        String carId = car.getId();
        // Handle click to toggle favorite
        holder.binding.btnFavorite.setOnClickListener(v -> {
//            holder.binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            Toast.makeText(
                    v.getContext(),                          // context from the clicked view
                    "Added to favorites",                    // message
                    Toast.LENGTH_SHORT                       // duration
            ).show();
            holder.binding.btnFavorite.setColorFilter(Color.parseColor("#FF0000"));
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                isFavorite = true;
                StoreFavoriteCar(holder.itemView.getContext(), user.getUid(), car.getId());
            } else {
                Toast.makeText(holder.itemView.getContext(), "Please log in", Toast.LENGTH_SHORT).show();
            }

        });

        // Navigate to DetailExpenseActivity and pass only the expense ID
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CarDetailsActivity.class);
            intent.putExtra("carId", car.getId()); // Pass only the ID
            holder.itemView.getContext().startActivity(intent);
        });
    }


    private void StoreFavoriteCar(Context context, String uid, String carId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> favoriteData = new HashMap<>();
        favoriteData.put("carId", carId);
        favoriteData.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users")
                .document(uid)
                .collection("favoriteCar")
                .document(carId)
                .set(favoriteData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                    // Optionally update UI here
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Failed to add favorite", Toast.LENGTH_SHORT).show();
                });
    }


    @Override
    public int getItemCount() {
        return cars.size();
    }
    // allow user to initialize the cars
    public void setCars(List<AppCar> newExpense) {
        cars.clear();
        cars.addAll(newExpense);
        notifyDataSetChanged();
    }

    // allow user to append new cars when receiving new car from real time database of firebase
    public void addCars(List<AppCar> newCar) {
        int startPosition = cars.size();
        cars.addAll(newCar);
        notifyItemRangeInserted(startPosition, newCar.size());
    }

    public class CarViewHolder extends RecyclerView.ViewHolder{
        private ItemCarsBinding binding;
        public CarViewHolder(@NonNull ItemCarsBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }
    }
    public boolean containsCarId(String carId) {
        for (AppCar car : cars) {
            if (car.getId().equals(carId)) {
                return true;
            }
        }
        return false;
    }

}
