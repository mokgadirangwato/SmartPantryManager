
package com.example.smartpantrymanager;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MatchingEngine {

    public static List<Recipe> getSuggestedRecipes(
            List<Recipe> allRecipes,
            List<PantryItem> pantryItems) {

        List<Recipe> matchedRecipes = new ArrayList<>();

        if (allRecipes == null || pantryItems == null) {
            return matchedRecipes;
        }

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantryItems)) {
                matchedRecipes.add(recipe);
            }
        }

        return matchedRecipes;
    }

    private static boolean canMakeRecipe(
            Recipe recipe,
            List<PantryItem> pantryItems) {

        if (recipe == null || recipe.ingredients == null
                || recipe.ingredients.trim().isEmpty()) {
            return false;
        }

        String[] requiredIngredients = recipe.ingredients.split(",");

        for (String required : requiredIngredients) {
            required = required.trim();

            if (required.isEmpty()) {
                return false;
            }

            // Format: Ingredient:quantity unit
            // Example: Mayonnaise:3 tbsp
            String[] parts = required.split(":", 2);
            String requiredName = normalise(parts[0]);

            if (requiredName.isEmpty()) {
                return false;
            }

            boolean quantitySpecified = parts.length == 2;
            double requiredQuantity = 0;
            String requiredUnit = "";

            if (quantitySpecified) {
                String amount = parts[1].trim();
                String[] amountParts = amount.split("\\s+", 2);

                try {
                    requiredQuantity =
                            Double.parseDouble(amountParts[0]);

                    if (requiredQuantity <= 0) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }

                if (amountParts.length < 2) {
                    return false;
                }

                requiredUnit = normaliseUnit(amountParts[1]);

                if (requiredUnit.isEmpty()) {
                    return false;
                }
            }

            boolean found = false;

            for (PantryItem item : pantryItems) {
                if (item == null || item.name == null) {
                    continue;
                }

                if (!normalise(item.name).equals(requiredName)) {
                    continue;
                }

                // If no quantity is specified in the recipe,
                // require the ingredient to be present.
                if (!quantitySpecified) {
                    found = true;
                    break;
                }

                if (item.quantity < 0 || item.unit == null) {
                    continue;
                }

                String pantryUnit = normaliseUnit(item.unit);

                double pantryAmount =
                        convertToBase(item.quantity, pantryUnit);

                double requiredAmount =
                        convertToBase(requiredQuantity, requiredUnit);

                if (pantryAmount >= 0
                        && requiredAmount >= 0
                        && sameUnitCategory(pantryUnit, requiredUnit)
                        && pantryAmount + 0.000001 >= requiredAmount) {

                    found = true;
                    break;
                }
            }

            // Strict matching: any missing or insufficient
            // ingredient excludes the whole recipe.
            if (!found) {
                return false;
            }
        }

        return true;
    }

    private static String normalise(String name) {
        if (name == null) {
            return "";
        }

        String cleaned = name.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (cleaned) {
            case "tomatoes":
                return "tomato";
            case "potatoes":
                return "potato";
            case "eggs":
                return "egg";
            case "apples":
                return "apple";
            case "bananas":
                return "banana";
            case "onions":
                return "onion";
            case "black pepper":
                return "pepper";
            case "grated cheese":
                return "cheese";
            default:
                return cleaned;
        }
    }

    private static String normaliseUnit(String unit) {
        if (unit == null) {
            return "";
        }

        String cleaned = unit.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("\\s+", " ");

        switch (cleaned) {
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

            case "tbsp":
            case "tablespoon":
            case "tablespoons":
                return "tbsp";

            case "tsp":
            case "teaspoon":
            case "teaspoons":
                return "tsp";

            default:
                return cleaned;
        }
    }

    private static boolean sameUnitCategory(
            String pantryUnit, String requiredUnit) {

        if (pantryUnit.equals(requiredUnit)) {
            return true;
        }

        return ((pantryUnit.equals("g") || pantryUnit.equals("kg"))
                && (requiredUnit.equals("g")
                || requiredUnit.equals("kg")))
                || ((pantryUnit.equals("ml") || pantryUnit.equals("l"))
                && (requiredUnit.equals("ml")
                || requiredUnit.equals("l")))
                || ((pantryUnit.equals("tbsp") || pantryUnit.equals("tsp"))
                && (requiredUnit.equals("tbsp")
                || requiredUnit.equals("tsp")));
    }

    // Convert compatible units to a common base:
    // weight -> grams; volume -> millilitres.
    private static double convertToBase(
            double quantity, String unit) {

        switch (unit) {
            case "kg":
                return quantity * 1000;

            case "g":
            case "ml":
            case "pieces":
                return quantity;

            case "l":
                return quantity * 1000;

            case "tbsp":
                return quantity * 15;

            case "tsp":
                return quantity * 5;

            default:
                return -1;
        }
    }
}
