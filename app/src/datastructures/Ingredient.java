/*
 * Class representing an ingredient 
 * Has a type, amount, and units for the amount
 */
public class Ingredient {
	private String type;
	private int amount;
	private String unit;
	
	public Ingredient(String type, int amount, String unit) {
		if (type == null || type.length() == 0) throw new IllegalArgumentException("ingredient type is empty");
		if (amount <= 0) throw new IllegalArgumentException("amount is <= 0");
		if (unit == null || unit.length() == 0) throw new IllegalArgumentException("unit is empty");

		this.type = type;
		this.amount = amount;
		this.unit = unit;
	}

	public String getType() {
		return type;
	}

	public int getAmount() {
		return amount;
	}

	public String getUnit() {
		return unit;
	}

}