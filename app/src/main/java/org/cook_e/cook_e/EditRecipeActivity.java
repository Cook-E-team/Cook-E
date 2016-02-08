package org.cook_e.cook_e;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Created by Tyler on 2/4/2016.
 */
public class EditRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        String[] steps = {"step 1", "step 2", "step 3", "step 4", "step 5", "step 6",
                            "step 7", "step 8", "step 9", "step 10"};
        ListAdapter stepsAdapter = new StepListAdapter(this);
        ListView stepsList = (ListView) findViewById(R.id.recipeSteps);
        stepsList.setAdapter(stepsAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_recipe_menu, menu);
        //MenuItem recipeNameItem = (MenuItem) menu.findItem(R.id.recipe_name);
        //recipeNameItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        //recipeNameItem.setTitle("The real title.");
        //recipeNameItem.setDisplayHomeAsUpEnabled(true);
        //getActionBar().setDisplayShowTitleEnabled(false);
        MenuItem finishItem = (MenuItem) menu.findItem(R.id.finish);
        finishItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle app bar item clicks here. The app bar
        // automatically handles clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.finish) {
            // do stuff...
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
