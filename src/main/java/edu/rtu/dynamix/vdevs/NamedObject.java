package edu.rtu.dynamix.vdevs;

public abstract class NamedObject {
	
	private static int objectCounter;

	private String name;
	
	private int id;
		
	public NamedObject(String name) {
		this.name = name;
		id = objectCounter++;
	}

	public String getName() {
		if (name == null) {
			return "Unnamed" + id; 
		} else {
			return name;
		}
	}
}
