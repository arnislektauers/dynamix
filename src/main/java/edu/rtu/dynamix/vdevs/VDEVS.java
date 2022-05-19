package edu.rtu.dynamix.vdevs;

import java.util.List;

public class VDEVS {

    public static final long TIME_ZERO = 0;
    public static final long TIME_INFINITE = Long.MAX_VALUE;
    private static final int FRAME_RATE = 50;
    public static final float FRAME_INTERVAL = 1.0f / FRAME_RATE;
    public static final boolean USE_ANIMATION = true;
    public static int frameCount;
    public static double fps;
    
    public static final long NANO_PER_SECOND = 1_000_000_000;

    public static long getCurrentSystemTime() {
        return System.nanoTime();
    }
    
    public static long secondsToNano(double secs) {	
    	return (long)(secs * NANO_PER_SECOND);
    }
    
    public static double nanoToSeconds(long nanoSecs) {	
    	return nanoSecs / NANO_PER_SECOND;
    }

    public static int findPortIndex(List<Event> x, Port port) {
        for (int i = 0; i < x.size(); i++) {
            if (x.get(i).getPort() == port) {
                return i;
            }
        }
        return -1;
    }
}
