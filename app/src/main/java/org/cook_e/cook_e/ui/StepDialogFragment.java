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
 */

package org.cook_e.cook_e.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import org.cook_e.cook_e.R;
import org.cook_e.data.Step;
import org.joda.time.Duration;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A dialog fragment that allows the user to edit a {@link org.cook_e.data.Step}
 *
 * The Activity that uses this dialog must implement the
 * {@link org.cook_e.cook_e.ui.StepDialogFragment.StepEditListener} interface.
 */
public class StepDialogFragment extends DialogFragment {

    /**
     * Argument key for description (String)
     */
    private static final String ARG_DESCRIPTION = StepDialogFragment.class.getName() + ".ARG_DESCRIPTION";
    /**
     * Argument key for ingredients (List&lt;String&gt;)
     */
    private static final String ARG_INGREDIENTS = StepDialogFragment.class.getName() + ".ARG_INGREDIENTS";
    /**
     * Argument key for duration ({@link org.joda.time.Duration})
     */
    private static final String ARG_DURATION = StepDialogFragment.class.getName() + ".ARG_DURATION";
    /**
     * Argument key for simultaneous (boolean)
     */
    private static final String ARG_SIMULTANEOUS = StepDialogFragment.class.getName() + ".ARG_SIMULTANEOUS";

    /**
     * An interface for objects that can accept notifications of when the user finishes editing
     * a step
     */
    public interface StepEditListener {
        /**
         * Called when the user finishes editing a step
         *
         * @param step the step the user has edited
         */
        void stepEditingFinished(@NonNull Step step);
    }

    /**
     * Description text field
     */
    private EditText mDescriptionField;
    /**
     * Duration text field
     */
    private EditText mDurationField;
    /**
     * Ingredients text field
     */
    private EditText mIngredientsField;

    /**
     * The formatter used to parse and format durations (periods)
     */
    private final PeriodFormatter mFormatter;

    /**
     * Creates a fragment
     *
     * Client code should normally use {@link #newInstance(Step)} instead.
     */
    public StepDialogFragment() {
        // Create formatter
        mFormatter = PeriodFormat.wordBased();
    }

    /**
     * Creates a new StepDialogFragment to edit a step
     *
     * @param step the step to edit, or null to create a new step
     * @return a dialog fragment
     */
    public static StepDialogFragment newInstance(@Nullable Step step) {
        final StepDialogFragment fragment = new StepDialogFragment();
        if (step != null) {
            final Bundle args = new Bundle();
            args.putString(ARG_DESCRIPTION, step.getDescription());
            args.putStringArrayList(ARG_INGREDIENTS, new ArrayList<>(step.getIngredients()));
            args.putSerializable(ARG_DURATION, step.getTime().toDuration());
            args.putBoolean(ARG_SIMULTANEOUS, step.isSimultaneous());
            fragment.setArguments(args);
        }
        return fragment;
    }


    /**
     * Called when the user presses the positive (OK) button
     *
     * @return true to dismiss the dialog, false to continue displaying it
     */
    private boolean onOkPressed() {
        final String description = mDescriptionField.getText().toString();
        final String durationString = mDurationField.getText().toString();
        final String ingredientsString = mIngredientsField.getText().toString();

        // Check required fields
        if (description.isEmpty()) {
            mDescriptionField.setError("Please enter a description");
            return false;
        }
        if (durationString.isEmpty()) {
            mDurationField.setError("Please enter a time");
            return false;
        }

        try {
            // Try to parse duration
            final Period period = mFormatter.parsePeriod(durationString);
            final Duration duration = period.toStandardDuration();

            // Parse ingredients
            final List<String> ingredients = ingredientsString.isEmpty() ? Collections.<String>emptyList()
                    : Arrays.asList(ingredientsString.split("\n"));

            final Step step = new Step(ingredients, description, duration);

            // Pass the step to the parent
            ((StepEditListener) getActivity()).stepEditingFinished(step);
            return true;
        } catch (IllegalArgumentException e) {
            mDurationField.setError("Invalid time format");
            return false;
        } catch (UnsupportedOperationException e) {
            mDurationField.setError("Enter a time less than 1 month");
            return false;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check parent
        if (!(getActivity() instanceof StepEditListener)) {
            throw new IllegalStateException("A StepDialogFragment may only be started from an " +
                    "activity that implements the StepDialogFragment.StepEditListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.edit_step)
                .setPositiveButton(android.R.string.ok, null)
                .setNegativeButton(android.R.string.cancel, null)
                .setView(createView())
                .create();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Override OK button behavior
        // Do not always dismiss dialog
        final AlertDialog dialog = (AlertDialog) getDialog();
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final boolean close = onOkPressed();
                        if (close) {
                            // Close this DialogFragment
                            dismiss();
                        }
                    }
                });
    }

    /**
     * Sets up and returns a View to be displayed in the dialog
     *
     * @return the view to display
     */
    private View createView() {
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_step, null);

        mDescriptionField = (EditText) view.findViewById(R.id.description_field);
        mDurationField = (EditText) view.findViewById(R.id.duration_field);
        mIngredientsField = (EditText) view.findViewById(R.id.ingredients_field);

        // Extract data from arguments
        final Bundle args = getArguments();
        if (args != null) {
            // Description
            final String description = args.getString(ARG_DESCRIPTION);
            mDescriptionField.setText(description);

            // Duration
            final Duration duration = (Duration) args.getSerializable(ARG_DURATION);
            if (duration == null) {
                throw new IllegalStateException("No duration argument");
            }
            final Period period = duration.toPeriod();
            final String durationString = mFormatter.print(period);
            mDurationField.setText(durationString);

            // Ingredients
            final List<String> ingredients = args.getStringArrayList(ARG_INGREDIENTS);
            if (ingredients == null) {
                throw new IllegalStateException("No ingredients argument");
            }
            final StringBuilder ingredientsString = new StringBuilder();
            for (String ingredient : ingredients) {
                ingredientsString.append(ingredient);
                ingredientsString.append('\n');
            }
            mIngredientsField.setText(ingredientsString);
        }

        return view;
    }
}
