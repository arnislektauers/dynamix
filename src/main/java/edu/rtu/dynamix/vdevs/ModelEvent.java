package edu.rtu.dynamix.vdevs;

import edu.rtu.vdevs.values.Value;


public class ModelEvent extends Event {
	
	DEVSModel model;
	
	/*private Port port;
	
	private Object[] values;
	
	private double time;*/
	
	public ModelEvent(DEVSModel model, Port port, Value value) {
		super(port, value);
		this.model = model;
	}
	
	/*public Event(Port port, Object[] values) {		
		set(port, values);
	}

	public Port getPort() {
		return port;
	}
	
	public Object getValue() {
		return values[0];
	}
	
	public Object[] getValues() {
		return values;
	}
	
	public void set(Port port) {
		set(port, null);
	}
	
	public void set(Port port, Object value) {
		this.port = port;
		this.values = new Object[] { value };
	}
	
	public void set(Port port, Object[] values) {
		this.port = port;
		this.values = values;
	}
	
	@Override
	public String toString() {
		if (port != null || values != null) {
			String str = "";
			if (port != null) {
				str = port.getName();
			}
			/*if (value != null) {
				str += ":" + value;
			}*/
	/*		return str;
		} else {
			return super.toString();
		}
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}*/
}
