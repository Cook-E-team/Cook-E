package org.cook_e.cook_e;

import android.os.Parcel;

import org.cook_e.data.Ingredient;
import org.cook_e.data.Recipe;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests parceling and unparceling of Recipes
 */
public class RecipeParcelTest {

	@Test
	public void testNewArray() {
		final int size = 32;
		final Recipe[] array = Recipe.CREATOR.newArray(size);
		assertNotNull(array);
		assertEquals("Incorrect array length", size, array.length);
	}

	@Test
	public void testParcelBasic() {
		final Parcel parcel = Parcel.obtain();
		final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", new ArrayList<Step>());

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);
		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

	@Test
	public void testParcelOneStep() {
		final Parcel parcel = Parcel.obtain();
		final Ingredient scallops = new Ingredient("Scallops", 4, "");
		final Step step = new Step(Collections.singletonList(scallops), "Poach", "Gently poach the scallops", Duration.standardMinutes(3));
		final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", Collections.singletonList(step));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);

		final Ingredient unparceledIngredient = unparceled.getSteps().get(0).getIngredients().get(0);
		assertEquals(scallops, unparceledIngredient);
	}

	@Test
	public void testParcelTwoSteps() {
		final Parcel parcel = Parcel.obtain();
		final Ingredient scallops = new Ingredient("Scallops", 4, "");
		final Ingredient butter = new Ingredient("Butter", 1, "kilogram");
		final Step step1 = new Step(Collections.singletonList(scallops),
				"Poach", "Gently poach the scallops", Duration.standardMinutes(2));
		final Step step2 = new Step(Collections.singletonList(butter),
				"Melt", "Melt the butter", Duration.standardMinutes(10));
		final Recipe original = new Recipe("Recipe title", "Clamify Flumingaster", Arrays.asList(step1, step2));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Recipe unparceled = Recipe.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}
}
