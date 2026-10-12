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

    // ==========================================================
    // DATABASE MIGRATION: VERSION 1 -> VERSION 2
    // ==========================================================

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

    // ==========================================================
    // GET DATABASE INSTANCE
    // ==========================================================

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

            // Add sample pantry items if the pantry is empty
            if (instance.pantryDao().getAllItems().isEmpty()) {
                seedPantry(instance.pantryDao());
            }
        }

        return instance;
    }

    // ==========================================================
    // SEED PANTRY ITEMS
    // ==========================================================

    private static void seedPantry(PantryDao dao) {

        dao.insert(new PantryItem(
                "Eggs",
                6,
                "pieces",
                ""
        ));

        dao.insert(new PantryItem(
                "Milk",
                2,
                "litres",
                ""
        ));

        dao.insert(new PantryItem(
                "Bread",
                1,
                "loaf",
                ""
        ));

        dao.insert(new PantryItem(
                "Cheese",
                1,
                "block",
                ""
        ));

        dao.insert(new PantryItem(
                "Butter",
                1,
                "pack",
                ""
        ));

        dao.insert(new PantryItem(
                "Oats",
                1,
                "box",
                ""
        ));

        dao.insert(new PantryItem(
                "Cereal",
                1,
                "box",
                ""
        ));

        dao.insert(new PantryItem(
                "Honey",
                1,
                "bottle",
                ""
        ));

        dao.insert(new PantryItem(
                "Cinnamon",
                1,
                "jar",
                ""
        ));

        dao.insert(new PantryItem(
                "Banana",
                8,
                "pieces",
                ""
        ));

        dao.insert(new PantryItem(
                "Rice",
                2,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Pasta",
                3,
                "packets",
                ""
        ));

        dao.insert(new PantryItem(
                "Flour",
                2,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Sugar",
                1,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Salt",
                1,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Cooking Oil",
                2,
                "litres",
                ""
        ));

        dao.insert(new PantryItem(
                "Chicken",
                2,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Beef",
                1,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Potatoes",
                2,
                "kg",
                ""
        ));

        dao.insert(new PantryItem(
                "Tomatoes",
                6,
                "pieces",
                ""
        ));

        dao.insert(new PantryItem(
                "Onion",
                5,
                "pieces",
                ""
        ));

        dao.insert(new PantryItem(
                "Apples",
                6,
                "pieces",
                ""
        ));

        dao.insert(new PantryItem(
                "Baked Beans",
                4,
                "cans",
                ""
        ));
    }

    // ==========================================================
    // SEED RECIPES
    // ==========================================================

    private static void seedRecipes(RecipeDao dao) {

        dao.insert(
                new Recipe(
                        "Honey Banana Cinnamon Oats",

                        "Oats, Honey, Butter, Cinnamon, Banana, Milk",

                        "1. Add oats and milk to a pot. " +
                                "2. Cook until soft and creamy. " +
                                "3. Stir in butter and cinnamon. " +
                                "4. Slice the banana and add it. " +
                                "5. Drizzle honey on top. " +
                                "6. Serve warm."
                )
        );

        dao.insert(
                new Recipe(
                        "Spaghetti Bolognese",

                        "Pasta, Beef, Tomatoes, Onion, Salt",

                        "1. Boil the pasta. " +
                                "2. Fry the beef with onion. " +
                                "3. Add tomatoes and salt. " +
                                "4. Simmer for 15 minutes. " +
                                "5. Serve the sauce over the pasta."
                )
        );

        dao.insert(
                new Recipe(
                        "Cheese Omelette",

                        "Eggs, Cheese, Butter, Salt, Pepper",

                        "1. Beat the eggs. " +
                                "2. Melt butter in a pan. " +
                                "3. Pour in the eggs. " +
                                "4. Add cheese, salt and pepper. " +
                                "5. Fold the omelette and serve."
                )
        );

        dao.insert(
                new Recipe(
                        "Chicken Mayo Sandwich",

                        "Bread, Chicken, Mayonnaise, Lettuce, Cheese",

                        "1. Cook and slice the chicken. " +
                                "2. Spread mayonnaise on the bread. " +
                                "3. Add chicken, lettuce and cheese. " +
                                "4. Place the second slice of bread on top. " +
                                "5. Cut and serve."
                )
        );

        dao.insert(
                new Recipe(
                        "Loaded Potato Bowl",

                        "Potatoes, Cheese, Butter, Onion, Salt",

                        "1. Boil or bake the potatoes until soft. " +
                                "2. Mash the potatoes with butter. " +
                                "3. Add chopped onion and cheese. " +
                                "4. Season with salt. " +
                                "5. Mix well and serve warm."
                )
        );
    }
}