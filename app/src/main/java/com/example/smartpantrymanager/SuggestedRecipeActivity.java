package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;

import java.util.ArrayList;
import java.util.List;

public class SuggestedRecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoRecipes;
    private View recipeCard;
    private RecipeAdapter adapter;

    private List<Recipe> suggestedRecipes = new ArrayList<>();
    private AppDatabase db;
    private boolean showRecipes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggested);

        recyclerView = findViewById(R.id.rvSuggested);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);
        recipeCard = findViewById(R.id.recipeCard);

        db = AppDatabase.getInstance(this);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Show matching recipes when the recipe picture is tapped.
        recipeCard.setOnClickListener(v -> {
            showRecipes = true;
            loadSuggestedRecipes();
        });

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        if (db == null) {
            db = AppDatabase.getInstance(this);
        }

        // Load all saved recipes.
        List<Recipe> allRecipes =
                db.recipeDao().getAllRecipes();

        // Load the current pantry contents.
        List<PantryItem> pantryItems =
                db.pantryDao().getAllItems();

        // Apply strict ingredient matching.
        suggestedRecipes =
                MatchingEngine.getSuggestedRecipes(
                        allRecipes,
                        pantryItems
                );

        // DEBUG: Print all pantry ingredients.
        for (PantryItem item : pantryItems) {
            Log.d(
                    "PANTRY_CHECK",
                    "Ingredient: [" + item.name + "]"
            );
        }

        // DEBUG: Print all saved recipes and their ingredients.
        for (Recipe recipe : allRecipes) {
            Log.d(
                    "PANTRY_CHECK",
                    "Recipe: " + recipe.getName()
                            + " | Ingredients: "
                            + recipe.getIngredients()
            );
        }

        // DEBUG: Show the number of matching recipes.
        Log.d(
                "PANTRY_CHECK",
                "Matching recipes: " + suggestedRecipes.size()
        );

        Log.d(
                "RECIPE_TEST",
                "Total recipes: " + allRecipes.size()
        );

        Log.d(
                "RECIPE_TEST",
                "Pantry items: " + pantryItems.size()
        );

        Log.d(
                "RECIPE_TEST",
                "Strictly matching recipes: "
                        + suggestedRecipes.size()
        );

        displaySuggestedRecipes();
    }

    private void displaySuggestedRecipes() {

        // Keep the list hidden until the recipe picture is tapped.
        if (!showRecipes) {
            recyclerView.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.GONE);
            return;
        }

        if (suggestedRecipes == null
                || suggestedRecipes.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.VISIBLE);

            tvNoRecipes.setText(
                    "No recipes match your current pantry. "
                            + "Add the missing ingredients to get suggestions."
            );

            recyclerView.setAdapter(null);
            adapter = null;

        } else {

            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Refresh the list with matching recipes only.
            adapter = new RecipeAdapter(
                    suggestedRecipes,
                    this::openRecipeDetails
            );

            recyclerView.setAdapter(adapter);
        }
    }

    private void openRecipeDetails(Recipe recipe) {

        Intent intent = new Intent(
                SuggestedRecipeActivity.this,
                RecipeDetailsActivity.class
        );

        intent.putExtra(
                "recipe_name",
                recipe.getName()
        );

        intent.putExtra(
                "recipe_ingredients",
                recipe.getIngredients()
        );

        intent.putExtra(
                "recipe_steps",
                recipe.getSteps()
        );

        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Reload pantry and recipes whenever this screen resumes.
        if (recyclerView != null && tvNoRecipes != null) {
            loadSuggestedRecipes();
        }
    }
}