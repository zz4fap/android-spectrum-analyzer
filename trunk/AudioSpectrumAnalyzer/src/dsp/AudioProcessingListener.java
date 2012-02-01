package dsp;

public interface AudioProcessingListener {
	
	void onDrawableFFTSignalAvailable(int[] drawableSignal, double samplingRate, int numberOfFFTPoints);
}
