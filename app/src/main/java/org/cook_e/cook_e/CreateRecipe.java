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

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class CreateRecipe extends AppCompatActivity {

    /**
     * Result code used when requesting images
     */
    private static int RESULT_LOAD_IMAGE = 42;

    /**
     * The image view that displays the recipe image
     */
    private ImageView mImageView;
    /**
     * Text field for title
     */
    private EditText mTitleField;
    /**
     * Text field for description
     */
    private EditText mDescriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // must be call beforehand
        setContentView(R.layout.activity_create_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();

        // Image select button
        final Button imageSelectButton = (Button) findViewById(R.id.image_choose_button);
        imageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestImageSelect();
            }
        });

        mImageView = (ImageView) findViewById(R.id.recipe_image_view);
        mTitleField = (EditText) findViewById(R.id.title_field);
        mDescriptionField = (EditText) findViewById(R.id.description_field);

        // Continue button
        final Button continueButton = (Button) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueCreatingRecipe();
            }
        });

    }

    private void continueCreatingRecipe() {
        final String title = mTitleField.getText().toString();
        final String description = mDescriptionField.getText().toString();
        if (title.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Empty title")
                    .setMessage("Please enter a recipe title")
                    .setPositiveButton(android.R.string.ok, null)
                    .show();
            return;
        }
        // TODO: Parcel arguments

        final Intent intent = new Intent(this, EditRecipeActivity.class);
        startActivity(intent);
    }


    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.create_recipe);
        bar.setDisplayHomeAsUpEnabled(true);
    }

    /*
     * This is a workaround for inconsistent behavior.
     *
     * Pressing the system back button or calling finish() returns a result to the parent activity,
     * as expected. However, the default action when the up button is pressed does not send a result
     * to the parent. This override ensures that a result is sent when the action bar up button is
     * pressed.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Starts an activity to ask the user to select an image
     */
    private void requestImageSelect() {
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Check for a received image
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();

                final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                final Bitmap loadedImage = BitmapFactory.decodeFile(picturePath);

                if (loadedImage != null) {
                    mImageView.setImageBitmap(loadedImage);
                }
                else {
                    new AlertDialog.Builder(this)
                            .setTitle("Could not access image")
                            .setMessage("Please ensure that the selected image is valid and accessible.")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
            }
        }
    }
}
