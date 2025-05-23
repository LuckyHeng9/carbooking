package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        checkAuth();
    }

    private void checkAuth() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String role = document.getString("user_role");
                            if ("admin".equals(role)) {
                                goToAdminActivity();
                            } else {
                                goToHomeActivity();
                            }
                        } else {
                            goToLoginActivity(); // fallback
                        }
                    })
                    .addOnFailureListener(e -> goToLoginActivity());
        } else {
            goToLoginActivity();
        }
    }

    private void goToHomeActivity() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    private void goToAdminActivity() {
        startActivity(new Intent(this, AdminActivity.class));
        finish();
    }

    private void goToLoginActivity() {
        startActivity(new Intent(this, Login.class));
        finish();
    }
}
