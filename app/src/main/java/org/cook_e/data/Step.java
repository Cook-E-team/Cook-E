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

import java.util.List;

/*
 * Class representing a step in a recipe
 *
 * Every step has a description, action, list of ingredients and an estimated time the step takes to
 * perform.
 */
public class Step {
	private String description;
	private String action;
	private int time;
	private List<Ingredient> ings;
	
	public Step(String description, int time, List<Ingredient> ings, String act) {
		if (description == null || description.length() == 0) throw new IllegalArgumentException("description is empty");
		if (time < 0) throw new IllegalArgumentException("time is negative");
		if (ings == null || ings.size() == 0) throw new IllegalArgumentException("ingredients is empty");
		if (act == null || act.length() == 0) throw new IllegalArgumentException("action is empty");
		this.description = description;
		this.time = time;
		this.ings = ings;
		this.action = act;
	}
	public String getDescription() {
		return description;
	}
	public int getTime() {
		return time;
	}
	public List<Ingredient> getIngredients() {
		return ings;
	}
	public String getAction() {
		return action;
	}
}