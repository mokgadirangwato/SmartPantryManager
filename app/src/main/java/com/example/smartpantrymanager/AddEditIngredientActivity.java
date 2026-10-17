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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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

    private int pantryItemId = -1;
    private boolean isEditMode = false;

    private final String[] units = {
            "kg", "g", "mg", "L", "mL", "pcs",
            "tbsp", "tsp", "cups", "pack", "bottle",
            "can", "cloves", "slices", "pinch", "whole",
            "dozen"
    };

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiryDate);

        btnSave = findViewById(R.id.btnSave);
        btnDelete = findViewById(R.id.btnDelete);

        dateFormat.setLenient(false);

        ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                units
        );

        etUnit.setAdapter(unitAdapter);
        etUnit.setInputType(0);

        etUnit.setOnClickListener(v -> etUnit.showDropDown());

        etUnit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                etUnit.post(() -> etUnit.showDropDown());
            }
        });

        db = AppDatabase.getInstance(this);

        if (getIntent().hasExtra("pantry_item_id")) {
            pantryItemId = getIntent().getIntExtra(
                    "pantry_item_id", -1
            );

            if (pantryItemId != -1) {
                isEditMode = true;
                loadPantryItem();
            }
        }

        btnSave.setOnClickListener(v -> saveIngredient());
        btnDelete.setOnClickListener(v -> deleteIngredient());

        btnDelete.setVisibility(
                isEditMode ? View.VISIBLE : View.GONE
        );

        etExpiry.setOnClickListener(v -> showDatePicker());

        etExpiry.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                showDatePicker();
            }
        });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();

        String existingDate = getText(etExpiry);

        if (!existingDate.isEmpty()) {
            try {
                Date date = dateFormat.parse(existingDate);

                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException ignored) {
                // Use today's date if the existing date is invalid.
            }
        }

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(
                            Locale.US,
                            "%04d-%02d-%02d",
                            year,
                            month + 1,
                            dayOfMonth
                    );

                    etExpiry.setText(selectedDate);
                    etExpiry.setError(null);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void loadPantryItem() {
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

        etName.setText(item.name);
        etQuantity.setText(String.valueOf(item.quantity));

        etUnit.setText(
                item.unit != null ? item.unit : "",
                false
        );

        etExpiry.setText(
                item.expiryDate != null ? item.expiryDate : ""
        );
    }

    private void saveIngredient() {
        String name = getText(etName);
        String quantityText = getText(etQuantity);
        String unit = getText(etUnit);
        String expiry = getText(etExpiry);

        // Validate ingredient name.
        if (name.isEmpty()) {
            etName.setError("Ingredient name is required");
            etName.requestFocus();
            return;
        }

        etName.setError(null);

        // Validate quantity.
        if (quantityText.isEmpty()) {
            etQuantity.setError("Quantity is required");
            etQuantity.requestFocus();
            return;
        }

        double quantity;

        try {
            quantity = Double.parseDouble(quantityText);

            if (Double.isNaN(quantity)
                    || Double.isInfinite(quantity)
                    || quantity <= 0) {
                etQuantity.setError(
                        "Quantity must be greater than zero"
                );
                etQuantity.requestFocus();
                return;
            }
        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        etQuantity.setError(null);

        // Accept only units from the dropdown.
        boolean validUnit = false;

        for (String allowedUnit : units) {
            if (allowedUnit.equals(unit)) {
                validUnit = true;
                break;
            }
        }

        if (!validUnit) {
            etUnit.setError("Select a unit from the dropdown");
            etUnit.requestFocus();
            etUnit.showDropDown();
            return;
        }

        etUnit.setError(null);

        // Expiry date is optional, but must be valid if provided.
        if (!expiry.isEmpty()) {
            try {
                Date parsedDate = dateFormat.parse(expiry);

                if (parsedDate == null
                        || !dateFormat.format(parsedDate).equals(expiry)) {
                    throw new ParseException("Invalid date", 0);
                }
            } catch (ParseException e) {
                etExpiry.setError(
                        "Use a valid date in yyyy-MM-dd format"
                );
                etExpiry.requestFocus();
                return;
            }
        }

        etExpiry.setError(null);

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

        finish();
    }

    private String getText(android.widget.TextView view) {
        return view.getText() != null
                ? view.getText().toString().trim()
                : "";
    }

    private PantryItem findPantryItem() {
        List<PantryItem> items = db.pantryDao().getAllItems();

        for (PantryItem item : items) {
            if (item.id == pantryItemId) {
                return item;
            }
        }

        return null;
    }

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