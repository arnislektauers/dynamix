package edu.rtu.dynamix.vdevs;

import edu.rtu.vdevs.DEVSModel;

public class Statistics {
	
	private DEVSModel model;

	private int discrCount;
	
	private int discrIntCount;
	
	private int contCount;
	
	private int contIntCount;
	
	public Statistics(DEVSModel model) {
		this.model = model;
	}
		
	public void collect(boolean continuous, boolean internal) {
		if (continuous) {
			if (internal) {
				contIntCount++;
			} else {
				contCount++;
			}
		} else {
			if (internal) {
				discrIntCount++;
			} else {
				discrCount++;
			}
		}
	}
	
	public void print() {
		System.out.println(model.getName() + " " + discrCount + " " + discrIntCount + " " + contCount + " " + contIntCount);
	}
	
	public void add(Statistics statistics) {
		contIntCount += statistics.contIntCount;
		contCount += statistics.contCount;
		discrIntCount += statistics.discrIntCount;
		discrCount += statistics.discrCount;
	}
}
