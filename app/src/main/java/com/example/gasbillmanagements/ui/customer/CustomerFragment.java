package com.example.gasbillmanagements.ui.customer;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;
import com.example.gasbillmanagements.ui.settings.OnSettingsChangeListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerFragment extends Fragment implements OnSettingsChangeListener {
    private int customerId;
    private DatabaseHelper databaseHelper;

    private TextView tvName;
    private TextView tvAddress;
    private TextView tvUsedNumGas;
    private TextView tvGasLevel;
    private TextView tvBirth;
    private TextView tvPrice;
    private List<Integer> customerIds;

    private static final String PREFS_NAME = "MusicSettings";

    @Override
    public void onSettingsChanged(boolean isAddressVisible, boolean isUsedNumGasVisible, boolean isGasLevelVisible, boolean isPriceVisible) {
        // Cập nhật trạng thái hiển thị của các TextView khi cài đặt thay đổi
        tvAddress.setVisibility(isAddressVisible ? View.VISIBLE : View.GONE);
        tvUsedNumGas.setVisibility(isUsedNumGasVisible ? View.VISIBLE : View.GONE);
        tvGasLevel.setVisibility(isGasLevelVisible ? View.VISIBLE : View.GONE);
        tvPrice.setVisibility(isPriceVisible ? View.VISIBLE : View.GONE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);
        databaseHelper = new DatabaseHelper(getActivity());

        tvName = view.findViewById(R.id.tv_name);
        tvAddress = view.findViewById(R.id.tv_address);
        tvUsedNumGas = view.findViewById(R.id.tv_used_num_gas);
        tvGasLevel = view.findViewById(R.id.tv_gas_level);
        tvBirth = view.findViewById(R.id.tv_birth);
        tvPrice = view.findViewById(R.id.tv_price);

        // Khôi phục trạng thái hiển thị từ SharedPreferences
        setVisibilityFromPreferences();
        loadCustomerIds();
        if (getArguments() != null) {
            customerId = getArguments().getInt("customer_id", -1);
            displayCustomerDetails(customerId);
        }

        // Nút điều hướng
        ImageButton btnPrevious = view.findViewById(R.id.btn_previous);
        btnPrevious.setOnClickListener(v -> {
            int currentIndex = customerIds.indexOf(customerId);
            if (currentIndex > 0) {
                navigateToCustomer(customerIds.get(currentIndex - 1));
            } else {
                Toast.makeText(getActivity(), "No more customers", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnNext = view.findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            int currentIndex = customerIds.indexOf(customerId);
            if (currentIndex < customerIds.size() - 1) {
                navigateToCustomer(customerIds.get(currentIndex + 1));
            } else {
                Toast.makeText(getActivity(), "No more customers", Toast.LENGTH_SHORT).show();
            }
        });

        ImageButton btnFirst = view.findViewById(R.id.btn_first);
        btnFirst.setOnClickListener(v -> navigateToCustomer(databaseHelper.getFirstCustomerId()));

        ImageButton btnLast = view.findViewById(R.id.btn_last);
        btnLast.setOnClickListener(v -> navigateToCustomer(databaseHelper.getLastCustomerId()));

        // Nút chỉnh sửa
        ImageButton editButton = view.findViewById(R.id.imageButton);
        editButton.setOnClickListener(v -> {
            try {
                Bundle bundle = new Bundle();
                bundle.putInt("customer_id", customerId);
                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_customer_to_edit, bundle);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Nút xóa
        ImageButton btnDelete = view.findViewById(R.id.btn_delete); // Thêm nút xóa
        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());

        return view;
    }

    private void setVisibilityFromPreferences() {
        SharedPreferences preferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        tvAddress.setVisibility(preferences.getBoolean("switch_address", true) ? View.VISIBLE : View.GONE);
        tvUsedNumGas.setVisibility(preferences.getBoolean("switch_used_num_gas", true) ? View.VISIBLE : View.GONE);
        tvGasLevel.setVisibility(preferences.getBoolean("switch_gas_level", true) ? View.VISIBLE : View.GONE);
        tvPrice.setVisibility(preferences.getBoolean("switch_price", true) ? View.VISIBLE : View.GONE);
    }

    private void displayCustomerDetails(int customerId) {
        Cursor cursor = null;
        Cursor gasLevelCursor = null;
        try {
            if (customerId != -1) {
                cursor = databaseHelper.getCustomerById(customerId);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
                    int usedNumGasValue = cursor.getInt(cursor.getColumnIndexOrThrow("USED_NUM_GAS"));
                    int gasLevelTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("GAS_LEVEL_TYPE_ID"));

                    // Lấy thông tin về UNIT_PRICE, MAX_NUM_GAS và RATE
                    gasLevelCursor = databaseHelper.getGasLevelById(gasLevelTypeId);
                    float unitPrice = 0.0f;
                    int maxNumGas = 0;
                    float rate = 0.0f;

                    if (gasLevelCursor != null && gasLevelCursor.moveToFirst()) {
                        unitPrice = gasLevelCursor.getFloat(gasLevelCursor.getColumnIndexOrThrow("UNIT_PRICE"));
                        maxNumGas = gasLevelCursor.getInt(gasLevelCursor.getColumnIndexOrThrow("MAX_NUM_GAS"));
                        rate = gasLevelCursor.getFloat(gasLevelCursor.getColumnIndexOrThrow("RATE_PRICE_FOR_OVER"));
                    }

                    // Tính toán PRICE
                    float price = calculatePrice(usedNumGasValue, unitPrice, maxNumGas, rate);

                    // Hiển thị thông tin
                    tvName.setText(name);
                    tvAddress.setText(address);
                    tvBirth.setText(cursor.getString(cursor.getColumnIndexOrThrow("YYYYMM")));
                    tvUsedNumGas.setText(String.valueOf(usedNumGasValue));
                    tvGasLevel.setText(String.valueOf(gasLevelTypeId));
                    tvPrice.setText(String.valueOf(price));

                } else {
                    Toast.makeText(getActivity(), "Customer not found", Toast.LENGTH_SHORT).show();
                    Log.d("CustomerFragment", "Customer not found for ID: " + customerId);
                }
            } else {
                Toast.makeText(getActivity(), "Invalid customer ID", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (gasLevelCursor != null) {
                gasLevelCursor.close();
            }
        }
    }

    private float calculatePrice(int usedNumGas, float unitPrice, int maxNumGas, float rate) {
        if (usedNumGas <= maxNumGas) {
            // Nếu USED_NUM_GAS <= MAX_NUM_GAS, giá sẽ là USED_NUM_GAS * UNIT_PRICE
            return usedNumGas * unitPrice;
        } else {
            // Nếu USED_NUM_GAS > MAX_NUM_GAS, giá sẽ là MAX_NUM_GAS * UNIT_PRICE + (USED_NUM_GAS - MAX_NUM_GAS) * UNIT_PRICE * RATE
            float basePrice = maxNumGas * unitPrice;
            float excessPrice = (usedNumGas - maxNumGas) * unitPrice * rate;
            return basePrice + excessPrice;
        }
    }
    private void loadCustomerIds() {
        customerIds = new ArrayList<>();
        Cursor cursor = databaseHelper.getAllCustomerIds(); // Thay bằng phương thức thực tế để lấy danh sách ID
        while (cursor.moveToNext()) {
            customerIds.add(cursor.getInt(0)); // Giả sử ID là cột đầu tiên
        }
        cursor.close();
    }
    private void navigateToCustomer(int newCustomerId) {
        // Không cần kiểm tra ID nữa vì đã có danh sách
        Cursor cursor = null;
        try {
            cursor = databaseHelper.getCustomerById(newCustomerId);
            if (cursor != null && cursor.moveToFirst()) {
                customerId = newCustomerId;
                displayCustomerDetails(customerId);
            } else {
                Toast.makeText(getActivity(), "No more customers", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void deleteCustomer() {
        if (databaseHelper.deleteCustomer(customerId)) {
            Toast.makeText(getActivity(), "Customer has been deleted!", Toast.LENGTH_SHORT).show();
            requireActivity().getSupportFragmentManager().popBackStack();
        } else {
            Toast.makeText(getActivity(), "Delete customer failed!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this customer?")
                .setPositiveButton(android.R.string.yes, (dialog, which) -> deleteCustomer())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }
}
