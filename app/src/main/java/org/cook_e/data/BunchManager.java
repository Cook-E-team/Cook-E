package org.cook_e.data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class keeps and manages all bunches in the app
 */
public class BunchManager {
    private List<Bunch> bunchList;
    private StorageAccessor sa;

    /**
     * constructor
     * @param sa StorageAccessor to access storage
     * @throws SQLException when an error happened in reading bunches
     */
    public BunchManager(StorageAccessor sa) throws SQLException{
        this.sa = sa;
        try {
            bunchList = sa.loadAllBunches();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }

    /**
     * get all bunches currently in bunch manager
     * return a copy of current bunches, doesn't reflect changes
     * @return list of bunches
     */
    public List<Bunch> getAllBunches() {
        return new ArrayList<Bunch>(bunchList);
    }

    /**
     * get titles of all bunches currently in bunch manager
     * titles are in the same order of bunches
     * this list doesn't reflect changes
     * @return list of titles
     */
    public List<String> getAllBunchTitles() {
        List<String> res = new ArrayList<String>();
        for (int i = 0; i < bunchList.size(); i++) {
            res.add(bunchList.get(i).getTitle());
        }
        return res;
    }

    /**
     * get the ith bunch in the current bunch list
     * @param i index of the bunch, index start from 0
     * @return bunch at ith position
     */
    public Bunch getBunch(int i) {
        return bunchList.get(i);
    }

    /**
     * add a new bunch to the current bunch list and storage
     * if addition failed, nothing is changed
     * @param b bunch to add
     * @return true on success, false on failure
     */
    public boolean addBunch(Bunch b) {
        try {
            sa.storeBunch(b);
        } catch (SQLException e) {
            return false;
        }
        bunchList.add(b);
        return true;
    }

    /**
     * create a new bunch with given title and recipes
     * and add it to current bunch list and storage
     * if addition failed, nothing is changed
     * @param title title of new bunch
     * @param recipes list of recipes in new bunch
     * @return true on success, false on failure
     */
    public boolean addNewBunch(String title, List<Recipe> recipes) {
        Bunch b = new Bunch(title, recipes);
        return addBunch(b);
    }

    /**
     * delete bunch in position i of the current bunch list
     * if deletion failed, nothing is changed
     * @param i index of bunch to remove, index starts from 0
     * @return true on success, false on failure
     */
    public boolean deleteBunch(int i) {
        Bunch b = bunchList.get(i);
        try {
            sa.deleteBunch(b);
        } catch (SQLException e) {
            return false;
        }
        bunchList.remove(i);
        return true;
    }

    /**
     * delete bunch b from current bunch list, if present
     * if deletion failed, nothing is changed
     * @param b bunch to delete
     * @return true on success, false on failure
     */
    public boolean deleteBunch(Bunch b) {
        try {
            sa.deleteBunch(b);
        } catch (SQLException e) {
            return false;
        }
        return bunchList.remove(b);
    }

    /**
     * add recipe r to the ith bunch in the current bunch list
     * if addition failed, nothing is changed
     * @param i index of bunch to edit, index starts from 0
     * @param r recipe to add
     * @return true on success, false on failure
     */
    public boolean addRecipeToBunch(int i, Recipe r) {
        Bunch b = bunchList.get(i);
        b.addRecipe(r);
        try {
            sa.editBunch(b);
        } catch (SQLException e) {
            b.removeRecipe(r);
            return false;
        }
        return true;
    }

    /**
     * delete recipe r from the ith bunch in the current bunch list, if present
     * if deletion failed, nothing is changed
     * @param i index of bunch to edit, index starts from 0
     * @param r recipe to remove
     * @return true on success, false on failure
     */
    public boolean deleteRecipeFromBunch(int i, Recipe r) {
        Bunch b = bunchList.get(i);
        boolean res = b.removeRecipe(r);
        if (!res) return false;
        try {
            sa.editBunch(b);
        } catch (SQLException e) {
            b.addRecipe(r);
            return false;
        }
        return true;
    }

    /**
     * change title of ith bunch in the current bunch list
     * if changing failed, nothing is changed
     * @param i index of bunch to edit, index starts from 0
     * @param title new title for the bunch
     * @return true on success, false on failure
     */
    public boolean changeBunchTitle(int i, String title) {
        Bunch b = bunchList.get(i);
        String otitle = b.getTitle();
        b.setTitle(title);
        try {
            sa.editBunch(b);
        } catch (SQLException e) {
            b.setTitle(otitle);
            return false;
        }
        return true;
    }
}
