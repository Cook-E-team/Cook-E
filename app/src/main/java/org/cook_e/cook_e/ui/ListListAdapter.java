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
 */e org.cook_e.cook_e.ui;

import android.content.Context;
import android.database.DataSetObserver;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.databinding.ObservableList.OnListChangedCallback;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

import org.cook_e.data.Objects;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract ListAdapter that gets its items from a List
 *
 * @param <T> The type of item stored in the list
 */
public abstract class ListListAdapter<T> implements ListAdapter {

    /**
     * The list of items to display
     */
    // This must be a concrete type for the callbacks to work
    @NonNull
    private final ObservableArrayList<? extends T> mItems;

    /**
     * The context provided to subclasses
     */
    @NonNull
    private final Context mContext;

    /**
     * The observers that will be notified when the data change
     */
    @NonNull
    private final Set<DataSetObserver> mObservers;

    /**
     * Creates a new adapter
     * @param context the context to use to create views
     * @param items the items to display
     * @throws NullPointerException if items is null
     */
    public ListListAdapter(@NonNull Context context, @NonNull ObservableArrayList<? extends T> items) {
        Objects.requireNonNull(items, "items must not be null");
        Objects.requireNonNull(context, "context must not be null");
        mItems = items;
        mContext = context;
        mObservers = new HashSet<>();

        // Set up listeners
        final ObservableList.OnListChangedCallback<? extends ObservableList<? extends T>> callback = new OnListChangedCallback<ObservableList<? extends T>>() {
            @Override
            public void onChanged(ObservableList<? extends T> sender) {
                notifyObservers();
            }

            @Override
            public void onItemRangeChanged(ObservableList<? extends T> sender, int positionStart, int itemCount) {
                notifyObservers();
            }

            @Override
            public void onItemRangeInserted(ObservableList<? extends T> sender, int positionStart, int itemCount) {
                notifyObservers();
            }

            @Override
            public void onItemRangeMoved(ObservableList<? extends T> sender, int fromPosition, int toPosition, int itemCount) {
                notifyObservers();
            }

            @Override
            public void onItemRangeRemoved(ObservableList<? extends T> sender, int positionStart, int itemCount) {
                notifyObservers();
            }
        };
        mItems.addOnListChangedCallback(callback);
    }

    /**
     * Notifies all observers that data have changed
     */
    private void notifyObservers() {
        for (DataSetObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    /**
     * Returns a view to represent an item from the list
     * @param item the item to display
     * @param index the index of the item to display in the list
     * @param convertView an existing view to reuse, or null if none is available
     * @param parent the parent that this view will eventually be attached to   @return a view to represent the item
     */
    public abstract View getViewForItem(T item, int index, View convertView, ViewGroup parent, Context context);

    /**
     * Returns true to indicate that all items are enabled
     * @return true
     */
    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    /**
     * Returns true to indicate that all items are enabled
     * @param position the item position
     * @return true
     */
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mObservers.add(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mObservers.remove(observer);
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewForItem(mItems.get(position), position, convertView, parent, mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }
}
