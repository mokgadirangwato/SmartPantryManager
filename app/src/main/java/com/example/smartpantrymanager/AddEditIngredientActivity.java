package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etUnit;
    private TextInputEditText etExpiry;

    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private AppDatabase db;

    // ID of the pantry item being edited
    private int pantryItemId = -1;

    // True when editing an existing item
    private boolean isEditMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit);

        // ==========================================================
        // LINK UI COMPONENTS
        // ==========================================================

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // ==========================================================
        // DATABASE
        // ==========================================================

        db = AppDatabase.getInstance(this);

        // ==========================================================
        // CHECK IF WE ARE EDITING AN EXISTING ITEM
        // ==========================================================

        if (getIntent().hasExtra("pantry_item_id")) {

            pantryItemId = getIntent().getIntExtra(
                    "pantry_item_id",
                    -1
            );

            if (pantryItemId != -1) {
                isEditMode = true;
                loadPantryItem();
            }
        }

        // ==========================================================
        // SAVE BUTTON
        // ==========================================================

        btnSave.setOnClickListener(v -> saveIngredient());

        // ==========================================================
        // DELETE BUTTON
        // ==========================================================

        btnDelete.setOnClickListener(v -> deleteIngredient());

        // Show/hide delete button
        if (isEditMode) {
            btnDelete.setVisibility(MaterialButton.VISIBLE);
        } else {
            btnDelete.setVisibility(MaterialButton.GONE);
        }
    }

    // ==========================================================
    // LOAD EXISTING PANTRY ITEM
    // ==========================================================

    private void loadPantryItem() {

        List<PantryItem> items =
                db.pantryDao().getAllItems();

        for (PantryItem item : items) {

            if (item.id == pantryItemId) {

                etName.setText(item.name);

                etQuantity.setText(
                        String.valueOf(item.quantity)
                );

                etUnit.setText(
                        item.unit != null ? item.unit : ""
                );

                etExpiry.setText(
                        item.expiryDate != null
                                ? item.expiryDate
                                : ""
                );

                break;
            }
        }
    }

    // ==========================================================
    // SAVE / UPDATE INGREDIENT
    // ==========================================================

    private void saveIngredient() {

        // Get values from input fields
        String name = etName.getText()
                .toString()
                .trim();

        String qtyString = etQuantity.getText()
                .toString()
                .trim();

        String unit = etUnit.getText()
                .toString()
                .trim();

        String expiry = etExpiry.getText()
                .toString()
                .trim();

        // ==========================================================
        // VALIDATE NAME
        // ==========================================================

        if (TextUtils.isEmpty(name)) {

            etName.setError("Name is required");
            etName.requestFocus();

            return;
        }

        // ==========================================================
        // VALIDATE QUANTITY
        // ==========================================================

        if (TextUtils.isEmpty(qtyString)) {

            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();

            return;
        }

        // ==========================================================
        // CONVERT QUANTITY TO DOUBLE
        // ==========================================================

        double quantity;

        try {

            quantity = Double.parseDouble(qtyString);

            if (quantity <= 0) {

                etQuantity.setError(
                        "Quantity must be greater than 0"
                );

                etQuantity.requestFocus();

                return;
            }

        } catch (NumberFormatException e) {

            etQuantity.setError(
                    "Enter a valid number"
            );

            etQuantity.requestFocus();

            return;
        }

        // ==========================================================
        // EDIT EXISTING ITEM
        // ==========================================================

        if (isEditMode) {

            PantryItem item = findPantryItem();

            if (item == null) {

                Toast.makeText(
                        this,
                        "Ingredient could not be found.",
                        Toast.LENGTH_SHORT
                ).show();

                finish();

                return;
            }

            // Update existing values
            item.name = name;
            item.quantity = quantity;
            item.unit = unit;
            item.expiryDate = expiry;

            // Save changes
            db.pantryDao().update(item);

            Toast.makeText(
                    this,
                    "Ingredient updated!",
                    Toast.LENGTH_SHORT
            ).show();

        }

        // ==========================================================
        // ADD NEW ITEM
        // ==========================================================

        else {

            PantryItem newItem = new PantryItem(
                    name,
                    quantity,
                    unit,
                    expiry
            );

            // Insert new item
            db.pantryDao().insert(newItem);

            Toast.makeText(
                    this,
                    "Ingredient saved!",
                    Toast.LENGTH_SHORT
            ).show();
        }

        // Return to pantry
        finish();
    }

    // ==========================================================
    // FIND PANTRY ITEM
    // ==========================================================

    private PantryItem findPantryItem() {

        List<PantryItem> items =
                db.pantryDao().getAllItems();

        for (PantryItem item : items) {

            if (item.id == pantryItemId) {
                return item;
            }
        }

        return null;
    }

    // ==========================================================
    // DELETE INGREDIENT
    // ==========================================================

    private void deleteIngredient() {

        if (!isEditMode) {
            return;
        }

        PantryItem item = findPantryItem();

        if (item == null) {

            Toast.makeText(
                    this,
                    "Ingredient could not be found.",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        // Delete from Room
        db.pantryDao().delete(item);

        Toast.makeText(
                this,
                "Ingredient deleted!",
                Toast.LENGTH_SHORT
        ).show();

        // Return to pantry
        finish();
    }
}