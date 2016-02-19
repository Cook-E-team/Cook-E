package org.cook_e.cook_e;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * A list adapter that displays a list of steps
 *
 */
public class SchedulerStepAdapter extends BaseAdapter {
    private final Context mContext;
    private final List<ScheduleStep> scheduleSteps;

    public SchedulerStepAdapter(Context mContext) {
        this.mContext = mContext;
        this.scheduleSteps = new ArrayList<>();

        // Temporary code to populate the steps with data.
        scheduleSteps.add(new ScheduleStep("Recipe A", "cut Carrots", null));
        scheduleSteps.add(new ScheduleStep("Recipe B", "cut Potatoes", null));
    }

    /**
     * Returns the object associated with the given position in
     * the data set. Returns null if there isn't one.
     */
    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return scheduleSteps.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getCount() {
        return scheduleSteps.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ScheduleStep step = (ScheduleStep) getItem(position);
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View viewRecipeView = layoutInflater.inflate(R.layout.view_recipe_step, parent, false);

        ((TextView) viewRecipeView.findViewById(R.id.stepTitle)).setText(step.recipeName);
        ((TextView) viewRecipeView.findViewById(R.id.stepDescription)).setText(step.stepDescription);

        return viewRecipeView;
    }

    // A temporary placeholder class representing a schedule step.
    private static class ScheduleStep {
        public String recipeName;
        public String stepDescription;
        public ScheduleStep next;

        public ScheduleStep(String recipeName, String stepDescription, ScheduleStep next) {
            this.recipeName = recipeName;
            this.stepDescription = stepDescription;
            this.next = next;
        }

        public ScheduleStep(String recipeName, String stepDescription) {
            new ScheduleStep(recipeName, stepDescription, null);
        }

    }
}
