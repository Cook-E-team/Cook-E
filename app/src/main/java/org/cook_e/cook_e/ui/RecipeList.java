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

import android.app.AlertDialog;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.cook_e.cook_e.CreateRecipe;
import org.cook_e.cook_e.R;
import org.cook_e.data.Recipe;
import org.cook_e.data.StorageAccessor;

import java.sql.SQLException;

/**
 * A fragment that displays a list of recipes
 */
public class RecipeList extends Fragment {
    private static final String TAG = RecipeList.class.getSimpleName();

    /**
     * The recipes to display
     */
    private ObservableArrayList<Recipe> mRecipes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final StorageAccessor accessor = new StorageAccessor(getActivity());
        try {
            mRecipes = new ObservableArrayList<>();
            mRecipes.addAll(accessor.loadAllRecipes());
            Log.v(TAG, "Read recipes from database: " + mRecipes);

        } catch (SQLException e) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Failed to load recipes")
                    .setMessage(e.getLocalizedMessage())
                    .show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_list, container, false);
        final ListView recipeList = (ListView) view.findViewById(R.id.list_view);
        recipeList.setAdapter(new RecipeListAdapter(getActivity(), mRecipes));

        // Set up floating action button
        final FloatingActionButton floatingButton = (FloatingActionButton) view.findViewById(R.id.add_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open recipe creation view
                final Intent intent = new Intent(getActivity(), CreateRecipe.class);
                startActivity(intent);
            }
        });

        return view;
    }

}
