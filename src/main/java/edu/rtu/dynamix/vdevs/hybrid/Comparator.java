package edu.rtu.dynamix.vdevs.hybrid;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.InputPort;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.TupleValue;

public class Comparator extends AtomicVDEVSModel {

	private InputPort[] inputs;

	private OutputPort output;

	private static final long LOW_LEVEL = 0;

	private static final long HIGH_LEVEL = 1;

	private long sigma = VDEVS.TIME_INFINITE;

	private long[] u = new long[2];

	private long[] mu = new long[2];

	private long[] pu = new long[2];

	private long tcross = VDEVS.TIME_INFINITE;

	private long sw;

	public Comparator(String name) {
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
				long timeElapsed = e;
				u[1 - port] += mu[1 - port] * timeElapsed + pu[1 - port] * timeElapsed * timeElapsed;
				mu[1 - port] += 2 * pu[1 - port] * timeElapsed;
				u[port] = xv[0];
				mu[port] = xv[1];
				if (xv.length > 2) {
					pu[port] = xv[2];
				} else {
					pu[port] = 0;
				}

				//calculate time to next crossing
				long a, b, c, s1, s2;

				a = pu[0] - pu[1];
				b = mu[0] - mu[1];
				c = u[0] - u[1];
				if (a == 0) {
					if (b == 0){
						s1 = VDEVS.TIME_INFINITE;
						s2 = VDEVS.TIME_INFINITE;
					} else {
						s1 = -c / b;
						s2 = VDEVS.TIME_INFINITE;
					}
				} else {
					s1 = (long)(-b + Math.sqrt(b * b - 4 * a * c)) / 2 / a;
					s2 = (long)(-b - Math.sqrt(b * b - 4 * a * c)) / 2 / a;
				}
				if ((s1 > 0) && ((s1 < s2) || (s2 < 0))) {
					tcross = s1;
				} else {
					if (s2 > 0) {
						tcross = s2;
					} else {
						tcross = VDEVS.TIME_INFINITE;
					}
				}
				if (sigma == timeElapsed) {
					sigma = 0;
				} else {
					if (((u[0] - u[1] > 0) && (sw == 1)) || ((u[0] - u[1] < 0) && (sw == 0))){
						sw = 1 - sw;
						sigma = 0;
					} else {
						sigma = tcross; 
					}
				}
			}
		}
	}

	@Override
	protected void deltaIntDiscr() {
		if (sigma == 0) {
			sigma = tcross;
		} else {
			for (int i = 0; i < 2; i++) {
				u[i] += mu[i] * sigma + pu[i] * sigma * sigma;
				mu[i] += 2 * pu[i] * sigma;
			};
			if ((mu[0] - mu[1]) * (pu[0] - pu[1]) < 0) {
				sigma = (mu[1] - mu[0]) / (pu[0] - pu[1]);
			} else {
				sigma = VDEVS.TIME_INFINITE;
			};
			tcross = sigma;
			if (mu[0] < mu[1]){
				sw = 1;
			} else {
				sw = 0;
			};
		};
	}

	@Override
	protected void lambdaDiscr(List<Event> y) {
		long y0;

		if (sigma == 0) {
			if (sw == 1 ){
				y0 = LOW_LEVEL;
			} else {
				y0 = HIGH_LEVEL;
			};			
		} else {
			if ((1 - sw) == 1) {
				y0 = LOW_LEVEL;
			} else { 
				y0 = HIGH_LEVEL;
			};
		};

		y.add(new Event(output, new TupleValue(new long[] { y0 })));
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

	@Override
	public void setParameter(String name, String value) {
		if (value == null) {
			return;
		}

		if (name.equals("Vl")) {			
		} else if (name.equals("Vh")) {			
		} else {
			System.out.println("Unknown parameter '" + getName() + "' " + name);
		}
	}
}
