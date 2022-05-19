package edu.rtu.dynamix.vdevs;

import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimulatorDiscr extends Simulator {
		
	// Sets for computing structure changes
	private Set<DEVSModel> added;
	
	private Set<DEVSModel> removed;
	
	private Set<DEVSModel> next;
	
	private Set<DEVSModel> prev;
	
	private SortedSet<CoupledModel> modelFuncEvalSet;
	
	private SortedSet<DEVSModel> sortedRemoved;
	
	/**
	 * Model transition functions are evaluated from the bottom up
	 */
	private class BottomToTopDepthComparator implements java.util.Comparator<CoupledModel> {
		@Override
		public int compare(CoupledModel m1, CoupledModel m2) {
			int d1 = 0, d2 = 0;
			
			// Compute depth of m1
			CoupledModel model = m1.getParent();
			while (model != null) {
				d1++;
				model = m1.getParent();
			}
			
			// Compute depth of m2
			model = m2.getParent();
			while (model != null) {
				d2++;
				model = m2.getParent();
			}
			
			if (d1 == d2) { // Models at the same depth are sorted by name
				return m1.getName().compareTo(m2.getName());
			} else { // Otherwise, sort by depth
				if (d1 > d2) {
					return 1;
				} else if (d1 < d2) {
					return -1;
				} else {
					return 0;
				}
			}				
		}
	}
		
	public SimulatorDiscr(SimulationEngine engine, DEVSModel model) {
		super(engine, model);
		
		modelFuncEvalSet = new TreeSet<CoupledModel>(new BottomToTopDepthComparator());
	}
			
	@Override
	protected synchronized void schedule(DEVSModel model, long t) {
		if (model instanceof AtomicVDEVSModel) {
			AtomicVDEVSModel atomicModel = (AtomicVDEVSModel)model;
			
			getSimulationData(atomicModel).timeLast = t;
			
			long dt = atomicModel.taDiscr();
			if (dt < VDEVS.TIME_ZERO) {
				throw new RuntimeException("Negative time advance for model: " + model.getName());
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
		return model.getDiscreteData(engine);
	}
	
	/**
	 * Compute the output values of the imminent models. 
	 * This will notify the EventListener objects as the outputs are found. 
	 * This, in effect, implements the output function of the resultant model.
	 */
	@Override
	protected void computeNextOutput() {
		// If the imminent set is up to date, then just return
		if (!imminent.isEmpty()) {
			return;
		}
		
		// Clear the imminent set
		//imminent.clear();
		
		// Get the imminent models from the schedule. This sets the active flags.
		schedule.getImminent(imminent);
		
		// Compute output functions and route the events. The bags of output
		// are held for garbage collection at a later time.
		for (AtomicVDEVSModel model : imminent) {
			SimulationData simulationData = getSimulationData(model);
			
			// If the output for this model has already been computed, then skip it
			if (!simulationData.outputsChanged) {				
				simulationData.outputsChanged = true;
				
				//System.out.println(engine + "lambdaDiscr '" + model.getName() + "'");
				model.lambdaDiscr(simulationData.outputs);
								
				// Send model outputs to their proper destination via the recursive routing function.
				for (Event y : simulationData.outputs) {
					route(model.getParent(), model, y);
				}
			}
			
			computeNextSynchronousOutput(model);
		}
	}
			
	@Override
	protected void execEvent(AtomicVDEVSModel model, boolean internal, long t) {
		SimulationData modelData = getSimulationData(model);
		if (!modelData.inputsChanged) {
			model.deltaIntDiscr();
		} else if (internal) {
			model.deltaIntDiscr();
			model.deltaExtDiscr(VDEVS.TIME_ZERO, modelData.inputs);
		} else {
			model.deltaExtDiscr(t - modelData.timeLast, modelData.inputs);
		}	
		
		/*if (externalEvents % 100000 == 0) {
			System.out.println("== External events: " + externalEvents);
		}*/
		
		// Check for a model transition
		if (model.modelStructureTransition() && model.getParent() != null) {
			modelFuncEvalSet.add(model.getParent());
		}
	}
	
	/**
	 * Set difference operator. Returns the set A-B.
	 */
	private static <T> void setAssignDiff(Set<T> result, Set<T> A, Set<T> B) {
		for (T iter : A) {
			if (!B.contains(iter)) {
				result.add(iter);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see edu.rtu.vdevs.Simulator#processStructureChanges(double)
	 */
	protected void processStructureChanges(long t) {
		if (modelFuncEvalSet.isEmpty()) {
			return;
		}
		
		while (!modelFuncEvalSet.isEmpty()) {
			CoupledModel networkModel = modelFuncEvalSet.first();
			getAllChildren(networkModel, prev);
			if (networkModel.modelStructureTransition() && networkModel.getParent() != null) {
				modelFuncEvalSet.add(networkModel.getParent());
			}
			getAllChildren(networkModel, next);
			modelFuncEvalSet.remove(networkModel);
		}
		// Find the set of models that were added.
		setAssignDiff(added, next, prev);
		
		// Find the set of models that were removed
		setAssignDiff(removed, prev, next);
		
		// Intersection of added and removed is always empty, so no need to look
		// for models in both (an earlier version of the code did this.
		next.clear();
		prev.clear();
				 
		// The model adds are processed first.  This is done so that, if any
		// of the added models are components something that was removed at
		// a higher level, then the models will not have been deleted when
		// trying to schedule them.
		for (DEVSModel iter : added) {
			schedule(iter, t);
		}
		
		// Done with the additions
		added.clear();
			
		// Remove the models that are in the removed set.
		for (DEVSModel iter : removed) {
			cleanUp(iter);
			unscheduleModel(iter);
			sortedRemoved.add(iter); // Add to a sorted remove set for deletion
		}
		
		// Done with the unsorted remove set
		removed.clear();
		
		// Delete the sorted removed models
		while (!sortedRemoved.isEmpty()) {
			// Get the model to erase
			DEVSModel modelToRemove = sortedRemoved.first();
			
			// If this model has children, then remove them from the deletion set.
			// This will avoid double delete problems.
			if (modelToRemove instanceof CoupledModel) {
				getAllChildren((CoupledModel)modelToRemove, prev);
				for (DEVSModel iter : prev) {
					sortedRemoved.remove(iter);
				}
				prev.clear();
			}
			
			// Remove the model
			sortedRemoved.remove(modelToRemove);
			
			// Delete the model and its children
			modelToRemove = null;
		}
		
		// Removed sets should be empty now
		assert (removed.isEmpty());
		assert (sortedRemoved.isEmpty());
	}
}
