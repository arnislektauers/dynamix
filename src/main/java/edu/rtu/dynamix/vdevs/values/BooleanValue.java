package edu.rtu.dynamix.vdevs.values;

public class BooleanValue extends Value {
	
	public static final BooleanValue TRUE = new BooleanValue(true);
	public static final BooleanValue FALSE = new BooleanValue(false);
	
	private boolean value;
	
	public BooleanValue(boolean value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.Boolean;
	}
	
	public boolean value() {
		return value;
	}
	
	@Override
	public String toString() {
		return Boolean.toString(value);
	}
}
