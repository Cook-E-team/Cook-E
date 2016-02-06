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
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cook_e.cook_e.R;

/**
 * A view that displays a small number and allows the user to increment or decrement it.
 *
 * The number displayed is always greater than or equal to 0 and less than or equal to
 * Integer.MAX_VALUE.
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
	private ImageButton mUpButton;
	private ImageButton mDownButton;

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

		setOrientation(VERTICAL);

		// Create up button
		mUpButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
		mUpButton.setImageResource(R.drawable.ic_arrow_drop_up_black_18dp);
		mUpButton.setMinimumWidth(1);
		mUpButton.setMaxWidth(10);

		mUpButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mValue++;
				updateTextView();
			}
		});


		// Create down button
		mDownButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
		mDownButton.setImageResource(R.drawable.ic_arrow_drop_down_black_18dp);
		mDownButton.setMinimumWidth(1);
		mDownButton.setMaxWidth(10);

		mDownButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mValue--;
				updateTextView();
			}
		});

		// Create text view to display number
		mNumberView = new TextView(context);
		mNumberView.setGravity(Gravity.CENTER);
		updateTextView();


		addView(mUpButton);
		addView(mNumberView);
		addView(mDownButton);
	}

	/**
	 * Returns the current value
	 * @return the value
	 */
	public int getValue() {
		return mValue;
	}

	/**
	 * Sets the value to display
	 * @param newValue the value
	 * @throws IllegalArgumentException if value < 0
	 */
	public void setValue(int newValue) {
		if (newValue < 0) {
			throw new IllegalArgumentException("The value of a CompactNumberSelector must not be negative");
		}
		mValue = newValue;
		updateTextView();
	}

	public void updateTextView() {
		mNumberView.setText(String.format("%d", mValue));
		if (mValue == 0) {
			mDownButton.setEnabled(false);
		}
		else {
			mDownButton.setEnabled(true);
		}
		if (mValue == Integer.MAX_VALUE) {
			mUpButton.setEnabled(false);
		}
		else {
			mUpButton.setEnabled(true);
		}
	}
}
