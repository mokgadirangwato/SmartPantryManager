package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pantry_items")
public class PantryItem {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public double quantity;
    public String unit;
    public String expiryDate;

    // Optional package information.
    // Example: 2 bottles, with 500 mL in each bottle.
    public double contentsPerPackage;
    public String contentsUnit;

    // Keep the original constructor so existing code still works.
    public PantryItem(
            String name,
            double quantity,
            String unit,
            String expiryDate) {

        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
        this.contentsPerPackage = 0;
        this.contentsUnit = "";
    }
}