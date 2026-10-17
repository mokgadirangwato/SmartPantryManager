package com.example.smartpantrymanager;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class RecipeDetailsActivity extends AppCompatActivity {

    private TextView tvRecipeTitle;
    private TextView tvIngredients;
    private TextView tvSteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        tvRecipeTitle = findViewById(R.id.tvRecipeTitle);
        tvIngredients = findViewById(R.id.tvIngredients);
        tvSteps = findViewById(R.id.tvSteps);

        String name = getIntent().getStringExtra("recipe_name");
        String ingredients =
                getIntent().getStringExtra("recipe_ingredients");
        String steps = getIntent().getStringExtra("recipe_steps");

        if (name != null) {
            tvRecipeTitle.setText(name);
        }

        if (ingredients != null) {
            tvIngredients.setText(formatList(ingredients));
        }

        if (steps != null) {
            tvSteps.setText(formatSteps(steps));
        }
    }

    private String formatList(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "No ingredients available.";
        }

        // Keep existing line breaks if the list already has them.
        String[] items = text.split("\\s*[,;\\n]\\s*");
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < items.length; i++) {
            String item = items[i].trim();

            if (!item.isEmpty()) {
                result.append("• ").append(item);

                if (i < items.length - 1) {
                    result.append("\n");
                }
            }
        }

        return result.toString();
    }

    private String formatSteps(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "No preparation instructions available.";
        }

        // Split instructions at existing line breaks or numbered steps.
        String[] parts = text.trim().split(
                "\\s*(?:\\r?\\n)+\\s*|\\s+(?=\\d+[.)]\\s*)"
        );

        StringBuilder result = new StringBuilder();
        int stepNumber = 1;

        for (String part : parts) {
            String step = part.trim();

            if (step.isEmpty()) {
                continue;
            }

            // Remove an existing number so it isn't duplicated.
            step = step.replaceFirst("^\\d+[.)]\\s*", "");

            result.append(stepNumber)
                    .append(". ")
                    .append(step)
                    .append("\n\n");

            stepNumber++;
        }

        return result.toString().trim();
    }
}