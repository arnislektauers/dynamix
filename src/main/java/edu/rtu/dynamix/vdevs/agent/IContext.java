package edu.rtu.dynamix.vdevs.agent;

import edu.rtu.vdevs.AtomicVDEVSModel;

public interface IContext {

	/**
	 * Gets the named projection. This does not search subcontexts.
	 *
	 * @param name the name of the projection to get
	 * @return the named projection.
	 */
	IProjection getProjection(String name);
	
	void add(AtomicVDEVSModel model);
	
	int size();
	
	void remove(AtomicVDEVSModel model);
}
