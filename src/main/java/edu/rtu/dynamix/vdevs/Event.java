package edu.rtu.dynamix.vdevs;

import edu.rtu.vdevs.values.Value;

public class Event {

    protected Port port;
    
    protected Value value;

    public Event() {
    }

    public Event(Port port, Value value) {
        this.port = port;
        this.value = value;
    }

    public Port getPort() {
        return port;
    }

    public Value getValue() {
        return value;
    }
}
