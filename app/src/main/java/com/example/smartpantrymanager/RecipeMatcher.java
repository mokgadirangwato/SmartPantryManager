package com.example.smartpantrymanager;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeMatcher {

    public static List<Recipe> getSuggestedRecipes(
            List<Recipe> recipes,
            List<PantryItem> pantryItems) {

        List<Recipe> suggestedRecipes = new ArrayList<>();

        if (recipes == null || pantryItems == null) {
            return suggestedRecipes;
        }

        for (Recipe recipe : recipes) {

            if (recipe == null || recipe.getIngredients() == null) {
                continue;
            }

            String[] ingredients =
                    recipe.getIngredients().split(",");

            boolean canMakeRecipe = true;

            for (String ingredient : ingredients) {

                String requiredIngredient =
                        ingredient.trim().toLowerCase();

                boolean found = false;

                for (PantryItem pantryItem : pantryItems) {

                    if (pantryItem != null
                            && pantryItem.name != null) {

                        String pantryName =
                                pantryItem.name.trim().toLowerCase();

                        if (pantryName.equals(requiredIngredient)) {
                            found = true;
                            break;
                        }
                    }
                }

                if (!found) {
                    canMakeRecipe = false;
                    break;
                }
            }

            if (canMakeRecipe) {
                suggestedRecipes.add(recipe);
            }
        }

        return suggestedRecipes;
    }
}