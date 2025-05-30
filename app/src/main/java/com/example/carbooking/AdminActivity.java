package com.example.carbooking;

import static androidx.core.app.PendingIntentCompat.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.cloudinary.android.MediaManager;
import com.example.carbooking.databinding.ActivityAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class AdminActivity extends AppCompatActivity {

    private ActivityAdminBinding binding;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Map config = new HashMap();
        config.put("cloud_name", "drcicatxm");
        config.put("api_key", "738524273472178");
        config.put("secure", true);
        //MediaManager.init(AdminActivity.this, config);
        Log.d("AdminActivity", "Before loading fragment");
        loadFragment(new CarFragment());
        Log.d("AdminActivity", "After loading fragment");



        // Initialize ViewBinding
        binding = ActivityAdminBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();

        setContentView(binding.getRoot());
        // In your onCreate method
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");


        // Set toolbar as ActionBar
        setSupportActionBar(binding.toolbar);
        if (savedInstanceState == null) {
            loadFragment(new CarFragment());
            binding.navView.setCheckedItem(R.id.nav_car);
        }

        // Handle menu item clicks
        binding.navView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) {
                loadFragment(new DashboardFragment());
            } else if (id == R.id.nav_car) {
                loadFragment(new CarFragment());
            } else if (id == R.id.manage_user) {
                loadFragment(new ManageUserFragment());

            } else if (id == R.id.btn_signout) {
                signOut();
                return  true;
            }
            binding.drawerLayout.closeDrawers();
            return true;
        });




        // Toggle Sidebar (Hamburger Menu)
        toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
                R.string.open_drawer, R.string.close_drawer);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Helper method to load a fragment
    private void loadFragment(@NonNull Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(this, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}