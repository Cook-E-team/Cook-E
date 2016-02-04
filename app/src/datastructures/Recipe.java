import java.util.List;
import java.util.ArrayList;

/*
 * Represents a recipe
 * Has an ordered list of steps, a title, and an author
 * Title and List of steps must not be empty
 */
public class Recipe {
	private List<Step> steps;
	private String title;
	private String author;
	/*
	 * Constructor
	 * Takes a list of steps, title and optionally an author
	 * 
	 */
	public Recipe(List<Step> steps, String title, String author) {
		if (steps == null || steps.size() == 0 ) throw new IllegalArgumentException("list of steps is empty");
		if (title == null || title.length() == 0) throw new IllegalArgumentException("title is empty");
		this.steps = steps;
		this.title = title;
		this.author = author;
	}
	
	/*
	 * Add step to end of the list of steps
	 * Caller responsibility that Step is properly formed
	 */
	public void addStep(Step step) {
		steps.add(step);
	}
	/*
	 * Remove step with given index
	 * Does nothing if index is out of bounds
	 */
	public void removeStep(int index) {
		if (index < steps.size() && index >= 0) steps.remove(index);
	}
	/*
	 * Returns a List of all the ingredients across the recipes' steps
	 */
	public List<Ingredient> getIngredients() {
		List<Ingredient> ings = new ArrayList<Ingredient>();
		for (Step s: steps) {
			for (Ingredient ingredient: s.getIngredients()) {
				ings.add(ingredient);
			}
		}
		return ings;
	}
	/*
	 * Returns the total estimated time of all the recipes' steps
	 */
	public int getTotalTime() {
		int time = 0;
		for (Step s: steps) {
			time += s.getTime();
		}
		return time;
	}
	public List<Step> getSteps() {
		return steps;
	}
	public String getTitle() {
		return title;
	}
	public String getAuthor() {
		if (author == null) {
			return "";
		} else return author;
	}
}