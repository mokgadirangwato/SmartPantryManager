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
public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }
    private final List<Recipe> recipes;
    private final OnRecipeClickListener listener;
    public RecipeAdapter(List<Recipe> recipes) {
        this(recipes, null);
    }
    public RecipeAdapter(
            List<Recipe> recipes,
            OnRecipeClickListener listener) {
        this.recipes = recipes;
        this.listener = listener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {
        Recipe recipe = recipes.get(position);
        // Display the recipe name
        holder.tvRecipeName.setText(recipe.getName());
        // Display the favourite status
        holder.tvFavorite.setText(
                recipe.isFavorite() ? "♥" : "♡"
        );
        // Handle favourite button clicks
        holder.tvFavorite.setOnClickListener(v -> {
            boolean newFavoriteStatus = !recipe.isFavorite();
            // Update the recipe object
            recipe.isFavorite = newFavoriteStatus;
            // Update the icon immediately
            holder.tvFavorite.setText(
                    newFavoriteStatus ? "♥" : "♡"
            );
            // Save the favourite status in the database
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(
                        v.getContext().getApplicationContext()
                );
                if (db != null) {
                    db.recipeDao().setFavorite(
                            recipe.id,
                            newFavoriteStatus
                    );
                }
            }).start();
        });
        // Handle recipe item clicks
        if (listener != null) {
            holder.itemView.setOnClickListener(
                    v -> listener.onRecipeClick(recipe)
            );
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }
    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRecipeName;
        TextView tvFavorite;
        ViewHolder(@NonNull View view) {
            super(view);
            tvRecipeName = view.findViewById(R.id.tvRecipeName);
            tvFavorite = view.findViewById(R.id.tvFavorite);
        }
    }
}