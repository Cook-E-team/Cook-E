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

package org.cook_e.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Utilities for working with instances of {@link android.graphics.Bitmap}
 */
public class Bitmaps {

    /**
     * Unpacks a Bitmap from a result
     *
     * @param context the context to use
     * @param data    an intent containing an image
     * @return a bitmap from the intent, or null if no image was provided
     */
    public static Bitmap bitmapFromResult(@NonNull Context context, @Nullable Intent data) {
        if (data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                return BitmapFactory.decodeFile(picturePath);
            }
        }
        return null;
    }

    /**
     * Starts an activity that can allow the user to choose an image
     * @param context the context to use
     * @param resultCode the result code to receive
     */
    public static void requestImageSelection(Activity context, int resultCode) {
        Intent intent = new Intent(
                Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        context.startActivityForResult(intent, resultCode);
    }

    private Bitmaps() {
    }
}
