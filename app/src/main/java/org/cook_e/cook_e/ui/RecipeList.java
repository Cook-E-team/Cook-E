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

package org.cook_e.cook_e.ui;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import org.cook_e.cook_e.App;
import org.cook_e.cook_e.CreateRecipe;
import org.cook_e.cook_e.R;
import org.cook_e.data.Recipe;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that displays a list of recipes
 */
public class RecipeList extends Fragment {
    private static final String TAG = RecipeList.class.getSimpleName();

    /**
     * The recipes to display
     */
    private ObservableArrayList<Recipe> mRecipes;

    /**
     * The recipes visible in the list
     * (may be a subset of {@link #mRecipes} if the user has entered a search query
     */
    private ObservableArrayList<Recipe> mVisibleRecipes;
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mRecipes = new ObservableArrayList<>();
            mVisibleRecipes = new ObservableArrayList<>();
            mRecipes.addAll(App.getAccessor().loadAllRecipes(App.getDisplayLimit()));
            mVisibleRecipes.addAll(mRecipes);

        } catch (SQLException e) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Failed to load recipes")
                    .setMessage(e.getLocalizedMessage())
                    .show();
            Log.e(TAG, "Failed to load recipes", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_list, container, false);
        final ListView recipeList = (ListView) view.findViewById(R.id.list_view);
        recipeList.setAdapter(new RecipeListAdapter(getActivity(), mVisibleRecipes));
        // Configure list for testing
        recipeList.setTag(R.id.test_tag_recipe_list, "Recipe List");
        
        // Search
        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new SearchHandler());

        // Empty view, shown when list is empty
        final TextView emptyView = (TextView) view.findViewById(R.id.empty_list_view);
        emptyView.setText(R.string.no_recipes);
        if (mVisibleRecipes.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
        mVisibleRecipes.addOnListChangedCallback(new ListEmptyViewManager(emptyView));

        // Update mVisibleRecipes when mRecipes changes
        mRecipes.addOnListChangedCallback(new VisibleRecipeUpdater<Recipe>());

        return view;
    }


    /**
     * Called from the parent activity when an add button is pressed. Starts the process of creating
     * a new recipe.
     */
    public void onAddButtonPressed() {
        // Open recipe creation view
        final Intent intent = new Intent(getActivity(), CreateRecipe.class);
        startActivity(intent);
    }

    /**
     * Updates mVisibleRecipes with recipes from mRecipes based on the current search query
     */
    private void updateVisibleRecipes() {
        final String query = mSearchView.getQuery().toString();
        // Make all recipes visible
        mVisibleRecipes.clear();
        if (!query.isEmpty()) {
            final List<Recipe> filteredRecipes = new ArrayList<>();
            // Limit mVisibleRecipes to the meals whose titles contain the query
            // Case insensitive
            final String lowerQuery = query.toLowerCase(Locale.getDefault());

            for (Recipe recipe : mRecipes) {
                final String lowerTitle = recipe.getTitle().toLowerCase(Locale.getDefault());
                if (lowerTitle.contains(lowerQuery)) {
                    filteredRecipes.add(recipe);
                }
            }
            mVisibleRecipes.addAll(filteredRecipes);
        } else {
            // Empty query
            mVisibleRecipes.addAll(mRecipes);
        }
    }

    /**
     * Updates the recipes in this list from the database
     *
     * Has no effect if this fragment has not yet been created.
     */
    public void reloadRecipes() {
        if (mRecipes != null) {
            mRecipes.clear();
            try {
                mRecipes.addAll(App.getAccessor().loadAllRecipes(App.getDisplayLimit()));
            } catch (SQLException e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Failed to load recipes")
                        .setMessage(e.getLocalizedMessage())
                        .show();
                Log.e(TAG, "Failed to load recipes", e);
            }
        }
    }


    private class SearchHandler implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            // Do nothing more
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            updateVisibleRecipes();
            return true;
        }
    }

    /**
     * Calls {@link #updateVisibleRecipes()} when the associated list changes
     * @param <T> the value type
     */
    private class VisibleRecipeUpdater<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {

        @Override
        public void onChanged(ObservableList<T> sender) {
            updateVisibleRecipes();
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            updateVisibleRecipes();
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            updateVisibleRecipes();
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
            updateVisibleRecipes();
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            updateVisibleRecipes();
        }
    }
}
