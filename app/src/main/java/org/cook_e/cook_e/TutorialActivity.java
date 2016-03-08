package org.cook_e.cook_e;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by carso on 3/2/2016.
 */
public class TutorialActivity extends AppCompatActivity {

    Button nextButton;
    Button prevButton;
    ImageView image;
    public static int[] imageIds = {R.drawable.recipe_view, R.drawable.create_recipe, R.drawable.recipe_editor,
                                    R.drawable.edit_step, R.drawable.meal_view, R.drawable.meal_editor1,
                                    R.drawable.meal_editor2, R.drawable.meal_editor_recipe,
                                    R.drawable.meal_editor2, R.drawable.schedular_view};
    public int currIndex;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        image = (ImageView) findViewById(R.id.imageView);
        image.setImageResource(imageIds[0]);
        nextButton = (Button) findViewById(R.id.button);
        prevButton = (Button) findViewById(R.id.previous);
        addListenerOnNextButton();
        addListenerOnPrevButton();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpActionBar();
        currIndex = 0;
    }

    private void addListenerOnPrevButton() {
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currIndex = Math.max(currIndex - 1, 0);
                prevButton.setText((currIndex == 0) ? "--------" : "PREVIOUS");
                nextButton.setText((currIndex == imageIds.length - 1) ? "----" : "NEXT");
                image.setImageResource(imageIds[currIndex]);
            }
        });
    }

    private void addListenerOnNextButton() {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currIndex = Math.min(currIndex + 1, imageIds.length - 1);
                prevButton.setText((currIndex == 0) ? "--------" : "PREVIOUS");
                nextButton.setText((currIndex == imageIds.length - 1) ? "----" : "NEXT");
                image.setImageResource(imageIds[currIndex]);
            }
        });
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle("Cook-E: Tutorial");
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
