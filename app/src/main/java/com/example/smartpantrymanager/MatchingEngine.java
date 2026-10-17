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

        List<Recipe> matches = new ArrayList<>();

        if (allRecipes == null || pantryItems == null) {
            return matches;
        }

        for (Recipe recipe : allRecipes) {
            if (canMakeRecipe(recipe, pantryItems)) {
                matches.add(recipe);
            }
        }

        return matches;
    }

    private static boolean canMakeRecipe(
            Recipe recipe, List<PantryItem> pantryItems) {

        if (recipe == null || recipe.ingredients == null
                || recipe.ingredients.trim().isEmpty()) {
            return false;
        }

        String[] ingredients = recipe.ingredients.split(",");

        for (String entry : ingredients) {
            entry = entry.trim();

            if (entry.isEmpty()) {
                return false;
            }

            // Detect optional ingredients before checking the pantry.
            boolean optional = entry.toLowerCase(Locale.ROOT)
                    .contains("(optional)");

            // Remove the optional label for ingredient-name matching.
            String cleanEntry = entry.replaceAll(
                    "(?i)\\s*\\(optional\\)", "").trim();

            String[] parts = cleanEntry.split(":", 2);
            String requiredName = normalise(parts[0]);

            if (requiredName.isEmpty()) {
                if (optional) {
                    continue;
                }
                return false;
            }

            boolean hasQuantity = parts.length == 2;
            double requiredAmount = 0;
            String requiredUnit = "";

            if (hasQuantity) {
                String amountText = parts[1].trim();
                String[] amountParts = amountText.split("\\s+", 2);

                if (amountParts.length != 2) {
                    if (optional) {
                        continue;
                    }
                    return false;
                }

                try {
                    requiredAmount =
                            Double.parseDouble(amountParts[0]);
                } catch (NumberFormatException e) {
                    if (optional) {
                        continue;
                    }
                    return false;
                }

                if (requiredAmount <= 0) {
                    if (optional) {
                        continue;
                    }
                    return false;
                }

                requiredUnit = normaliseUnit(amountParts[1]);

                if (toBase(requiredAmount, requiredUnit) < 0) {
                    if (optional) {
                        continue;
                    }
                    return false;
                }
            }

            boolean found = false;

            for (PantryItem item : pantryItems) {
                if (item == null || item.name == null
                        || !normalise(item.name).equals(requiredName)) {
                    continue;
                }

                if (!hasQuantity) {
                    found = true;
                    break;
                }

                if (item.quantity < 0 || item.unit == null) {
                    continue;
                }

                String pantryUnit = normaliseUnit(item.unit);

                if (!sameCategory(pantryUnit, requiredUnit)) {
                    continue;
                }

                double pantryAmount =
                        toBase(item.quantity, pantryUnit);

                double neededAmount =
                        toBase(requiredAmount, requiredUnit);

                if (pantryAmount >= 0 && neededAmount >= 0
                        && pantryAmount + 0.000001 >= neededAmount) {
                    found = true;
                    break;
                }
            }

            // Optional ingredients never exclude a recipe.
            // Required ingredients must all be available in sufficient
            // quantities.
            if (!found && !optional) {
                return false;
            }
        }

        return true;
    }

    private static String normalise(String name) {
        String value = name == null ? "" :
                name.trim().toLowerCase(Locale.ROOT)
                        .replaceAll("\\s+", " ");

        switch (value) {
            case "eggs": return "egg";
            case "tomatoes": return "tomato";
            case "potatoes": return "potato";
            case "onions": return "onion";
            case "bananas": return "banana";
            case "apples": return "apple";
            case "black pepper": return "pepper";
            default: return value;
        }
    }

    private static String normaliseUnit(String unit) {
        String value = unit == null ? "" :
                unit.trim().toLowerCase(Locale.ROOT);

        switch (value) {
            case "g":
            case "gram":
            case "grams":
            case "grams (g)":
                return "g";

            case "kg":
            case "kilogram":
            case "kilograms":
            case "kilograms (kg)":
                return "kg";

            case "ml":
            case "millilitre":
            case "millilitres":
            case "milliliters":
            case "millilitres (ml)":
                return "ml";

            case "l":
            case "litre":
            case "litres":
            case "liters":
            case "litres (l)":
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

    private static boolean sameCategory(String a, String b) {
        if (a.equals(b)) return true;

        return (isWeight(a) && isWeight(b))
                || (isVolume(a) && isVolume(b));
    }

    private static boolean isWeight(String unit) {
        return unit.equals("g") || unit.equals("kg");
    }

    private static boolean isVolume(String unit) {
        return unit.equals("ml") || unit.equals("l")
                || unit.equals("tsp") || unit.equals("tbsp")
                || unit.equals("cups");
    }

    private static double toBase(double amount, String unit) {
        switch (unit) {
            case "g": return amount;
            case "kg": return amount * 1000;
            case "ml": return amount;
            case "l": return amount * 1000;
            case "tsp": return amount * 5;
            case "tbsp": return amount * 15;
            case "cups": return amount * 240;
            case "pieces": return amount;
            default: return -1;
        }
    }
}