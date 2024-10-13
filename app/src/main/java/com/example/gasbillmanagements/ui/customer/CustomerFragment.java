package com.example.gasbillmanagements.ui.customer;

import androidx.lifecycle.ViewModelProvider;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

public class CustomerFragment extends Fragment {
    private int customerId;
    private DatabaseHelper databaseHelper;

    // Tham chiếu đến các TextView trong layout
    private TextView tvName;
    private TextView tvAddress;
    private TextView tvUsedNumGas;
    private TextView tvGasLevel;
    private TextView tvBirth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer, container, false);

        // Khởi tạo TextView
        tvName = view.findViewById(R.id.tv_name);
        tvAddress = view.findViewById(R.id.tv_address);
        tvUsedNumGas = view.findViewById(R.id.tv_used_num_gas);
        tvGasLevel = view.findViewById(R.id.tv_gas_level);
        tvBirth = view.findViewById(R.id.tv_birth);
        databaseHelper = new DatabaseHelper(getActivity());

        // Lấy customer_id từ bundle
        if (getArguments() != null) {
            customerId = getArguments().getInt("customer_id", -1);
        }

        // Hiển thị thông tin chi tiết của customer
        displayCustomerDetails(customerId);

        return view;
    }


    private void displayCustomerDetails(int customerId) {
        Cursor cursor = null;
        try {
            if (customerId != -1) {
                cursor = databaseHelper.getCustomerById(customerId);
                if (cursor != null && cursor.moveToFirst()) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("NAME"));
                    String address = cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS"));
                    String usedNumGas = cursor.getString(cursor.getColumnIndexOrThrow("USED_NUM_GAS"));
                    String gasLevel = cursor.getString(cursor.getColumnIndexOrThrow("GAS_LEVEL_TYPE_ID"));
                    String birth = cursor.getString(cursor.getColumnIndexOrThrow("YYYYMM"));
                    tvName.setText      ("Name            :  " + name);
                    tvAddress.setText   ("Adress          :  " + address);
                    tvBirth.setText     ("DayofBrith   :  " 	+ birth);
                    tvUsedNumGas.setText("Used gas     :  "  + usedNumGas);
                    tvGasLevel.setText  ("Gas level      :  " + gasLevel);
                } else {
                    Toast.makeText(getActivity(), "Customer not found", Toast.LENGTH_SHORT).show();
                    Log.d("CustomerFragment", "Customer not found for ID: " + customerId);
                }
            } else {
                Toast.makeText(getActivity(), "Invalid customer ID", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close(); // Close cursor to avoid memory leaks
            }
        }
    }


}
