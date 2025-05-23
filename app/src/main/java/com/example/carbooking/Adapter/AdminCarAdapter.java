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

import java.util.ArrayList;
import java.util.List;

public class AdminCarAdapter extends RecyclerView.Adapter<AdminCarAdapter.CarViewHolder> implements Filterable {

    public interface OnCarActionListener {
        void onEdit(AppCar car, int position);
    }

    private List<AppCar> carList;
    private List<AppCar> allCars;
    private OnCarActionListener listener;

    public AdminCarAdapter(List<AppCar> carList, OnCarActionListener listener) {
        this.carList = carList;
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

        // Load car image with placeholder and error fallback
        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .into(holder.binding.carImage);

        holder.binding.carModel.setText(car.getModel());
        holder.binding.carPrice.setText( "$"+ car.getPrice()+ "/day");

        if(car.getCarstatus() != null && car.getCarstatus()){
            holder.binding.carStatus.setText("Available");
            holder.binding.carStatus.setTextColor(Color.parseColor("#4CAF50"));

        }else {
            holder.binding.carStatus.setText("Rented");
            holder.binding.carStatus.setTextColor(Color.parseColor("#90A4AE"));
        }

//        holder.binding.carDescription.setText(car.getDescription());

        Log.d("CarAdapter", "Model: " + car.getModel());
        Log.d("CarAdapter", "Price: " + car.getPrice() + "/day");
        Log.d("CarAdapter", "Image: " + car.getImage());

        // Dot icon click -> edit action
        holder.editButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEdit(car, holder.getAdapterPosition());
            }
        });

//        // Delete icon click -> delete action (if you have one)
//        holder.deleteButton.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onDelete(car, holder.getAdapterPosition());
//            }
//        });



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




    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<AppCar> filtered = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filtered.addAll(allCars);
                } else {
                    String query = constraint.toString().toLowerCase().trim();
                    for (AppCar car : allCars) {
                        if (car.getName().toLowerCase().contains(query) ||
                                car.getModel().toLowerCase().contains(query)) {
                            filtered.add(car);
                        }
                    }
                }
                FilterResults results = new FilterResults();
                results.values = filtered;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                carList.clear();
                carList.addAll((List<AppCar>) results.values);
                notifyDataSetChanged();
            }
        };
    }



}
