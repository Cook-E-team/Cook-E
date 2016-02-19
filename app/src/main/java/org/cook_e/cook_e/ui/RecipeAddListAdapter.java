/*
 * Copyright 2016 the Cook-E development team
 *
 *  This file is part of Cook-E.
 *
 *  Cook-E is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Cook-E is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Cook-E.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.cook_e.cook_e.ui;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import org.cook_e.data.Recipe;

/**
 * A list adapter that displays recipes in {@link RecipeListItemView} views
 */
public class RecipeAddListAdapter extends ListListAdapter<Recipe> {

    /**
     * An interface for listeners to respond to requests from the user to add a recipe
     */
    public interface RecipeAddListener {
        /**
         * Called when the user asks that a recipe be added
         * @param recipe the recipe to add
         */
        void recipeAddRequested(Recipe recipe);
    }

    /**
     * The add listener, or null if none is present
     */
    @Nullable
    private RecipeAddListener mAddListener;

    /**
     * Creates a new adapter
     *
     * @param items the items to display
     * @throws NullPointerException if items is null
     */
    public RecipeAddListAdapter(@NonNull Context context, @NonNull ObservableArrayList<? extends Recipe> items) {
        super(context, items);
    }

    /**
     * Sets the listener to be notified when the user asks to add a recipe
     * @param addListener the listener, or null if none should be notified
     */
    public void setAddListener(@Nullable RecipeAddListener addListener) {
        mAddListener = addListener;
    }

    @Override
    public View getViewForItem(final Recipe item, Context context, View convertView, ViewGroup parent) {
        RecipeAddItemView view;
        if (convertView instanceof RecipeAddItemView) {
            view = (RecipeAddItemView) convertView;
        }
        else {
            view = new RecipeAddItemView(context);
        }

        view.setTitle(item.getTitle());

        view.setAddListener(new RecipeAddItemView.RecipeAddListener() {
            @Override
            public void recipeAddRequested() {
                if (mAddListener != null) {
                    mAddListener.recipeAddRequested(item);
                }
            }
        });

        return view;
    }
}
