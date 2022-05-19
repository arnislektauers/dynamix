package edu.rtu.dynamix.vdevs.values;


public class LongValue extends Value {

    private long value;

    public LongValue(long value) {
        this.value = value;
    }

    @Override
    public Type getType() {
        return Type.Long;
    }

    public long value() {
        return value;
    }
}