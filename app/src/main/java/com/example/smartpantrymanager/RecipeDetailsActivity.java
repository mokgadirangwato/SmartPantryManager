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

        String name =
                getIntent().getStringExtra("recipe_name");

        String ingredients =
                getIntent().getStringExtra("recipe_ingredients");

        String steps =
                getIntent().getStringExtra("recipe_steps");

        if (name != null) {
            tvRecipeTitle.setText(name);
        }

        if (ingredients != null) {
            tvIngredients.setText(ingredients);
        }

        if (steps != null) {
            tvSteps.setText(steps);
        }
    }
}