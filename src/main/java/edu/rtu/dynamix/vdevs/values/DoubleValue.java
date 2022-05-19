package edu.rtu.dynamix.vdevs.values;

public class DoubleValue extends Value {

    private double value;

    public DoubleValue(double value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.Double;
    }

    public double value() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
