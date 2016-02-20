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

package org.cook_e.cook_e;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.cook_e.data.Step;

/**
 * Created by Tyler on 2/6/2016.
 *
 * This class is an adapter for viewing and editing a recipes steps.
 */
public class StepListAdapter extends BaseAdapter {
    private static final String TAG = StepListAdapter.class.getSimpleName();

    private final Context mContext;
    private final ObservableArrayList<? extends Step> testSteps;
    private int selectedStepIndex;


    public StepListAdapter(Context mContext, ObservableArrayList<? extends Step> steps) {
        this.mContext = mContext;
        testSteps = steps;
        selectedStepIndex = -1;
    }

    /*
     * Returns the object associated with the given position in
     * the data set. Returns null if there isn't one.
     */
    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return testSteps.get(position);
        } else {
            return null;
        }
    }

    /*
     * Returns the row id of the item at the given position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * Returns true if the items id's won't change and false
     * if the items may change.
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Step step = (Step) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (getItemViewType(position) == 0) {
            View editRecipeView = layoutInflater.inflate(R.layout.edit_recipe_step, parent, false);

            ((TextView) editRecipeView.findViewById(R.id.stepTitle)).setText(
                    String.format(mContext.getString(R.string.step_list_title), position + 1));
            ((TextView) editRecipeView.findViewById(R.id.stepDescription)).setText(step.getDescription());
            ((TextView) editRecipeView.findViewById(R.id.stepIngredient)).setText(step.getIngredients().toString());
            editRecipeView.findViewById(R.id.stepDelete).setOnClickListener(
                    new DeleteStepOnClickListener(this, position));

            return editRecipeView;
        } else {
            View viewRecipeView = layoutInflater.inflate(R.layout.view_recipe_step, parent, false);

            ((TextView) viewRecipeView.findViewById(R.id.stepTitle)).setText(
                    String.format(mContext.getString(R.string.step_list_title), position + 1));
            ((TextView) viewRecipeView.findViewById(R.id.stepDescription)).setText(step.getDescription());

            viewRecipeView.setOnClickListener(new EditStepOnClickListener(this, position));
            return viewRecipeView;
        }
    }

    /*
     * Returns a particular number for each type of view created
     * by getView.
     */
    @Override
    public int getItemViewType(int position) {
        if (position == selectedStepIndex) {
            return 0;
        } else {
            return 1;
        }
    }

    /*
     * Returns the number of types of views created by getView.
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /*
     * Returns the number of items in the data set represented
     * by this adapter.
     */
    @Override
    public int getCount() {
        return testSteps.size();
    }

    /*
     * Returns true if the data backing the adapter is empty.
     */
    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }


    /* ********* Additional Functions **********/


    /* ********* Private Helper Classes **********/

    /**
     * A custom OnClickListener that switches the selected step in
     * the given adapter to the given position when clicked.
     */
    private class EditStepOnClickListener implements View.OnClickListener {
        private StepListAdapter adapter;
        private int position;

        public EditStepOnClickListener(StepListAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            selectedStepIndex = position;
            adapter.notifyDataSetChanged();
        }
    }

    /*
     * A custom OnClickListener that deletes the step in the
     * given adapter at the given position when clicked.
     */
    private class DeleteStepOnClickListener implements View.OnClickListener {
        private StepListAdapter adapter;
        private int position;

        public DeleteStepOnClickListener(StepListAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            selectedStepIndex = -1;
            testSteps.remove(position);
            adapter.notifyDataSetChanged();
        }
    }
}
