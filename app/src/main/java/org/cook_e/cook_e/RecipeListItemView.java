package org.cook_e.cook_e;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A view group that displays a drawable image, a title, and a description
 */
public class RecipeListItemView extends LinearLayout {

	/**
	 * Padding for selected elements, in some pixel-like units
	 */
	private static final int PADDING = 20;

	/**
	 * Maximum width and height of the image, in some pixel-like units
	 */
	private static final int MAX_IMAGE_DIMENSION = 300;

	/**
	 * The view that displays the image
	 */
	final ImageView mImageView;
	/**
	 * The text view that displays the title
	 */
	final TextView mTitleView;
	/**
	 * The text view that displays the description
	 */
	final TextView mDescriptionView;

	/**
	 * Creates a new view
	 * @param context the context to create the view in
	 */
	@SuppressWarnings("Deprecated")
	public RecipeListItemView(Context context) {
		super(context);

		// Set up layout
		setOrientation(LinearLayout.HORIZONTAL);
		setPadding(PADDING, PADDING, PADDING, PADDING);

		// Create image view
		mImageView = new ImageView(context);
		mImageView.setScaleType(ScaleType.FIT_CENTER);
		mImageView.setAdjustViewBounds(true);
		mImageView.setMaxHeight(MAX_IMAGE_DIMENSION);
		mImageView.setMaxWidth(MAX_IMAGE_DIMENSION);

		// Create right pane
		final LinearLayout rightPane = new LinearLayout(context);
		rightPane.setOrientation(LinearLayout.VERTICAL);
		rightPane.setPadding(PADDING, PADDING, PADDING, PADDING);
		// Put things in right pane
		mTitleView = new TextView(context);
		mTitleView.setPadding(PADDING, PADDING, PADDING, PADDING);
		mTitleView.setTextAppearance(context, android.R.style.TextAppearance_Large);
		rightPane.addView(mTitleView);
		mDescriptionView = new TextView(context);
		mDescriptionView.setPadding(PADDING, PADDING, PADDING, PADDING);
		mDescriptionView.setTextAppearance(context, android.R.style.TextAppearance_Medium);
		rightPane.addView(mDescriptionView);

		final LayoutParams imageParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		imageParams.gravity = Gravity.CENTER;
		addView(mImageView, imageParams);
		addView(rightPane, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
	}

	/**
	 * Returns the image being displayed, which may be null
	 * @return the current image
	 */
	@Nullable
	public Drawable getImage() {
		return mImageView.getDrawable();
	}

	/**
	 * Sets the image to display
	 * @param image an image to display, or null to display no image
	 */
	public void setImage(@Nullable Drawable image) {
		mImageView.setImageDrawable(image);
	}

	/**
	 * Returns the title displayed in this view
	 * @return the title
	 */
	@NonNull
	public String getTitle() {
		return mTitleView.getText().toString();
	}

	/**
	 * Sets the title to display
	 * @param title the title. Must not be null.
	 */
	public void setTitle(@NonNull String title) {
		mTitleView.setText(title);
	}

	/**
	 * Returns the description displayed in this view
	 * @return the description
	 */
	@NonNull
	public String getDescription() {
		return mDescriptionView.getText().toString();
	}

	/**
	 * Sets the description to display
	 * @param description the description. Must not be null.
	 */
	public void setDescription(@NonNull String description) {
		mDescriptionView.setText(description);
	}
}
