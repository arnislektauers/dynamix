package edu.rtu.dynamix.vdevs;

public class SimulationEngine extends Thread {

	private enum SimulationState {
		Running,
		Paused,
		Stopped
	}

	private SimulatorDiscr simulatorDiscr;

	private SimulatorCont simulatorCont;

	private static long startTime;

	private long timeCurrent; // laiks nanosekundēs

	private ISimulationListener simulationListener;

	private Simulator.Mode simulatorMode;

	private long prevClock;

	//private double DDiscr, DCont;    
	//private int iDicr, iCont;    
	private double queueDiscr, queueCont, iQueue;

	private SimulationState simulationState = SimulationState.Stopped;

	private DEVSModel model;

	private long simulationEndTime = VDEVS.TIME_INFINITE;

	private int speedUp = 1;

	public SimulationEngine(Simulator.Mode simulatorMode, String name) {
		super();
		this.simulatorMode = simulatorMode;
		setName(name);
		//setPriority(8);
	}

	public SimulationEngine(DEVSModel model, Simulator.Mode simulatorMode, String name) {
		this(simulatorMode, name);
		setModel(model);
	}

	public void setModel(DEVSModel model) {
		this.model = model;
		assignModelEngine(model);
		simulatorDiscr = new SimulatorDiscr(this, model);
		simulatorCont = new SimulatorCont(this, model, simulatorDiscr);
		simulatorDiscr.coupledSimulator = simulatorCont;
	}

	public void start() {
		startTime = VDEVS.getCurrentSystemTime();

		//DDiscr = 0; DCont = 0;
		//iDicr = 0; iCont = 0;
		prevClock = getTimeFromStart();
		queueDiscr = 0;
		queueCont = 0;
		iQueue = 0;

		super.start();
	}

	public void endRun() {
		simulationState = SimulationState.Stopped;
	}

	@Override
	public void run() {
		simulationState = SimulationState.Running;
		startTime = VDEVS.getCurrentSystemTime();

		model.initialize();

		/*final String fileName = "d:/data/time_1.dat";  
        FileWriter  fileWriter = null;
        try {
        fileWriter = new FileWriter(new File(fileName));
        fileWriter.write("T_DISCR,T_CONT\n");
        } catch (IOException e) {
        e.printStackTrace();
        }*/

		//double dt = 0;
		if (simulatorMode == Simulator.Mode.RealTime) {
			runRealTime();
		} else {
			runVirtualTime();
		}

		/*try {
        fileWriter.close();
        } catch (IOException e) {
        e.printStackTrace();
        }*/
	}

	private void executeSimulationStep() {

		long nowClock = getTimeFromStart();           
		timeCurrent += (nowClock - prevClock) * speedUp;
		prevClock = nowClock;

		if (timeCurrent >= simulatorDiscr.nextEventTime()) {
			simulatorDiscr.execNextEvent();

			/*double dDiscr = getCurentTimeSecs() - nowClock;
                DDiscr += dDiscr; iDicr++;*/
		}

		nowClock = getTimeFromStart();
		timeCurrent += (nowClock - prevClock) * speedUp;
		prevClock = nowClock;

		if (timeCurrent >= simulatorCont.nextEventTime()) {
			simulatorCont.execNextEvent();

			/*double dCont = getCurentTimeSecs() - nowClock;
                DCont += dCont; iCont++;*/
		}

		queueDiscr += simulatorDiscr.getSchedule().getSize();
		queueCont += simulatorCont.getSchedule().getSize();
		iQueue++;
	}

	private void runRealTime() {
		while (simulationState != SimulationState.Stopped && timeCurrent < simulationEndTime) {
			if (simulationState == SimulationState.Running) {
				executeSimulationStep();
			}

			/*try {				
            fileWriter.write(dDiscr + "," + dCont + "\n");				
            } catch (IOException e) {				
            e.printStackTrace();
            }*/
			//System.out.println(dt * 1000);
			yield();

			//dt = getCurentTimeSecs() - timeCurrent;			
		}

		if (simulationListener != null) {
			simulationListener.processSimulationEndEvent();
		}

		/*DDiscr = DDiscr / iDicr * 1000;
        DCont = DCont / iCont * 1000;
        System.out.println("Discr: " + DDiscr + " , Cont: " + DCont + " , iDiscr: " + iDicr + " , iCont: " + iCont);*/
		VDEVS.fps = VDEVS.fps / VDEVS.frameCount;
		System.out.println("FPS:" + VDEVS.fps + " , Frames: " + VDEVS.frameCount);
		//System.out.println("Objects Gen:" + Generator.count + " Sink: " + Sink.count);
		System.out.println("queueDiscr: " + (queueDiscr / iQueue) + " , queueCont: " + (queueCont / iQueue));
	}

