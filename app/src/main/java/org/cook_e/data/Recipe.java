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

package org.cook_e.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Represents a recipe
 *
 * Has an ordered list of steps, a title, and an author.
 * No field may be null.
 */
public final class Recipe implements Parcelable {

	/**
	 * The steps that this recipe contains
	 */
	@NonNull
	private List<Step> mSteps;
	/**
	 * The title of this recipe
	 */
	@NonNull
	private String mTitle;
	/**
	 * The author of this recipe
	 */
	@NonNull
	private String mAuthor;
	/**
	 * Constructor
	 *@param title the title of the recipe, must not be null
	 *@param author the author of the recipe, must not be null
	 *@param steps the list of steps for the recipe, must not be null
	 *@throws NullPointerException if any of the parameters passed to it are null
	 */
	public Recipe(@NonNull String title, @NonNull String author, @NonNull List<Step> steps) {
		Objects.requireNonNull(title, "title must not be null");
		Objects.requireNonNull(author, "author must not be null");
		Objects.requireNonNull(steps, "steps must not be null");

		mSteps = new ArrayList<>(steps);
		mTitle = title;
		mAuthor = author;
	}

	/**
	 * Creates a deep copy of another recipe. No part of the new recipe will be modifiable from the
	 * old one.
	 * @param other the recipe to copy from
	 */
	public Recipe(Recipe other) {
		// Ingredient, Step, and String are immutable, so they do not need to be copied.
		// The delegated constructor copies the list of steps.
		this(other.getTitle(), other.getAuthor(), other.getSteps());
	}


	/**
	 * Returns the steps in this recipe
	 * @return the steps
	 */
	@NonNull
	public List<Step> getSteps() {
		return new ArrayList<>(mSteps);
	}

	/**
	 * Sets the steps in this recipe
	 * @param steps the steps to set
	 * @throws NullPointerException if steps is null
	 */
	public void setSteps(@NonNull List<Step> steps) {
		Objects.requireNonNull(steps, "steps must not be null");
		mSteps = new ArrayList<>(steps);
	}

	/**
	 * Set the ith step in this recipe
	 * The original step will be replaced by the new one
	 * Do nothing if index is less than 0 or greeter than the max index
	 * @param step the step to set
	 * @param i the target index of new step, index start from 0
	 * @throws NullPointerException if step is null
	 */
	public void setStep(@NonNull Step step, int i) {
		Objects.requireNonNull(step, "step must not be null");
		if (i >= 0 && i < mSteps.size()) {
			mSteps.set(i, step);
		}
	}

	/**
	 * Add step to end of the list of steps
	 * @param step the step to add
	 * @throws NullPointerException if step is null
	 */
	public void addStep(@NonNull Step step) {
		Objects.requireNonNull(step, "step must not be null");
		mSteps.add(step);
	}

	/**
	 * Add step to the ith place of the list of steps,
	 * all steps after it will be moved one step backward.
	 * If index is less than 0, add it to the first of the list.
	 * If index is greater than max index, add it to the last of the list.
	 * @param step the step to add
	 * @param i the target index to add step, index starts from 0
	 * @throws NullPointerException if step is null
	 */
	public void addStep(@NonNull Step step, int i) {
		Objects.requireNonNull(step, "step must not be null");
		if (i < 0) {
			mSteps.add(0, step);
		} else if (i >= mSteps.size()) {
			mSteps.add(step);
		} else {
			mSteps.add(i, step);
		}
	}

	/**
	 * Remove the ith step in this recipe.
	 * All steps after it will be moved one step forward.
	 * Do nothing if index is less than 0 or greater than max index.
	 * @param i the index of step to remove, index starts from 0.
	 */
	public void removeStep(int i) {
		if (i >= 0 && i < mSteps.size()) mSteps.remove(i);
	}

	/**
	 * Remove all steps in this recipe.
	 */
	public void clearSteps() {
		mSteps.clear();
	}

	/**
	 * Returns a List of all the ingredients required by all the steps of the recipe
	 */
	@NonNull
	public List<String> getIngredients() {
		List<String> ings = new ArrayList<>();
		for (Step s: mSteps) {
			for (String ingredient: s.getIngredients()) {
				ings.add(ingredient);
			}
		}
		return ings;
	}
	/**
	 * Returns the total estimated time of all the recipe's mSteps
	 */
	@NonNull
	public Duration getTotalTime() {
		Duration time = Duration.ZERO;
		for (Step s: mSteps) {
			time = time.withDurationAdded(s.getTime(), 1);
		}
		return time;
	}
	@NonNull
	public String getTitle() {
		return mTitle;
	}
	@NonNull
	public String getAuthor() {
		return mAuthor;
	}

	public void setTitle(String title) {
		mTitle = title;
	}

	public void setAuthor(String author) {
		mAuthor = author;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Recipe recipe = (Recipe) o;

		return mAuthor.equals(recipe.mAuthor) && mSteps.equals(recipe.mSteps) && mTitle.equals(recipe.mTitle);
	}

	@Override
	public int hashCode() {
		int result = mSteps.hashCode();
		result = 31 + mTitle.hashCode();
		result = 31 * result + mAuthor.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Recipe{" +
				"mSteps=" + mSteps +
				", mTitle='" + mTitle + '\'' +
				", mAuthor='" + mAuthor + '\'' +
				'}';
	}

	// Parceling section
	public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {

		@Override
		public Recipe createFromParcel(Parcel source) {
			final Step[] steps = Objects.castArray(
					source.readParcelableArray(Step.class.getClassLoader()), Step[].class);
			final String title = source.readString();
			final String author = source.readString();
			return new Recipe(title, author, Arrays.asList(steps));
		}

		@Override
		public Recipe[] newArray(int size) {
			return new Recipe[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeParcelableArray(mSteps.toArray(new Step[mSteps.size()]), flags);
		dest.writeString(mTitle);
		dest.writeString(mAuthor);
	}

}