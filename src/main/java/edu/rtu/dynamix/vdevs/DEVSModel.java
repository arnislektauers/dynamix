package edu.rtu.dynamix.vdevs;

import java.util.HashMap;
import java.util.Map;

public abstract class DEVSModel extends NamedObject {

    protected enum ModelType {

        Passive,
        Continuous,
        Discrete,
        Hybrid
    }
    protected CoupledModel parent;
    protected Map<String, InputPort> inputPorts = new HashMap<String, InputPort>();
    protected Map<String, OutputPort> outputPorts = new HashMap<String, OutputPort>();
    protected Statistics statistics;
    protected ModelType type = ModelType.Hybrid;

    public DEVSModel(String name) {
        super(name);
        statistics = new Statistics(this);
    }

    public InputPort addInputPort(String name) {
        assert !inputPorts.containsKey(name);

        InputPort port = new InputPort(name, this);
        inputPorts.put(name, port);
        return port;
    }

    public InputPort getInputPort(String name) {
        return inputPorts.get(name);
    }

    public OutputPort getOutputPort(String name) {
        return outputPorts.get(name);
    }

    public OutputPort addOutputPort(String name) {
        assert !outputPorts.containsKey(name);

        OutputPort port = new OutputPort(name, this);
        outputPorts.put(name, port);
        return port;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void printStatistics() {
        statistics.print();
    }

    protected void sourceAdded(DEVSModel source) {
    }

    public CoupledModel getParent() {
        return parent;
    }

    public void collectStatistics(boolean continuous, boolean internal) {
        statistics.collect(continuous, internal);
    }

    /**
     * This is the structure transition function.  It should return true if a structure change is to occur, 
     * and false otherwise. False is the default return value.
     * @return
     */
    public boolean modelStructureTransition() {
        return false;
    }

    public void initialize() {
    }
}
