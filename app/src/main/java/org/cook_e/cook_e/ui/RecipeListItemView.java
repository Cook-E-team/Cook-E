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
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import org.cook_e.cook_e.R;

/**
 * A view group that displays a drawable image, and a title
 */
public class RecipeListItemView extends LinearLayout {

	/**
	 * Padding for selected elements, in some pixel-like units
	 */
	private static final int PADDING = 20;

	/**
	 * Maximum width and height of the image, in some pixel-like units
	 */
	private static final int MAX_IMAGE_DIMENSION = 300;

	/**
	 * The view that displays the image
	 */
	final ImageView mImageView;
	/**
	 * The text view that displays the title
	 */
	final TextView mTitleView;
	/**
	 * The number picker used to select the number of recipe quantities to prepare
	 */
	final CompactNumberSelector mNumberPicker;

	/**
	 * Creates a new view
	 * @param context the context to create the view in
	 */
	@SuppressWarnings("Deprecated")
	public RecipeListItemView(Context context) {
		super(context);

		// Set up layout
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(PADDING, PADDING, PADDING, PADDING);

		// Create image view
		mImageView = new ImageView(context);
		mImageView.setScaleType(ScaleType.FIT_CENTER);
		mImageView.setAdjustViewBounds(true);
		mImageView.setMaxHeight(MAX_IMAGE_DIMENSION);
		mImageView.setMaxWidth(MAX_IMAGE_DIMENSION);

		// Create title view
		mTitleView = new TextView(context);
		mTitleView.setPadding(PADDING, PADDING, PADDING, PADDING);
		mTitleView.setTextAppearance(context, android.R.style.TextAppearance_Large);

		// Create quantity selector
		mNumberPicker = new CompactNumberSelector(context);

		// Create delete button
		final ImageButton deleteButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
		deleteButton.setImageResource(R.drawable.ic_remove_circle_black_24dp);
		deleteButton.setMinimumWidth(1);
		deleteButton.setMaxWidth(10);


		final LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.2f);
		imageParams.gravity = Gravity.CENTER;
		addView(mImageView, imageParams);
		final LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.4f);
		titleParams.gravity = Gravity.CENTER;
		addView(mTitleView, titleParams);
		final LayoutParams numberPickerParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		numberPickerParams.gravity = Gravity.CENTER;
		addView(mNumberPicker, numberPickerParams);
		final LayoutParams deleteButtonParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		deleteButtonParams.gravity = Gravity.CENTER;
		addView(deleteButton, deleteButtonParams);
	}

	/**
	 * Returns the image being displayed, which may be null
	 * @return the current image
	 */
	@Nullable
	public Drawable getImage() {
		return mImageView.getDrawable();
	}

	/**
	 * Sets the image to display
	 * @param image an image to display, or null to display no image
	 */
	public void setImage(@Nullable Drawable image) {
		mImageView.setImageDrawable(image);
	}

	/**
	 * Returns the title displayed in this view
	 * @return the title
	 */
	@NonNull
	public String getTitle() {
		return mTitleView.getText().toString();
	}

	/**
	 * Sets the title to display
	 * @param title the title. Must not be null.
	 */
	public void setTitle(@NonNull String title) {
		mTitleView.setText(title);
	}

	/**
	 * Returns the displayed count
	 * @return the count
	 */
	public int getCount() {
		return mNumberPicker.getValue();
	}

	/**
	 * Sets the count to display
	 * @param count the count
	 * @throws IllegalArgumentException if count < 0
	 */
	public void setCount(int count) {
		mNumberPicker.setValue(count);
	}
}
