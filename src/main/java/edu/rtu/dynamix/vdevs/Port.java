package edu.rtu.dynamix.vdevs;

import java.util.ArrayList;
import java.util.List;

public abstract class Port extends NamedObject {
		
	private DEVSModel parent;
	
	private List<Port> destinations = new ArrayList<>();
	
	private List<Port> sources = new ArrayList<>();
	
	public Port(String name, DEVSModel parent) {
		super(name);
		this.parent = parent;
	}

	public List<Port> getDestinations() {
		return destinations;
	}

	public DEVSModel getParent() {
		return parent;
	}

	public List<Port> getSources() {
		return sources;
	}
	
	public void addDestination(Port destination) {
		destinations.add(destination);
	}
	
	public void addSource(Port source) {
		sources.add(source);
		parent.sourceAdded(source.getParent());
	}
}
