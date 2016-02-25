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
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cook_e.cook_e.R;

/**
 * A view group that displays a drawable image and a title
 */
public class RecipeListItemView extends LinearLayout {

    /**
     * An interface for listeners that can handle requests from the user to remove a recipe from
     * a meal
     */
    public interface OnRecipeRemoveListener {
        /**
         * Called when the user asks that the recipe that this view represents be removed
         */
        void recipeRemoveRequested();
    }

    /**
     * Padding for selected elements, in some pixel-like units
     */
    private static final int PADDING = 20;

    /**
     * Maximum width and height of the image, in some pixel-like units
     */
    private static final int IMAGE_DIMENSION = 150;

    /**
     * The text view that displays the title
     */
    private final TextView mTitleView;

    /**
     * The recipe removal listener, or null if none is associated
     */
    @Nullable
    private OnRecipeRemoveListener mRemoveListener;

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

        // Create title view
        mTitleView = new TextView(context);
        mTitleView.setPadding(PADDING, PADDING, PADDING, PADDING);
        mTitleView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        mTitleView.setCompoundDrawablePadding(PADDING);
        mTitleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);

        // Create delete button
        final ImageButton deleteButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
        deleteButton.setImageResource(R.drawable.ic_remove_circle_black_24dp);
        deleteButton.setMinimumWidth(1);
        deleteButton.setMaxWidth(10);
        deleteButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mRemoveListener != null) {
                    mRemoveListener.recipeRemoveRequested();
                }
            }
        });

        final LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, 0.4f);
        titleParams.gravity = Gravity.CENTER;
        addView(mTitleView, titleParams);

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
        final Drawable[] drawables = mTitleView.getCompoundDrawables();
        return drawables[0];
    }

    /**
     * Sets the image to display
     * @param image an image to display, or null to display no image
     */
    public void setImage(@Nullable Drawable image) {
        if (image != null) {
            // Call mutate() so that changing the bounds will not affect other users of the same
            // Drawable
            image = image.mutate();
            image.setBounds(0, 0, IMAGE_DIMENSION, IMAGE_DIMENSION);
        }
        mTitleView.setCompoundDrawables(image, null, null, null);
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
     * Sets the recipe removal listener. The listener will be notified when the user asks to
     * remove this recipe. If a removal listener is already present, it will be replaced.
     * @param removeListener the listener, or null to set no listener
     */
    public void setRemoveListener(@Nullable OnRecipeRemoveListener removeListener) {
        mRemoveListener = removeListener;
    }
}
