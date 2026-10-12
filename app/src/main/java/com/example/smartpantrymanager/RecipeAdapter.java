package com.example.smartpantrymanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartpantrymanager.data.Recipe;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.VH> {

    public interface OnRecipeClick { void onClick(Recipe r); }

    private final List<Recipe> recipes;
    private final OnRecipeClick listener;

    public RecipeAdapter(List<Recipe> recipes) {
        this(recipes, null);
    }

    public RecipeAdapter(List<Recipe> recipes, OnRecipeClick listener) {
        this.recipes = recipes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Recipe r = recipes.get(pos);
        h.name.setText(r.getName());
        if (listener != null) {
            h.itemView.setOnClickListener(v -> listener.onClick(r));
        }
    }

    @Override
    public int getItemCount() { return recipes != null ? recipes.size() : 0; }

    static class VH extends RecyclerView.ViewHolder {
        TextView name;
        VH(View v) {
            super(v);
            name = v.findViewById(R.id.tvRecipeName);
        }
    }
}
