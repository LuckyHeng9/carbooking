package com.example.carbooking.Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carbooking.AdminActivity;
import com.example.carbooking.HomeActivity;
import com.example.carbooking.Login;
import com.example.carbooking.R;
import com.example.carbooking.UserAccountSetting;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private Button btnLogout;
    private LinearLayout btnGoSetting;
    private View view;
    private FirebaseFirestore db;
    private TextView tvUserName;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Assign the inflated view
        view = inflater.inflate(R.layout.fragment_profile, container, false);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        db = FirebaseFirestore.getInstance();

        btnLogout = view.findViewById(R.id.btnLogout);
        btnGoSetting = view.findViewById(R.id.btn_goSetting);

        // Redirect to LoginActivity if user is not logged in
        if (user == null) {
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return view;
        }else{
            String uid = user.getUid();
            tvUserName = view.findViewById(R.id.tv_username);
            db.collection("users").document(uid).get()
                    .addOnSuccessListener(document -> {
                        if (document.exists()) {
                            String firstName = document.getString("firstName");
                            String lastName = document.getString("lastName");
                            String userName = getString(R.string.username,firstName,lastName);
                            tvUserName.setText(userName);
                        } else {
                            Log.w("Login", "User data not found.");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Login", "Failed to fetch user data", e);
                    });
            long creationMillis = user.getMetadata().getCreationTimestamp();
            Date creationDate = new Date(creationMillis);

            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            String formattedCreationDate = sdf.format(creationDate);
            String joined = getString(R.string.joined,formattedCreationDate);


            // Optionally display it in a TextView
            TextView tvCreatedAt = view.findViewById(R.id.tv_createdAt);
            tvCreatedAt.setText(joined);
        }

        btnLogout.setOnClickListener(v -> signOut());
        btnGoSetting.setOnClickListener(v -> GoSetting());

        return view;
    }

    private void GoSetting() {
        Intent intent = new Intent(getActivity(), UserAccountSetting.class);
        startActivity(intent);
    }

    private void signOut() {
        auth.signOut();
        Intent intent = new Intent(getActivity(), Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
