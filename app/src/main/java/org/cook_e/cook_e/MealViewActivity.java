package org.cook_e.cook_e;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

public class MealViewActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meal_view);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		setUpActionBar();

		final ListView recipeList = (ListView) findViewById(R.id.recipe_list);
		recipeList.setAdapter(new MealListAdapter(this));
	}

	private void setUpActionBar() {
		final ActionBar bar = getSupportActionBar();
		assert bar != null;
		bar.setTitle("Meal name");
	}
}
