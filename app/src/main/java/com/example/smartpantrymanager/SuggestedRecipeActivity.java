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

        // Set up the recipe list.
        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        // Show all recipes when the picture is clicked.
        recipeCard.setOnClickListener(v -> {
            showRecipes = true;
            loadSuggestedRecipes();
        });

        loadSuggestedRecipes();
    }

    // Load ALL recipes from the database.
    private void loadSuggestedRecipes() {

        if (db == null) {
            db = AppDatabase.getInstance(this);
        }

        List<Recipe> allRecipes = db.recipeDao().getAllRecipes();

        // Display every saved recipe, without ingredient filtering.
        suggestedRecipes = new ArrayList<>(allRecipes);

        Log.d(
                "RECIPE_TEST",
                "Recipes loaded from database: " + allRecipes.size()
        );

        Log.d(
                "RECIPE_TEST",
                "Recipes displayed: " + suggestedRecipes.size()
        );

        displaySuggestedRecipes();
    }

    // Display recipes in the RecyclerView.
    private void displaySuggestedRecipes() {

        // Keep the list hidden until the picture is tapped.
        if (!showRecipes) {
            recyclerView.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.GONE);
            return;
        }

        if (suggestedRecipes == null || suggestedRecipes.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.VISIBLE);
            tvNoRecipes.setText("No recipes available. Please add recipes.");

        } else {

            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Create the adapter only once.
            if (adapter == null) {

                adapter = new RecipeAdapter(
                        suggestedRecipes,
                        recipe -> openRecipeDetails(recipe)
                );

                recyclerView.setAdapter(adapter);

            } else {

                // Refresh the adapter with the latest database records.
                adapter = new RecipeAdapter(
                        suggestedRecipes,
                        recipe -> openRecipeDetails(recipe)
                );

                recyclerView.setAdapter(adapter);
            }
        }
    }

    // Open the selected recipe's details.
    private void openRecipeDetails(Recipe recipe) {

        Intent intent = new Intent(
                SuggestedRecipeActivity.this,
                RecipeDetailsActivity.class
        );

        intent.putExtra("recipe_name", recipe.getName());
        intent.putExtra("recipe_ingredients", recipe.getIngredients());
        intent.putExtra("recipe_steps", recipe.getSteps());

        startActivity(intent);
    }

    // Reload recipes when returning to this screen.
    @Override
    protected void onResume() {
        super.onResume();

        if (recyclerView != null && tvNoRecipes != null) {
            loadSuggestedRecipes();
        }
    }
}