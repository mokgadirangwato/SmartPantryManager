package com.example.smartpantrymanager.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface RecipeDao {

    @Query("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();

    @Insert
    void insertAll(List<Recipe> recipes);

    @Query("SELECT COUNT(*) FROM recipes")
    int getCount();
}