package com.example.carbooking.Adapter;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import com.example.carbooking.Model.AppCar;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.carbooking.R;
import com.example.carbooking.databinding.AdminCarItemsBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class AdminCarAdapter extends RecyclerView.Adapter<AdminCarAdapter.CarViewHolder> {
    public interface OnCarActionListener {
        void onEdit(AppCar car, int position);
    }

    private List<AppCar> carList;
    private List<AppCar> allCars;
    private OnCarActionListener listener;

    public AdminCarAdapter(List<AppCar> carList, OnCarActionListener listener) {
        this.carList = new ArrayList<>(carList);
        this.listener = listener;
        this.allCars = new ArrayList<>(carList);

    }

    @NonNull
    @Override
    public CarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdminCarItemsBinding binding = AdminCarItemsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CarViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CarViewHolder holder, int position) {
        AppCar car = carList.get(position);



        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .into(holder.binding.carImage);

        holder.binding.carModel.setText(car.getModel());
        holder.binding.carPrice.setText("$" + car.getPrice() + "/day");

        // Show availability based on boolean 'available' field, NOT on status
        if (car.getAvailable() != null && car.getAvailable()) {
            holder.binding.carStatus.setText("Available");
            holder.binding.carStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else {
            holder.binding.carStatus.setText("Rented");
            holder.binding.carStatus.setTextColor(Color.parseColor("#90A4AE")); // Grayish
        }

        Log.d("CarAdapter", "Model: " + car.getModel());
        Log.d("CarAdapter", "Price: " + car.getPrice() + "/day");
        Log.d("CarAdapter", "Image: " + car.getImage());

        // Edit button click
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(car, holder.getAdapterPosition());
            }
        });

    }


    private void updateCarAvailability(String carId, boolean isAvailable) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("cars") // your collection name
                .document(carId)
                .update("available", isAvailable)  // update the correct field name
                .addOnSuccessListener(aVoid -> Log.d("AdminCarAdapter", "Car availability updated"))
                .addOnFailureListener(e -> Log.e("AdminCarAdapter", "Failed to update car availability", e));
    }



    @Override
    public int getItemCount() {
        return carList.size();
    }

    public void updateCarList(List<AppCar> newList) {
        this.carList = new ArrayList<>(newList);
        this.allCars = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public static class CarViewHolder extends RecyclerView.ViewHolder {
        private final AdminCarItemsBinding binding;

        public ImageButton editButton;
        public ImageButton deleteButton;

        public CarViewHolder(@NonNull AdminCarItemsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            editButton = itemView.findViewById(R.id.dot);
        }
    }
    public boolean isListEmpty() {
        return carList.isEmpty();
    }

    public void filter(String query) {
        Log.d("CarFilter", "=== FILTER START ===");
        Log.d("CarFilter", "Query: '" + query + "'");
        Log.d("CarFilter", "allCars size: " + (allCars != null ? allCars.size() : "NULL"));

        List<AppCar> filteredList = new ArrayList<>();

        if (query == null || query.trim().isEmpty()) {
            // When search is cleared, restore all cars
            if (allCars != null) {
                filteredList.addAll(allCars);
                Log.d("CarFilter", "Empty query - restoring all cars: " + filteredList.size());
            }
        } else {
            String filterPattern = query.toLowerCase().trim();
            Log.d("CarFilter", "Searching for: '" + filterPattern + "'");

            if (allCars != null) {
                for (AppCar car : allCars) {
                    // Try both name and model to see which one works
                    String carName = car.getName();
                    String carModel = car.getModel();

                    Log.d("CarFilter", "Car - Name: '" + carName + "', Model: '" + carModel + "'");

                    boolean matchFound = false;

                    // Check name first
                    if (carName != null && carName.toLowerCase().contains(filterPattern)) {
                        matchFound = true;
                        Log.d("CarFilter", "MATCH by name: " + carName);
                    }
                    // If no name match, check model
                    else if (carModel != null && carModel.toLowerCase().contains(filterPattern)) {
                        matchFound = true;
                        Log.d("CarFilter", "MATCH by model: " + carModel);
                    }

                    if (matchFound) {
                        filteredList.add(car);
                    }
                }
            }

            Log.d("CarFilter", "Total matches found: " + filteredList.size());
        }

        // Update the list
        carList.clear();
        carList.addAll(filteredList);

        Log.d("CarFilter", "Final carList size: " + carList.size());
        Log.d("CarFilter", "=== FILTER END ===");

        notifyDataSetChanged();
    }




//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                List<AppCar> filtered = new ArrayList<>();
//                if (constraint == null || constraint.length() == 0) {
//                    filtered.addAll(allCars);
//                } else {
//                    String query = constraint.toString().toLowerCase().trim();
//                    for (AppCar car : allCars) {
//                        if (car.getName().toLowerCase().contains(query) ||
//                                car.getModel().toLowerCase().contains(query)) {
//                            filtered.add(car);
//                        }
//                    }
//                }
//                FilterResults results = new FilterResults();
//                results.values = filtered;
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                carList.clear();
//                carList.addAll((List<AppCar>) results.values);
//                notifyDataSetChanged();
//            }
//        };
//    }



}
