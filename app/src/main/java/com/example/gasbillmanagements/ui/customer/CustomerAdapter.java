package com.example.gasbillmanagements.ui.customer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gasbillmanagements.R;
import com.example.gasbillmanagements.model.Customer;

import java.util.ArrayList;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {

    private ArrayList<Customer> customerList;
    private OnCustomerClickListener listener;

    public CustomerAdapter(ArrayList<Customer> customerList, OnCustomerClickListener listener) {
        this.customerList = customerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_customers, parent, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder holder, int position) {
        Customer customer = customerList.get(position);
        holder.bind(customer);
    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder {
        private TextView textViewId;
        private TextView textViewName;
        private TextView textViewAddress;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewId = itemView.findViewById(R.id.textview_id);
            textViewName = itemView.findViewById(R.id.textview_name);
            textViewAddress = itemView.findViewById(R.id.textview_address);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onCustomerClick(customerList.get(position));
                }
            });
        }

        public void bind(Customer customer) {
            textViewId.setText(String.valueOf(customer.getID()));
            textViewName.setText(customer.getNAME());
            textViewAddress.setText(customer.getADDRESS());
        }
    }

    public interface OnCustomerClickListener {
        void onCustomerClick(Customer customer);
    }
}
