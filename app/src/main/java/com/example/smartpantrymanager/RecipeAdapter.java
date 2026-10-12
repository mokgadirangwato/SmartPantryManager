package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        if (listener != null) {

            holder.itemView.setOnClickListener(
                    v -> listener.onClick(recipe)
            );

        } else {

            holder.itemView.setOnClickListener(null);
        }
    }

    @Override
    public int getItemCount() {

        return recipes != null
                ? recipes.size()
                : 0;
    }

    static class VH extends RecyclerView.ViewHolder {

        TextView name;

        VH(@NonNull View view) {
            super(view);

            name = view.findViewById(
                    R.id.tvRecipeName
            );
        }
    }
}