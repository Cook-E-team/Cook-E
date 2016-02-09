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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 2/6/2016.
 *
 * This class is an adapter for viewing and editing a recipes steps.
 */
public class StepListAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<TestStep> testSteps;
    private int selectedStepIndex;


    public StepListAdapter(Context mContext) {
        this.mContext = mContext;
        testSteps = new ArrayList<TestStep>();
        selectedStepIndex = -1;

        // Temporary code to populate the steps with data.
        testSteps.add(new TestStep("Cut", "Carrots", "1 Cup", "Cut one cup of carrots and set aside.", 60));
        testSteps.add(new TestStep("Boil", "Potatoes", "", "Boil a potato until soft.", 600));
        testSteps.add(new TestStep("action", "ingredient", "amount", "This is a really long description " +
                "so that we can see how the application behaves. If the description is displayed here," +
                "then it may be worth considered limiting the length of it so that it doesn't get out of hand.", 600));
        testSteps.add(new TestStep("action", "ingredient", "amount", "This is a really long description " +
                "so that we can see how the application behaves. If the description is displayed here," +
                "then it may be worth considered limiting the length of it so that it doesn't get out of hand.", 600));
        testSteps.add(new TestStep("action", "ingredient", "amount", "This is a really long description " +
                "so that we can see how the application behaves. If the description is displayed here," +
                "then it may be worth considered limiting the length of it so that it doesn't get out of hand.", 600));
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
        TestStep step = (TestStep)getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        if (getItemViewType(position) == 0) {
            View editRecipeView = layoutInflater.inflate(R.layout.edit_recipe_step, parent, false);

            ((TextView) editRecipeView.findViewById(R.id.stepTitle)).setText("Step " + (position + 1));
            ((TextView) editRecipeView.findViewById(R.id.stepDescription)).setText(step.description);
            ((TextView) editRecipeView.findViewById(R.id.stepAction)).setText(step.action);
            ((TextView) editRecipeView.findViewById(R.id.stepIngredient)).setText(step.ingredient);
            ((TextView) editRecipeView.findViewById(R.id.stepIngredientAmount)).setText(step.amount);
            ((Button) editRecipeView.findViewById(R.id.stepDelete)).setOnClickListener(new DeleteStepOnClickListener(this, position));

            return editRecipeView;
        } else {
            View viewRecipeView = layoutInflater.inflate(R.layout.view_recipe_step, parent, false);

            ((TextView) viewRecipeView.findViewById(R.id.stepTitle)).setText("Step " + (position + 1));
            ((TextView) viewRecipeView.findViewById(R.id.stepDescription)).setText(step.description);

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


    /********** Additional Functions **********/

    /*
     * Adds a new step to the end of the recipe and selects
     * it for editing.
     */
    public void addStep() {
        testSteps.add(new TestStep());
        selectedStepIndex = testSteps.size() - 1;
        this.notifyDataSetChanged();
    }

    /********** Private Helper Classes **********/

    /*
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

    // A temporary placeholder class representing a recipes step.
    private static class TestStep {
        public String action;
        public String ingredient;
        public String amount;
        public String description;
        public int estimatedTime;// in seconds

        public TestStep(String action, String ingredient, String amount,
                        String description, int estimatedTime) {
            this.action = action;
            this.ingredient = ingredient;
            this.amount = amount;
            this.description = description;
            this.estimatedTime = estimatedTime;
        }

        public TestStep() {
            this.action = "";
            this.ingredient = "";
            this.amount = "";
            this.description = "";
            this.estimatedTime = 0;
        }
    }
}
