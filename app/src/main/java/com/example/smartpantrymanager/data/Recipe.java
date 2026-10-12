package com.example.smartpantrymanager.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String ingredients;
    public String steps;

    // Stores whether the recipe is a favorite
    public boolean isFavorite = false;

    // Constructor
    public Recipe(String name, String ingredients, String steps) {
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.isFavorite = false;
    }

    // Get recipe name
    public String getName() {
        return name;
    }

    // Get recipe ingredients
    public String getIngredients() {
        return ingredients;
    }

    // Get recipe steps
    public String getSteps() {
        return steps;
    }

    // Check whether recipe is a favorite
    public boolean isFavorite() {
        return isFavorite;
    }
}