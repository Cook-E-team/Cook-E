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
 */e org.cook_e.cook_e;

import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Tests the behavior of iterators of the {@link android.databinding.ObservableArrayList} class
 */
public class ObservableArrayListTest {

    /**
     * Checks whether the ObservableArrayList notifies its callbacks when an iterator is used
     * to remove items
     *
     * This test is temporarily disabled because of an Android bug.
     * A bug report has been filed: https://code.google.com/p/android/issues/detail?id=201481
     */
//    @Test
    public void testRemoveWithIterator() {
        final ObservableList<String> items = new ObservableArrayList<>();

        final ListChangeMock<String> mock = new ListChangeMock<>();
        items.addOnListChangedCallback(mock);

        // Add items to list
        items.addAll(Arrays.asList("Cake", "Pie", "Galette", "Pudding"));
        // Change listener reflects the added items
        assertEquals(items, mock.getCurrentList());

        // Remove all items from the list using an iterator
        for (Iterator<String> iter = items.iterator(); iter.hasNext(); ) {
            iter.next();
            iter.remove();
        }
        // List is now empty
        assertTrue(items.isEmpty());
        // Change listener should reflect this
        assertEquals(items, mock.getCurrentList());
    }

    /**
     * A list change callback that makes a copy of a list whenever it changes
     * @param <T> the element type
     */
    private class ListChangeMock<T> extends ObservableList.OnListChangedCallback<ObservableList<T>> {

        /**
         * The current list, based on the last callback
         */
        private List<T> mCurrentList = Collections.emptyList();

        /**
         * Returns the current list
         * @return the current list
         */
        public List<T> getCurrentList() {
            return mCurrentList;
        }

        @Override
        public void onChanged(ObservableList<T> sender) {
            mCurrentList = new ArrayList<>(sender);
        }

        @Override
        public void onItemRangeChanged(ObservableList<T> sender, int positionStart, int itemCount) {
            mCurrentList = new ArrayList<>(sender);
        }

        @Override
        public void onItemRangeInserted(ObservableList<T> sender, int positionStart, int itemCount) {
            mCurrentList = new ArrayList<>(sender);
        }

        @Override
        public void onItemRangeMoved(ObservableList<T> sender, int fromPosition, int toPosition, int itemCount) {
            mCurrentList = new ArrayList<>(sender);
        }

        @Override
        public void onItemRangeRemoved(ObservableList<T> sender, int positionStart, int itemCount) {
            mCurrentList = new ArrayList<>(sender);
        }
    }
}
