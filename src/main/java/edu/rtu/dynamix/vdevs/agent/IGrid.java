package edu.rtu.dynamix.vdevs.agent;

import edu.rtu.vdevs.AtomicVDEVSModel;

public interface IGrid extends IProjection {

	boolean moveTo(AtomicVDEVSModel model, int... newLocation);
	
	GridDimensions getDimensions();
	
	GridPoint getLocation(AtomicVDEVSModel model);
	
	/**
	 * Gets all the objects at the specified location. For a multi occupancy
	 * space this will be all the objects at that location. For a single
	 * occupancy space this will be the single object at that location.
	 * 
	 * @param location
	 * @return the object at the specified location.
	 */
	Iterable<AtomicVDEVSModel> getObjectsAt(int... location);
	
	void remove(AtomicVDEVSModel model);
}
