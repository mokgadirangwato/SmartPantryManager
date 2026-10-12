package com.example.smartpantrymanager;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;
import java.util.ArrayList;
import java.util.List;

public class MatchingEngine {

    public static List<Recipe> getSuggestedRecipes(List<Recipe> allRecipes, List<PantryItem> pantryItems) {
        List<Recipe> matchedRecipes = new ArrayList<>();

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantryItems)) {
                matchedRecipes.add(recipe);
            }
        }
        return matchedRecipes;
    }

    private static boolean canMakeRecipe(Recipe recipe, List<PantryItem> pantryItems) {
        // Split recipe ingredients (e.g. "Eggs, Cheese, Butter") into an array
        String[] requiredIngredients = recipe.ingredients.split(",");

        for (String required : requiredIngredients) {
            String cleanRequired = required.trim().toLowerCase();
            if (cleanRequired.isEmpty()) continue;

            boolean hasIngredient = false;
            for (PantryItem item : pantryItems) {
                if (item.name.trim().toLowerCase().equals(cleanRequired)) {
                    hasIngredient = true;
                    break;
                }
            }

            if (!hasIngredient) {
                return false; // Missing an ingredient
            }
        }
        return true; // Has all ingredients!
    }
}