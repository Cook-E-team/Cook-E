package org.cook_e.cook_e;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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

        System.out.println("getView called!!!");

        if (getItemViewType(position) == 0) {
            View editRecipeView = layoutInflater.inflate(R.layout.edit_recipe_step, parent, false);

            ((TextView) editRecipeView.findViewById(R.id.stepTitle)).setText("Step " + (position + 1));
            ((TextView) editRecipeView.findViewById(R.id.stepDescription)).setText(step.description);
            ((TextView) editRecipeView.findViewById(R.id.stepAction)).setText(step.action);
            ((TextView) editRecipeView.findViewById(R.id.stepIngredient)).setText(step.ingredient);
            ((TextView) editRecipeView.findViewById(R.id.stepIngredientAmount)).setText(step.amount);

            return editRecipeView;
        } else {
            View viewRecipeView = layoutInflater.inflate(R.layout.view_recipe_step, parent, false);

            ((TextView) viewRecipeView.findViewById(R.id.stepTitle)).setText("Step " + (position + 1));
            ((TextView) viewRecipeView.findViewById(R.id.stepDescription)).setText(step.description);

            viewRecipeView.setOnClickListener(new CustomOnClickListener(this, position));
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

    private class CustomOnClickListener implements View.OnClickListener {
        private StepListAdapter adapter;
        private int position;

        public CustomOnClickListener(StepListAdapter adapter, int position) {
            this.adapter = adapter;
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            // save fields
            if (selectedStepIndex >= 0 && selectedStepIndex < testSteps.size()){
                TestStep closingStep = testSteps.get(selectedStepIndex);
                //editRecipeView
                //adapter.getView(selectedStepIndex, null, );

                //closingStep.description = ((TextView) v.findViewById(R.id.stepDescription)).getText().toString();
                //closingStep.action = ((TextView) v.findViewById(R.id.stepAction)).getText().toString();
                //closingStep.ingredient = ((TextView) editRecipeView.findViewById(R.id.stepIngredient)).getText();
                //closingStep.amount = ((TextView) editRecipeView.findViewById(R.id.stepIngredientAmount)).getText();
            }

            selectedStepIndex = position;
            adapter.notifyDataSetChanged();
        }
    }
}
