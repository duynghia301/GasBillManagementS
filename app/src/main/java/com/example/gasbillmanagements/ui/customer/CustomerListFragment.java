package com.example.gasbillmanagements.ui.customer;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;


public class CustomerListFragment extends Fragment {

    private EditText editTextSearch;
    private ListView listView;
    private DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_customer_list, container, false);

        editTextSearch = view.findViewById(R.id.edittext_search);
        listView = view.findViewById(R.id.listview);

        databaseHelper = new DatabaseHelper(getActivity());

        // Hiển thị tất cả khách hàng khi fragment được khởi tạo
        displayAllCustomers();

        // Thêm TextWatcher để tìm kiếm theo từng ký tự
        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Không cần làm gì trước khi văn bản thay đổi
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Gọi phương thức tìm kiếm khi văn bản thay đổi
                performSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Không cần làm gì sau khi văn bản thay đổi
            }
        });





        return view;
    }



    private void performSearch(String query) {
        try {
            Cursor cursor;
            if (!query.isEmpty()) {
                cursor = databaseHelper.searchCustomers(query);
            } else {
                // Nếu trường tìm kiếm rỗng, hiển thị tất cả khách hàng
                cursor = databaseHelper.getAllCustomers();
            }

            if (cursor != null && cursor.getCount() > 0) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getActivity(),
                        R.layout.list_customers,
                        cursor,
                        new String[]{"NAME" ,"ADDRESS"},
                        new int[]{R.id.textview_name, R.id.textview_address},
                        0);

                listView.setAdapter(adapter);
            } else {
                listView.setAdapter(null);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void displayAllCustomers() {
        try {
            Cursor cursor = databaseHelper.getAllCustomers();
            if (cursor != null && cursor.getCount() > 0) {
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(
                        getActivity(),
                        R.layout.list_customers,
                        cursor,
                        new String[]{"NAME","ADDRESS"},
                        new int[]{R.id.textview_name, R.id.textview_address},
                        0);

                listView.setAdapter(adapter);
            } else {
                Toast.makeText(getActivity(), "No customers found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Error occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}