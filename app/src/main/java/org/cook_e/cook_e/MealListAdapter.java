package org.cook_e.cook_e;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListAdapter;

/**
 * A list adapter that displays a list of meals, with each meal shown in a {@link RecipeListItemView}
 */
public class MealListAdapter implements ListAdapter {

	/**
	 * The activity used to access resources
	 */
	private final Context mContext;

	public MealListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean isEnabled(int position) {
		if (position == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {

	}

	@Override
	public int getCount() {
		return 1;
	}

	@Override
	public Object getItem(int position) {
		return "Test";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	// Deprecated warnings suppressed because of the use of Resources.getDrawable(int), which
	// was deprecated in API level 22. The replacement was not introduced until API level 21,
	// which is more recent than this application's minimum supported API level.
	@Override
	@SuppressWarnings("Deprecated")
	public View getView(int position, View convertView, ViewGroup parent) {
		RecipeListItemView view;
		if (convertView instanceof RecipeListItemView) {
			view = (RecipeListItemView) convertView;
		}
		else {
			view = new RecipeListItemView(mContext);
		}

		final Drawable drawable = mContext.getResources().getDrawable(R.drawable.test_image_1);

		view.setTitle("A recipe");
		view.setDescription("This is a recipe for something good.");
		view.setImage(drawable);

		return view;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}
}
