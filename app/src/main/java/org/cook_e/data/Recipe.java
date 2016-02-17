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

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.joda.time.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
 * Represents a recipe
 *
 * Has an ordered list of steps, a title, an author, and an image. The image may be null; all
 * other fields may not be null.
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
     * The image associated with this recipe, or null if the recipe has no image
     */
    @Nullable
    private Bitmap mImage;

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
        mImage = null;
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

    /**
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

    /**
     * Returns the image associated with this recipe
     * @return an immutable image, or null if this recipe has no image
     */
    @Nullable
    public Bitmap getImage() {
        if (mImage != null) {
            return Bitmap.createBitmap(mImage);
        }
        else {
            return null;
        }
    }

    /**
     * Sets the image to associate with this recipe
     * @param image the image to set, or null to set no image
     */
    public void setImage(@Nullable Bitmap image) {
        if (image != null) {
            mImage = Bitmap.createBitmap(image);
        }
        else {
            mImage = null;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Recipe recipe = (Recipe) o;

        if (!mSteps.equals(recipe.mSteps)) return false;
        if (!mTitle.equals(recipe.mTitle)) return false;
        if (!mAuthor.equals(recipe.mAuthor)) return false;
        return !(mImage != null ? !mImage.sameAs(recipe.mImage) : recipe.mImage != null);

    }

    @Override
    public int hashCode() {
        int result = mSteps.hashCode();
        result = 31 * result + mTitle.hashCode();
        result = 31 * result + mAuthor.hashCode();
        result = 31 * result + (mImage != null ? mImage.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "mSteps=" + mSteps +
                ", mTitle='" + mTitle + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mImage=" + mImage +
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

            final byte hasImage = source.readByte();
            Bitmap image = null;
            if (hasImage == 1) {
                image = Bitmap.CREATOR.createFromParcel(source);
            }

            final Recipe recipe = new Recipe(title, author, Arrays.asList(steps));
            recipe.setImage(image);
            return recipe;
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
        // Write has image: 0 (false) or 1 (true)
        dest.writeByte(mImage != null ? (byte) 1 : (byte) 0);
        if (mImage != null) {
            mImage.writeToParcel(dest, 0);
        }
    }
}
