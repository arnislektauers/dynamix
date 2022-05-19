package edu.rtu.dynamix.vdevs;

import java.util.ArrayList;
import java.util.List;

public class SimulationData {

	/**
	 * Time of last event
	 */
	long timeLast; 
	
	/**
	 * Index in the priority queue
	 */
	int queueIndex;
	
	/**
	 * Has this model been actived?
	 */
	boolean active;
	
	/**
	 * Input event list
	 */
	List<Event> inputs = new ArrayList<Event>();
	
	boolean inputsChanged;
	
	/**
	 * Output event list
	 */
	List<Event> outputs = new ArrayList<Event>();	
	
	boolean outputsChanged;
	
	List<Event> outputsSynchronous = new ArrayList<Event>();	
}
