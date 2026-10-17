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
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

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

        // Preserve the original picture-click behaviour.
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

        List<Recipe> allRecipes =
                db.recipeDao().getAllRecipes();

        List<PantryItem> pantryItems =
                db.pantryDao().getAllItems();

        // Preserve the existing strict ingredient/quantity matcher.
        suggestedRecipes =
                MatchingEngine.getSuggestedRecipes(
                        allRecipes,
                        pantryItems
                );

        Log.d("RECIPE_TEST",
                "Total saved recipes: " + allRecipes.size());

        Log.d("RECIPE_TEST",
                "Pantry items: " + pantryItems.size());

        Log.d("RECIPE_TEST",
                "Matching recipes: " + suggestedRecipes.size());

        displaySuggestedRecipes(
                allRecipes,
                pantryItems
        );
    }

    private void displaySuggestedRecipes(
            List<Recipe> allRecipes,
            List<PantryItem> pantryItems) {

        // Keep the original layout hidden until the picture is tapped.
        if (!showRecipes) {
            recyclerView.setVisibility(View.GONE);
            tvNoRecipes.setVisibility(View.GONE);
            return;
        }

        if (suggestedRecipes == null) {
            suggestedRecipes = new ArrayList<>();
        }

        // Find recipes that are currently unavailable.
        List<String> missingRecipeMessages =
                getMissingRecipeMessages(
                        allRecipes,
                        pantryItems
                );

        if (suggestedRecipes.isEmpty()) {

            recyclerView.setVisibility(View.GONE);
            recyclerView.setAdapter(null);
            adapter = null;

            StringBuilder message = new StringBuilder();

            message.append(
                    "No recipes match your pantry yet.\n\n"
            );

            message.append(
                    "Please add the missing ingredients or "
                            + "increase their quantities in your "
                            + "pantry to unlock more recipes."
            );

            appendMissingRecipeSummary(
                    message,
                    missingRecipeMessages
            );

            tvNoRecipes.setText(message.toString());
            tvNoRecipes.setVisibility(View.VISIBLE);

        } else {

            // Show recipes that can be made.
            recyclerView.setVisibility(View.VISIBLE);

            adapter = new RecipeAdapter(
                    suggestedRecipes,
                    this::openRecipeDetails
            );

            recyclerView.setAdapter(adapter);

            // Show additional feedback about unavailable recipes.
            if (missingRecipeMessages.isEmpty()) {

                tvNoRecipes.setVisibility(View.GONE);

            } else {

                StringBuilder message = new StringBuilder();

                message.append(
                        "You can make the recipes shown above!\n\n"
                );

                message.append(
                        "Want more recipes? Some recipes are "
                                + "currently unavailable because "
                                + "ingredients are missing or "
                                + "quantities are too low.\n\n"
                );

                message.append(
                        "Add the missing ingredients to your "
                                + "pantry to unlock more recipes."
                );

                appendMissingRecipeSummary(
                        message,
                        missingRecipeMessages
                );

                tvNoRecipes.setText(message.toString());
                tvNoRecipes.setVisibility(View.VISIBLE);
            }
        }
    }

    private void appendMissingRecipeSummary(
            StringBuilder message,
            List<String> missingRecipeMessages) {

        if (missingRecipeMessages == null
                || missingRecipeMessages.isEmpty()) {
            return;
        }

        message.append("\n\nRecipes you could unlock:\n");

        int limit = Math.min(
                missingRecipeMessages.size(),
                5
        );

        for (int i = 0; i < limit; i++) {
            message.append("\n• ");
            message.append(missingRecipeMessages.get(i));
        }

        if (missingRecipeMessages.size() > limit) {
            message.append("\n\nAnd ");
            message.append(
                    missingRecipeMessages.size() - limit
            );
            message.append(" more recipes need ingredients.");
        }
    }

    /**
     * Identifies unavailable recipes without changing the
     * existing strict matching rules.
     *
     * Ingredient requirements are parsed from the same
     * comma-separated format used by MatchingEngine.
     */
    private List<String> getMissingRecipeMessages(
            List<Recipe> allRecipes,
            List<PantryItem> pantryItems) {

        List<String> messages = new ArrayList<>();

        if (allRecipes == null || pantryItems == null) {
            return messages;
        }

        Set<String> matchingNames = new LinkedHashSet<>();

        for (Recipe recipe : suggestedRecipes) {
            if (recipe != null && recipe.getName() != null) {
                matchingNames.add(
                        recipe.getName().trim().toLowerCase(
                                Locale.ROOT
                        )
                );
            }
        }

        for (Recipe recipe : allRecipes) {

            if (recipe == null
                    || recipe.getName() == null
                    || recipe.getIngredients() == null) {
                continue;
            }

            String recipeKey = recipe.getName()
                    .trim()
                    .toLowerCase(Locale.ROOT);

            // Do not list recipes that can already be made.
            if (matchingNames.contains(recipeKey)) {
                continue;
            }

            List<String> missing = getMissingIngredients(
                    recipe.getIngredients(),
                    pantryItems
            );

            if (!missing.isEmpty()) {

                StringBuilder entry = new StringBuilder();

                entry.append(recipe.getName());
                entry.append(" — Missing: ");

                for (int i = 0; i < missing.size(); i++) {

                    if (i > 0) {
                        entry.append(", ");
                    }

                    entry.append(missing.get(i));
                }

                messages.add(entry.toString());
            }
        }

        Collections.sort(
                messages,
                Comparator.comparing(
                        String::toLowerCase,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        return messages;
    }

    /**
     * Reports ingredients that are absent or have insufficient
     * quantities. Optional ingredients do not block a recipe.
     */
    private List<String> getMissingIngredients(
            String ingredientsText,
            List<PantryItem> pantryItems) {

        Set<String> missing = new LinkedHashSet<>();

        if (ingredientsText == null
                || ingredientsText.trim().isEmpty()) {
            return new ArrayList<>(missing);
        }

        String[] entries = ingredientsText.split(",");

        for (String rawEntry : entries) {

            String entry = rawEntry.trim();

            if (entry.isEmpty()) {
                continue;
            }

            boolean optional = entry.toLowerCase(Locale.ROOT)
                    .contains("(optional)");

            entry = entry.replaceAll(
                    "(?i)\\s*\\(optional\\)",
                    ""
            ).trim();

            String[] parts = entry.split(":", 2);

            String ingredientName = parts[0].trim();

            if (ingredientName.isEmpty()) {
                continue;
            }

            if (optional) {
                continue;
            }

            // Use the same matching engine as the actual suggestions.
            // Test each ingredient independently to identify which
            // requirement is not satisfied.
            if (!isIngredientAvailable(
                    ingredientName,
                    parts,
                    pantryItems)) {

                missing.add(ingredientName);
            }
        }

        return new ArrayList<>(missing);
    }

    /**
     * Checks one requirement using the same name and quantity
     * rules as MatchingEngine, without modifying that class.
     */
    private boolean isIngredientAvailable(
            String ingredientName,
            String[] parts,
            List<PantryItem> pantryItems) {

        String requiredName = normaliseName(ingredientName);

        if (requiredName.isEmpty()) {
            return false;
        }

        boolean hasQuantity = parts.length == 2;

        double requiredAmount = 0;
        String requiredUnit = "";

        if (hasQuantity) {

            String amountText = parts[1].trim();

            String[] amountParts = amountText.split(
                    "\\s+",
                    2
            );

            if (amountParts.length != 2) {
                return false;
            }

            try {
                requiredAmount = Double.parseDouble(
                        amountParts[0]
                );
            } catch (NumberFormatException e) {
                return false;
            }

            if (requiredAmount <= 0) {
                return false;
            }

            requiredUnit = normaliseUnit(
                    amountParts[1]
            );

            if (toBase(requiredAmount, requiredUnit) < 0) {
                return false;
            }
        }

        for (PantryItem item : pantryItems) {

            if (item == null || item.name == null) {
                continue;
            }

            if (!normaliseName(item.name).equals(requiredName)) {
                continue;
            }

            if (!hasQuantity) {
                return true;
            }

            if (item.unit == null || item.quantity < 0) {
                continue;
            }

            String pantryUnit = normaliseUnit(item.unit);

            if (!sameCategory(pantryUnit, requiredUnit)) {
                continue;
            }

            double pantryAmount = toBase(
                    item.quantity,
                    pantryUnit
            );

            double neededAmount = toBase(
                    requiredAmount,
                    requiredUnit
            );

            if (pantryAmount >= 0
                    && neededAmount >= 0
                    && pantryAmount + 0.000001 >= neededAmount) {
                return true;
            }
        }

        return false;
    }

    private String normaliseName(String name) {

        String value = name == null ? "" :
                name.trim()
                        .toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " ");

        switch (value) {
            case "eggs":
                return "egg";

            case "tomatoes":
                return "tomato";

            case "potatoes":
                return "potato";

            case "onions":
                return "onion";

            case "bananas":
                return "banana";

            case "apples":
                return "apple";

            case "black pepper":
                return "pepper";

            case "cooking oil":
            case "vegetable oil":
            case "sunflower oil":
                return "oil";

            default:
                return value;
        }
    }

    private String normaliseUnit(String unit) {

        String value = unit == null ? "" :
                unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "g":
            case "gram":
            case "grams":
                return "g";

            case "kg":
            case "kilogram":
            case "kilograms":
                return "kg";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliter":
            case "milliliters":
                return "ml";

            case "l":
            case "litre":
            case "litres":
            case "liter":
            case "liters":
                return "l";

            case "piece":
            case "pieces":
            case "pc":
            case "pcs":
                return "pieces";

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "tsp";

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "cup":
            case "cups":
                return "cups";

            default:
                return value;
        }
    }

    private boolean sameCategory(String a, String b) {

        if (a.equals(b)) {
            return true;
        }

        return (isWeight(a) && isWeight(b))
                || (isVolume(a) && isVolume(b));
    }

    private boolean isWeight(String unit) {

        return unit.equals("g")
                || unit.equals("kg");
    }

    private boolean isVolume(String unit) {

        return unit.equals("ml")
                || unit.equals("l")
                || unit.equals("tsp")
                || unit.equals("tbsp")
                || unit.equals("cups");
    }

    private double toBase(double amount, String unit) {

        switch (unit) {
            case "g":
                return amount;

            case "kg":
                return amount * 1000;

            case "ml":
                return amount;

            case "l":
                return amount * 1000;

            case "tsp":
                return amount * 5;

            case "tbsp":
                return amount * 15;

            case "cups":
                return amount * 240;

            case "pieces":
                return amount;

            default:
                return -1;
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

        // Refresh suggestions and missing-ingredient feedback
        // whenever the user returns from another screen.
        if (recyclerView != null && tvNoRecipes != null) {
            loadSuggestedRecipes();
        }
    }
}