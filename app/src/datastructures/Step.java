import java.util.List;

/*
 * Class representing a step in a recipe
 * Every step has a description, action, list of ingredients and estimate time the step takes
 */
public class Step {
	private String description;
	private String action;
	private int time;
	private List<Ingredient> ings;
	
	public Step(String description, int time, List<Ingredient> ings, String act) {
		if (description == null || description.length() == 0) throw new IllegalArgumentException("description is empty");
		if (time < 0) throw new IllegalArgumentException("time is negative");
		if (ings == null || ings.size() == 0) throw new IllegalArgumentException("ingredients is empty");
		if (act == null || act.length() == 0) throw new IllegalArgumentException("action is empty");
		this.description = description;
		this.time = time;
		this.ings = ings;
		this.action = act;
	}
	public String getDescription() {
		return description;
	}
	public int getTime() {
		return time;
	}
	public List<Ingredient> getIngredients() {
		return ings;
	}
	public String getAction() {
		return action;
	}
}