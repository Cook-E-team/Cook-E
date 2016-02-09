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

import android.os.Parcel;

import org.cook_e.data.Bunch;
import org.cook_e.data.Ingredient;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests parceling and unparceling of Bunches
 */
public class BunchParcelTest {

	@Test
	public void testNewArray() {
		final int size = 32;
		final Bunch[] array = Bunch.CREATOR.newArray(size);
		assertNotNull(array);
		assertEquals("Incorrect array length", size, array.length);
	}

	@Test
	public void testParcelBasic() {
		final Parcel parcel = Parcel.obtain();
		final Bunch original = new Bunch("Meal 83", Collections.<Recipe>emptyList());

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Bunch unparceled = Bunch.CREATOR.createFromParcel(parcel);
		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

	@Test
	public void testParcelOneRecipe() {
		final Parcel parcel = Parcel.obtain();
		final Ingredient scallops = new Ingredient("Scallops", 4, "");
		final Step step = new Step(Collections.singletonList(scallops), "Poach", "Gently poach the scallops", Duration.standardMinutes(3));
		final Recipe recipe = new Recipe("Recipe title", "Clamify Flumingaster", Collections.singletonList(step));
		final Bunch original = new Bunch("Meal 83", Collections.singletonList(recipe));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Bunch unparceled = Bunch.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

	@Test
	public void testParcelTwoRecipes() {
		final Parcel parcel = Parcel.obtain();
		final Ingredient scallops = new Ingredient("Scallops", 4, "");
		final Ingredient butter = new Ingredient("Butter", 1, "kilogram");
		final Step step1 = new Step(Collections.singletonList(scallops),
				"Poach", "Gently poach the scallops", Duration.standardMinutes(2));
		final Step step2 = new Step(Collections.singletonList(butter),
				"Melt", "Melt the butter", Duration.standardMinutes(10));
		final Recipe recipe1 = new Recipe("Recipe title", "Clamify Flumingaster", Arrays.asList(step1, step2));
		final Recipe recipe2 = createRecipe2();
		final Bunch original = new Bunch("Some meal", Arrays.asList(recipe1, recipe2));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Bunch unparceled = Bunch.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

	private static Recipe createRecipe2() {
		final Ingredient whippedCream = new Ingredient("Whipped cream", 90, "milliliter");
		final Ingredient lemonJuice = new Ingredient("Lemon juice", 140, "milliliter");

		final List<Step> steps = new ArrayList<>();
		steps.add(new Step(Collections.singletonList(lemonJuice), "Slice", "Slice the lemon juice", Duration.standardMinutes(1)));
		steps.add(new Step(Collections.singletonList(whippedCream), "Saute", "Saute the whipped cream", Duration.standardMinutes(3)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Microwave", "Microwave the whipped cream", Duration.standardMinutes(2)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Heat", "Heat the whipped cream", Duration.standardMinutes(2)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Add", "Add the whipped cream", Duration.standardMinutes(1)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Stir", "Stir the lemon juice", Duration.standardMinutes(4)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Boil", "Bring the whipped cream to the boil", Duration.standardMinutes(10)));
		steps.add(new Step(Collections.<Ingredient>emptyList(), "Order", "Order out", Duration.standardMinutes(30)));

		return new Recipe("Battered whipped cream", "Random recipe generator", steps);
	}
}
