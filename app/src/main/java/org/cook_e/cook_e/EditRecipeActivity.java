package org.cook_e.cook_e;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

/**
 * Created by Tyler on 2/4/2016.
 *
 * This is the activity for editing a particular recipe.
 */
public class EditRecipeActivity extends AppCompatActivity {

    /*
     * Sets up the main view to edit recipes.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        final StepListAdapter stepsAdapter = new StepListAdapter(this);
        ListView stepsList = (ListView) findViewById(R.id.recipeSteps);
        stepsList.setAdapter(stepsAdapter);

        ((android.support.design.widget.FloatingActionButton)findViewById(R.id.stepAdd)).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stepsAdapter.addStep();
                    }
                }
        );
    }

    /*
     * Sets up the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_recipe_menu, menu);
        MenuItem finishItem = (MenuItem) menu.findItem(R.id.finish);
        finishItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    /*
     * Handles action bar items "onClick" events.
     */
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
