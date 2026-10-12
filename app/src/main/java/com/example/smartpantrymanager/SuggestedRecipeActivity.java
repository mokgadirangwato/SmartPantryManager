package com.example.smartpantrymanager;

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

import java.util.List;

public class SuggestedRecipeActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvNoRecipes;
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_suggested);

        recyclerView = findViewById(R.id.rvSuggested);
        tvNoRecipes = findViewById(R.id.tvNoRecipes);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );

        loadSuggestedRecipes();
    }

    private void loadSuggestedRecipes() {

        AppDatabase db = AppDatabase.getInstance(this);

        // Get recipes from Room database
        List<Recipe> allRecipes =
                db.recipeDao().getAllRecipes();

        // Get pantry items from Room database
        List<PantryItem> pantryItems =
                db.pantryDao().getAllItems();

        Log.d(
                "RECIPE_TEST",
                "Recipes in database: " + allRecipes.size()
        );

        Log.d(
                "RECIPE_TEST",
                "Pantry items in database: " + pantryItems.size()
        );

        // Match recipes against pantry
        List<Recipe> suggestedRecipes =
                RecipeMatcher.getSuggestedRecipes(
                        allRecipes,
                        pantryItems
                );

        Log.d(
                "RECIPE_TEST",
                "Suggested recipes: " + suggestedRecipes.size()
        );

        if (suggestedRecipes.isEmpty()) {

            tvNoRecipes.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

        } else {

            tvNoRecipes.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new RecipeAdapter(suggestedRecipes);

            recyclerView.setAdapter(adapter);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadSuggestedRecipes();
    }
}