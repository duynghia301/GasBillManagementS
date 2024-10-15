package com.example.gasbillmanagements.ui.customer;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

import java.util.Calendar;

public class AddCustomerFragment extends Fragment {

    private EditText etName, etDateOfBirth, etAddress, etUsedNumGas;
    private RadioGroup radioGroupGasLevel;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        etName = view.findViewById(R.id.et_name);
        etDateOfBirth = view.findViewById(R.id.et_date_of_birth);
        etAddress = view.findViewById(R.id.et_address);
        etUsedNumGas = view.findViewById(R.id.et_used_num_gas);
        radioGroupGasLevel = view.findViewById(R.id.radio_group_gas_level);
        Button btnSave = view.findViewById(R.id.btn_save);

        databaseHelper = new DatabaseHelper(getContext());

        // Thiết lập sự kiện cho trường chọn ngày sinh
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> saveCustomer());

        return view;
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (view, selectedYear, selectedMonth, selectedDay) -> {
            // Định dạng tháng để hiển thị đúng
            String formattedMonth = String.format("%02d", selectedMonth + 1);
            etDateOfBirth.setText(selectedYear+ formattedMonth);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String dateOfBirth = etDateOfBirth.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String usedNumGasStr = etUsedNumGas.getText().toString().trim();

        // Check if all fields have been filled
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(dateOfBirth) || TextUtils.isEmpty(address) || TextUtils.isEmpty(usedNumGasStr)) {
            showAlertDialog("Notification", "Please fill in all the information.");
            return;
        }

        // Check if the user has selected a gas level
        if (radioGroupGasLevel.getCheckedRadioButtonId() == -1) {
            showAlertDialog("Notification", "Please select a gas level.");
            return;
        }

        int usedNumGas;
        try {
            usedNumGas = Integer.parseInt(usedNumGasStr);
        } catch (NumberFormatException e) {
            showAlertDialog("Notification", "Invalid gas amount!");
            return;
        }

        // Get the ID of the selected gas level
        int selectedGasLevelId = radioGroupGasLevel.getCheckedRadioButtonId() == R.id.radio_level_1 ? 1 : 2;

        // Show confirmation dialog before saving
        new AlertDialog.Builder(requireContext())
                .setTitle("Confirmation")
                .setMessage("Are you sure you want to add this customer?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Insert customer information into the database
                    databaseHelper.insertCustomer(name, dateOfBirth, address, usedNumGas, selectedGasLevelId);
                    showAlertDialog("Notification", "Customer added successfully!");
                    clearFields();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }


    private void clearFields() {
        etName.setText("");
        etDateOfBirth.setText("");
        etAddress.setText("");
        etUsedNumGas.setText("");
        radioGroupGasLevel.clearCheck();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
