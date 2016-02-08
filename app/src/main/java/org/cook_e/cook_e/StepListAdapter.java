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
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tyler on 2/6/2016.
 */
public class StepListAdapter extends BaseAdapter {

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


    private final Context mContext;
    private final List<TestStep> testSteps;
    private int selectedStepIndex;


    public StepListAdapter(Context mContext) {
        this.mContext = mContext;

        testSteps = new ArrayList<TestStep>();
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

        selectedStepIndex = -1;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return position >= 0 && position < testSteps.size();
    }

    @Override
    public int getCount() {
        return testSteps.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return testSteps.get(position);
        } else {
            return null;
        }
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

    @Override
    public int getItemViewType(int position) {
        if (position == selectedStepIndex) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public boolean isEmpty() {
        return getCount() == 0;
    }



    public void addStep() {
        testSteps.add(new TestStep());
        selectedStepIndex = testSteps.size() - 1;
        this.notifyDataSetChanged();
    }

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
