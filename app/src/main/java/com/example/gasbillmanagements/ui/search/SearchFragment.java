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

        FloatingActionButton fab = requireActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        editTextSearch = view.findViewById(R.id.edittext_search);
        listViewResults = view.findViewById(R.id.listview_results);
        checkboxSearchName = view.findViewById(R.id.checkbox_search_name);
        checkboxSearchAddress = view.findViewById(R.id.checkbox_search_address);
        buttonSearch = view.findViewById(R.id.button_search);
        databaseHelper = new DatabaseHelper(getActivity());

        // Display all customers when the fragment is initialized
        displayAllCustomers();

        // Add listener for the search button
        buttonSearch.setOnClickListener(v -> performSearch());
        return view;

    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (query.isEmpty()) {
            Toast.makeText(getActivity(), "You haven't entered anything to search", Toast.LENGTH_SHORT).show();
            return; // Stop if there is nothing to search
        }

        Cursor cursor = null;
        // Check if any CheckBox is selected
        if (checkboxSearchName.isChecked() && checkboxSearchAddress.isChecked()) {
            // Search by both name and address
            cursor = databaseHelper.searchCustomersByNameAndAddress(query);
        } else if (checkboxSearchName.isChecked()) {
            // Search by name
            cursor = databaseHelper.searchCustomersByName(query);
        } else if (checkboxSearchAddress.isChecked()) {
            // Search by address
            cursor = databaseHelper.searchCustomersByAddress(query);
        } else {
            // If no CheckBox is selected, show a message
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
}
