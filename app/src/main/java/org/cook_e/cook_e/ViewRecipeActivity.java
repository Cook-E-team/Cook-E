package org.cook_e.cook_e;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ListView;

public class ViewRecipeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // must be call beforehand
        setContentView(R.layout.activity_view_recipe);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setUpActionBar();
        ListView listView  = (ListView) findViewById(R.id.listView2);


    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle("Recipe Name");
        bar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.topright_menu, menu);
        return true;
    }
}
