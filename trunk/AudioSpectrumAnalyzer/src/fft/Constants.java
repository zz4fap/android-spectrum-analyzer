package fft;

public class Constants {

    public static final double SAMPLING_FREQUENCY = 8000.0;
    
    public static final int NUMBER_OF_FFT_POINTS = 512;

    // 2*Fs because it's a short variable into a array of bytes.
    //Samples - it will always be equal to 1[s] stored into an array of shorts.
    public static final int BUFFER_SIZE = (int) (2 * SAMPLING_FREQUENCY);

    public final static double PI = 4 * Math.atan((double) 1);

    public final static boolean DEBUG_MODE = true;
    
    public final static double FREQ_1KHz = 1000.0;
    
    public final static int MAX_LEVEL = 32767;
    
    public final static int MIN_LEVEL = 0;
    
    public final static int DEFAULT_PROGRESS = 0;
}
