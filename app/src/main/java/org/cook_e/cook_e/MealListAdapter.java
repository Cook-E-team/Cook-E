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
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.Space;

import org.cook_e.cook_e.ui.RecipeListItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * A list adapter that displays a list of meals, with each meal shown in a {@link RecipeListItemView}
 *
 * A space is added at the bottom of the list for compatibility with floating action buttons.
 */
public class MealListAdapter implements ListAdapter {

	private static class TestRecipe {
		public String title;
		public Drawable image;
		public int count;

		public TestRecipe(String title, Drawable image, int count) {
			this.title = title;
			this.image = image;
			this.count = count;
		}
	}

	/**
	 * The context used to access resources
	 */
	private final Context mContext;

	/**
	 * The list of recipes, for testing only
	 */
	private final List<TestRecipe> testRecipes;

	public MealListAdapter(Context mContext) {
		this.mContext = mContext;

		// Add some test recipes
		testRecipes = new ArrayList<>();
		testRecipes.add(new TestRecipe("Lasagna", mContext.getResources().getDrawable(R.drawable.test_image_1), 1));
		testRecipes.add(new TestRecipe("Pie", mContext.getResources().getDrawable(R.drawable.test_image_1), 3));
		testRecipes.add(new TestRecipe("Opera cake", mContext.getResources().getDrawable(R.drawable.test_image_1), 1));
		testRecipes.add(new TestRecipe("Banana Bread", mContext.getResources().getDrawable(R.drawable.test_image_1), 1));
		testRecipes.add(new TestRecipe("Lemon cake", mContext.getResources().getDrawable(R.drawable.test_image_1), 1));
		testRecipes.add(new TestRecipe("Pesto", mContext.getResources().getDrawable(R.drawable.test_image_1), 1));
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public int getCount() {
		return testRecipes.size() + 1;
	}

	@Override
	public Object getItem(int position) {
		if (position < testRecipes.size()) {
			return testRecipes.get(position);
		}
		else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	// Deprecated warnings suppressed because of the use of Resources.getDrawable(int), which
	// was deprecated in API level 22. The replacement was not introduced until API level 21,
	// which is more recent than this application's minimum supported API level.
	@Override
	@SuppressWarnings("Deprecated")
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position < testRecipes.size()) {
			RecipeListItemView view;
			if (convertView instanceof RecipeListItemView) {
				view = (RecipeListItemView) convertView;
			} else {
				view = new RecipeListItemView(mContext);
			}

			final TestRecipe recipe = testRecipes.get(position);

			view.setTitle(recipe.title);
			view.setImage(recipe.image);
			view.setCount(recipe.count);

			return view;
		}
		else {
			// Space
			Space space;
			if (convertView instanceof Space) {
				space = (Space) convertView;
			}
			else {
				space = new Space(mContext);
			}
			space.setMinimumHeight(300);
			return space;
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (position < testRecipes.size()) {
			return 0;
		}
		else {
			return 1;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
