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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Class that represents a grouping of recipes
 * Stores a map of recipe titles to recipe file names
 * Modifiable
 * Bunches must have a name
 */
public class Bunch {
	private String title;
	private Map<String, String> recipe_refs;
	
	public Bunch(String title, Map<String, String> recipe_refs) {
		if (title == null || title.equals("")) throw new IllegalArgumentException("no name for this bunch");
		this.title = title;
		this.recipe_refs = recipe_refs;
	}

	public String getTitle() {
		return title;
	}
	/*
	 * Returns a List of all of the recipe titles in this bunch
	 */
	public List<String> getRecipeTitles() {
		return new ArrayList<String>(recipe_refs.keySet());
	}
	public Map<String, String> getRecipeRefs() {
		return Collections.unmodifiableMap(recipe_refs);
	}
	/*
	 * Changes the name of this bunch
	 * Cannot be changed to null or the empty string
	 */
	public void changeTitle(String title) {
		if (title != null && !title.equals("")) 
		this.title = title;
	}
	/*
	 * Adds a recipe to this bunch
	 * Takes a recipe title and filename for the recipe
	 * If either title or filename is null this method does nothing
	 */
	public void addRecipe(String recipe_name, String recipe_ref) {
		if (recipe_name != null && recipe_ref != null)recipe_refs.put(recipe_name, recipe_ref);
	}
	/*
	 * Removes a recipe from this bunch
	 * Does nothing if the recipe name given is not in this bunch
	 */
	public void removeRecipe(String recipe_name) {
		if (recipe_name != null) recipe_refs.remove(recipe_name);
	}
}