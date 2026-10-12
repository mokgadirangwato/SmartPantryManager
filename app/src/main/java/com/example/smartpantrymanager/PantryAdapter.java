package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryItem;

import java.util.List;

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.VH> {

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

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);

        return new VH(v);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH h,
            int pos) {

        PantryItem item = items.get(pos);

        h.name.setText(item.name);

        String qtyText =
                item.quantity + " "
                        + (item.unit != null ? item.unit : "");

        h.qty.setText(qtyText.trim());

        if (item.expiryDate != null
                && !item.expiryDate.isEmpty()) {

            h.expiry.setText(
                    "Expires: " + item.expiryDate
            );

            h.expiry.setVisibility(View.VISIBLE);

        } else {

            h.expiry.setVisibility(View.GONE);
        }

        if (listener != null) {
            h.itemView.setOnClickListener(
                    v -> listener.onItemClick(item)
            );
        } else {
            h.itemView.setOnClickListener(null);
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

        VH(View v) {
            super(v);

            name = v.findViewById(R.id.tvPantryName);
            qty = v.findViewById(R.id.tvPantryQuantity);
            expiry = v.findViewById(R.id.tvPantryExpiry);
        }
    }
}