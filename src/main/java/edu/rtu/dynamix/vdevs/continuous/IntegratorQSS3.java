package edu.rtu.dynamix.vdevs.continuous;

import java.util.List;

import edu.rtu.vdevs.AtomicVDEVSModel;
import edu.rtu.vdevs.InputPort;
import edu.rtu.vdevs.OutputPort;
import edu.rtu.vdevs.Event;
import edu.rtu.vdevs.VDEVS;
import edu.rtu.vdevs.values.TupleValue;

public class IntegratorQSS3 extends AtomicVDEVSModel {

	private static final double pidiv3 = Math.PI / 3;

	private InputPort input;

	private OutputPort output;
	
	private OutputPort opNextTime;

	private long dQ = VDEVS.secondsToNano(0.001);

	private long X;

	private long q;

	private long mq;

	private long pq;

	private long u;

	private long mu;

	private long pu;

	private long sigma;
	
	public IntegratorQSS3(String name) {
		super(name);

		input = addInputPort("Input");
		output = addOutputPort("Output");	
		opNextTime = addOutputPort("NextTime");
	}
	
	@Override
	protected void deltaExtDiscr(long e, List<Event> x) {		
		int i = VDEVS.findPortIndex(x, input);
		Event pv;
		if (i >= 0) {
			pv = x.get(i);
		} else {
			return;
		}
		
		long[] xv = pv.getValue().toTuple();
		long timeElapsed = e;

		X += u * timeElapsed + (mu * timeElapsed * timeElapsed) / 2 + (pu * timeElapsed * timeElapsed * timeElapsed) / 3;
		u = xv[0];  //input value
		mu = xv[1];  //input slope
		pu = xv[2]; //input derivative

		long a = 0, b = 0, c = 0, v = 0, s = VDEVS.TIME_INFINITE;
		if (sigma != 0 ) {
			long w = 0, A, B, i1 = 0, i2 = 0;
			q += mq * timeElapsed + pq * timeElapsed * timeElapsed;
			mq += 2 * pq * timeElapsed;
			a = mu / 2 - pq;
			b= u - mq;
			c = X - q - dQ;
			if (pu != 0) {
				a = 3 * a / pu;
				b = 3 * b / pu;
				c = 3 * c / pu;
				v = b - a * a / 3;
				w = c - b * a / 3 + 2 * a * a  * a / 27;
				i1 = -w / 2;
				i2 = i1 * i1 + v * v * v / 27;
				if (i2 > 0) {
					i2 = (long)Math.sqrt(i2);
					A = i1 + i2;
					B = i1 - i2;
					if (A > 0) {
						A = (long)Math.pow(A, 1.0 / 3);
					} else {
						A = -(long)Math.pow(Math.abs(A), 1.0 / 3);
					}
					if (B > 0) {
						B = (long)Math.pow(B,1.0/3);
					} else {
						B = -(long)Math.pow(Math.abs(B), 1.0 / 3);
					}
					s = A + B - a / 3;
					//esta raiz es la unica real pero puede ser negativa
					if (s < 0) { 
						s = VDEVS.TIME_INFINITE;
					}
				} else if (i2 == 0) {
					long x1, x2;
					A = i1;
					if (A > 0) {
						A = (long)Math.pow(A, 1.0/3);
					} else {
						A = -(long)Math.pow(Math.abs(A), 1.0 / 3);
					}
					x1 = 2 * A - a / 3;
					x2 = -(A + a / 3);
					if (x1 < 0) {
						if (x2 < 0) { 
							s = VDEVS.TIME_INFINITE;
						} else {
							s = x2;
						}
					} else if (x2 < 0) {
						s = x1;
					} else if (x1 < x2) {
						s = x1;
					} else {
						s = x2;
					}    
				} else {
					long y1, y2, y3, arg;
					arg = w * (long)Math.sqrt(27 / (-v)) / (2 * v);
					arg = (long)Math.acos(arg) / 3;
					y1 = 2 * (long)Math.sqrt(-v / 3);                    
					y2 = -y1 * (long)Math.cos(pidiv3 - arg) - a / 3;
					y3 = -y1 * (long)Math.cos(pidiv3 + arg) - a / 3;
					y1 = y1 * (long)Math.cos(arg) - a / 3;
					if (y1 < 0) {
						s = VDEVS.TIME_INFINITE;
					} else if (y3 < 0) {
						s = y1;
					} else if( y2 < 0) {
						s = y3;
					} else { 
						s = y2;
					}
				}
				c = c + 6 * dQ / pu;
				w = c - b * a / 3 + 2 * a  * a * a / 27;
				i1 = -w / 2;
				i2 = i1 * i1 + v * v * v / 27;
				if (i2 > 0) {
					i2 = (long)Math.sqrt(i2);
					A = i1 + i2;
					B = i1 - i2;
					if (A > 0) {
						A = (long)Math.pow(A, 1.0 / 3);
					} else {
						A = -(long)Math.pow(Math.abs(A), 1.0 / 3);
					}
					if (B > 0) {
						B = (long)Math.pow(B, 1.0 / 3);
					} else {
						B = -(long)Math.pow(Math.abs(B), 1.0 / 3);
					}
					sigma = A + B - a / 3;
					//esta raiz es la unica real pero puede ser negativa
					if (s < sigma || sigma < 0) { 
						sigma = s;
					}
				} else if (i2 == 0) {
					long x1, x2;
					A = i1;
					if (A > 0) {
						A = (long)Math.pow(A, 1.0/3);
					} else {
						A = -(long)Math.pow(Math.abs(A), 1.0 / 3);
					}
					x1 = 2 * A - a / 3;
					x2 = -(A + a / 3);
					if (x1 < 0) {
						if (x2 < 0) {
							sigma = VDEVS.TIME_INFINITE;
						} else {
							sigma = x2;
						}
					} else if (x2 < 0) { 
						sigma = x1;
					} else if (x1 < x2) {
						sigma = x1;
					} else {
						sigma = x2;
					}    
					if (s < sigma) { 
						sigma = s;
					}                   
				} else {
					long y1, y2, y3, arg;
					arg = w * (long)Math.sqrt(27 / (-v)) / (2 * v);
					arg = (long)Math.acos(arg) / 3;
					y1 = 2 * (long)Math.sqrt(-v / 3);                    
					y2 = -y1 *(long)Math.cos(pidiv3 - arg) - a / 3;
					y3 = -y1 *(long)Math.cos(pidiv3 + arg) - a / 3;
					y1 = y1 * (long)Math.cos(arg) - a / 3;
					if (y1 < 0) { 
						sigma = VDEVS.TIME_INFINITE;
					} else if (y3 < 0) {
						sigma = y1;
					} else if (y2 < 0) {
						sigma = y3;
					} else { 
						sigma = y2;
					}
					if (s < sigma) { 
						sigma = s;
					}
				}
			} else { 
				long x1, x2;
				if (a != 0) {
					x1 = b * b - 4 * a * c;
					if (x1 < 0) { 
						s = VDEVS.TIME_INFINITE;
					} else { 
						x1 = (long)Math.sqrt(x1);
						x2 = (-b - x1) / 2 / a;
						x1 = (-b + x1) / 2 / a;
						if (x1 < 0) {
							if (x2 < 0) { 
								s = VDEVS.TIME_INFINITE;
							} else {
								s = x2;
							}
						} else if (x2 < 0) {
							s = x1;
						} else if (x1 < x2) {
							s = x1;
						} else {
							s = x2;
						}
					}
					c = c + 2 * dQ;
					x1 = b * b - 4 *  a * c;
					if (x1 < 0) { 
						sigma = VDEVS.TIME_INFINITE;
					} else { 
						x1 = (long)Math.sqrt(x1);
						x2 = (-b - x1) / 2 / a;
						x1 = (-b + x1) / 2 / a;
						if (x1 < 0) {
							if (x2 < 0) {
								sigma = VDEVS.TIME_INFINITE;
							} else {
								sigma = x2;
							}
						} else if (x2 < 0) {
							sigma = x1;
						} else if (x1 < x2) {
							sigma = x1;
						} else {
							sigma = x2;
						}
					}
					if (s < sigma) {
						sigma = s;
					}
				} else {
					if (b != 0) { 
						x1 = -c / b;
						x2 = x1 - 2 * dQ / b;
						if (x1 < 0) {
							x1 = VDEVS.TIME_INFINITE;
						}
						if (x2 < 0) {
							x2 = VDEVS.TIME_INFINITE;
						}
						if (x1 < x2) {
							sigma = x1;
						} else {
							sigma = x2;
						}
					}
				}
			}     
			if ((Math.abs(X - q)) > dQ) {
				sigma = 0;
			}     
		}		
	}

