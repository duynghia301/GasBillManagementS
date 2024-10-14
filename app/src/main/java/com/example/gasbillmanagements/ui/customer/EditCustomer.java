package com.example.gasbillmanagements.ui.customer;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

public class EditCustomer extends Fragment {

    private static final String ARG_CUSTOMER_ID = "customer_id";
    private static final String ARG_NAME = "name";
    private static final String ARG_ADDRESS = "address";
    private static final String ARG_BIRTH = "birth";
    private static final String ARG_USED_NUM_GAS = "used_num_gas";
    private static final String ARG_GAS_LEVEL = "gas_level";

    private int customerId;
    private DatabaseHelper databaseHelper;

    private EditText etName;
    private EditText etAddress;
    private EditText etBirth;
    private EditText etUsedNumGas;
    private EditText etGasLevel;

    public EditCustomer() {
        // Required empty public constructor
    }

    public static EditCustomer newInstance(int customerId, String name, String address,
                                           String birth, String usedNumGas, String gasLevel) {
        EditCustomer fragment = new EditCustomer();
        Bundle args = new Bundle();
        args.putInt(ARG_CUSTOMER_ID, customerId);
        args.putString(ARG_NAME, name);
        args.putString(ARG_ADDRESS, address);
        args.putString(ARG_BIRTH, birth);
        args.putString(ARG_USED_NUM_GAS, usedNumGas);
        args.putString(ARG_GAS_LEVEL, gasLevel);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            customerId = getArguments().getInt(ARG_CUSTOMER_ID);
            // Retrieve the data passed from CustomerFragment
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_customer, container, false);

        // Initialize EditTexts
        etName = view.findViewById(R.id.et_name);
        etAddress = view.findViewById(R.id.et_address);
        etBirth = view.findViewById(R.id.et_birth);
        etUsedNumGas = view.findViewById(R.id.et_used_num_gas);
        etGasLevel = view.findViewById(R.id.et_gas_level);
        Button btnSave = view.findViewById(R.id.btn_save);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(getActivity());

        // Load data into EditTexts
        loadData();

        // Set up the save button click listener
        btnSave.setOnClickListener(v -> updateCustomer());

        return view;
    }

    private void loadData() {
        if (getArguments() != null) {
            etName.setText(getArguments().getString(ARG_NAME));
            etAddress.setText(getArguments().getString(ARG_ADDRESS));
            etBirth.setText(getArguments().getString(ARG_BIRTH));
            etUsedNumGas.setText(getArguments().getString(ARG_USED_NUM_GAS));
            etGasLevel.setText(getArguments().getString(ARG_GAS_LEVEL));
        }
    }

    private void updateCustomer() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String birth = etBirth.getText().toString().trim();
        String usedNumGas = etUsedNumGas.getText().toString().trim();
        String gasLevel = etGasLevel.getText().toString().trim();

        // Update the customer in the database
        boolean isUpdated = databaseHelper.updateCustomer(customerId, name, address, birth, usedNumGas, gasLevel);
        if (isUpdated) {
            Toast.makeText(getActivity(), "Customer updated successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to update customer", Toast.LENGTH_SHORT).show();
        }
    }
}
