package com.example.carbooking.Fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.carbooking.Adapter.CarAdapter;
import com.example.carbooking.Model.AppCar;
import com.example.carbooking.R;
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

public class FavoriteFragment extends Fragment {

    private RecyclerView recyclerView;
    private CarAdapter carAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        recyclerView = view.findViewById(R.id.rv_favorites);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        carAdapter = new CarAdapter();
        recyclerView.setAdapter(carAdapter);

        loadFavoriteCars();
        return view;
    }

    private void loadFavoriteCars() {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .collection("favoriteCar")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<String> favoriteCarIds = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        String carId = doc.getString("carId");
                        if (carId != null) {
                            favoriteCarIds.add(carId);
                        }
                    }

                    if (!favoriteCarIds.isEmpty()) {
                        // Update adapter with favorite IDs for red heart
                        Set<String> favoriteIdSet = new HashSet<>(favoriteCarIds);
                        carAdapter.setFavoriteCarIds(favoriteIdSet);
                        loadCarsByIds(favoriteCarIds);
                    } else {
                        carAdapter.setCars(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to load favorites", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadCarsByIds(List<String> carIds) {
        DatabaseReference carRef = FirebaseDatabase.getInstance().getReference("cars");

        carRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<AppCar> favoriteCars = new ArrayList<>();
                for (DataSnapshot carSnap : snapshot.getChildren()) {
                    AppCar car = carSnap.getValue(AppCar.class);
                    if (car != null && carIds.contains(car.getId())) {
                        favoriteCars.add(car);
                    }
                }

                carAdapter.setCars(favoriteCars);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Failed to load car data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
