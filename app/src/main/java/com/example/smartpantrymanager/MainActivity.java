package com.example.smartpantrymanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // ==========================================================
    // PANTRY
    // ==========================================================

    private List<PantryItem> pantryList = new ArrayList<>();
    private PantryAdapter pantryAdapter;

    private RecyclerView rvPantry;
    private TextView tvEmpty;

    // ==========================================================
    // RECIPES
    // ==========================================================

    private List<Recipe> recipeList = new ArrayList<>();

    // ==========================================================
    // DATABASE
    // ==========================================================

    private AppDatabase db;

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

        db = AppDatabase.getInstance(this);

        // ==========================================================
        // PANTRY RECYCLER VIEW
        // ==========================================================

        rvPantry = findViewById(R.id.rvPantry);
        tvEmpty = findViewById(R.id.tvEmpty);

        pantryList = db.pantryDao().getAllItems();

        pantryAdapter = new PantryAdapter(
                pantryList,
                item -> openEditIngredient(item)
        );

        rvPantry.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvPantry.setAdapter(pantryAdapter);

        updateEmptyMessage();

        Log.d(
                "PANTRY_TEST",
                "Pantry items loaded: " + pantryList.size()
        );

        // ==========================================================
        // LOAD RECIPES
        // ==========================================================

        recipeList = db.recipeDao().getAllRecipes();

        Log.d(
                "DB_LOAD",
                "Recipes loaded from DB: " + recipeList.size()
        );

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
    // OPEN ITEM FOR EDITING
    // ==========================================================

    private void openEditIngredient(PantryItem item) {

        Intent intent = new Intent(
                MainActivity.this,
                AddEditIngredientActivity.class
        );

        // Send the ID of the item that was clicked
        intent.putExtra(
                "pantry_item_id",
                item.id
        );

        startActivity(intent);
    }

    // ==========================================================
    // UPDATE EMPTY PANTRY MESSAGE
    // ==========================================================

    private void updateEmptyMessage() {

        if (pantryList == null || pantryList.isEmpty()) {

            tvEmpty.setVisibility(TextView.VISIBLE);
            rvPantry.setVisibility(RecyclerView.GONE);

        } else {

            tvEmpty.setVisibility(TextView.GONE);
            rvPantry.setVisibility(RecyclerView.VISIBLE);
        }
    }

    // ==========================================================
    // REFRESH PANTRY WHEN RETURNING TO SCREEN
    // ==========================================================

    @Override
    protected void onResume() {

        super.onResume();

        // Don't refresh before the views are created
        if (db == null || pantryAdapter == null) {
            return;
        }

        // Get latest pantry items from Room
        List<PantryItem> updatedPantry =
                db.pantryDao().getAllItems();

        Log.d(
                "PANTRY_TEST",
                "Pantry items in DB: " + updatedPantry.size()
        );

        // Update the existing list
        pantryList.clear();
        pantryList.addAll(updatedPantry);

        // Tell RecyclerView that the data changed
        pantryAdapter.notifyDataSetChanged();

        // Update empty message
        updateEmptyMessage();
    }
}