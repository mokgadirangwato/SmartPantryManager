package com.example.smartpantrymanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private MaterialAutoCompleteTextView etUnit;
    private TextInputEditText etExpiry;

    private MaterialButton btnSave;
    private MaterialButton btnDelete;

    private AppDatabase db;

    // ID of the pantry item being edited
    private int pantryItemId = -1;

    // True when editing an existing item
    private boolean isEditMode = false;

    // Available measurement units
    private final String[] units = {
            "kg",
            "g",
            "mg",
            "L",
            "mL",
            "pcs",
            "tbsp",
            "tsp",
            "cups",
            "pack",
            "bottle",
            "can",
            "cloves",
            "slices",
            "pinch",
            "whole",
            "dozen"
    };

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
        etExpiry = findViewById(R.id.etExpiryDate);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        // ==========================================================
        // UNIT DROPDOWN
        // ==========================================================

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                units
        );

        etUnit.setAdapter(unitAdapter);

        // Prevent the keyboard from appearing for the unit field
        etUnit.setInputType(0);

        // Open dropdown when the field is tapped
        etUnit.setOnClickListener(v -> {
            etUnit.showDropDown();
        });

        // Open dropdown when the field receives focus
        etUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etUnit.post(() -> etUnit.showDropDown());
            }
        });

        // ==========================================================
        // DATABASE
        // ==========================================================

        db = AppDatabase.getInstance(this);

        // ==========================================================
        // CHECK IF EDITING AN EXISTING ITEM
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

        // Show or hide delete button
        if (isEditMode) {
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }

        // ==========================================================
        // EXPIRY DATE PICKER
        // ==========================================================

        etExpiry.setOnClickListener(v -> showDatePicker());

        etExpiry.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
    }

    // ==========================================================
    // SHOW EXPIRY DATE PICKER
    // ==========================================================

    private void showDatePicker() {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {

                    String selectedDate = String.format(
                            Locale.getDefault(),
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    etExpiry.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    // ==========================================================
    // LOAD EXISTING PANTRY ITEM
    // ==========================================================

    private void loadPantryItem() {

        List<PantryItem> items = db.pantryDao().getAllItems();

        for (PantryItem item : items) {

            if (item.id == pantryItemId) {

                etName.setText(item.name);

                etQuantity.setText(
                        String.valueOf(item.quantity)
                );

                // Restore the previously saved unit
                etUnit.setText(
                        item.unit != null ? item.unit : "",
                        false
                );

                etExpiry.setText(
                        item.expiryDate != null
                                ? item.expiryDate
                                : ""
                );

                return;
            }
        }

        Toast.makeText(
                this,
                "Ingredient could not be found.",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    // ==========================================================
    // SAVE OR UPDATE INGREDIENT
    // ==========================================================

    private void saveIngredient() {

        String name = etName.getText() != null
                ? etName.getText().toString().trim()
                : "";

        String qtyString = etQuantity.getText() != null
                ? etQuantity.getText().toString().trim()
                : "";

        String unit = etUnit.getText() != null
                ? etUnit.getText().toString().trim()
                : "";

        String expiry = etExpiry.getText() != null
                ? etExpiry.getText().toString().trim()
                : "";

        // Validate name
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        // Validate quantity
        if (TextUtils.isEmpty(qtyString)) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        // Validate quantity format
        double quantity;

        try {
            quantity = Double.parseDouble(qtyString);

            if (Double.isNaN(quantity)
                    || Double.isInfinite(quantity)
                    || quantity <= 0) {

                etQuantity.setError(
                        "Quantity must be greater than 0"
                );

                etQuantity.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {

            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        // Validate unit selection
        if (TextUtils.isEmpty(unit)) {
            etUnit.setError("Please select a unit");
            etUnit.requestFocus();
            etUnit.showDropDown();
            return;
        }

        etUnit.setError(null);

        // ==========================================================
        // UPDATE EXISTING ITEM
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

            item.name = name;
            item.quantity = quantity;
            item.unit = unit;
            item.expiryDate = expiry;

            db.pantryDao().update(item);

            Toast.makeText(
                    this,
                    "Ingredient updated!",
                    Toast.LENGTH_SHORT
            ).show();

        } else {

            // ======================================================
            // ADD NEW ITEM
            // ======================================================

            PantryItem newItem = new PantryItem(
                    name,
                    quantity,
                    unit,
                    expiry
            );

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

        List<PantryItem> items = db.pantryDao().getAllItems();

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

        db.pantryDao().delete(item);

        Toast.makeText(
                this,
                "Ingredient deleted!",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }
}