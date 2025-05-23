package com.example.carbooking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.carbooking.Fragment.ProfileFragment;
import com.example.carbooking.model.AppUser;


import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;



public class UserAccountSetting extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private TextInputEditText etEditFirstName,etEditLastName,etEditPhoneNumber;
    private Button btnSave;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_account_setting);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        btnSave = findViewById(R.id.btn_save);
        etEditFirstName = findViewById(R.id.et_editFirstName);
        etEditLastName = findViewById(R.id.et_editLastName);
        etEditPhoneNumber = findViewById(R.id.et_editPhone);

        if (user != null) {
            String uid = user.getUid();

            // Load current data from Firestore
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String phoneNumber = document.getString("phoneNumber");

                            etEditFirstName.setText(firstName);
                            etEditLastName.setText(lastName);
                            etEditPhoneNumber.setText(phoneNumber);
                        } else {
                            Log.w("UserSetting", "User data not found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("UserSetting", "Failed to fetch user data", e);
                    });

            btnSave.setOnClickListener(v -> {
                String newFirstName = etEditFirstName.getText() != null ? etEditFirstName.getText().toString().trim() : "";
                String newLastName = etEditLastName.getText() != null ? etEditLastName.getText().toString().trim() : "";
                String newPhoneNumber = etEditPhoneNumber.getText() != null ? etEditPhoneNumber.getText().toString().trim() : "";

                if (newFirstName.isEmpty() || newLastName.isEmpty() || newPhoneNumber.isEmpty()) {
                    Log.w("UserSetting", "Empty fields!");
                    return;
                }

                SaveUpdate(uid, newFirstName, newLastName, newPhoneNumber);
            });

        }
    }

    private void SaveUpdate(String uid, String firstName, String lastName, String phoneNumber) {
        db.collection("users").document(uid)
                .update(
                        "firstName", firstName,
                        "lastName", lastName,
                        "phoneNumber", phoneNumber
                )
                .addOnSuccessListener(aVoid -> {
                    Intent intent = new Intent(getApplicationContext(), ProfileFragment.class);
                    startActivity(intent);
                    Log.d("UserSetting", "User fields updated successfully");
                })
                .addOnFailureListener(e -> {
                    Log.w("UserSetting", "Failed to update user fields", e);
                });
    }


}