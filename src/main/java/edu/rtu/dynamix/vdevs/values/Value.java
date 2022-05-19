package edu.rtu.dynamix.vdevs.values;

public abstract class Value {

    public enum Type {

        Boolean,
        Integer,
        Double,
        Long,
        String,
        Set,
        Map,
        Tuple,
        Table,
        XML,
        Object,
        Matrix
    }

    public abstract Type getType();

    public final boolean isBoolean() {
        return getType() == Type.Boolean;
    }

    public final boolean isDouble() {
        return getType() == Type.Double;
    }
    
    public final boolean isLong() {
        return getType() == Type.Long;
    }

    public final boolean isObject() {
        return getType() == Type.Object;
    }

    public final boolean isInteger() {
        return getType() == Type.Integer;
    }

    public final boolean isTuple() {
        return getType() == Type.Tuple;
    }

    public final boolean toBoolean() {
        if (!isBoolean()) {
            throw new RuntimeException("Value is not a boolean");
        }
        return ((BooleanValue) this).value();
    }

    public final double toDouble() {
        if (!isDouble()) {
            throw new RuntimeException("Value is not a double");
        }
        return ((DoubleValue) this).value();
    }
    
    public final long toLong() {
        if (!isLong()) {
            throw new RuntimeException("Value is not a long");
        }
        return ((LongValue) this).value();
    }

    @SuppressWarnings("unchecked")
    public final <T> T toObject() {
        if (!isObject()) {
            throw new RuntimeException("Value is not a object");
        }
        return ((ObjectValue<T>) this).getValue();
    }

    public int toInteger() {
        if (isInteger()) {
            return ((IntegerValue) this).getValue();
        } else if (isBoolean()) {
            return (((BooleanValue) this).value() ? 1 : 0);
        } else if (isObject()) {
            return (((ObjectValue) this).toInteger());
        } else {
            throw new RuntimeException("Value is not a integer");
        }
    }

    public final long[] toTuple() {
        if (!isTuple()) {
            throw new RuntimeException("Value is not a integer");
        }
        return ((TupleValue) this).value();
    }

    public static double toDouble(Value value) {
        return value != null ? value.toDouble() : 0;
    }

    public static int toInteger(Value value) {
        return value != null ? value.toInteger() : 0;
    }

    public static long[] toTuple(Value value) {
        return value != null ? value.toTuple() : null;
    }
}
