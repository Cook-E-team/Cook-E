/*
 * Copyright 2016 the Cook-E development team
 *
 * This file is part of Cook-E.
 *
 * Cook-E is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Cook-E is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.cook_e;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;

import org.cook_e.cook_e.ui.ListListAdapter;
import org.cook_e.cook_e.ui.RecipeListItemView;
import org.cook_e.data.Recipe;

import java.util.List;

/**
 * A list adapter that displays a list of meals, with each meal shown in a {@link RecipeListItemView}
 *
 * A space is added at the bottom of the list for compatibility with floating action buttons.
 */
public class MealRecipeListAdapter extends ListListAdapter<Recipe> {

    /**
     * The recipes displayed in this list
     *
     * This is a reference to the same list that the superclass uses, so it can modify the list
     * data source.
     */
    private final List<? extends Recipe> recipes;

    /**
     * Creates a new adapter
     *
     * @param context the context to use to create views
     * @param items   the items to display
     * @throws NullPointerException if items is null
     */
    public MealRecipeListAdapter(@NonNull Context context, @NonNull ObservableArrayList<? extends Recipe> items) {
        super(context, items);
        recipes = items;
    }


    @Override
    public View getViewForItem(final Recipe item, int index, View convertView, ViewGroup parent, final Context context) {
        RecipeListItemView view;
        if (convertView instanceof RecipeListItemView) {
            view = (RecipeListItemView) convertView;
        } else {
            view = new RecipeListItemView(context);
        }

        view.setTitle(item.getTitle());
        final Bitmap image = item.getImage();
        if (image != null) {
            view.setImage(new BitmapDrawable(context.getResources(), image));
        }
        else {
            view.setImage(null);
        }
        // Set up listener for removal
        view.setRemoveListener(new RecipeListItemView.OnRecipeRemoveListener() {
            @Override
            public void recipeRemoveRequested() {
                recipes.remove(item);
            }
        });

        // Set up listener for recipe viewing
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start an activity to view the recipe
                final Intent intent = new Intent(context, EditRecipeActivity.class);
                intent.putExtra(EditRecipeActivity.EXTRA_RECIPE, item);
                context.startActivity(intent);
            }
        });

        return view;
    }

}
