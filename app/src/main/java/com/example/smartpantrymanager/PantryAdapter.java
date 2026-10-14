package com.example.smartpantrymanager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.example.smartpantrymanager.data.PantryItem;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
public class PantryAdapter
        extends RecyclerView.Adapter<PantryAdapter.VH> {
    private static final String PREFS_NAME = "SmartPantrySettings";
    private static final String KEY_EXPIRY_ALERTS = "expiry_alerts";
    public interface OnItemClickListener {
        void onItemClick(PantryItem item);
    }
    private final List<PantryItem> items;
    private final OnItemClickListener listener;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    public PantryAdapter(List<PantryItem> items) {
        this(items, null);
    }
    public PantryAdapter(
            List<PantryItem> items,
            OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
        dateFormat.setLenient(false);
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
        // Display ingredient name
        holder.name.setText(item.name);
        // Display quantity and unit
        String quantityText = String.valueOf(item.quantity);
        if (item.unit != null && !item.unit.trim().isEmpty()) {
            quantityText += " " + item.unit;
        }
        holder.qty.setText(quantityText);
        // Reset recycled card styling
        resetCard(holder);
        // Read the saved expiry-alert preference
        Context context = holder.itemView.getContext();
        SharedPreferences preferences =
                context.getSharedPreferences(
                        PREFS_NAME,
                        Context.MODE_PRIVATE
                );
        boolean alertsEnabled = preferences.getBoolean(
                KEY_EXPIRY_ALERTS, true
        );
        // Display the expiry date even when alerts are OFF
        if (item.expiryDate == null
                || item.expiryDate.trim().isEmpty()) {
            holder.expiry.setVisibility(View.GONE);
            holder.warning.setVisibility(View.GONE);
        } else {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expires: " + item.expiryDate);
            if (alertsEnabled) {
                applyExpiryHighlight(holder, item);
            } else {
                // Hide warning colours and labels when switched OFF
                resetCard(holder);
            }
        }
        // Preserve ingredient click/edit functionality
        if (listener != null) {
            holder.itemView.setOnClickListener(
                    v -> listener.onItemClick(item)
            );
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }
    private void applyExpiryHighlight(VH holder, PantryItem item) {
        Date expiry;
        try {
            expiry = dateFormat.parse(item.expiryDate);
        } catch (ParseException | NullPointerException e) {
            resetCard(holder);
            return;
        }
        if (expiry == null) {
            resetCard(holder);
            return;
        }
        // Compare dates without considering the current time
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);
        Calendar expiryDay = Calendar.getInstance();
        expiryDay.setTime(expiry);
        expiryDay.set(Calendar.HOUR_OF_DAY, 0);
        expiryDay.set(Calendar.MINUTE, 0);
        expiryDay.set(Calendar.SECOND, 0);
        expiryDay.set(Calendar.MILLISECOND, 0);
        long difference = expiryDay.getTimeInMillis()
                - today.getTimeInMillis();
        long days = TimeUnit.MILLISECONDS.toDays(difference);
        if (days < 0) {
            // EXPIRED: red
            holder.card.setCardBackgroundColor(
                    Color.parseColor("#FFEBEE"));
            holder.card.setStrokeColor(
                    Color.parseColor("#E53935"));
            holder.card.setStrokeWidth(dp(holder, 2));
            holder.expiry.setTextColor(
                    Color.parseColor("#B71C1C"));
            holder.warning.setText("⚠ EXPIRED");
            holder.warning.setTextColor(
                    Color.parseColor("#B71C1C"));
            holder.warning.setVisibility(View.VISIBLE);
        } else if (days <= 30) {
            // EXPIRING SOON: orange
            holder.card.setCardBackgroundColor(
                    Color.parseColor("#FFF3E0"));
            holder.card.setStrokeColor(
                    Color.parseColor("#FB8C00"));
            holder.card.setStrokeWidth(dp(holder, 2));
            holder.expiry.setTextColor(
                    Color.parseColor("#B75F00"));
            holder.warning.setText(
                    "⚠ EXPIRING SOON (" + days
                            + (days == 1 ? " day)" : " days)")
            );
            holder.warning.setTextColor(
                    Color.parseColor("#B75F00"));
            holder.warning.setVisibility(View.VISIBLE);
        } else {
            // More than 30 days until expiry
            resetCard(holder);
        }
    }
    private int dp(VH holder, int value) {
        return (int) (value * holder.itemView.getResources()
                .getDisplayMetrics().density + 0.5f);
    }
    private void resetCard(VH holder) {
        holder.card.setCardBackgroundColor(Color.WHITE);
        holder.card.setStrokeColor(
                Color.parseColor("#E0E0E0"));
        holder.card.setStrokeWidth(dp(holder, 1));
        holder.expiry.setTextColor(
                Color.parseColor("#64748B"));
        holder.warning.setVisibility(View.GONE);
    }
    @Override
    public int getItemCount() {
        return items == null ? 0 : items.size();
    }
    static class VH extends RecyclerView.ViewHolder {
        MaterialCardView card;
        TextView name;
        TextView qty;
        TextView expiry;
        TextView warning;
        VH(@NonNull View view) {
            super(view);
            card = view.findViewById(R.id.pantryCard);
            name = view.findViewById(R.id.tvPantryName);
            qty = view.findViewById(R.id.tvPantryQuantity);
            expiry = view.findViewById(R.id.tvPantryExpiry);
            warning = view.findViewById(R.id.tvPantryWarning);
        }
    }
}