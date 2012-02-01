package dsp;

public interface AudioProcessingListener {
	
	void onDrawableFFTSignalAvailable(int[] drawableSignal);
}
