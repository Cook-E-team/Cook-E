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

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.cook_e.cook_e.ui.ListListAdapter;
import org.cook_e.data.Step;

import java.util.List;

/**
 * Created by Tyler on 2/6/2016.
 *
 * This class is an adapter for viewing and editing a recipes steps.
 */
public class StepListAdapter extends ListListAdapter<Step> {

    /**
     * An interface for objects that can handle clicks on steps
     */
    public interface StepClickListener {
        /**
         * Called when the user clicks on a step
         * @param step the step the user clicked on
         * @param index the index in the list of the step
         */
        void onStepClicked(Step step, int index);
    }

    /**
     * The steps displayed in this list
     */
    private final List<? extends Step> mSteps;

    /**
     * The listener to notify when a step is clicked on
     */
    private StepClickListener mStepClickListener;

    /**
     * Creates a new adapter
     * @param context the context to use
     * @param steps the steps to display
     */
    public StepListAdapter(Context context, ObservableArrayList<? extends Step> steps) {
        super(context, steps);
        mSteps = steps;
    }

    @Override
    public View getViewForItem(final Step item, final int index, View convertView, ViewGroup parent, Context context) {

        View view;
        if (convertView != null) {
            view = convertView;
        }
        else {
            final LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.view_recipe_step_item, parent, false);
        }

        final TextView titleField = (TextView) view.findViewById(R.id.description_view);
        final ImageButton deleteButton = (ImageButton) view.findViewById(R.id.delete_button);

        titleField.setText(item.getDescription());
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSteps.remove(item);
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mStepClickListener != null) {
                    mStepClickListener.onStepClicked(item, index);
                }
            }
        });

        return view;
    }

    /**
     * Sets the step click listener
     * @param stepClickListener the listener to notify when the user clicks on a step,
     *                          or null to remove an existing listener
     */
    public void setStepClickListener(StepClickListener stepClickListener) {
        mStepClickListener = stepClickListener;
    }
}
