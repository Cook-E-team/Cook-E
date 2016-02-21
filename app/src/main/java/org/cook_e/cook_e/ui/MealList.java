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
import android.app.Fragment;
import android.content.Intent;
import android.databinding.ObservableArrayList;
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
import org.cook_e.cook_e.CreateMealActivity;
import org.cook_e.cook_e.R;
import org.cook_e.data.Bunch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * A fragment that displays a simple list of meals
 */
public class MealList extends Fragment {
    private static final String TAG = MealList.class.getSimpleName();

    /**
     * The meals to display
     */
    private ObservableArrayList<Bunch> mMeals;

    /**
     * The meals actually visible in the list
     * (may be a subset of {@link #mMeals} if the user has entered a search query
     */
    private ObservableArrayList<Bunch> mVisibleMeals;

    /**
     * The view used for searching
     */
    private SearchView mSearchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mMeals = new ObservableArrayList<>();
        mVisibleMeals = new ObservableArrayList<>();
        try {
            mMeals.addAll(App.getAccessor().loadAllBunches());
            mVisibleMeals.addAll(mMeals);
        } catch (SQLException e) {
            new AlertDialog.Builder(getActivity())
                    .setTitle("Failed to load Meals")
                    .setMessage(e.getLocalizedMessage())
                    .show();
            Log.e(TAG, "Failed to load Meals", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home_list, container, false);

        mSearchView = (SearchView) view.findViewById(R.id.search);
        mSearchView.setOnQueryTextListener(new SearchHandler());

        final ListView list = (ListView) view.findViewById(R.id.list_view);
        list.setAdapter(new MealListAdapter(getActivity(), mVisibleMeals));
        // Configure list for testing
        list.setTag(R.id.test_tag_meal_list, "Meal List");

        // Set up floating action button
        final FloatingActionButton floatingButton = (FloatingActionButton) view.findViewById(R.id.add_button);
        floatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open item add view
                final Intent intent = new Intent(getActivity(), CreateMealActivity.class);
                startActivity(intent);
            }
        });

        // Empty view, shown when list is empty
        final TextView emptyView = (TextView) view.findViewById(R.id.empty_list_view);
        emptyView.setText(R.string.no_meals);
        if (mVisibleMeals.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.INVISIBLE);
        }
        mVisibleMeals.addOnListChangedCallback(new ListEmptyViewManager(emptyView));

        return view;
    }



    /**
     * Updates the meals in this list from the database
     *
     * Has no effect if this fragment has not yet been created.
     */
    public void reloadMeals() {
        if (mMeals != null) {
            mMeals.clear();
            try {
                mMeals.addAll(App.getAccessor().loadAllBunches());
            } catch (SQLException e) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("Failed to load meals")
                        .setMessage(e.getLocalizedMessage())
                        .show();
                Log.e(TAG, "Failed to load meals", e);
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
            // Make all meals visible
            mVisibleMeals.clear();
            if (!newText.isEmpty()) {
                final List<Bunch> filteredMeals = new ArrayList<>();
                // Limit mVisibleMeals to the meals whose titles contain the query
                // Case insensitive
                final String lowerQuery = newText.toLowerCase(Locale.getDefault());

                for (Bunch meal : mMeals) {
                    final String lowerTitle = meal.getTitle().toLowerCase(Locale.getDefault());
                    if (lowerTitle.contains(lowerQuery)) {
                        filteredMeals.add(meal);
                    }
                }
                mVisibleMeals.addAll(filteredMeals);
            } else {
                // Empty query
                mVisibleMeals.addAll(mMeals);
            }

            return true;
        }
    }
}
