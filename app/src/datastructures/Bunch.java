import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/*
 * Class that represents a grouping of recipes
 * Stores a map of recipe titles to recipe file names
 * Modifiable
 * Bunches must have a name
 */
public class Bunch {
	private String title;
	private Map<String, String> recipe_refs;
	
	public Bunch(String title, Map<String, String> recipe_refs) {
		if (title == null || title.equals("")) throw new IllegalArgumentException("no name for this bunch");
		this.title = title;
		this.recipe_refs = recipe_refs;
	}

	public String getTitle() {
		return title;
	}
	/*
	 * Returns a List of all of the recipe titles in this bunch
	 */
	public List<String> getRecipeTitles() {
		return new ArrayList<String>(recipe_refs.keySet());
	}
	public Map<String, String> getRecipeRefs() {
		return Collections.unmodifiableMap(recipe_refs);
	}
	/*
	 * Changes the name of this bunch
	 * Cannot be changed to null or the empty string
	 */
	public void changeTitle(String title) {
		if (title != null && !title.equals("")) 
		this.title = title;
	}
	/*
	 * Adds a recipe to this bunch
	 * Takes a recipe title and filename for the recipe
	 * If either title or filename is null this method does nothing
	 */
	public void addRecipe(String recipe_name, String recipe_ref) {
		if (recipe_name != null && recipe_ref != null)recipe_refs.put(recipe_name, recipe_ref);
	}
	/*
	 * Removes a recipe from this bunch
	 * Does nothing if the recipe name given is not in this bunch
	 */
	public void removeRecipe(String recipe_name) {
		if (recipe_name != null) recipe_refs.remove(recipe_name);
	}
}