package com.example.carbooking.Adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.databinding.ItemRentalHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private final List<AppCar> rentalHistoryList = new ArrayList<>();

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRentalHistoryBinding binding = ItemRentalHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        AppCar car = rentalHistoryList.get(position);

        holder.binding.tvCarModel.setText(car.getName());



        String status = car.getStatus();
        holder.binding.tvStatus.setText(status);

        if ("Pending".equals(status)) {
            holder.binding.tvStatus.setTextColor(Color.parseColor("#FF9900")); // Orange
        } else if ("Confirmed".equals(status)) {
            holder.binding.tvStatus.setTextColor(Color.parseColor("#4CAF50")); // Green
        } else if ("Rejected".equals(status)) {
            holder.binding.tvStatus.setTextColor(Color.parseColor("#F44336")); // Red
        } else {
            holder.binding.tvStatus.setTextColor(Color.parseColor("#9E9E9E")); // Gray for unknown statuses
        }

        // holder.binding.tvRentalDate.setText(car.getRentalDate());
        //holder.binding.tvCarPrice.setText(String.valueOf(car.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .centerCrop()
                .into(holder.binding.imageCar);
        Log.d("history",car.getModel());
        Log.d("history",car.getStatus());
    }

    @Override
    public int getItemCount() {
        return rentalHistoryList.size();
    }

    public void setHistoryList(List<AppCar> historyList) {
        rentalHistoryList.clear();
        rentalHistoryList.addAll(historyList);
        notifyDataSetChanged();
    }

    public static class HistoryViewHolder extends RecyclerView.ViewHolder {
        ItemRentalHistoryBinding binding;

        public HistoryViewHolder(@NonNull ItemRentalHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
