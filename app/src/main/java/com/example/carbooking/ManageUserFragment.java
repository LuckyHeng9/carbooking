package com.example.carbooking;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carbooking.databinding.FragmentManageUserBinding;

public class ManageUserFragment extends Fragment {

   private FragmentManageUserBinding binding;
    ManageUserFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = FragmentManageUserBinding.inflate(inflater,container,false);
        ClickButton();
        return binding.getRoot();
    }

    private void ClickButton() {
        binding.btnApprove.setOnClickListener(v -> {

            binding.tvStatus.setText("Confirmed");
            binding.tvStatus.setBackgroundResource(R.drawable.status_background_confirmed);
            // When Approve is clicked, only hide the Cancel button
            if (binding.btnCancel.getVisibility() == View.VISIBLE) {
                binding.btnCancel.setVisibility(View.GONE);

            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            binding.tvStatus.setText("Rejected");
            binding.tvStatus.setBackgroundResource(R.drawable.status_background_pending);
            // When Cancel is clicked, hide both buttons
            if (binding.btnApprove.getVisibility() == View.VISIBLE && binding.btnCancel.getVisibility() == View.VISIBLE) {
                binding.btnApprove.setVisibility(View.GONE);
                binding.btnCancel.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}