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

import org.cook_e.data.Ingredient;
import org.junit.Test;

import static org.junit.Assert.*;

import android.os.Parcel;


/**
 * Tests parceling and unparceling of Ingredients
 */
public class IngredientParcelTest {

	@Test
	public void testNewArray() {
		final int size = 32;
		final Ingredient[] array = Ingredient.CREATOR.newArray(size);
		assertNotNull(array);
		assertEquals("Incorrect array length", size, array.length);
	}

	@Test
	public void testParcelUnparcelBasic() {
		final Parcel parcel = Parcel.obtain();
		final Ingredient original = new Ingredient("White truffle oil", 50, "milliliter");

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);

		final Ingredient unparceled = Ingredient.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

}
