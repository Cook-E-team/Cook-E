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

import org.atteo.evo.inflector.English;

/*
 * Class representing an ingredient 
 * Has a mType, mAmount, and units for the mAmount
 *
 * The mAmount is positive. The mType and mUnit are not null. The mType is not empty. The mUnit may be
 * empty if the ingredient comes in discrete units (for example, eggs).
 *
 * Objects of this class are immutable.
 */
public final class Ingredient implements Parcelable {
	/**
	 * The name of this ingredient
	 */
	@NonNull
	private final String mType;
	/**
	 * The amount of units of this ingredient
	 */
	private final double mAmount;
	/**
	 * The unit of measurement used for the mAmount field
	 */
	@NonNull
	private final String mUnit;

	/**
	 * Creates a new ingredient
	 * @param type the name of the ingredient
	 * @param amount the mAmount of the ingredient to use
	 * @param unit the units of measure
	 */
	public Ingredient(@NonNull String type, double amount, @NonNull String unit) {
		if (type == null || type.isEmpty()) throw new IllegalArgumentException("ingredient type is empty");
		if (amount <= 0) throw new IllegalArgumentException("amount is <= 0");
		if (unit == null) throw new IllegalArgumentException("unit is null");

		this.mType = type;
		this.mAmount = amount;
		this.mUnit = unit;
	}

	/**
	 * Returns the type of this ingredient
	 * @return the type
	 */
	@NonNull
	public String getType() {
		return mType;
	}

	/**
	 * Returns the amount of this ingredient
	 * @return the amount
	 */
	public double getAmount() {
		return mAmount;
	}

	/**
	 * Returns the unit name of this ingredient
	 * @return the unit name
	 */
	@NonNull
	public String getUnit() {
		return mUnit;
	}

	/**
	 * Returns the unit name of this ingredient. If the quantity of this ingredient is less than or,
	 * equal to 1, the unit name will be in plural form.
	 * @return the unit name
	 */
	@NonNull
	public String getUnitPluralized() {
		if (mUnit.isEmpty()) {
			return mUnit;
		}

		if (mAmount <= 1) {
			return getUnit();
		}
		else {
			return English.plural(getUnit());
		}
	}

	// Parceling section

	public static final Parcelable.Creator<Ingredient> CREATOR = new Parcelable.Creator<Ingredient>() {

		@Override
		public Ingredient createFromParcel(Parcel source) {
			final String type = source.readString();
			final double amount = source.readDouble();
			final String unit = source.readString();
			return new Ingredient(type, amount, unit);
		}

		@Override
		public Ingredient[] newArray(int size) {
			return new Ingredient[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mType);
		dest.writeDouble(mAmount);
		dest.writeString(mUnit);
	}
}