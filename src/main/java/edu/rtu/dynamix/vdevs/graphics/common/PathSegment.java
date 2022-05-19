package edu.rtu.dynamix.vdevs.graphics.common;

public class PathSegment {
	
	public enum Type {
		Straight,
		Curved
	}
	
	private double length;
	
	private double angle;
	
	private double radius;
	
	private Type type;
	
	public PathSegment(Type type, double length, double angle, double radius) {
		this.type = type;
		this.length = length;
		this.angle = angle;
		this.radius = radius;
	}

	public double getLength() {
		if (type == Type.Straight) {
			return length;
		} else {
			return Math.PI / 180.0 * angle * radius;
		}
	}

	public Type getType() {
		return type;
	}

	public double getAngle() {
		return angle;
	}

	public double getRadius() {
		return radius;
	}
}
