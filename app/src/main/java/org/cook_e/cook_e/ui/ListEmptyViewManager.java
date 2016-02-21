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
