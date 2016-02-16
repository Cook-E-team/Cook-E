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

import org.atteo.evo.inflector.English;
import org.cook_e.data.Ingredient;
import org.junit.Test;
import org.cook_e.data.UnitTestSharedData;
import org.cook_e.data.UnitTestSharedData.WordPair;

import static org.junit.Assert.*;

/**
 *Unit Tests for the Ingredient class
 */
public class IngredientUnitTest {

	@Test
	public void testPluralizeCommonUnits() {
		for (WordPair pair : UnitTestSharedData.COMMON_UNITS) {
			final String singular = pair.word;
			final String expectedPlural = pair.plural;
			final String actualPlural = English.plural(singular);
			assertEquals("Plural of " + singular + " incorrect", expectedPlural, actualPlural);
		}
	}

	@Test
	public void testOneUnit() {
		for (String ingredientName : UnitTestSharedData.INGREDIENTS) {
			for (WordPair unitPair : UnitTestSharedData.COMMON_UNITS) {

				final Ingredient ingredient = new Ingredient(ingredientName, 1, unitPair.word);

				assertEquals(ingredientName, ingredient.getType());
				assertEquals(1, ingredient.getAmount(), 1E-6);
				assertEquals(unitPair.word, ingredient.getUnit());
				// Should not pluralize quantity 1
				assertEquals(unitPair.word, ingredient.getUnitPluralized());
			}
		}
	}
	@Test
	public void testThreeUnits() {
		for (String ingredientName : UnitTestSharedData.INGREDIENTS) {
			for (WordPair unitPair : UnitTestSharedData.COMMON_UNITS) {

				final Ingredient ingredient = new Ingredient(ingredientName, 3, unitPair.word);

				assertEquals(ingredientName, ingredient.getType());
				assertEquals(3, ingredient.getAmount(), 1E-6);
				assertEquals(unitPair.word, ingredient.getUnit());
				// Should pluralize quantity 3
				assertEquals(unitPair.plural, ingredient.getUnitPluralized());
			}
		}
	}
	@Test(expected = NullPointerException.class)
	public void testNonNullExceptionNoType() {
		final Ingredient ingredient = new Ingredient(null, 1, UnitTestSharedData.COMMON_UNITS[0].word);
	}
	@Test(expected = NullPointerException.class)
	public void testNonNullExceptionNoUnit() {
		final Ingredient ingredient = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 1, null);
	}
	@Test
	public void testEquals() {
		final Ingredient ing1 = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 1, UnitTestSharedData.COMMON_UNITS[0].word);
		final Ingredient ing1_match = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 1, UnitTestSharedData.COMMON_UNITS[0].word);
		final Ingredient ing2 = new Ingredient(UnitTestSharedData.INGREDIENTS[1], 1, UnitTestSharedData.COMMON_UNITS[0].word);
		final Ingredient ing3 = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 3, UnitTestSharedData.COMMON_UNITS[0].word);
		final Ingredient ing4 = new Ingredient(UnitTestSharedData.INGREDIENTS[0], 1, UnitTestSharedData.COMMON_UNITS[1].word);

		assertEquals(ing1, ing1_match);
		assertFalse(ing1.equals(ing2));
		assertFalse(ing1.equals(ing3));
		assertFalse(ing1.equals(ing4));
	}
}
