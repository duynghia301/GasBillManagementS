package com.example.gasbillmanagements.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.databinding.FragmentHomeBinding;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Sử dụng binding để ánh xạ các CardView
        CardView cardHome = binding.cardHome;
        CardView cardCustomer = binding.cardCustomer;
        CardView cardGasLevel = binding.cardGas;
        CardView cardSchedule = binding.cardSchedule;
        CardView cardAdd = binding.cardAdd;
        CardView cardSettings = binding.cardSettings;

        // Thêm sự kiện click cho các CardView
        cardHome.setOnClickListener(v -> navigateTo(R.id.nav_home));
        cardCustomer.setOnClickListener(v -> navigateTo(R.id.action_home_to_list_customer));
        cardGasLevel.setOnClickListener(v -> navigateTo(R.id.action_home_to_gas));
        cardSchedule.setOnClickListener(v -> navigateTo(R.id.action_home_to_task));
        cardAdd.setOnClickListener(v -> navigateTo(R.id.action_home_to_add));
        cardSettings.setOnClickListener(v -> navigateTo(R.id.action_home_to_settings));

        return root;
    }

    private void navigateTo(int actionId) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);

        // Lấy ID của destination hiện tại
        int currentDestinationId = navController.getCurrentDestination().getId();

        // Kiểm tra xem có cần điều hướng hay không
        if (currentDestinationId != actionId) {
            navController.navigate(actionId);
        } else {
            // Nếu đã ở destination này, có thể hiển thị thông báo hoặc không làm gì
            Snackbar.make(binding.getRoot(), "You are already here", Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
