package edu.rtu.dynamix.vdevs.agent;

import java.util.ArrayList;
import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.CoupledModel;

public class DefaultContext extends CoupledModel implements IContext {
	
	private List<AtomicVDEVSModel> modelsToRemove = new ArrayList<AtomicVDEVSModel>();
	
	public DefaultContext(String name) {
		super(name);
	}

	@Override
	public IProjection getProjection(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void add(AtomicVDEVSModel model) {
		addSubModel(model);	
	}

	@Override
	public int size() {
		return getSubModels().size();
	}
	
	@Override
	public void remove(AtomicVDEVSModel model) {
		modelsToRemove.add(model);
	}
	
	@Override
	public boolean modelStructureTransition() {
		if (modelsToRemove.size() > 0) {
			getSubModels().removeAll(modelsToRemove);
			modelsToRemove.clear();
		}
		return false;
	}
}
