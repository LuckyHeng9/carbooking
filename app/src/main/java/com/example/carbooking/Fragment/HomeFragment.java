package com.example.carbooking.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carbooking.Model.AppCar;
import com.example.carbooking.Adapter.CarAdapter;
import com.example.carbooking.databinding.FragmentHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {
    FragmentHomeBinding binding;
    private int currentPage = 1;
    private boolean isLoading = false;
    private static final int PRE_LOAD_ITEMS = 1;
    private FirebaseAuth mAuth;
    private CarAdapter carAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.rcListCars.setLayoutManager(layoutManager);
        carAdapter = new CarAdapter();
        binding.rcListCars.setAdapter(carAdapter);
        mAuth = FirebaseAuth.getInstance();

        binding.rcListCars.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + PRE_LOAD_ITEMS)) {
                    loadTasks(false);
                }
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        currentPage = 1;
        loadTasks(true);
    }

    private void loadTasks(boolean reset) {
        isLoading = true;
        showProgressBar();

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection("users")
                .document(uid)
                .collection("favoriteCar")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Set<String> favoriteIds = new HashSet<>();
                    for (QueryDocumentSnapshot doc : snapshot) {
                        String id = doc.getString("carId");
                        if (id != null) favoriteIds.add(id);
                    }
                    carAdapter.setFavoriteCarIds(favoriteIds);
                    loadCarData(reset);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error loading favorites", Toast.LENGTH_SHORT).show();
                    loadCarData(reset);
                });
    }

    private void loadCarData(boolean reset) {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars");

        carRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AppCar> carList = new ArrayList<>();
                for (DataSnapshot carSnap : snapshot.getChildren()) {
                    AppCar car = carSnap.getValue(AppCar.class);

                    if (car != null && Boolean.TRUE.equals(car.getAvailable())) {
                        carList.add(car);
                    }
                }

                if (reset) {
                    carAdapter.setCars(carList);
                } else {
                    List<AppCar> uniqueCars = new ArrayList<>();
                    for (AppCar car : carList) {
                        if (!carAdapter.containsCarId(car.getId())) {
                            uniqueCars.add(car);
                        }
                    }
                    carAdapter.addCars(uniqueCars);
                }

                hideProgressBar();
                isLoading = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load cars: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                hideProgressBar();
                isLoading = false;
            }
        });
    }
    public void filterCars(String query) {
        if (carAdapter != null) {
            carAdapter.filter(query);

            if (carAdapter.getItemCount() == 0) {
                binding.tvNotFound.setVisibility(View.VISIBLE);
            } else {
                binding.tvNotFound.setVisibility(View.GONE);
            }
        }
    }


    private void showProgressBar() {
        binding.carsProgressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        binding.carsProgressBar.setVisibility(View.GONE);
    }
}
