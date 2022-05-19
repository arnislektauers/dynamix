package edu.rtu.dynamix.vdevs;

import java.util.ArrayList;
import java.util.List;

import edu.rtu.vdevs.distributions.IStream;
import edu.rtu.vdevs.distributions.MersenneTwister;
import edu.rtu.vdevs.values.MapValue;
import edu.rtu.vdevs.values.Value;

public class ModelManager {

    private static ModelManager instance;
    
    private IStream randomNumberGenerator;
    
    //private IGraphicsScene graphicsScene;
    
    private List<SimulationEngine> simulators = new ArrayList<SimulationEngine>();
    
    private CoupledModel rootModel;
    
    private MapValue parameters = new MapValue();

    private ModelManager() {
        randomNumberGenerator = new MersenneTwister();
    }

    public SimulationEngine addSimulator(Simulator.Mode simulatorMode, String name) {
        SimulationEngine simulator = new SimulationEngine(rootModel, simulatorMode, name);
        simulators.add(simulator);
        return simulator;
    }

    public CoupledModel createRootModel(String modelName) {
        if (rootModel != null) {
            throw new RuntimeException("Root model already exists!");
        }
        rootModel = new CoupledModel(modelName);
        return rootModel;
    }

    public void multiRun(int n) {
        for (SimulationEngine simulator : simulators) {
            simulator.start();
        }
    }

    public void stopSimulation() {
        for (SimulationEngine simulator : simulators) {
            simulator.endRun();
        }
        instance = null;
    }
    
    public void pauseSimulation() {
    	 for (SimulationEngine simulator : simulators) {
             simulator.pause();
         }
    }
    
    public void continueSimulation() {
    	for (SimulationEngine simulator : simulators) {
            simulator.continueSimulation();
        }
    }

    /*public void doSimulationStep() {
        for (SimulationEngine simulator : simulators) {
            simulator.doSimulationStep();
        }
    }*/

    public IStream getRandomNumberGenerator() {
        return randomNumberGenerator;
    }

   /* public IGraphicsScene getGraphicsScene() {
        return graphicsScene;
    }

    public void setGraphicsScene(IGraphicsScene graphicsScene) {
        this.graphicsScene = graphicsScene;
    }*/

    public static ModelManager getInstance() {
        if (instance == null) {
            instance = new ModelManager();
        }
        return instance;
    }

    public CoupledModel getRootModel() {
        return rootModel;
    }

    public void setRootModel(CoupledModel model) {
        if (rootModel != null) {
            throw new RuntimeException("Root model already exists!");
        }
        this.rootModel = model;
    }

    public Value getParameter(String paramName) {
        return parameters.get(paramName);
    }

    public MapValue getParameters() {
        return parameters;
    }

    /*public AssetManager getAssetManager() {
        return graphicsScene.getAssetManager();
    }*/
    public void setSimulationParams(long simulationEndTime, int speedUp, ISimulationListener simulationListener) {
    	for (SimulationEngine simulator : simulators) {
            simulator.setSimulationEndTime(simulationEndTime);
            simulator.setSpeedUp(speedUp);
            simulator.setSimulationListener(simulationListener);
        }
	}
    
    public void setSpeedUp(int speedUp) {
    	for (SimulationEngine simulator : simulators) {
            simulator.setSpeedUp(speedUp);
        }
    }
}