	@Override
	protected void deltaIntDiscr() {		
		X += u * sigma + (mu * sigma * sigma) / 2 + (pu * sigma * sigma * sigma) / 3;
		q = X;
		u += mu * sigma + pu * sigma * sigma;
		mq = u;
		mu += 2 * pu * sigma;
		pq = mu / 2;
		//pu=2*pu;
		if (pu == 0) {
			sigma = VDEVS.TIME_INFINITE;
		} else { 
			sigma = (long)Math.pow(Math.abs(3 * dQ / pu), 1.0 / 3);
		};		
	}
		
	@Override
	public long taDiscr() {
		return sigma;
	}
	
	@Override
	protected void lambdaDiscr(List<Event> y) {
		long yy0 = X + u * sigma + (mu * sigma * sigma) / 2 + (pu * sigma * sigma * sigma) / 3;
		long yy1 = u + mu * sigma + pu * sigma * sigma;
		long yy2 = mu / 2 + pu * sigma;
		
		y.add(new Event(output, new TupleValue(new long[] { yy0, yy1, yy2 })));	
	}
	
	public OutputPort getOutput() {
		return output;
	}

	public InputPort getInput() {
		return input;
	}

	public void setX(long x) {
		X = x;
		q = X;	
	}

	public void setDQ(long dq) {
		dQ = dq;
	}

	public OutputPort getOPNextTime() {
		return opNextTime;
	}
	
	@Override
	public void setParameter(String name, String value) {
		if (value == null) {
			return;
		}
		
		if (name.equals("dq")) {			
			dQ = Long.valueOf(value);
		} else if (name.equals("x0")) {			
			X = Long.valueOf(value);
		} else {
			System.out.println("Unknown parameter '" + getName() + "' " + name);
		}
	}
}
