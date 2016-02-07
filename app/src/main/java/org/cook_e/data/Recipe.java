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
	 *
	 * Takes a list of mSteps, mTitle and an mAuthor.
	 * 
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
	 * Add step to end of the list of steps
	 * @param step the step to add
	 * @throws NullPointerException if step is null
	 */
	public void addStep(@NonNull Step step) {
		Objects.requireNonNull(step, "step must not be null");
		mSteps.add(step);
	}

	/*
	 * Returns a List of all the ingredients required by all the steps of the recipe
	 */
	@NonNull
	public List<Ingredient> getIngredients() {
		List<Ingredient> ings = new ArrayList<>();
		for (Step s: mSteps) {
			for (Ingredient ingredient: s.getIngredients()) {
				ings.add(ingredient);
			}
		}
		return ings;
	}
	/*
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

	// Parceling section
	public static final Parcelable.Creator<Recipe> CREATOR = new Parcelable.Creator<Recipe>() {

		@Override
		public Recipe createFromParcel(Parcel source) {
			final Step[] steps = (Step[]) source.readParcelableArray(ClassLoader.getSystemClassLoader());
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