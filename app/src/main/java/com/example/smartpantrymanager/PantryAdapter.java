package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryItem;

import java.util.List;

public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.VH> {

    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }

    private final List<PantryItem> items;
    private final OnItemClickListener listener;

    public PantryAdapter(List<PantryItem> items) {
        this(items, null);
    }

    public PantryAdapter(
            List<PantryItem> items,
            OnItemClickListener listener) {

        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position) {

        PantryItem item = items.get(position);

        // Pantry item name
        holder.name.setText(item.name);

        // Quantity and unit
        String quantityText = String.valueOf(item.quantity);

        if (item.unit != null && !item.unit.isEmpty()) {
            quantityText += " " + item.unit;
        }

        holder.qty.setText(quantityText);

        // Expiry date
        if (item.expiryDate != null
                && !item.expiryDate.isEmpty()) {

            holder.expiry.setText(
                    "Expires: " + item.expiryDate
            );

            holder.expiry.setVisibility(View.VISIBLE);

        } else {

            holder.expiry.setVisibility(View.GONE);
        }

        // Item click
        if (listener != null) {

            holder.itemView.setOnClickListener(
                    v -> listener.onItemClick(item)
            );

        } else {

            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView name;
        TextView qty;
        TextView expiry;

        VH(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.tvPantryName);
            qty = view.findViewById(R.id.tvPantryQuantity);
            expiry = view.findViewById(R.id.tvPantryExpiry);
        }
    }
}