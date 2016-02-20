package org.cook_e.cook_e.ui;

import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableArrayList;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cook_e.cook_e.MealViewActivity;
import org.cook_e.data.Bunch;

/**
 * A list adapter that displays meal titles
 */
public class MealListAdapter extends ListListAdapter<Bunch> {
    /**
     * Creates a new adapter
     *
     * @param context the context to use to create views
     * @param items   the items to display
     * @throws NullPointerException if items is null
     */
    public MealListAdapter(@NonNull Context context, @NonNull ObservableArrayList<? extends Bunch> items) {
        super(context, items);
    }

    @Override
    public View getViewForItem(final Bunch item, final Context context, View convertView, ViewGroup parent) {

        TextView textView;
        if (convertView instanceof TextView) {
            textView = (TextView) convertView;
        }
        else {
            textView = new TextView(context);
        }

        // TODO: Make a separate view class and clean up
        textView.setTextAppearance(context, android.R.style.TextAppearance_Large);
        textView.setPadding(30, 30, 30, 30);
        textView.setText(item.getTitle());

        // TODO: Move the activity starting code into an activity, so that this list adapter does
        // not have to do it
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent intent = new Intent(context, MealViewActivity.class);
                intent.putExtra(MealViewActivity.EXTRA_MEAL, item);
                context.startActivity(intent);
            }
        });

        return textView;
    }
}
