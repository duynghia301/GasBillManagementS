package com.example.gasbillmanagements.ui.search;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class SearchFragment extends Fragment {

    private EditText editTextSearch;
    private ListView listViewResults;
    private DatabaseHelper databaseHelper;
    private CheckBox checkboxSearchName;
    private CheckBox checkboxSearchAddress;
    private Button buttonSearch;

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
        databaseHelper = new DatabaseHelper(getActivity());

        // Khôi phục trạng thái
        if (savedInstanceState != null) {
            String query = savedInstanceState.getString("search_query");
            editTextSearch.setText(query);
            checkboxSearchName.setChecked(savedInstanceState.getBoolean("checkbox_search_name"));
            checkboxSearchAddress.setChecked(savedInstanceState.getBoolean("checkbox_search_address"));
        }

        // Hiển thị tất cả khách hàng khi Fragment khởi tạo
        displayAllCustomers();

        // Thêm listener cho nút tìm kiếm
        buttonSearch.setOnClickListener(v -> performSearch());

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Thiết lập FloatingActionButton
        FloatingActionButton fab = view.findViewById(R.id.fab); // Ensure ID matches layout
        if (fab != null) {
            fab.setVisibility(View.INVISIBLE); // Example operation
        } else {
            Log.e("SearchFragment", "FloatingActionButton is null");
        }
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getActivity(), "You haven't entered anything to search", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu không có gì để tìm kiếm
        }

        Cursor cursor = null;
        // Kiểm tra xem có bất kỳ CheckBox nào được chọn không
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
            // Nếu không có CheckBox nào được chọn, hiển thị thông báo
            Toast.makeText(getActivity(), "You haven't selected a search option", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cursor != null) {
            int resultCount = cursor.getCount();

            if (resultCount > 0) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getActivity(),
                        R.layout.list_item,
                        cursor,
                        new String[]{"_id", "NAME", "ADDRESS"},
                        new int[]{R.id.textview_id, R.id.textview_name, R.id.textview_address},
                        0);

                listViewResults.setAdapter(adapter);
                Toast.makeText(getActivity(), "Found " + resultCount + " customers", Toast.LENGTH_SHORT).show();
            } else {
                listViewResults.setAdapter(null);
                Toast.makeText(getActivity(), "No customers found", Toast.LENGTH_SHORT).show();
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
                    new String[]{"_id", "NAME", "ADDRESS"},
                    new int[]{R.id.textview_id, R.id.textview_name, R.id.textview_address},
                    0);

            listViewResults.setAdapter(adapter);
        } else {
            Toast.makeText(getActivity(), "No customers found", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Lưu trạng thái của EditText
        outState.putString("search_query", editTextSearch.getText().toString());
        // Lưu trạng thái của CheckBox
        outState.putBoolean("checkbox_search_name", checkboxSearchName.isChecked());
        outState.putBoolean("checkbox_search_address", checkboxSearchAddress.isChecked());
    }
}
