package edu.rtu.dynamix.vdevs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoupledModel extends DEVSModel {
	
	private static final Logger LOG = LoggerFactory.getLogger(CoupledModel.class);
	
	private List<DEVSModel> subModels = new ArrayList<DEVSModel>();
	
	private List<IModelListener> modelListeners;
	
	public CoupledModel(String name) {
		super(name);		
	}
	
	public boolean addCoupling(String sptName, String dptName) {
		Port sourcePort = findPort(sptName);
		Port destPort = findPort(dptName);
		
		if (sourcePort == null || destPort == null) {			
			return false;
		}
				
		if (sourcePort instanceof InputPort) {
			addCoupling((InputPort)sourcePort, (InputPort)destPort);
		} else if (destPort instanceof InputPort) {
			if (destPort.getParent().getParent() != getParent()) {
				addCoupling((OutputPort)sourcePort, (InputPort)destPort);
			} else {
				getParent().addCoupling((OutputPort)sourcePort, (InputPort)destPort);
			}
		} else {
			addCoupling((OutputPort)sourcePort, (OutputPort)destPort);
		}
		return true;
	}
	
	private Port findPort(String portName) {
		String[] names = portName.split(":");
		String coupledName = names[0];
		
		CoupledModel coupledModel = null;
		if (getName().equals(coupledName)) {
			coupledModel = this;
		} else if (parent != null && parent.getName().equals(coupledName)) {
			coupledModel = parent;
		} else {			
			for (DEVSModel model : subModels) {
				if (model instanceof CoupledModel && model.getName().equals(coupledName)) {
					coupledModel = (CoupledModel)model;
					break;
				}
			}
		}
		if (coupledModel == null) {
			LOG.error("Coupled model for '" + portName + "' not found");
			return null;
		}
		
		DEVSModel devsModel = null;
		if (names.length > 2) {
			String atomicName = names[1];			
			for (DEVSModel model : coupledModel.subModels) {
				if (model instanceof AtomicVDEVSModel && model.getName().equals(atomicName)) {
					devsModel = model;
					break;
				}
			}
			if (devsModel == null) {
				LOG.error("Atomic model for '" + portName + "' not found");
				return null;
			}
		} else {
			devsModel = coupledModel;
		}
		
		String portname = names.length > 2 ? names[2] : names[1];
		Port port = devsModel.getInputPort(portname);
		if (port == null) {
			port = devsModel.getOutputPort(portname);
		}
		if (port == null) {
			LOG.error("Port '" + portName + "' not found");			
		}
		return port;			
	}
	
	/**
	 * External input coupling
	 * @param spt
	 * @param dpt
	 */
	public void addCoupling(InputPort spt, InputPort dpt) {
		if (spt == null || dpt == null || !(spt.getParent() == this && dpt.getParent() != this)) {
			throw new RuntimeException("Assert Failure in External Input Coupling!");			
		}		
		spt.addDestination(dpt);
		dpt.addSource(spt);
	}

	public void addCoupling(OutputPort spt, InputPort dpt) {
		if (spt == null || dpt == null || !(spt.getParent() != this && dpt.getParent() != this)) {
			throw new RuntimeException("Assert Failure in Internal Coupling!");			
		}
		spt.addDestination(dpt);
		dpt.addSource(spt);
	}
	
	/**
	 * External output coupling
	 * @param spt
	 * @param dpt
	 */
	public void addCoupling(OutputPort spt, OutputPort dpt) {
		if (spt == null || dpt == null || !(spt.getParent() != this && dpt.getParent() == this)) {
			throw new RuntimeException("Assert Failure in External Output Coupling!");			
		}
		spt.addDestination(dpt);
		dpt.addSource(spt);
	}
	
	public void addSubModel(DEVSModel model) {
		if (!subModels.contains(model)) {
			subModels.add(model);			
			model.parent = this;
			
			if (modelListeners != null) {
				for (IModelListener listener : modelListeners) {
					listener.modelChanged(this);
				}
			}
		}
	}
	
	public List<DEVSModel> getSubModels() {
		return subModels;
	}	
	
	public void printStatistics() {
		for (DEVSModel subModel : subModels) {
			subModel.printStatistics();
			statistics.add(subModel.getStatistics());
		}
		statistics.print();
	}
	
	public void route(Event x, DEVSModel sourceModel, List<ModelEvent> receivers) {
		for (Port destPort : x.port.getDestinations()) {
			ModelEvent event = new ModelEvent(destPort.getParent(), destPort, x.value);	
			receivers.add(event);
		}
	}
	
	public void getComponents(Set<DEVSModel> modelSet) {
		modelSet.addAll(subModels);
	}
	
	public void addModelListener(IModelListener listener) {
		if (modelListeners == null) {
			modelListeners = new ArrayList<IModelListener>();
		}
		modelListeners.add(listener);
	}
	
	@Override
	public void initialize() {
		for (DEVSModel subModel : subModels) {
			subModel.initialize();
		}
	}
}
