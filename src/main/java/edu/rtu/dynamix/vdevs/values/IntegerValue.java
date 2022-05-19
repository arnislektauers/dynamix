package edu.rtu.dynamix.vdevs.values;

public class IntegerValue extends Value {

    private int value;

    public IntegerValue(int value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.Integer;
    }

    public int getValue() {
        return value;
    }
}
