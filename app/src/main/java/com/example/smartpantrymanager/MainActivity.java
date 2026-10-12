package com.example.smartpantrymanager;

import com.example.smartpantrymanager.data.AppDatabase;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ==========================================================
    // PANTRY
    // ==========================================================

    private List<PantryItem> pantryList = new ArrayList<>();
    private PantryAdapter pantryAdapter;

    // ==========================================================
    // RECIPES
    // ==========================================================

    private List<Recipe> recipeList = new ArrayList<>();
    private RecipeAdapter recipeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // ==========================================================
        // TOOLBAR
        // ==========================================================

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ==========================================================
        // DATABASE
        // ==========================================================

        AppDatabase db = AppDatabase.getInstance(this);

        // ==========================================================
        // PANTRY RECYCLER VIEW
        // ==========================================================

        RecyclerView rvPantry = findViewById(R.id.rvPantry);

        pantryList = db.pantryDao().getAllItems();

        pantryAdapter = new PantryAdapter(pantryList);

        rvPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPantry.setAdapter(pantryAdapter);

        Log.d(
                "PANTRY_TEST",
                "Pantry items loaded: " + pantryList.size()
        );

        // ==========================================================
        // LOAD RECIPES FROM LOCAL DATABASE (Replacing API code)
        // ==========================================================

        recipeList = db.recipeDao().getAllRecipes();

        Log.d("DB_LOAD", "Recipes loaded from DB: " + recipeList.size());

        // Run the matching test after recipes are loaded
        testRecipeMatching();

        // ==========================================================
        // BOTTOM NAVIGATION
        // ==========================================================

        BottomNavigationView bottomNav =
                findViewById(R.id.bottomNav);

        bottomNav.setOnItemSelectedListener(item -> {

            int itemId = item.getItemId();

            // ------------------------------------------------------
            // PANTRY
            // ------------------------------------------------------

            if (itemId == R.id.nav_pantry) {

                return true;
            }

            // ------------------------------------------------------
            // ADD INGREDIENT
            // ------------------------------------------------------

            else if (itemId == R.id.nav_add) {

                Intent intent = new Intent(
                        MainActivity.this,
                        AddEditIngredientActivity.class
                );

                startActivity(intent);

                return true;
            }

            // ------------------------------------------------------
            // RECIPES
            // ------------------------------------------------------

            else if (itemId == R.id.nav_recipes) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SuggestedRecipeActivity.class
                );

                startActivity(intent);

                return true;
            }

            // ------------------------------------------------------
            // SETTINGS
            // ------------------------------------------------------

            else if (itemId == R.id.nav_settings) {

                Intent intent = new Intent(
                        MainActivity.this,
                        SettingsActivity.class
                );

                startActivity(intent);

                return true;
            }

            return false;
        });
    }

    // ==========================================================
    // TEST RECIPE MATCHING
    // ==========================================================

    private void testRecipeMatching() {

        // Create a fake pantry for testing the recipe matcher
        List<PantryItem> testPantry =
                new ArrayList<>();

        testPantry.add(
                new PantryItem(
                        "Eggs",
                        6,
                        "pieces",
                        ""
                )
        );

        testPantry.add(
                new PantryItem(
                        "Cheese",
                        1,
                        "block",
                        ""
                )
        );

        testPantry.add(
                new PantryItem(
                        "Butter",
                        1,
                        "stick",
                        ""
                )
        );

        // ==========================================================
        // FIXED LINE HERE: Changed RecipeMatcher to MatchingEngine
        // ==========================================================
        List<Recipe> matched =
                MatchingEngine.getSuggestedRecipes(
                        recipeList,
                        testPantry
                );

        // Log results
        Log.d(
                "MATCH_TEST",
                "Recipes you can make: "
                        + matched.size()
        );

        for (Recipe recipe : matched) {

            Log.d(
                    "MATCH_TEST",
                    "Matched Recipe: "
                            + recipe.getName()
            );
        }
    }

    // ==========================================================
    // REFRESH PANTRY WHEN RETURNING TO THIS SCREEN
    // ==========================================================

    @Override
    protected void onResume() {

        super.onResume();

        // Get database
        AppDatabase db =
                AppDatabase.getInstance(this);

        // Get latest pantry items
        List<PantryItem> updatedPantry =
                db.pantryDao().getAllItems();

        Log.d(
                "PANTRY_TEST",
                "Pantry items in DB: "
                        + updatedPantry.size()
        );

        // Update the RecyclerView
        if (pantryAdapter != null) {

            pantryList.clear();

            pantryList.addAll(
                    updatedPantry
            );

            pantryAdapter.notifyDataSetChanged();
        }
    }
}