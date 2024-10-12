package com.example.gasbillmanagements.ui.customer;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

public class AddCustomerFragment extends Fragment {

    private EditText etName, etYear, etMonth, etDay, etAddress, etUsedNumGas;
    private RadioGroup radioGroupGasLevel;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_customer, container, false);

        // Khởi tạo các view
        etName = view.findViewById(R.id.et_name);
        etYear = view.findViewById(R.id.et_year);
        etMonth = view.findViewById(R.id.et_month);
        etDay = view.findViewById(R.id.et_day);
        etAddress = view.findViewById(R.id.et_address);
        etUsedNumGas = view.findViewById(R.id.et_used_num_gas);
        radioGroupGasLevel = view.findViewById(R.id.radio_group_gas_level);
        Button btnSave = view.findViewById(R.id.btn_save);

        databaseHelper = new DatabaseHelper(getContext());

        // Thiết lập sự kiện cho nút Lưu
        btnSave.setOnClickListener(v -> saveCustomer());

        return view;
    }

    private void saveCustomer() {
        String name = etName.getText().toString().trim();
        String year = etYear.getText().toString().trim();
        String month = etMonth.getText().toString().trim();
        String day = etDay.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String usedNumGasStr = etUsedNumGas.getText().toString().trim();

        // Kiểm tra các trường nhập liệu
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(year) || TextUtils.isEmpty(month) ||
                TextUtils.isEmpty(day) || TextUtils.isEmpty(address) || TextUtils.isEmpty(usedNumGasStr)) {
            showAlertDialog("Thông báo", "Vui lòng điền đầy đủ thông tin.");
            return;
        }

        // Chọn cấp gas
        int selectedGasLevelId = -1;
        int checkedRadioButtonId = radioGroupGasLevel.getCheckedRadioButtonId();
        if (checkedRadioButtonId == R.id.radio_level_1) {
            selectedGasLevelId = 1; // Cấp gas 1
        } else if (checkedRadioButtonId == R.id.radio_level_2) {
            selectedGasLevelId = 2; // Cấp gas 2
        } else {
            showAlertDialog("Thông báo", "Vui lòng chọn cấp gas.");
            return;
        }

        int usedNumGas = Integer.parseInt(usedNumGasStr);
        String yyyyMm = year + month; // Tạo chuỗi YYYYMM

        // Lưu thông tin khách hàng vào cơ sở dữ liệu
        databaseHelper.insertCustomer(name, yyyyMm, address, usedNumGas, selectedGasLevelId);
        showAlertDialog("Thông báo", "Thêm khách hàng thành công!");

        // Xóa dữ liệu nhập
        clearFields();
    }

    private void clearFields() {
        etName.setText("");
        etYear.setText("");
        etMonth.setText("");
        etDay.setText("");
        etAddress.setText("");
        etUsedNumGas.setText("");
        radioGroupGasLevel.clearCheck();
    }

    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
