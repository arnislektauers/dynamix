package edu.rtu.dynamix.vdevs.graphics.common;

import java.util.ArrayList;
import java.util.List;

public class Path {
	
	private List<PathSegment> segments = new ArrayList<PathSegment>();

	public double getLength() {
		double length = 0;
		for (PathSegment segment : segments) {
			length += segment.getLength();
		}
		return length;
	}
}
