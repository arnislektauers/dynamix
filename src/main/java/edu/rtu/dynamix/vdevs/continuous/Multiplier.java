package edu.rtu.dynamix.vdevs.continuous;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.ModelEvent;
import edu.rtu.vdevs.InputPort;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.TupleValue;

public class Multiplier extends AtomicVDEVSModel {

	private InputPort[] inputs;

	private OutputPort output;

	private long sigma = VDEVS.TIME_INFINITE;;

	private long[] u = new long[2];

	private long[] mu = new long[2];

	private long[] pu = new long[2];

	private int nm;

	public Multiplier(String name) {
		super(name);

		inputs = new InputPort[2];
		inputs[0] = addInputPort("Input1");	
		inputs[1] = addInputPort("Input2");	
		output = addOutputPort("Output");
	}

	@Override
	protected void deltaExtDiscr(long e, List<Event> x) {
		for (Event pv : x) {			
			int port = -1;
			if (pv.getPort() == inputs[0]) {
				port = 0;
			} else if (pv.getPort() == inputs[1]) {
				port = 1;
			} 

			if (port != -1) {
				long[] xv = pv.getValue().toTuple();

				u[port] = xv[0];
				if (xv.length > 1) {
					mu[port] = xv[1];
				} else {
					mu[port] = 0;
				}
				if (xv.length > 2) {
					pu[port] = xv[2];
					nm = 1;
				} else {
					pu[port] = 0;
				}

				double timeElapsed = e;
				u[1 - port] += mu[1 - port] * timeElapsed + pu[1 - port] * timeElapsed * timeElapsed;
				mu[1 - port] += 2 * pu[1 - port] * timeElapsed;
				sigma = 0;
			}
		}
	}

	@Override
	protected void deltaIntDiscr() {
		sigma = VDEVS.TIME_INFINITE;
	}

	@Override
	protected void lambdaDiscr(List<Event> y) {
		long y0 = u[0] * u[1];
		long y1 = mu[0] * u[1] + mu[1] * u[0];		
		if (nm == 1) {
			long y2 = u[0] * pu[1] + mu[0] * mu[1] + pu[0] * u[1];
			y.add(new Event(output, new TupleValue(new long[] { y0, y1, y2 })));
		} else {
			y.add(new Event(output, new TupleValue(new long[] { y0, y1 })));
		}	
	}

	@Override
	public long taDiscr() {		
		return sigma;
	}

	public InputPort getInput(int idx) {
		return inputs[idx];
	}

	public OutputPort getOutput() {
		return output;
	}

}