	private void runVirtualTime() {
		int iDicr = 0;
		int iCont = 0;
		double queueDiscr = 0;
		double queueCont = 0;
		int iQueue = 0;
		while (timeCurrent < 3600 * VDEVS.NANO_PER_SECOND) {
			if (simulationState == SimulationState.Running) {
				//timeCurrent = Math.min(simulatorDiscr.nextEventTime(), simulatorCont.nextEventTime());

				//timeCurrent = getCurentTimeSecs();
				long nt = simulatorDiscr.nextEventTime();
				if (nt < VDEVS.TIME_INFINITE) {
					timeCurrent = nt;
					simulatorDiscr.execNextEvent();
					iDicr++;
				}

				//timeCurrent = getCurentTimeSecs();
				nt = simulatorCont.nextEventTime();
				if (nt < VDEVS.TIME_INFINITE) {
					simulatorCont.execNextEvent();
					iCont++;
				}

				queueDiscr += simulatorDiscr.getSchedule().getSize();
				queueCont += simulatorCont.getSchedule().getSize();
				iQueue++;
			}

			//yield();			
		}

		if (simulationListener != null) {
			simulationListener.processSimulationEndEvent();
		}

		System.out.println("iDiscr: " + iDicr + " , iCont: " + iCont);
		VDEVS.fps = VDEVS.fps / VDEVS.frameCount;
		System.out.println("FPS:" + VDEVS.fps + " , Frames: " + VDEVS.frameCount);
		//System.out.println("Objects Gen:" + Generator.count + " Sink: " + Sink.count);
		System.out.println("queueDiscr: " + (queueDiscr / iQueue) + " , queueCont: " + (queueCont / iQueue));
	}

	public static long getTimeFromStart() {
		return VDEVS.getCurrentSystemTime() - startTime;
	}

	public long getTimeCurrent() {
		return timeCurrent;
	}

	public void forceScheduleDiscr(AtomicVDEVSModel model) {
		simulatorDiscr.schedule(model, simulatorDiscr.nextEventTime());
	}

	public void forceScheduleDiscrASAP(AtomicVDEVSModel model) {
		long t = simulatorDiscr.nextEventTime();
		long t2 = model.getSimulationEngine().getTimeCurrent();
		if (t > t2) {
			t = t2;
		}
		simulatorDiscr.schedule(model, t);
	}

	public void forceScheduleDiscr(AtomicVDEVSModel model, long t) {
		simulatorDiscr.schedule(model, t);
	}

	public synchronized void pause() {
		System.out.println("=== Simulator '" + ""/*getName()*/ + "' paused ===");
		simulationState = SimulationState.Paused;
	}

	public synchronized void continueSimulation() {
		System.out.println("=== Simulator '" + ""/*getName()*/ + "' continued ===");
		
		prevClock = getTimeFromStart();  
		if (simulationState == SimulationState.Paused) {
			simulationState = SimulationState.Running;
		}
	}

	public void setSimulationListener(ISimulationListener simulationListener) {
		this.simulationListener = simulationListener;
	}

	private void assignModelEngine(DEVSModel model) {
		if (model instanceof AtomicVDEVSModel) {
			((AtomicVDEVSModel) model).simulationEngine = this;
		} else {
			for (DEVSModel subModel : ((CoupledModel) model).getSubModels()) {
				assignModelEngine(subModel);
			}
		}
	}

	public void setSimulationEndTime(long simulationEndTime) {
		this.simulationEndTime = simulationEndTime;
	}

	public int getSpeedUp() {
		return speedUp;
	}

	public void setSpeedUp(int speedUp) {
		this.speedUp = speedUp;
	}
}
