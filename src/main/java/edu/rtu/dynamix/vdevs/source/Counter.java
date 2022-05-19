package edu.rtu.dynamix.vdevs.source;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.IntegerValue;

public class Counter extends AtomicVDEVSModel {

    private long periodOn = VDEVS.secondsToNano(0.7);
    private long periodOff = VDEVS.secondsToNano(0.3);
    private OutputPort output;
    private int counter;
    private int maxCount = 1;
    private long sigma = periodOn;
    
    public Counter(String name) {
        super(name);

        output = addOutputPort("Output");
    }

    @Override
    protected void deltaExtDiscr(long e, List<Event> x) {
    }

    @Override
    protected void deltaIntDiscr() {
        counter++;
        if (counter > maxCount) {
            counter = 0;
            sigma = periodOff;
        } else {
            sigma = periodOn;
        }
    }

    @Override
    protected void lambdaDiscr(List<Event> y) {
        y.add(new Event(output, new IntegerValue(counter)));
    }

    @Override
    public long taDiscr() {
        return sigma;
    }

    public OutputPort getOutput() {
        return output;
    }
}
