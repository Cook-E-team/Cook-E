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

package org.cook_e.cook_e;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.cook_e.cook_e.ui.HomePageAdapter;


public class HomeActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private HomePageAdapter mPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpActionBar();

        mViewPager = (ViewPager) findViewById(R.id.pager);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        // Tags identify the tabs for testing.
        tabLayout.addTab(tabLayout.newTab().setText(R.string.meals));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.recipes).setTag(1));

        // Set the page when a tab is selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        // Set the selected tab when a page is selected
        mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                final Tab tab = tabLayout.getTabAt(position);
                if (tab != null) {
                    tab.select();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Set up HomePageAdapter
        mPageAdapter = new HomePageAdapter(getFragmentManager());
        mViewPager.setAdapter(mPageAdapter);

        // Set up add button for recipes/meals
        final FloatingActionButton addButton = (FloatingActionButton) findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Fragment visibleFragment = mPageAdapter.getItem(mViewPager.getCurrentItem());
                if (visibleFragment == mPageAdapter.getMealList()) {
                    mPageAdapter.getMealList().onAddButtonPressed();
                } else {
                    mPageAdapter.getRecipeList().onAddButtonPressed();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // May be returning from another activity that modified data, so reload everything
        mPageAdapter.getRecipeList().reloadRecipes();
        mPageAdapter.getMealList().reloadMeals();
    }

    private void setUpActionBar() {
        final ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setTitle(R.string.app_name);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.topright_menu, menu);

        final MenuItem tutorialItem = menu.findItem(R.id.menu_tutorial);
        tutorialItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Intent intent = new Intent(HomeActivity.this, TutorialActivity.class);
                startActivity(intent);
                return true;
            }
        });

        final MenuItem aboutItem = menu.findItem(R.id.menu_about);
        aboutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Open the about activity
                final Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);
                return true;
            }
        });

        final MenuItem reportProblemItem = menu.findItem(R.id.menu_report_problem);
        reportProblemItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                final Intent intent = new Intent(HomeActivity.this, BugReportActivity.class);
                startActivity(intent);
                return true;
            }
        });

        return true;
    }
}
