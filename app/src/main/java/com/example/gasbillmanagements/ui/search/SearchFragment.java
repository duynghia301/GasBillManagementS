package com.example.gasbillmanagements.ui.search;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private ListView listViewResults;
    private DatabaseHelper databaseHelper;
    private CheckBox checkboxSearchName;
    private CheckBox checkboxSearchAddress;
    private Button buttonSearch;
    private ImageButton buttonSearchImage;
    @SuppressLint("WrongViewCast")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextSearch = view.findViewById(R.id.edittext_search);
        listViewResults = view.findViewById(R.id.listview_results);
        checkboxSearchName = view.findViewById(R.id.checkbox_search_name);
        checkboxSearchAddress = view.findViewById(R.id.checkbox_search_address);
        buttonSearch = view.findViewById(R.id.button_search);
        buttonSearchImage = view.findViewById(R.id.button_SearchImage);
        databaseHelper = new DatabaseHelper(getActivity());

        // Hiển thị tất cả khách hàng khi fragment được khởi tạo
        displayAllCustomers();

        // Thêm listener cho nút tìm kiếm
        buttonSearch.setOnClickListener(v -> performSearch());
        buttonSearchImage.setOnClickListener(v -> performSearch());
        return view;

    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getActivity(), "Bạn chưa nhập gì để tìm kiếm", Toast.LENGTH_SHORT).show();
            return; // Dừng thực hiện nếu không có gì để tìm kiếm
        }

        Cursor cursor = null;
        // Kiểm tra xem có chọn CheckBox nào không
        if (checkboxSearchName.isChecked() && checkboxSearchAddress.isChecked()) {
            // Tìm kiếm theo cả tên và địa chỉ
            cursor = databaseHelper.searchCustomersByNameAndAddress(query);
        } else if (checkboxSearchName.isChecked()) {
            // Tìm kiếm theo tên
            cursor = databaseHelper.searchCustomersByName(query);
        } else if (checkboxSearchAddress.isChecked()) {
            // Tìm kiếm theo địa chỉ
            cursor = databaseHelper.searchCustomersByAddress(query);
        } else {
            // Nếu không chọn CheckBox nào, thông báo
            Toast.makeText(getActivity(), "Bạn chưa chọn tùy chọn tìm kiếm", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor != null) {
            int resultCount = cursor.getCount();

            if (resultCount > 0) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getActivity(),
                        R.layout.list_item,
                        cursor,
                        new String[]{"NAME", "ADDRESS"},
                        new int[]{R.id.textview_name, R.id.textview_address},
                        0);

                listViewResults.setAdapter(adapter);
                Toast.makeText(getActivity(), "Tìm thấy " + resultCount + " khách hàng", Toast.LENGTH_SHORT).show();
            } else {
                listViewResults.setAdapter(null);
                Toast.makeText(getActivity(), "Không tìm thấy khách hàng nào", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayAllCustomers() {
        Cursor cursor = databaseHelper.getAllCustomers();
        if (cursor != null && cursor.getCount() > 0) {
            SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                    getActivity(),
                    R.layout.list_item,
                    cursor,
                    new String[]{"NAME", "ADDRESS"},
                    new int[]{R.id.textview_name, R.id.textview_address},
                    0);

            listViewResults.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "Không tìm thấy khách hàng nào", Toast.LENGTH_SHORT).show();
        }
    }
}
