package edu.rtu.dynamix.vdevs.hybrid;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.ModelEvent;
import edu.rtu.vdevs.InputPort;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.DoubleValue;

public class Quantizer extends AtomicVDEVSModel {

	private InputPort input;

	private OutputPort output;

	private long sigma = VDEVS.TIME_INFINITE;

	private long u;

	private long mu;

	private long pu;

	private long xu;

	private long xl;
	
	private long dq;

	public Quantizer(String name) {
		super(name);	

		input = addInputPort("Input");
		output = addOutputPort("Output");
	}
	
	@Override
	protected void deltaExtDiscr(long e, List<Event> x) {
		int idx = VDEVS.findPortIndex(x, input);		
		Event pv;
		if (idx != -1) {
			pv = x.get(idx);
		} else {
			return;
		}
		
		long[] xv = pv.getValue().toTuple();
		u = xv[0];
		mu = xv[1];
		if (xv.length > 2) {
			pu = xv[2];
		} else {
			pu = 0;
		}
		long s[] = new long[2];
		long c;
		s[0] = s[1] = VDEVS.TIME_INFINITE;
		if ((u >= xu) || (u < xl)){               //CALCULO DE XL Y XU ANTE UN CAMBIO DE NIVEL
			xl = Math.round(Math.floor(u / dq));
			xl = xl * dq;
			xu = xl + dq;
			sigma = 0;
		} else {     //no hay cambio de nivel y se debe calcular el sigma que generara uno
			long sol[] = new long[2]; // correspondientes a los cruce inmediatos con xl y xu resp 
			long a = pu;
			long b = mu;
			for (int i = 0; i < 2; i++){
				if (i == 0) { 
					c = u - xl;
				} else {
					c = u - xu;
				};

				if (a == 0){
					if (b != 0) {
						s[0] = -c / b;
					};
				} else {
					if (b * b >= 4 * a * c){
						s[0] = (long)(-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
						s[1] = (long)(-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
						if (s[0] <= 0) {
							s[0] = VDEVS.TIME_INFINITE;
						};
						if (s[1] <= 0) {
							s[1] = VDEVS.TIME_INFINITE;
						};
					};
				};
				if( (u == xl) && (((b <= 0) && (a < 0)) || (b < 0))){
					s[0] = 0;
					s[1] = VDEVS.TIME_INFINITE;
				}else {
					if (s[0] <= 0) {
						s[0] = VDEVS.TIME_INFINITE;
					};
					if (s[1] <= 0) {
						s[1] = VDEVS.TIME_INFINITE;
					}; 
				};
				if (s[0] < s[1]) { 
					sol[i] = s[0];
				} else {
					sol[i] = s[1];
				};
			};
			if (sol[0] < sol[1]) {
				sigma = sol[0];
			} else {
				sigma = sol[1];
			};			
		}
	}

	@Override
	protected void deltaIntDiscr() {
		if (sigma == 0){
			if ((u == xu) && ((mu > 0) || ((mu == 0) && (pu > 0)))) {
				xl = xu;
				xu = xl + dq;
			};
		} else {
			if ((u + mu * sigma + pu * sigma * sigma) > ((xu + xl) / 2)){
				xu = xu + dq;
				xl = xl + dq;
			} else {
				xu = xu - dq;
				xl = xl - dq;
			};
			u = u + mu * sigma + pu * sigma * sigma;
			mu = mu + 2 * pu * sigma;
			if (u < ((xu + xl) / 2)) { 
				u = xl;
			} else {
				u = xu;
			};     //agregado
		};
		long a, b,c;
		long s[] = new long[2];
		long sol[] = new long[2];
		s[0] = s[1] = VDEVS.TIME_INFINITE;
		sol[0] = sol[1] = VDEVS.TIME_INFINITE;
		a = pu;
		b = mu;
		for (int i = 0; i < 2; i++){
			if (i == 0) {
				c = u - xu;
			} else { 
				c = u - xl; 
			};
			if (a == 0){
				if (b != 0){					//QSS2
					s[0] = -c / b;
				} else {					//QSS
					sigma = VDEVS.TIME_INFINITE;
				};
			} else {    				//solo aplicable a QSS3
				if (b * b > 4 * a * c){
					s[0] = (long)(-b + Math.sqrt(b * b - 4 * a * c)) / (2 * a);
					s[1] = (long)(-b - Math.sqrt(b * b - 4 * a * c)) / (2 * a);
				};
			};
			if ((u == xl) && (((b == 0) && (a < 0)) || ((sigma == 0) && (b < 0)) ) ){
				xl = xl - dq;
				xu = xu - dq;
				s[0] = 0;
				s[1] = VDEVS.TIME_INFINITE;
			}else{
				if (s[0] <= 0) {
					s[0] = VDEVS.TIME_INFINITE;
				};
				if (s[1] <= 0) {
					s[1] = VDEVS.TIME_INFINITE;
				};
			};
			if (s[0] < s[1]) { 
				sol[i] = s[0];
			} else { 
				sol[i] = s[1];
			};
		};
		if (sol[0] < sol[1]) { 
			sigma = sol[0];
		} else {
			sigma = sol[1];
		};
	}
	
	@Override
	protected void lambdaDiscr(List<Event> y) {
		double yy;
		if (sigma == 0) {
			yy = xl;
		} else {
			if ((u + mu * sigma + pu * sigma * sigma) > ((xu + xl) / 2)){
				yy = (xl + dq);
			} else {
				yy = (xl - dq);
			};
		};
		y.add(new Event(output, new DoubleValue(yy)));
	}

	@Override
	public long taDiscr() {		
		return sigma;
	}

	public InputPort getInput() {
		return input;
	}

	public OutputPort getOutput() {
		return output;
	}

	public void setDq(long dq) {
		this.dq = dq;
	}
	
	@Override
	public void setParameter(String name, String value) {
		if (value == null) {
			return;
		}
		
		if (name.equals("dq")) {			
			dq = Long.valueOf(value);
		} else {
			System.out.println("Unknown parameter '" + getName() + "' " + name);
		}
	}
}
