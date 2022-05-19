package edu.rtu.dynamix.vdevs.values;

import java.util.HashMap;
import java.util.Map;

public class MapValue extends Value {
	
	private Map<String, Value> values = new HashMap<String, Value>();

	@Override
	public Type getType() {
		return Type.Map;
	}
	
	/**
	 * Add a value into the map
	 * @param name
	 * @param value
	 */
	public void add(String name, Value value) {
		values.put(name, value);
	}
	
	/**
	 * Get the Value object for specified name
	 * @param name The name of the Value in the map
	 * @return a reference to the Value
	 */
	public Value get(String name) {
		return values.get(name);
	}
}
