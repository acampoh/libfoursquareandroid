package es.viridian.foursquare.enums;

public enum FSCategory {
	NONE(""),
	NIGHT_LIFE("4d4b7105d754a06376d81259"),
	FOOD("4d4b7105d754a06374d81259"),
	;
	
	private String categoryId;
	
	private FSCategory(String id)	{
		this.categoryId = id;
	}
	
	public String getId() {
		return this.categoryId;
	}
}
