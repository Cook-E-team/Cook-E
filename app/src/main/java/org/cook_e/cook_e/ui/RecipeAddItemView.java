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

package org.cook_e.cook_e.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.cook_e.cook_e.R;
import org.cook_e.data.Objects;

/**
 * A view that shows a recipe and allows it to be added to something
 */
public class RecipeAddItemView extends LinearLayout {

    /**
     * An interface for listeners to respond to requests from the user to add a recipe
     */
    public interface RecipeAddListener {
        /**
         * Called when the user asks that a recipe be added
         */
        void recipeAddRequested();
    }

    /**
     * Padding in this view
     */
    private static final int PADDING = 15;

    /**
     * The text view that displays the title
     */
    private TextView mTitleView;
    /**
     * The button used to add the recipe
     */
    private ImageButton mAddButton;
    /**
     * The context used to get resources
     */
    private Context mContext;

    /**
     * The recipe add listener, or null if none is present
     */
    @Nullable
    private RecipeAddListener mAddListener;

    public RecipeAddItemView(Context context) {
        super(context);
        initRecipeAddItemView(context);
    }

    public RecipeAddItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initRecipeAddItemView(context);
    }

    public RecipeAddItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRecipeAddItemView(context);
    }

    private void initRecipeAddItemView(Context context) {
        setOrientation(HORIZONTAL);
        setPadding(PADDING, PADDING, PADDING, PADDING);
        mContext = context;

        mTitleView = new TextView(context, null, android.R.attr.textAppearanceLarge);
        mTitleView.setText(R.string.recipe);

        mAddButton = new ImageButton(context, null, android.R.attr.borderlessButtonStyle);
        mAddButton.setMinimumWidth(1);
        mAddButton.setMaxWidth(10);
        enableAddButton();

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                disableAddButton();
                if (mAddListener != null) {
                    mAddListener.recipeAddRequested();
                }
            }
        });

        final LayoutParams titleParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT, 0.9f);
        titleParams.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        addView(mTitleView, titleParams);
        addView(mAddButton,
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 0.1f));


        // Tapping the list entry adds the recipe, as if the add button were pressed
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mAddButton.performClick();
            }
        });
    }

    /**
     * Sets the recipe title to display
     *
     * If the new title is not equal to the current title, this method also resets the add button
     * to its default state.
     *
     * @param title the title
     * @throws NullPointerException if title is null
     */
    public void setTitle(@NonNull String title) {
        Objects.requireNonNull(title, "title must not be null");

        final String currentTitle = mTitleView.getText().toString();
        if (!currentTitle.equals(title)) {
            enableAddButton();
        }

        mTitleView.setText(title);
    }

    /**
     * Enables the add button and sets it to display an add icon
     */
    private void enableAddButton() {
        mAddButton.setImageDrawable(
                mContext.getResources().getDrawable(R.drawable.ic_add_box_black_24dp));
        mAddButton.setEnabled(true);
    }

    /**
     * Disables the add button and sets it to display a check mark icon
     */
    public void disableAddButton() {
        mAddButton.setImageDrawable(
                mContext.getResources().getDrawable(R.drawable.ic_check_box_black_24dp));
        mAddButton.setEnabled(false);
    }

    /**
     * Sets the listener to be notified when the user asks to add the displayed recipe
     *
     * @param addListener the listener, or null to set no listener
     */
    public void setAddListener(@Nullable RecipeAddListener addListener) {
        mAddListener = addListener;
    }
}
