package edu.rtu.dynamix.vdevs.values;


public class TupleValue extends Value {
	
	private long[] value;
	
	public TupleValue(long[] value) {
		this.value = value;
	}

	@Override
	public Type getType() {
		return Type.Tuple;
	}
	
	public long[] value() {
		return value;
	}
	
	public double get(int idx) {
		return value[idx];
	}
}
