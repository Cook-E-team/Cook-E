package org.cook_e.cook_e.ui;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cook_e.data.Recipe;

/**
 * A list adapter that displays recipe titles
 */
public class RecipeListAdapter extends ListListAdapter<Recipe> {
    /**
     * Creates a new adapter
     *
     * @param context the context to use to create views
     * @param items   the items to display
     * @throws NullPointerException if items is null
     */
    public RecipeListAdapter(@NonNull Context context, @NonNull ObservableArrayList<? extends Recipe> items) {
        super(context, items);
    }

    @Override
    public View getViewForItem(Recipe item, Context context, View convertView, ViewGroup parent) {

        TextView textView;
        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        }
        else {
            textView = new TextView(context);
        }

        textView.setText(item.getTitle());

        return textView;
    }
}
