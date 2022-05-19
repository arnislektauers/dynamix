package edu.rtu.dynamix.vdevs.values;

public class ObjectValue<T> extends Value {

    T value;

    public ObjectValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    @Override
    public Type getType() {
        return Type.Object;
    }
    
    @Override
    public int toInteger() {
        if (value instanceof Boolean) {
            return (Boolean)value == true ? 1 : 0;
        } else {
            return super.toInteger();
        }
    }
}
