package org.cook_e.cook_e.ui;


import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

/**
 * Created by zijin on 2016/2/7.
 */
public class HomePageAdapter extends FragmentPagerAdapter {

    public HomePageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 1) {
            return new RecipeList();
        } else {
            return new MealList();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
