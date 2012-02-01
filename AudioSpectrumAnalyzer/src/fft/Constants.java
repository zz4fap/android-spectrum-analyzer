package fft;

public class Constants {
	
	//public static final double SAMPLING_FREQUENCY = 44100.0;
	public static final double SAMPLING_FREQUENCY = 8000.0;
	public static final int NUMBER_OF_FFT_POINTS = 512;
	
	//2*Fs because it's a short variable into a array of bytes.
	public static final int BUFFER_SIZE = (int)(2*SAMPLING_FREQUENCY); //Samples - it will always be equal to 1[s] stored into an array of shorts.
	
	public final static double PI = 4*Math.atan((double)1);
	
	public final static boolean DEBUG_MODE = false;
}
