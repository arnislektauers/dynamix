package edu.rtu.dynamix.vdevs.source;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.TupleValue;

public class Step extends AtomicVDEVSModel {

    private long sigma;
    private OutputPort output;
    private boolean aux;
    private long finalValue;
    private long delay;

    public Step(String name) {
        super(name);

        output = addOutputPort("Output");
    }

    @Override
    protected void deltaExtDiscr(long e, List<Event> x) {
    }

    @Override
    protected void deltaIntDiscr() {
        if (!aux) {
            sigma = delay;
            aux = true;
        } else {
            sigma = VDEVS.TIME_INFINITE;
        };
    }

    @Override
    protected void lambdaDiscr(List<Event> y) {
        y.add(new Event(output, new TupleValue(new long[]{finalValue})));
    }

    @Override
    public long taDiscr() {
        return sigma;
    }

    public void setFinalValue(long x) {
        finalValue = x;
    }

    public OutputPort getOutput() {
        return output;
    }

    @Override
    public void setParameter(String name, String value) {
        if (value == null) {
            return;
        }

        if (name.equals("uf")) {
            finalValue = Long.valueOf(value);
        } else {
            System.out.println("Unknown parameter '" + getName() + "' " + name);
        }
    }
}
