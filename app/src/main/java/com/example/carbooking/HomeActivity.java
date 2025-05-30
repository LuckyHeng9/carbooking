package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.carbooking.Adapter.CarAdapter;
import com.example.carbooking.Fragment.ExploreFragment;
import com.example.carbooking.Fragment.FavoriteFragment;
import com.example.carbooking.Fragment.HomeFragment;
import com.example.carbooking.Fragment.ProfileFragment;
import com.example.carbooking.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {
    FirebaseAuth auth;
    ActivityHomeBinding binding;
    CarAdapter carAdapter;

    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        auth = FirebaseAuth.getInstance();
        carAdapter = new CarAdapter();
        HomeFragment homeFragment = new HomeFragment();

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.svHome.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                homeFragment.filterCars(newText);
                return true;
            }
        });



        binding.bottomNav.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                showSearchView(true); // 👈 Show SearchView
                LoadFragment(homeFragment);
            } else if (itemId == R.id.nav_explore) {
                showSearchView(false); // 👈 Hide SearchView
                LoadFragment(new ExploreFragment());
            } else if (itemId == R.id.nav_favorite) {
                showSearchView(false);
                LoadFragment(new FavoriteFragment());
            } else if (itemId == R.id.nav_profile) {
                showSearchView(false);
                LoadFragment(new ProfileFragment());
            } else {
                return false;
            }
            return true;
        });
        if (savedInstanceState == null) {
            binding.bottomNav.setSelectedItemId(R.id.nav_home);
            showSearchView(true); // 👈 Show initially
        } else {
            int id = savedInstanceState.getInt("selectedItemId");
            binding.bottomNav.setSelectedItemId(id);
            showSearchView(id == R.id.nav_home);
        }

    }
    public void showSearchView(boolean show) {
        if (show) {
            binding.svHome.setVisibility(View.VISIBLE);
        } else {
            binding.svHome.setVisibility(View.GONE);
        }
    }


    private void LoadFragment(Fragment fragment) {
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fragmentContainer,fragment)
                .commit();
    }
}
