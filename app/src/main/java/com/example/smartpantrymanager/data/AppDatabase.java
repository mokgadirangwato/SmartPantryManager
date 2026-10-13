package com.example.smartpantrymanager.data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {PantryItem.class, Recipe.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    // DAO access
    public abstract PantryDao pantryDao();

    public abstract RecipeDao recipeDao();

    // Database migration: Version 1 to Version 2
    private static final Migration MIGRATION_1_2 =
            new Migration(1, 2) {
                @Override
                public void migrate(
                        @NonNull SupportSQLiteDatabase database) {

                    database.execSQL(
                            "ALTER TABLE recipes " +
                                    "ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0"
                    );
                }
            };

    // Get database instance
    public static synchronized AppDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "smart_pantry_db"
                    )
                    .allowMainThreadQueries()
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build();

            // Add sample recipes if the recipe table is empty
            if (instance.recipeDao().getCount() == 0) {
                seedRecipes(instance.recipeDao());
            }

            // Add new recipes and update existing recipe ingredients
            seedNewRecipes(instance.recipeDao());


            // Remove recipes that are no longer needed
            instance.recipeDao().deleteRecipeByName(
                    "Chicken Mayo Sandwich"
            );

            instance.recipeDao().deleteRecipeByName(
                    "Baked Beans on Toast"
            );

            instance.recipeDao().deleteRecipeByName(
                    "Tomato and Cheese Omelette"
            );

            instance.recipeDao().deleteRecipeByName(
                    "Apple Cinnamon Oats"
            );

            instance.recipeDao().deleteRecipeByName(
                    "Cheesy Baked Potatoes"
            );


            // Add sample pantry items only if the pantry is empty
            if (instance.pantryDao().getAllItems().isEmpty()) {
                seedPantry(instance.pantryDao());
            }
        }

        return instance;
    }

    // Seed pantry items
    private static void seedPantry(PantryDao dao) {

        dao.insert(new PantryItem("Eggs", 6, "pieces", ""));
        dao.insert(new PantryItem("Milk", 2, "litres", ""));
        dao.insert(new PantryItem("Bread", 1, "loaf", ""));
        dao.insert(new PantryItem("Cheese", 1, "block", ""));
        dao.insert(new PantryItem("Butter", 1, "pack", ""));
        dao.insert(new PantryItem("Oats", 1, "box", ""));
        dao.insert(new PantryItem("Cereal", 1, "box", ""));
        dao.insert(new PantryItem("Honey", 1, "bottle", ""));
        dao.insert(new PantryItem("Cinnamon", 1, "jar", ""));
        dao.insert(new PantryItem("Banana", 8, "pieces", ""));
        dao.insert(new PantryItem("Rice", 2, "kg", ""));
        dao.insert(new PantryItem("Pasta", 3, "packets", ""));
        dao.insert(new PantryItem("Flour", 2, "kg", ""));
        dao.insert(new PantryItem("Sugar", 1, "kg", ""));
        dao.insert(new PantryItem("Salt", 1, "kg", ""));
        dao.insert(new PantryItem("Cooking Oil", 2, "litres", ""));
        dao.insert(new PantryItem("Chicken", 2, "kg", ""));
        dao.insert(new PantryItem("Beef", 1, "kg", ""));
        dao.insert(new PantryItem("Potatoes", 2, "kg", ""));
        dao.insert(new PantryItem("Tomatoes", 6, "pieces", ""));
        dao.insert(new PantryItem("Onion", 5, "pieces", ""));
        dao.insert(new PantryItem("Apples", 6, "pieces", ""));
        dao.insert(new PantryItem("Baked Beans", 4, "cans", ""));
    }

    // Seed initial recipes
    private static void seedRecipes(RecipeDao dao) {

        dao.insert(new Recipe(
                "Honey Banana Cinnamon Oats",
                "Oats: 2 cups, Honey, Butter, Cinnamon, Banana, Milk",
                "1. Add oats and milk to a pot. " +
                        "2. Cook until soft and creamy. " +
                        "3. Stir in butter and cinnamon. " +
                        "4. Slice the banana and add it. " +
                        "5. Drizzle honey on top. " +
                        "6. Serve warm."
        ));

        dao.insert(new Recipe(
                "Spaghetti Bolognese",
                "Pasta, Beef, Tomatoes, Onion, Salt",
                "1. Boil the pasta. " +
                        "2. Fry the beef with onion. " +
                        "3. Add tomatoes and salt. " +
                        "4. Simmer for 15 minutes. " +
                        "5. Serve the sauce over the pasta."
        ));

        dao.insert(new Recipe(
                "Cheese Omelette",
                "Eggs: 2, Cheese:grated, Butter, Salt, Pepper",
                "1. Beat the eggs. " +
                        "2. Melt butter in a pan. " +
                        "3. Pour in the eggs. " +
                        "4. Add cheese, salt and pepper. " +
                        "5. Fold the omelette and serve."
        ));

        dao.insert(new Recipe(
                "Loaded Potato Bowl",
                "Potatoes, Cheese, Butter: 10g, Onion, Salt",
                "1. Boil or bake the potatoes until soft. " +
                        "2. Mash the potatoes with butter. " +
                        "3. Add chopped onion and cheese. " +
                        "4. Season with salt. " +
                        "5. Mix well and serve warm."
        ));
    }

    // Add new recipes and update existing recipes
    private static void seedNewRecipes(RecipeDao dao) {

        // Toasted Chicken Mayo
        if (dao.getCountByName("Toasted Chicken Mayo") == 0) {

            dao.insert(new Recipe(
                    "Toasted Chicken Mayo",
                    "Chicken, Bread, Mayonnaise:3 tbsp, Onion, Cheese, Salt, Black Pepper",
                    "1. Cook the chicken until fully cooked. " +
                            "2. Allow the chicken to cool, then dice it. " +
                            "3. Dice the onion into small pieces. " +
                            "4. Mix the chicken and onion with mayonnaise. " +
                            "5. Spread the mixture onto a slice of bread. " +
                            "6. Add cheese. " +
                            "7. Cover with another slice of bread. " +
                            "8. Toast until golden brown and the cheese melts. " +
                            "9. Serve warm."
            ));
        }

        // Update the saved recipe even if it already existed
        dao.updateIngredientsByName(
                "Toasted Chicken Mayo",
                "Chicken, Bread, Mayonnaise:3 tbsp, Onion, Cheese, Salt, Black Pepper"
        );

        // Corn Flakes
        if (dao.getCountByName("Corn Flakes") == 0) {

            dao.insert(new Recipe(
                    "Corn Flakes",
                    "Corn Flakes, Milk, Sugar",
                    "1. Pour the corn flakes into a bowl. " +
                            "2. Add milk. " +
                            "3. Add one teaspoon of sugar. " +
                            "4. Enjoy immediately."
            ));
        }

        // Update the saved Corn Flakes recipe
        dao.updateIngredientsByName(
                "Corn Flakes",
                "Corn Flakes, Milk, Sugar"
        );

        // Pancakes
        if (dao.getCountByName("Pancakes") == 0) {

            dao.insert(new Recipe(
                    "Pancakes",
                    "Flour: cups, Eggs: 2 pieces, Milk, Sugar, Butter",
                    "1. Add flour and sugar to a mixing bowl. " +
                            "2. Add the eggs and milk. " +
                            "3. Mix until you have a smooth batter. " +
                            "4. Melt a little butter in a frying pan. " +
                            "5. Pour some batter into the pan. " +
                            "6. Cook until bubbles appear on the surface. " +
                            "7. Flip the pancake and cook the other side until golden brown. " +
                            "8. Repeat with the remaining batter. " +
                            "9. Serve warm."
            ));
        }
        // 8. Chicken Fried Rice
        if (dao.getCountByName("Chicken Fried Rice") == 0) {
            dao.insert(new Recipe(
                    "Chicken Fried Rice",
                    "Rice, Chicken, Eggs, Onion, Cooking Oil, Salt",
                    "1. Cook the rice and set it aside. " +
                            "2. Cut the chicken into small pieces. " +
                            "3. Cook the chicken thoroughly in a pan. " +
                            "4. Fry the onion in cooking oil. " +
                            "5. Add the eggs and stir until cooked. " +
                            "6. Add the rice and salt. " +
                            "7. Mix everything together and serve."
            ));
        }

// 9. Beef and Tomato Rice
        if (dao.getCountByName("Beef and Tomato Rice") == 0) {
            dao.insert(new Recipe(
                    "Beef and Tomato Rice",
                    "Rice, Beef, Tomatoes, Onion, Cooking Oil, Salt",
                    "1. Cook the rice according to the instructions. " +
                            "2. Chop the onion and tomatoes. " +
                            "3. Fry the onion in cooking oil. " +
                            "4. Add the beef and cook thoroughly. " +
                            "5. Add the tomatoes and salt. " +
                            "6. Simmer until the tomatoes soften. " +
                            "7. Serve the beef mixture with rice."
            ));
        }

// 10. Creamy Chicken Pasta
        if (dao.getCountByName("Creamy Chicken Pasta") == 0) {
            dao.insert(new Recipe(
                    "Creamy Chicken Pasta",
                    "Pasta, Chicken, Milk, Cheese, Butter, Salt",
                    "1. Boil the pasta until tender. " +
                            "2. Cut the chicken into small pieces. " +
                            "3. Cook the chicken thoroughly. " +
                            "4. Melt butter in a saucepan. " +
                            "5. Add milk and cheese and stir. " +
                            "6. Add the chicken and cooked pasta. " +
                            "7. Mix well and season with salt."
            ));
        }

// 11. Banana Pancakes
        if (dao.getCountByName("Banana Pancakes") == 0) {
            dao.insert(new Recipe(
                    "Banana Pancakes",
                    "Banana, Flour, Eggs, Milk, Sugar, Butter",
                    "1. Mash the banana in a bowl. " +
                            "2. Add flour, eggs, milk and sugar. " +
                            "3. Mix until the batter is smooth. " +
                            "4. Melt butter in a frying pan. " +
                            "5. Pour small portions of batter into the pan. " +
                            "6. Cook both sides until golden brown. " +
                            "7. Serve warm."
            ));
        }

// 12. Honey Cinnamon Toast
        if (dao.getCountByName("Honey Cinnamon Toast") == 0) {
            dao.insert(new Recipe(
                    "Honey Cinnamon Toast",
                    "Bread, Butter, Honey, Cinnamon, Sugar",
                    "1. Toast the bread until golden brown. " +
                            "2. Spread butter over the warm toast. " +
                            "3. Drizzle honey over the bread. " +
                            "4. Sprinkle cinnamon and sugar on top. " +
                            "5. Serve immediately."
            ));
        }

//
// 13. Chicken Burger
        if (dao.getCountByName("Chicken Burger") == 0) {
            dao.insert(new Recipe(
                    "Chicken Burger",
                    "Chicken, Burger Buns, Lettuce, Tomato, Mayonnaise, Cheese, Cooking Oil, Salt",
                    "1. Season the chicken with salt. " +
                            "2. Cook the chicken thoroughly in cooking oil. " +
                            "3. Lightly toast the burger buns. " +
                            "4. Spread mayonnaise on the buns. " +
                            "5. Add lettuce, tomato, chicken and cheese. " +
                            "6. Close the burger and serve."
            ));
        }

// 14. Tuna Pasta Salad
        if (dao.getCountByName("Tuna Pasta Salad") == 0) {
            dao.insert(new Recipe(
                    "Tuna Pasta Salad",
                    "Pasta, Canned Tuna, Mayonnaise, Sweetcorn, Cucumber, Salt, Black Pepper",
                    "1. Boil the pasta until tender and allow it to cool. " +
                            "2. Drain the canned tuna. " +
                            "3. Chop the cucumber into small pieces. " +
                            "4. Mix the pasta, tuna, sweetcorn and cucumber. " +
                            "5. Add mayonnaise, salt and black pepper. " +
                            "6. Mix well and serve chilled."
            ));
        }

// 15. French Toast
        if (dao.getCountByName("French Toast") == 0) {
            dao.insert(new Recipe(
                    "French Toast",
                    "Bread, Eggs, Milk, Cinnamon, Sugar, Butter",
                    "1. Beat the eggs with milk, cinnamon and sugar. " +
                            "2. Dip each slice of bread into the mixture. " +
                            "3. Melt butter in a frying pan. " +
                            "4. Fry the bread on both sides until golden brown. " +
                            "5. Serve warm."
            ));
        }

// 16. Beef Tacos
        if (dao.getCountByName("Beef Tacos") == 0) {
            dao.insert(new Recipe(
                    "Beef Tacos",
                    "Beef, Taco Shells, Onion, Tomatoes, Lettuce, Cheese, Cooking Oil, Salt, Paprika",
                    "1. Chop the onion and tomatoes. " +
                            "2. Heat cooking oil in a pan. " +
                            "3. Add the beef and cook thoroughly. " +
                            "4. Season with salt and paprika. " +
                            "5. Warm the taco shells according to the package instructions. " +
                            "6. Fill the shells with beef, onion, tomato, lettuce and cheese. " +
                            "7. Serve immediately."
            ));
        }

// 17. Creamy Mushroom Pasta
        if (dao.getCountByName("Creamy Mushroom Pasta") == 0) {
            dao.insert(new Recipe(
                    "Creamy Mushroom Pasta",
                    "Pasta, Mushrooms, Milk, Cheese, Butter, Garlic, Salt, Black Pepper",
                    "1. Boil the pasta until tender. " +
                            "2. Slice the mushrooms and chop the garlic. " +
                            "3. Melt butter in a pan and cook the mushrooms and garlic. " +
                            "4. Add milk and cheese, stirring until creamy. " +
                            "5. Add the cooked pasta. " +
                            "6. Season with salt and black pepper. " +
                            "7. Mix well and serve."
            ));
        }

// 18. Chicken Curry
        if (dao.getCountByName("Chicken Curry") == 0) {
            dao.insert(new Recipe(
                    "Chicken Curry",
                    "Chicken, Rice, Onion, Tomatoes, Cooking Oil, Curry Powder, Garlic, Salt",
                    "1. Chop the onion and tomatoes. " +
                            "2. Heat cooking oil in a pot. " +
                            "3. Add onion and garlic and cook until fragrant. " +
                            "4. Add the chicken and cook thoroughly. " +
                            "5. Stir in curry powder and tomatoes. " +
                            "6. Simmer until the chicken is fully cooked and the sauce thickens. " +
                            "7. Cook the rice separately and serve with the curry."
            ));
        }
    }
}