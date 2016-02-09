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

package org.cook_e.cook_e;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import org.cook_e.data.Recipe;
import org.cook_e.data.Step;

import java.util.ArrayList;
import java.util.Collections;

public class MealViewActivity extends AppCompatActivity {
	private static final String TAG = MealViewActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meal_view);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setUpActionBar();

		// Set up recipe list
		final ListView recipeList = (ListView) findViewById(R.id.recipe_list);
		recipeList.setAdapter(new MealListAdapter(this));

		// Set up floating action button
		final FloatingActionButton floatingButton = (FloatingActionButton) findViewById(R.id.add_button);
		floatingButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Open item add view
				final Intent intent = new Intent(MealViewActivity.this, MealRecipeAddActivity.class);
				intent.putExtra(MealRecipeAddActivity.EXTRA_RECIPES, new Recipe[] {
						new Recipe("Apple turnover", "Clamify Flumingaster", new ArrayList<Step>()),
						new Recipe("Maple walnut scone", "Scallopify Fragilistigaster", new ArrayList<Step>()),
				});
				startActivityForResult(intent, MealRecipeAddActivity.REQUEST_ADD_RECIPES);
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MealRecipeAddActivity.REQUEST_ADD_RECIPES && resultCode == RESULT_OK) {
			final Parcelable[] parcelables = data.getParcelableArrayExtra(MealRecipeAddActivity.EXTRA_RECIPES);
		}
	}

	private void setUpActionBar() {
		final ActionBar bar = getSupportActionBar();
		assert bar != null;
		bar.setTitle("Meal name");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.meal_view, menu);
		return true;
	}

	/**
	 * Saves the current menu
	 * @param outState the state to save to
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		// TODO
	}
}
