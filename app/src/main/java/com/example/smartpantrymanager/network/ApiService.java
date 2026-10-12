package com.example.smartpantrymanager.network;

import com.example.smartpantrymanager.data.PantryItem;
import com.example.smartpantrymanager.data.Recipe;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface ApiService {

    @GET("pantry.php")
    Call<List<PantryItem>> getPantry();

    @POST("pantry.php")
    Call<ApiResponse> addPantry(@Body PantryItem item);

    @PUT("pantry.php")
    Call<ApiResponse> updatePantry(@Body PantryItem item);

    @DELETE("pantry.php")
    Call<ApiResponse> deletePantry(@Query("id") long id);

    @GET("recipes.php")
    Call<List<Recipe>> getRecipes();
}
