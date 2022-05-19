package edu.rtu.dynamix.vdevs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AtomicVDEVSModel extends DEVSModel {
	
	private Map<SimulationEngine, SimulationData> discreteData = new HashMap<>();
	
	private Map<SimulationEngine, SimulationData> continuousData = new HashMap<>();
	
	SimulationEngine simulationEngine;
	
	protected String phase;
		
	public AtomicVDEVSModel(String name) {
		super(name);		
	}
	
	public SimulationData getDiscreteData(SimulationEngine engine) {
		SimulationData data = discreteData.get(engine);
		if (data == null) {
			data = new SimulationData();
			discreteData.put(engine, data);
		}
		return data;
	}
	
	public SimulationData getContinuousData(SimulationEngine engine) {
		SimulationData data = continuousData.get(engine);
		if (data == null) {
			data = new SimulationData();
			continuousData.put(engine, data);
		}
		return data;
	}
			
	protected abstract void deltaExtDiscr(long e, List<Event> x);
		
	protected void deltaExtCont(long e, List<Event> x) {		
	}
	
	protected abstract void deltaIntDiscr(); // delta_y
	
	protected void deltaIntCont() {		
	}
	
	protected abstract void lambdaDiscr(List<Event> y);
	
	protected void lambdaSynchronous(List<Event> y) {
	}
	
	protected void lambdaResultSynchronous(List<Event> y) {
	}
	
	protected boolean deltaExtSynchronous(Event x) {
		return false;
	}
	
	protected void lambdaCont(List<Event> y) {		
	}
			
	public abstract long taDiscr();
	
	public long taCont() {
		return VDEVS.TIME_INFINITE;
	}
	
	public String getPhase() {
		return phase;
	}
	
	public void setParameter(String name, String value) {	
		if (value != null) {
			System.out.println("Unknown '" + getName() + "' parameter " + name);
		}
	}

	public long getTimeCurrent() {
		return simulationEngine.getTimeCurrent();
	}
	
	public int getSimulationSpeedUp() {
		return simulationEngine.getSpeedUp();
	}

	public SimulationEngine getSimulationEngine() {
		return simulationEngine;
	}
	
	public boolean allowContinuousToDiscreteFlow(Event value) {
		return false;
	}
}
