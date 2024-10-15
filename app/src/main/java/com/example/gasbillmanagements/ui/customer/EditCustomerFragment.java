package com.example.gasbillmanagements.ui.customer;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.widget.DatePicker;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

import java.util.Calendar;

public class EditCustomerFragment extends Fragment {
    private DatabaseHelper databaseHelper;
    private EditText etName, etAddress, etUsedNumGas;
    private TextView etBirth;
    private RadioGroup radioGroupGasLevel;
    private int customerId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_customer, container, false);

        databaseHelper = new DatabaseHelper(getActivity());
        etName = view.findViewById(R.id.et_name);
        etAddress = view.findViewById(R.id.et_address);
        etBirth = view.findViewById(R.id.et_birth);
        etUsedNumGas = view.findViewById(R.id.et_used_num_gas);
        Button btnSave = view.findViewById(R.id.btn_save);
        radioGroupGasLevel = view.findViewById(R.id.radio_group_gas_level);

        if (getArguments() != null) {
            customerId = getArguments().getInt("customer_id", -1);
            loadCustomerData(customerId);
        }

        etBirth.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> showConfirmationDialog());

        return view;
    }

    private void loadCustomerData(int id) {
        Cursor cursor = databaseHelper.getCustomerById(id);
        if (cursor != null && cursor.moveToFirst()) {
            try {
                etName.setText(cursor.getString(cursor.getColumnIndexOrThrow("NAME")));
                etAddress.setText(cursor.getString(cursor.getColumnIndexOrThrow("ADDRESS")));
                etBirth.setText(cursor.getString(cursor.getColumnIndexOrThrow("YYYYMM")));
                etUsedNumGas.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("USED_NUM_GAS"))));

                int gasLevelTypeId = cursor.getInt(cursor.getColumnIndexOrThrow("GAS_LEVEL_TYPE_ID"));
                if (gasLevelTypeId == 1) {
                    radioGroupGasLevel.check(R.id.radio_level_1);
                } else if (gasLevelTypeId == 2) {
                    radioGroupGasLevel.check(R.id.radio_level_2);
                }

            } catch (IllegalArgumentException e) {
                Toast.makeText(getActivity(), "Invalid column name!", Toast.LENGTH_SHORT).show();
            }
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {

            String formattedMonth = String.format("%02d", selectedMonth + 1);
            etBirth.setText(selectedYear+ formattedMonth);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(getActivity())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to save?")
                .setPositiveButton("Yes", (dialog, which) -> updateCustomer())
                .setNegativeButton("No", null)
                .show();
    }

    private void updateCustomer() {
        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String birth = etBirth.getText().toString().trim();

        // Get used num gas as a number
        int usedNumGas;
        try {
            usedNumGas = Integer.parseInt(etUsedNumGas.getText().toString().trim());
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Used gas must be a valid number!", Toast.LENGTH_SHORT).show();
            return;
        }

        int gasLevelTypeId = radioGroupGasLevel.getCheckedRadioButtonId() == R.id.radio_level_1 ? 1 : 2;

        if (databaseHelper.updateCustomer(customerId, name, address, birth, usedNumGas, gasLevelTypeId)) {
            String updatedInfo = String.format("Updated Information:\nName: %s\nAddress: %s\nDate of Birth: %s\nGas Used: %d\nGas Level ID: %d",
                    name, address, birth, usedNumGas, gasLevelTypeId);

            new AlertDialog.Builder(getActivity())
                    .setTitle("Customer Updated")
                    .setMessage(updatedInfo)
                    .setPositiveButton("OK", (dialog, which) -> {
                        Bundle bundle = new Bundle();
                        bundle.putInt("customer_id", customerId);
                        NavController navController = NavHostFragment.findNavController(this);
                        navController.popBackStack(R.id.nav_customer, true);
                        navController.navigate(R.id.nav_customer, bundle);
                    })
                    .show();
        } else {
            Toast.makeText(getActivity(), "Update failed!", Toast.LENGTH_SHORT).show();
        }
    }
}
