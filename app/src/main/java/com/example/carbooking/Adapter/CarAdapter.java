package com.example.carbooking.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbooking.CarDetailsActivity;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.databinding.ItemCarsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {
    private final List<AppCar> cars;
    private final Set<String> favoriteCarIds;
    private final List<AppCar> filteredCars;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private FirebaseUser user;

    public CarAdapter() {
        this.cars = new ArrayList<>();
        this.favoriteCarIds = new HashSet<>();
        this.filteredCars = new ArrayList<>();
    }

    public CarAdapter(List<AppCar> carList) {
        this.cars = new ArrayList<>(carList);
        this.favoriteCarIds = new HashSet<>();
        this.filteredCars = new ArrayList<>(carList);
    }

    public void setFavoriteCarIds(Set<String> favoriteCarIds) {
        this.favoriteCarIds.clear();
        this.favoriteCarIds.addAll(favoriteCarIds);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        ItemCarsBinding binding = ItemCarsBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new CarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        AppCar car = filteredCars.get(position);

        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .centerCrop()
                .into(holder.binding.ivCar);

        holder.binding.tvCarName.setText(car.getName());
        holder.binding.tvCarPrice.setText(String.format("$%s/day", car.getPrice()));

        boolean isFavorite = favoriteCarIds.contains(car.getId());
        holder.binding.btnFavorite.setColorFilter(isFavorite ? Color.RED : Color.BLACK);

        holder.binding.btnFavorite.setOnClickListener(v -> {
            if (user == null) {
                Toast.makeText(holder.itemView.getContext(), "Please log in", Toast.LENGTH_SHORT).show();
                return;
            }

            if (isFavorite) {
                removeFavoriteCar(holder.itemView.getContext(), user.getUid(), car.getId());
                favoriteCarIds.remove(car.getId());
                holder.binding.btnFavorite.setColorFilter(Color.BLACK);
            } else {
                addFavoriteCar(holder.itemView.getContext(), user.getUid(), car.getId());
                favoriteCarIds.add(car.getId());
                holder.binding.btnFavorite.setColorFilter(Color.RED);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CarDetailsActivity.class);
            intent.putExtra("carId", car.getId());
            holder.itemView.getContext().startActivity(intent);
        });

        holder.binding.btnRent.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), CarDetailsActivity.class);
            intent.putExtra("carId", car.getId());
            holder.itemView.getContext().startActivity(intent);
        });
    }

    private void addFavoriteCar(Context context, String uid, String carId) {
        Map<String, Object> data = new HashMap<>();
        data.put("carId", carId);
        data.put("timestamp", FieldValue.serverTimestamp());

        db.collection("users").document(uid)
                .collection("favoriteCar").document(carId)
                .set(data)
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to add", Toast.LENGTH_SHORT).show());
    }

    private void removeFavoriteCar(Context context, String uid, String carId) {
        db.collection("users").document(uid)
                .collection("favoriteCar").document(carId)
                .delete()
                .addOnSuccessListener(unused ->
                        Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(context, "Failed to remove", Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return filteredCars.size();
    }

    public void setCars(List<AppCar> newCars) {
        cars.clear();
        cars.addAll(newCars);
        filteredCars.clear();
        filteredCars.addAll(newCars);
        notifyDataSetChanged();
    }

    public void addCars(List<AppCar> newCars) {
        int start = cars.size();
        cars.addAll(newCars);
        filteredCars.addAll(newCars);
        notifyItemRangeInserted(start, newCars.size());
    }

    public void filter(String query) {
        filteredCars.clear();
        if (query.isEmpty()) {
            filteredCars.addAll(cars);
        } else {
            for (AppCar car : cars) {
                if (car.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredCars.add(car);
                }
            }
        }
        notifyDataSetChanged();
    }

    public boolean containsCarId(String carId) {
        for (AppCar car : cars) {
            if (car.getId().equals(carId)) return true;
        }
        return false;
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        ItemCarsBinding binding;

        public CarViewHolder(@NonNull ItemCarsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
