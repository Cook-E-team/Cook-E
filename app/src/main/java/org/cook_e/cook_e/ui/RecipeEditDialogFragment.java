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
import android.view.View;
import android.widget.EditText;

import org.cook_e.cook_e.R;
import org.cook_e.data.Recipe;

/**
 * A dialog fragment that allows the user to the title and author of a {@link org.cook_e.data.Recipe}
 *
 * The Activity that uses this dialog must implement the
 * {@link org.cook_e.cook_e.ui.RecipeEditDialogFragment.RecipeEditListener} interface.
 */
public class RecipeEditDialogFragment extends DialogFragment {

    /**
     * Argument key for title (String)
     */
    private static final String ARG_TITLE = RecipeEditDialogFragment.class.getName() + ".ARG_TITLE";
    /**
     * Argument key for author (String)
     */
    private static final String ARG_AUTHOR = RecipeEditDialogFragment.class.getName() + ".ARG_AUTHOR";

    /**
     * An interface for objects that can accept notifications of when the user finishes editing
     * a recipe
     */
    public interface RecipeEditListener {
        /**
         * Called when the user finishes editing a recipe
         *
         * @param newTitle the new title of the recipe
         * @param newAuthor the new author of the recipe
         */
        void recipeEditingFinished(@NonNull String newTitle, @NonNull String newAuthor);
    }

    /**
     * Title text field
     */
    private EditText mTitleField;
    /**
     * Author text field
     */
    private EditText mAuthorField;

    /**
     * Creates a fragment
     *
     * Client code should normally use {@link #newInstance(Recipe)} instead.
     */
    public RecipeEditDialogFragment() {

    }

    /**
     * Creates a new StepDialogFragment to edit a recipe
     *
     * @param recipe the recipe to edit
     * @return a dialog fragment
     */
    public static RecipeEditDialogFragment newInstance(@NonNull Recipe recipe) {
        final RecipeEditDialogFragment fragment = new RecipeEditDialogFragment();

        final Bundle args = new Bundle();
        args.putString(ARG_TITLE, recipe.getTitle());
        args.putString(ARG_AUTHOR, recipe.getAuthor());
        fragment.setArguments(args);

        return fragment;
    }


    /**
     * Called when the user presses the positive (OK) button
     *
     * @return true to dismiss the dialog, false to continue displaying it
     */
    private boolean onOkPressed() {
        final String title = mTitleField.getText().toString();
        final String author = mAuthorField.getText().toString();

        if (title.isEmpty()) {
            mTitleField.setError("Please enter a title");
            return false;
        }

        // Notify parent
        ((RecipeEditListener) getActivity()).recipeEditingFinished(title, author);
        return true;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check parent
        if (!(getActivity() instanceof RecipeEditListener)) {
            throw new IllegalStateException("A RecipeEditDialogFragment may only be started from an " +
                    "activity that implements the RecipeEditDialogFragment.RecipeEditListener interface");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.edit_recipe)
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
        final View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_recipe_edit, null);

        mTitleField = (EditText) view.findViewById(R.id.recipe_title_field);
        mAuthorField = (EditText) view.findViewById(R.id.recipe_author_field);

        // Extract data from arguments
        final Bundle args = getArguments();
        if (args != null) {
            mTitleField.setText(args.getString(ARG_TITLE));
            mAuthorField.setText(args.getString(ARG_AUTHOR));
        }

        return view;
    }
}
