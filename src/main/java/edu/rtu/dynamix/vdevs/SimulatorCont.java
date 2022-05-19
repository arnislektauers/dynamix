package edu.rtu.dynamix.vdevs;

import java.util.List;

public class SimulatorCont extends Simulator {

	private SimulatorDiscr simulatorDiscr;
	
	//private int simulatorDiscrInjectedCount;

	public SimulatorCont(SimulationEngine engine, DEVSModel model, SimulatorDiscr simulatorDiscr) {
		super(engine, model);
		this.simulatorDiscr = simulatorDiscr;
	}

	/*@Override
	protected void injectEvent(AtomicVDEVSModel model, Event value) {
		if (model.allowContinuousToDiscreteFlow(value)) {
			simulatorDiscr.injectEvent(model, value);
			simulatorDiscrInjectedCount++;
		} else {
			super.injectEvent(model, value);
		}
	}*/

	@Override
	protected void schedule(DEVSModel model, long t) {
		if (model instanceof AtomicVDEVSModel) {
			AtomicVDEVSModel atomicModel = (AtomicVDEVSModel)model;
			getSimulationData(atomicModel).timeLast = t;
			
			long dt = atomicModel.taCont();
			if (dt < VDEVS.TIME_ZERO) {
				throw new RuntimeException("Negative time advance");
			}
			
			if (t == VDEVS.TIME_INFINITE || dt == VDEVS.TIME_INFINITE) {
				schedule.schedule(atomicModel, VDEVS.TIME_INFINITE);
			} else {
				schedule.schedule(atomicModel, t + dt);
			}	
		} else {
			List<DEVSModel> subModels = ((CoupledModel)model).getSubModels();			
			for (DEVSModel subModel : subModels) {
				schedule(subModel, t);
			}
		}
	}

	@Override
	protected SimulationData getSimulationData(AtomicVDEVSModel model) {
		return model.getContinuousData(engine);
	}

	/**
	 * Compute the output values of the imminent models. 
	 * This will notify the EventListener objects as the outputs are found. 
	 * This, in effect, implements the output function of the resultant model.
	 */
	@Override
	protected void computeNextOutput() {
		//simulatorDiscrInjectedCount = 0;
		
		// If the imminent set is up to date, then just return
		if (!imminent.isEmpty()) {
			return;
		}
		// Clear the imminent set
		//imminent.clear();

		// Get the imminent models from the schedule. This sets the active flags.
		schedule.getImminent(imminent);

		// Compute I/O functions and route events for the imminent models.  Save bags of output
		// values for garbage collection at the end of the cycle.
		for (AtomicVDEVSModel model : imminent) {
			SimulationData simulationData = getSimulationData(model);

			// If the output for this model has already been computed, then skip it
			if (!simulationData.outputsChanged) {
				simulationData.outputsChanged = true;

				model.lambdaCont(simulationData.outputs);

				// Send model outputs to their proper destination via the recursive routing function.
				for (Event y : simulationData.outputs) {
					route(model.getParent(), model, y);
				}
			}
		}
		
		/*if (simulatorDiscrInjectedCount > 0) {
			double t = engine.getTimeCurrent();
			
			simulatorDiscr.executeExternalTransitions(t);
			simulatorDiscr.scheduleActivatedModels(t);
			simulatorDiscr.clearActivatedModelList();
		}*/
	}

	@Override
	protected void execEvent(AtomicVDEVSModel model, boolean internal, long t) {
		SimulationData modelData = getSimulationData(model);
		if (!modelData.inputsChanged) {
			model.deltaIntCont();
		} else if (internal) {
			model.deltaIntCont();
			model.deltaExtCont(VDEVS.TIME_ZERO, modelData.inputs);
		} else {
			model.deltaExtCont(t - modelData.timeLast, modelData.inputs);
		}		
	}
}
