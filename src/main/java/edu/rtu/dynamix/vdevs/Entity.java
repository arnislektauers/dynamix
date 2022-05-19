package edu.rtu.dynamix.vdevs;

import java.util.ArrayList;
import java.util.List;

public class Entity {

	private double creationTime;

	private double creationSystemTime;

	//private GraphicsObject graphicsObject;

	public String prevModel;

	private List<Entity> subEntities;

	public Entity(Entity... entities) {
		subEntities = new ArrayList<Entity>();
		for (int i = 0; i < entities.length; i++) {
			subEntities.add(entities[i]);
		}
	}

	/*public Entity(GraphicsObject actor, double creationTime) {
		graphicsObject = actor;
		this.creationTime = creationTime;
		creationSystemTime = VDEVS.getCurrentSystemTime();
	}*/
	
	public double getTimeInSystem() {
		double curTime = VDEVS.getCurrentSystemTime();
		if (subEntities != null && subEntities.size() > 0) {
			/*double t = 0;
			for (Entity entity : subEntities) {
				t += curTime - entity.creationSystemTime;
			}
			return t / subEntities.size();*/
			return curTime - subEntities.get(0).creationSystemTime;
		} else {
			return curTime - creationSystemTime;
		}
	}

	/*public GraphicsObject getActor() {
		return graphicsObject;
	}

	public void setActor(GraphicsObject graphicsObject) {
		/*
		 * if (this.graphicsObject != null) {
		 * this.graphicsObject.setParent(null); }
		 */
	/*	this.graphicsObject = graphicsObject;
	}*/

	public double getCreationTime() {
		return creationTime;
	}

	public double getCreationSystemTime() {
		return creationSystemTime;
	}

	public List<Entity> getSubEntities() {
		return subEntities;
	}

	/*public void dispose() {
		if (subEntities != null) {
			for (Entity entity : subEntities) {
				if (entity.getActor() != null) {
					ModelManager.getInstance().getGraphicsScene().disposeGraphicsObject(entity.getActor());
				}
			}
		}
		if (getActor() != null) {
			ModelManager.getInstance().getGraphicsScene().disposeGraphicsObject(getActor());
		}
	}*/
}
