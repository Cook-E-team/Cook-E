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
import android.util.Log;
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
    private final View mView;

    /**
     * Creates a ListEmptyViewManager
     * @param view the view to show or hide
     */
    public ListEmptyViewManager(View view) {
        mView = view;
    }

    private void checkVisibility(ObservableList<?> list) {
        if (list.isEmpty()) {
            mView.setVisibility(View.VISIBLE);
        }
        else {
            mView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onChanged(ObservableList<?> sender) {
        checkVisibility(sender);
    }

    @Override
    public void onItemRangeChanged(ObservableList<?> sender, int positionStart, int itemCount) {
        checkVisibility(sender);
    }

    @Override
    public void onItemRangeInserted(ObservableList<?> sender, int positionStart, int itemCount) {
        checkVisibility(sender);
    }

    @Override
    public void onItemRangeMoved(ObservableList<?> sender, int fromPosition, int toPosition, int itemCount) {
        checkVisibility(sender);
    }

    @Override
    public void onItemRangeRemoved(ObservableList<?> sender, int positionStart, int itemCount) {
        checkVisibility(sender);
    }
}
