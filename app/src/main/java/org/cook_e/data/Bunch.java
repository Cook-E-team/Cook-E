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

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class that represents a grouping of recipes
 *
 * Modifiable
 */
public final class Bunch implements Parcelable {

    /**
     * The title of this bunch
     */
    @NonNull
    private String mTitle;
    /**
     * The recipes that this bunch contains
     */
    @NonNull
    private List<Recipe> mRecipes;

    /**
     * Creates a new bunch with an empty title and no recipes
     */
    public Bunch() {
        mTitle = "";
        mRecipes = new ArrayList<>();
    }

    /**
     * Creates a new bunch
     * @param title the title
     * @param recipes the recipes to include
     * @throws IllegalArgumentException if title is empty
     * @throws NullPointerException if title or recipes is null
     */
    public Bunch(@NonNull String title, @NonNull List<Recipe> recipes) {
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(recipes, "recipes must not be null");
        if (title.isEmpty()) throw new IllegalArgumentException("title must not be empty");
        mTitle = title;
        mRecipes = new ArrayList<>(recipes);
    }

    /**
     * Returns the title of this bunch
     * @return the title
     */
    @NonNull
    public String getTitle() {
        return mTitle;
    }

    /**
     * Sets the title of this bunch
     * @throws IllegalArgumentException if title is empty
     * @throws NullPointerException if title or recipes is null
     */
    public void setTitle(@NonNull String title) {
        Objects.requireNonNull(title, "title must not be null");
        if (title.isEmpty()) throw new IllegalArgumentException("title must not be empty");
        mTitle = title;
    }

    /**
     * Adds a recipe to this bunch
     * @param recipe the recipe to add
     * @throws NullPointerException if recipe is null
     */
    public void addRecipe(@NonNull Recipe recipe) {
        Objects.requireNonNull(recipe, "recipe must not be null");
        // Deep-copy the recipe
        mRecipes.add(new Recipe(recipe));
    }

    /**
     * Returns the recipes in this bunch
     * @return the recipes
     */
    @NonNull
    public List<Recipe> getRecipes() {
        return new ArrayList<>(mRecipes);
    }

    /**
     * Returns number of recipes in this bunch
     * @return number of recipes
     */
    public int getNumOfRecipes() {
        return mRecipes.size();
    }

    /**
     * Sets the recipes in this bunch
     * @param recipes the recipes to set
     * @throws NullPointerException if recipes or any recipe it contains is null
     */
    public void setRecipes(@NonNull List<Recipe> recipes) {
        Objects.requireNonNull(recipes, "recipes must not be null");
        mRecipes = new ArrayList<>(recipes.size());
        // Deep copy each recipe
        for (Recipe recipe : recipes) {
            Objects.requireNonNull(recipe, "no recipe in recipes may be null");
            mRecipes.add(new Recipe(recipe));
        }
    }

    /**
     * Remove recipe r from this bunch, if present.
     * All recipes after it will be moved one step forward.
     * @param r recipe to remove
     * @return true if recipe r is present and removed, otherwise false
     */
    public boolean removeRecipe(@NonNull Recipe r) {
        Objects.requireNonNull(r, "recipe must not be null");
        return mRecipes.remove(r);
    }

    /**
     * Remove the ith recipe from this bunch.
     * All recipes after it will be moved one step forward.
     * Doesn't modify bunch if index is less than 0 or greater than max index,
     * @param i The index of recipe to remove, index starts from 0.
     * @return The removed recipe if succeeded, null if failed.
     */
    public Recipe removeRecipe(int i) {
        if (i >= 0 && i < mRecipes.size()) return mRecipes.remove(i);
        else return null;
    }

    /**
     * Remove all recipes in this bunch
     */
    public void clearRecipes() {
    	mRecipes.clear();;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bunch bunch = (Bunch) o;

        if (!mTitle.equals(bunch.mTitle)) return false;
        return mRecipes.equals(bunch.mRecipes);

    }

    @Override
    public int hashCode() {
        int result = mTitle.hashCode();
        result = 31 * result + mRecipes.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Bunch{" +
                "mTitle='" + mTitle + '\'' +
                ", mRecipes=" + mRecipes +
                '}';
    }

    // Parceling section

    public static final Parcelable.Creator<Bunch> CREATOR = new Parcelable.Creator<Bunch>() {

        @Override
        public Bunch createFromParcel(Parcel source) {
            final String title = source.readString();
            final Recipe[] recipes = Objects.castArray(
                    source.readParcelableArray(Recipe.class.getClassLoader()), Recipe[].class);
            return new Bunch(title, Arrays.asList(recipes));
        }

        @Override
        public Bunch[] newArray(int size) {
            return new Bunch[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mTitle);
        dest.writeParcelableArray(mRecipes.toArray(new Recipe[mRecipes.size()]), flags);
    }
}
