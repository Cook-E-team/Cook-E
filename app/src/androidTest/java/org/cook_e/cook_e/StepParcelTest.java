package org.cook_e.cook_e;

import android.os.Parcel;

import org.cook_e.data.Ingredient;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests parceling and unparceling of Steps
 */
public class StepParcelTest {

	@Test
	public void testNewArray() {
		final int size = 32;
		final Step[] array = Step.CREATOR.newArray(size);
		assertNotNull(array);
		assertEquals("Incorrect array length", size, array.length);
	}

	@Test
	public void testParcelBasic() {
		final Parcel parcel = Parcel.obtain();
		final Step original = new Step(new ArrayList<Ingredient>(), "Something",
				"Do something with particular qualities", Duration.standardSeconds(30));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Step unparceled = Step.CREATOR.createFromParcel(parcel);
		assertNotNull(unparceled);
		assertEquals(original, unparceled);
	}

	@Test
	public void testParcelOneIngredient() throws ClassNotFoundException {
		final Parcel parcel = Parcel.obtain();
		final Ingredient scallops = new Ingredient("Scallops", 4, "");
		final Step original = new Step(Collections.singletonList(scallops), "Poach", "Gently poach the scallops", Duration.standardMinutes(3));

		original.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		final Step unparceled = Step.CREATOR.createFromParcel(parcel);

		assertNotNull(unparceled);
		assertEquals(original, unparceled);

		final Ingredient unparceledIngredient = unparceled.getIngredients().get(0);
		assertEquals(scallops, unparceledIngredient);
	}
}
