package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.AppDatabase;
import com.example.smartpantrymanager.data.Recipe;

import java.util.List;

public class RecipeAdapter
        extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick {
        void onClick(Recipe recipe);
    }

    private final List<Recipe> recipes;
    private final OnRecipeClick listener;

    public RecipeAdapter(List<Recipe> recipes) {
        this(recipes, null);
    }

    public RecipeAdapter(
            List<Recipe> recipes,
            OnRecipeClick listener) {

        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull VH holder,
            int position) {

        Recipe recipe = recipes.get(position);

        holder.name.setText(recipe.getName());

        // Show whether this recipe is already a favourite.
        updateFavoriteIcon(holder, recipe);

        // Favourite button: save the change in the database.
        holder.favorite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !recipe.isFavorite;

            AppDatabase db = AppDatabase.getInstance(
                    holder.itemView.getContext()
            );

            db.recipeDao().setFavorite(
                    recipe.id,
                    newFavoriteStatus
            );

            // Update the in-memory recipe and refresh its heart.
            recipe.isFavorite = newFavoriteStatus;
            updateFavoriteIcon(holder, recipe);
        });

        // Keep the existing recipe-details click functionality.
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(recipe);
            }
        });
    }

    private void updateFavoriteIcon(VH holder, Recipe recipe) {
        if (recipe.isFavorite) {
            holder.favorite.setText("♥");
            holder.favorite.setContentDescription(
                    "Remove from favourites"
            );
        } else {
            holder.favorite.setText("♡");
            holder.favorite.setContentDescription(
                    "Add to favourites"
            );
        }
    }

    @Override
    public int getItemCount() {
        return recipes != null ? recipes.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView name;
        TextView favorite;

        VH(@NonNull View view) {
            super(view);

            name = view.findViewById(R.id.tvRecipeName);
            favorite = view.findViewById(R.id.tvFavorite);
        }
    }
}