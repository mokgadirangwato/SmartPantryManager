package com.example.smartpantrymanager.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface RecipeDao {

    // READ: Get all recipes
    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();

    // CREATE: Add one recipe
    @Insert
    void insert(Recipe recipe);

    // CREATE: Add multiple sample recipes
    @Insert
    void insertAll(List<Recipe> recipes);

    // UPDATE: Edit an existing recipe
    @Update
    void update(Recipe recipe);

    // DELETE: Remove a recipe
    @Delete
    void delete(Recipe recipe);

    // COUNT: Check how many recipes exist
    @Query("SELECT COUNT(*) FROM recipes")
    int getCount();

    // FAVORITES: Get only favorited recipes
    @Query("SELECT * FROM recipes WHERE isFavorite = 1")
    List<Recipe> getFavoriteRecipes();

    // FAVORITES: Mark or unmark a recipe as favorite
    @Query("UPDATE recipes SET isFavorite = :favorite WHERE id = :recipeId")
    void setFavorite(int recipeId, boolean favorite);
}