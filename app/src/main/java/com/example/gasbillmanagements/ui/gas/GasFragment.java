package com.example.gasbillmanagements.ui.gas;

import android.app.AlertDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

import java.util.HashMap;
import java.util.Map;

public class GasFragment extends Fragment {

    private RadioGroup radioGroupGasLevel;
    private TextView tvGasLevel, tvCurrentPrice;
    private EditText etIncreaseAmount;
    private Button btnIncreasePrice;
    private DatabaseHelper databaseHelper;
    private Map<String, Integer> gasLevelMap = new HashMap<>();
    private int currentGasLevelId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gas, container, false);

        // Initialize views
        radioGroupGasLevel = view.findViewById(R.id.radio_group_gas_level);
        tvGasLevel = view.findViewById(R.id.tv_gas_level);
        tvCurrentPrice = view.findViewById(R.id.tv_current_price);
        etIncreaseAmount = view.findViewById(R.id.et_increase_amount);
        btnIncreasePrice = view.findViewById(R.id.btn_increase_price);
        databaseHelper = new DatabaseHelper(getContext());

        // Setup gas levels in RadioGroup
        setupRadioGroup();

        // Set up event for gas level selection
        radioGroupGasLevel.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton selectedRadioButton = group.findViewById(checkedId);
            if (selectedRadioButton != null) {
                String selectedLevel = selectedRadioButton.getText().toString();
                currentGasLevelId = gasLevelMap.get(selectedLevel);
                updateGasInfo(currentGasLevelId);
            }
        });

        // Set up event for "Increase Gas Price" button
        btnIncreasePrice.setOnClickListener(v -> increaseGasPrice());
//        btnDecreasePrice.setOnClickListener(v -> decreaseGasPrice()); // Thêm sự kiện cho nút giảm giá

        return view;
    }

    private void setupRadioGroup() {
        Cursor cursor = databaseHelper.getAllGasLevels();
        if (cursor != null) {
            try {
                radioGroupGasLevel.removeAllViews(); // Xóa tất cả các nút RadioButton trước khi thêm mới
                gasLevelMap.clear(); // Xóa bản đồ để tránh lưu trữ trùng lặp

                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("GAS_LEVEL_TYPE_NAME"));

                    RadioButton radioButton = new RadioButton(getContext());
                    radioButton.setText(name);
                    radioButton.setId(View.generateViewId());
                    radioGroupGasLevel.addView(radioButton);

                    gasLevelMap.put(name, id);
                }
            } finally {
                cursor.close();
            }
        }
    }


    private void updateGasInfo(int gasLevelId) {
        double currentPrice = databaseHelper.getCurrentGasPrice(gasLevelId);
        tvGasLevel.setText("Cấp Gas: " + gasLevelId);
        tvCurrentPrice.setText("Giá gas hiện tại: " + currentPrice + " VND");
    }

    private void increaseGasPrice() {
        // Kiểm tra xem có cấp gas nào được chọn không
        int selectedId = radioGroupGasLevel.getCheckedRadioButtonId();
        if (selectedId == -1) {
            showAlertDialog("Thông báo", "Vui lòng chọn cấp gas trước khi tăng giá");
            return;
        }

        String increaseAmountStr = etIncreaseAmount.getText().toString();

        if (increaseAmountStr.isEmpty()) {
            showAlertDialog("Thông báo", "Vui lòng nhập số tiền tăng giá gas");
            return;
        }

        try {
            double increaseAmount = Double.parseDouble(increaseAmountStr);
            if (increaseAmount <= 0) {
                showAlertDialog("Thông báo", "Số tiền tăng giá phải lớn hơn 0");
                return;
            }

            Log.d("GasFragment", "Increasing price for Gas Level ID: " + currentGasLevelId + " by amount: " + increaseAmount);

            boolean isUpdated = databaseHelper.updateGasPrice(currentGasLevelId, increaseAmount);

            if (isUpdated) {
                showAlertDialog("Thông báo", "Cập nhật giá gas thành công");
                updateGasInfo(currentGasLevelId);
                etIncreaseAmount.setText("");
            } else {
                showAlertDialog("Thông báo", "Cập nhật giá gas thất bại");
            }
        } catch (NumberFormatException e) {
            showAlertDialog("Thông báo", "Số tiền tăng giá không hợp lệ");
        } catch (Exception e) {
            e.printStackTrace();
            showAlertDialog("Thông báo", "Có lỗi xảy ra. Vui lòng thử lại sau.");
        }
    }

    // Phương thức giảm giá gas
//    private void decreaseGasPrice() {
//        // Kiểm tra xem có cấp gas nào được chọn không
//        int selectedId = radioGroupGasLevel.getCheckedRadioButtonId();
//        if (selectedId == -1) {
//            showAlertDialog("Thông báo", "Vui lòng chọn cấp gas trước khi giảm giá");
//            return;
//        }
//
//        String decreaseAmountStr = etDecreaseAmount.getText().toString();
//
//        if (decreaseAmountStr.isEmpty()) {
//            showAlertDialog("Thông báo", "Vui lòng nhập số tiền giảm giá gas");
//            return;
//        }
//
//        try {
//            double decreaseAmount = Double.parseDouble(decreaseAmountStr);
//            if (decreaseAmount <= 0) {
//                showAlertDialog("Thông báo", "Số tiền giảm giá phải lớn hơn 0");
//                return;
//            }
//
//            Log.d("GasFragment", "Decreasing price for Gas Level ID: " + currentGasLevelId + " by amount: " + decreaseAmount);
//
//            boolean isUpdated = databaseHelper.decreaseGasPrice(currentGasLevelId, decreaseAmount);
//
//            if (isUpdated) {
//                showAlertDialog("Thông báo", "Cập nhật giá gas thành công");
//                updateGasInfo(currentGasLevelId);
//                etDecreaseAmount.setText("");
//            } else {
//                showAlertDialog("Thông báo", "Cập nhật giá gas thất bại");
//            }
//        } catch (NumberFormatException e) {
//            showAlertDialog("Thông báo", "Số tiền giảm giá không hợp lệ");
//        } catch (Exception e) {
//            e.printStackTrace();
//            showAlertDialog("Thông báo", "Có lỗi xảy ra. Vui lòng thử lại sau.");
//        }
//    }
    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
