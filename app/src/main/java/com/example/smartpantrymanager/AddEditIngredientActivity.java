package com.example.smartpantrymanager;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditIngredientActivity extends AppCompatActivity {

    private TextInputEditText etName;
    private TextInputEditText etQuantity;
    private TextInputEditText etUnit;
    private TextInputEditText etExpiry;

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_edit);

        // Link the UI components
        etName = findViewById(R.id.etName);
        etQuantity = findViewById(R.id.etQuantity);
        etUnit = findViewById(R.id.etUnit);
        etExpiry = findViewById(R.id.etExpiry);

        MaterialButton btnSave = findViewById(R.id.btnSave);

        // Get database instance
        db = AppDatabase.getInstance(this);

        // Save button
        btnSave.setOnClickListener(v -> saveIngredient());
    }

    private void saveIngredient() {

        // Get values from the input fields
        String name = etName.getText().toString().trim();
        String qtyString = etQuantity.getText().toString().trim();
        String unit = etUnit.getText().toString().trim();
        String expiry = etExpiry.getText().toString().trim();

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

        // Convert quantity to double
        double quantity;

        try {
            quantity = Double.parseDouble(qtyString);

            if (quantity <= 0) {
                etQuantity.setError("Quantity must be greater than 0");
                etQuantity.requestFocus();
                return;
            }

        } catch (NumberFormatException e) {
            etQuantity.setError("Enter a valid number");
            etQuantity.requestFocus();
            return;
        }

        // Create PantryItem
        PantryItem newItem = new PantryItem(
                name,
                quantity,
                unit,
                expiry
        );

        // Save to Room database
        db.pantryDao().insert(newItem);

        // Show confirmation
        Toast.makeText(
                this,
                "Ingredient saved!",
                Toast.LENGTH_SHORT
        ).show();

        // Return to MainActivity
        finish();
    }
}