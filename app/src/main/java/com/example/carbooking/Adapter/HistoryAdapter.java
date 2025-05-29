package com.example.carbooking.Adapter;

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
       // holder.binding.tvRentalDate.setText(car.getRentalDate());
        //holder.binding.tvCarPrice.setText(String.valueOf(car.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(car.getImage())
                .centerCrop()
                .into(holder.binding.imageCar);
        Log.d("history",car.getModel());
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
