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

/*
 * Class representing an ingredient 
 * Has a type, amount, and units for the amount
 */
public class Ingredient {
	private String type;
	private int amount;
	private String unit;
	
	public Ingredient(String type, int amount, String unit) {
		if (type == null || type.length() == 0) throw new IllegalArgumentException("ingredient type is empty");
		if (amount <= 0) throw new IllegalArgumentException("amount is <= 0");
		if (unit == null || unit.length() == 0) throw new IllegalArgumentException("unit is empty");

		this.type = type;
		this.amount = amount;
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public String getUnit() {
		return unit;
	}

}