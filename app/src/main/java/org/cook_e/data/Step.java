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
import org.joda.time.ReadableDuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Class representing a step in a recipe
 *
 * Every step has a human-readable description, an action category, list of ingredients, and an
 * estimated time the step takes to perform. None of these values may be null.
 *
 * Objects of this class are immutable.
 */
public final class Step implements Parcelable {
	/**
	 * A human-readable mDescription of the actions involved in this step
	 */
	@NonNull
	private final String mDescription;
	/**
	 * The mAction category of this step
	 */
	@NonNull
	private final String mAction;
	/**
	 * The expected time required to complete this step
	 */
	@NonNull
	private final Duration mTime;
	/**
	 * The ingredients required for this step
	 */
	@NonNull
	private final List<Ingredient> mIngredients;

	/**
	 * Creates a Step
	 * @param ingredients the ingredients required for this step
	 * @param action the action category of this step
	 * @param description a human-readable description of this step
	 * @param duration an estimate of the time required to complete this step
	 * @throws NullPointerException if any parameter is null
	 */
	public Step(@NonNull List<Ingredient> ingredients, @NonNull String action, @NonNull String description, @NonNull ReadableDuration duration) {
		Objects.requireNonNull(ingredients, "ingredients must not be null");
		Objects.requireNonNull(action, "action must not be null");
		Objects.requireNonNull(description, "description must not be null");
		Objects.requireNonNull(duration, "duration must not be null");

		mDescription = description;
		mAction = action;
		mTime = duration.toDuration();
		mIngredients = new ArrayList<>(ingredients);
	}

	/**
	 * Returns the description of this step
	 * @return the description
	 */
	@NonNull
	public String getDescription() {
		return mDescription;
	}

	/**
	 * Returns the duration of this step
	 * @return the duration
	 */
	@NonNull
	public ReadableDuration getTime() {
		return mTime;
	}

	/**
	 * Returns the ingredients that this step requires
	 * @return the ingredients
	 */
	@NonNull
	public List<Ingredient> getIngredients() {
		return new ArrayList<>(mIngredients);
	}

	/**
	 * Returns the action category of this step
	 * @return the action category
	 */
	@NonNull
	public String getAction() {
		return mAction;
	}

	// Parceling section

	public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>() {

		@Override
		public Step createFromParcel(Parcel source) {
			final String description = source.readString();
			final String action = source.readString();
			final Duration duration = (Duration) source.readSerializable();
			final Ingredient[] ingredients = (Ingredient[])
					source.readParcelableArray(ClassLoader.getSystemClassLoader());
			return new Step(Arrays.asList(ingredients), action, description, duration);
		}

		@Override
		public Step[] newArray(int size) {
			return new Step[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mDescription);
		dest.writeString(mAction);
		dest.writeSerializable(mTime);
		dest.writeParcelableArray(mIngredients.toArray(new Ingredient[mIngredients.size()]), flags);
	}
}