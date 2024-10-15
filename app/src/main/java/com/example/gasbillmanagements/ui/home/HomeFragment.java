package com.example.gasbillmanagements.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    @Nullable
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Ánh xạ các CardView
        CardView cardCustomer = binding.cardCustomer;
        CardView cardGasLevel = binding.cardGas;
        CardView cardAdd = binding.cardAdd;
        CardView cardSettings = binding.cardSettings;

        // Thêm sự kiện click cho các CardView
        cardCustomer.setOnClickListener(v -> navigateTo(R.id.nav_ListCustomer));
        cardGasLevel.setOnClickListener(v -> navigateTo(R.id.nav_gas));
        cardAdd.setOnClickListener(v -> navigateTo(R.id.nav_add));
        cardSettings.setOnClickListener(v -> navigateTo(R.id.nav_settings));

        return root;
    }

    private void navigateTo(int destinationId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        // Lấy ID của destination hiện tại
        int currentDestinationId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : -1;

        // Kiểm tra xem có cần điều hướng hay không
        if (currentDestinationId != destinationId) {
            navController.navigate(destinationId);
        } else {
            Snackbar.make(binding.getRoot(), "You are already here", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // Giải phóng binding
    }
}
