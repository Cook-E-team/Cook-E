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

package org.cook_e.cook_e.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view that displays a small number and allows the user to increment or decrement it
 */
public class CompactNumberSelector extends LinearLayout {

	/**
	 * The value currently selected
	 */
	int mValue;

	/**
	 * The text view that displays the selected number
	 */
	TextView mNumberView;

	public CompactNumberSelector(Context context) {
		super(context);
		initCompactNumberSelector(context);
	}

	public CompactNumberSelector(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCompactNumberSelector(context);
	}

	public CompactNumberSelector(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initCompactNumberSelector(context);
	}

	private void initCompactNumberSelector(Context context) {
		mValue = 0;

		// Create text view to display number
		mNumberView = new TextView(context);
		mNumberView.setText(Integer.toString(mValue));

		addView(mNumberView);
	}
}
