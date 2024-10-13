package com.example.gasbillmanagements.ui.gas;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
    private ImageButton btnShowDetails;
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
        btnShowDetails = view.findViewById(R.id.btn_show_details);

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


        btnShowDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGasDetails(); // Gọi phương thức để hiển thị thông tin chi tiết
            }
        });
        return view;
    }

    private void setupRadioGroup() {
        Cursor cursor = databaseHelper.getAllGasLevels();
        if (cursor != null) {
            try {
                radioGroupGasLevel.removeAllViews(); // Remove all RadioButtons before adding new ones
                gasLevelMap.clear(); // Clear the map to avoid duplicate storage

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
        tvGasLevel.setText("Gas Level: " + gasLevelId);
        tvCurrentPrice.setText("Current gas price: " + currentPrice + "$");
    }

    private void increaseGasPrice() {
        // Check if any gas level is selected
        int selectedId = radioGroupGasLevel.getCheckedRadioButtonId();
        if (selectedId == -1) {
            showAlertDialog("Notice", "Please select a gas level before increasing the price");
            return;
        }

        String increaseAmountStr = etIncreaseAmount.getText().toString();

        if (increaseAmountStr.isEmpty()) {
            showAlertDialog("Notice", "Please enter the price increase amount");
            return;
        }

        try {
            double increaseAmount = Double.parseDouble(increaseAmountStr);
            if (increaseAmount <= 0) {
                showAlertDialog("Notice", "The increase amount must be greater than 0");
                return;
            }

            Log.d("GasFragment", "Increasing price for Gas Level ID: " + currentGasLevelId + " by amount: " + increaseAmount);

            boolean isUpdated = databaseHelper.updateGasPrice(currentGasLevelId, increaseAmount);

            if (isUpdated) {
                showAlertDialog("Notice", "Gas price updated successfully");
                updateGasInfo(currentGasLevelId);
                etIncreaseAmount.setText("");
            } else {
                showAlertDialog("Notice", "Failed to update gas price");
            }
        } catch (NumberFormatException e) {
            showAlertDialog("Notice", "Invalid price increase amount");
        } catch (Exception e) {
            e.printStackTrace();
            showAlertDialog("Notice", "An error occurred. Please try again later.");
        }
    }

    private void showGasDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Gas Details");

        Cursor cursor = databaseHelper.getAllGasLevels(); // Lấy tất cả các cấp gas
        StringBuilder details = new StringBuilder();

        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow("GAS_LEVEL_TYPE_NAME"));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow("UNIT_PRICE"));
                    int maxGas = cursor.getInt(cursor.getColumnIndexOrThrow("MAX_NUM_GAS"));
                    double rate = cursor.getDouble(cursor.getColumnIndexOrThrow("RATE_PRICE_FOR_OVER"));

                    details.append(name)
                            .append("          Price: ")
                            .append(price)
                            .append("\nMax Gas:    ")
                            .append(maxGas)
                            .append("\nRate        :    ")
                            .append(rate)
                            .append("\n-----------------------------------------\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("GasFragment", "Error retrieving gas details: " + e.getMessage()); // Ghi log lỗi
                details.append("Error retrieving gas details."); // Thông báo lỗi cho người dùng
            } finally {
                cursor.close(); // Đảm bảo rằng bạn luôn đóng cursor
            }
        } else {
            Log.e("GasFragment", "Cursor is null."); // Ghi log nếu cursor là null
            details.append("No gas levels available."); // Thông báo cho người dùng
        }

        builder.setMessage(details.toString());
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }








    private void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
