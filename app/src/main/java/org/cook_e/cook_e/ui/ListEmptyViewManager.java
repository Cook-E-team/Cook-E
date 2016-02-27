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

import android.databinding.ObservableList;
import android.view.View;

/**
 * A user interface utility class that shows a view when a list is empty and hides it when
 * a list is not empty
 *
 * This class can be used with a {@link android.widget.ListView} to show an empty view when
 * the list has no content.
 *
 * An instance of this class can be added to an {@link ObservableList} with the
 * {@link ObservableList#addOnListChangedCallback(ObservableList.OnListChangedCallback)} method.
 */
public class ListEmptyViewManager extends ObservableList.OnListChangedCallback<ObservableList<?>> {

    /**
     * The view to display when the list is empty
     */
    private final View mEmptyView;

    /**
     * The view to display when an operation is in progress
     */
    private final View mProgressView;

    /**
     * If an operation is in progress
     */
    private boolean mInProgress;

    /**
     * If the list is empty
     */
    private boolean mListEmpty;

    /**
     * Creates a ListEmptyViewManager
     * @param emptyView the view to show when the list is empty
     * @param progressView the view to display when an operation is in progress
     */
    public ListEmptyViewManager(View emptyView, View progressView) {
        mEmptyView = emptyView;
        mProgressView = progressView;
        mInProgress = false;
        mListEmpty = true;
    }

    /**
     * Indicates that an operation is in progress or not.
     *
     * While an operation is in progress, the in-progress view will be shown if the list is empty.
     * Otherwise, the empty view will be shown.
     *
     * @param inProgress if an operation is in progress
     */
    public void setInProgress(boolean inProgress) {
        mInProgress = inProgress;
        updateVisibility();
    }

    private void updateList(ObservableList<?> list) {
        mListEmpty = list.isEmpty();
        updateVisibility();
    }

    private void updateVisibility() {
        if (mListEmpty) {
            if (mInProgress) {
                mProgressView.setVisibility(View.VISIBLE);
                mEmptyView.setVisibility(View.INVISIBLE);
            } else {
                mProgressView.setVisibility(View.INVISIBLE);
                mEmptyView.setVisibility(View.VISIBLE);
            }
        } else {
            mEmptyView.setVisibility(View.INVISIBLE);
            mProgressView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onChanged(ObservableList<?> sender) {
        updateList(sender);
    }

    @Override
    public void onItemRangeChanged(ObservableList<?> sender, int positionStart, int itemCount) {
        updateList(sender);
    }

    @Override
    public void onItemRangeInserted(ObservableList<?> sender, int positionStart, int itemCount) {
        updateList(sender);
    }

    @Override
    public void onItemRangeMoved(ObservableList<?> sender, int fromPosition, int toPosition, int itemCount) {
        updateList(sender);
    }

    @Override
    public void onItemRangeRemoved(ObservableList<?> sender, int positionStart, int itemCount) {
        updateList(sender);
    }
}
