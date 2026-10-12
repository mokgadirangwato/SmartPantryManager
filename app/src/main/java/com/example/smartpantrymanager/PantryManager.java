package com.example.smartpantrymanager;

import com.example.smartpantrymanager.data.PantryItem;

import java.util.ArrayList;
import java.util.List;

public class PantryManager {
    private final List<PantryItem> items = new ArrayList<>();

    public List<PantryItem> getItems() {
        return items;
    }

    public void setItems(List<PantryItem> newItems) {
        items.clear();
        if (newItems != null) {
            items.addAll(newItems);
        }
    }
}
