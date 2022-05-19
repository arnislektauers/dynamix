package edu.rtu.dynamix.vdevs.continuous;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.InputPort;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.TupleValue;
import edu.rtu.vdevs.values.Value;

public class WeightedSummator extends AtomicVDEVSModel {

	private long[] k;

	private long[] Xs;
	
	private long[] Mxs;
	
	private long[] Pxs;

	private long sigma = VDEVS.TIME_INFINITE;

	private InputPort[] inputs;

	private OutputPort output;

	public WeightedSummator(String name, int inputCount) {
		super(name);
		k = new long[inputCount];
		Xs = new long[inputCount];
		inputs = new InputPort[inputCount];
		Mxs = new long[inputCount];
		Pxs = new long[inputCount];

		for (int i = 0; i < inputCount; i++) {
			String portName = i > 0 ? "Input" + (i + 1) : "Input";
			inputs[i] = addInputPort(portName);			
		}
		output = addOutputPort("Output");
	}
		
	@Override
	protected void deltaExtDiscr(long e, List<Event> x) {				
		for (int i = 0; i < inputs.length; i++) {	
			int k = VDEVS.findPortIndex(x, inputs[i]);
			if (k == -1) {			
				double timeElapsed = e;
				Xs[i] += Mxs[i] * timeElapsed + Pxs[i] * timeElapsed * timeElapsed;
				Mxs[i] += 2 * Pxs[i] * timeElapsed;
			} else {
				Event pv = x.get(k);
				Value obj = pv.getValue();
				if (obj.isTuple()) {
					long[] o = pv.getValue().toTuple();
					
					Xs[i] = o[0];	
					if (o.length > 1) {
						Mxs[i] = o[1];
					} else {
						Mxs[i] = 0;
					}
					if (o.length > 2) {
						Pxs[i] = o[2];
					} else {
						Pxs[i] = 0;
					}
				} else {
					Xs[i] = obj.toLong();
					Mxs[i] = 0;
					Pxs[i] = 0;
				}
				 
				sigma = 0;
			}
		};			
	}
	
	@Override
	protected void deltaIntDiscr() {
		sigma = VDEVS.TIME_INFINITE;
	}
	
	@Override
	protected void lambdaDiscr(List<Event> y) {
		long s0 = 0;
		long s1 = 0;
		long s2 = 0;
		
		for (int i = 0; i < inputs.length; i++) {
			s0 += Xs[i] * k[i];		
			s1 += Mxs[i] * k[i];
			s2 += Pxs[i] * k[i];	
		};
		
		y.add(new Event(output, new TupleValue(new long[] { s0, s1, s2 })));
	}

	@Override
	public long taDiscr() {
		return sigma;
	}

	public OutputPort getOutput() {
		return output;
	}

	public InputPort getInput(int i) {
		return inputs[i];
	}

	public void setK(long[] kk) {
		for (int i = 0; i < k.length; i++) {
			k[i] = kk[i];
		}
	}
	
	@Override
	public void setParameter(String name, String value) {
		if (value == null) {
			return;
		}
		
		if (name.startsWith("K")) {
			String numStr = name.substring(1);			
			int num = numStr.length() == 0 ? 0 : Integer.valueOf(numStr) - 1;
			k[num] = Long.valueOf(value);
		} else {
			System.out.println("Unknown parameter '" + getName() + "' " + name);
		}
	}
}
