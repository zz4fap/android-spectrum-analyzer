package dsp;

public interface AudioProcessingListener {

    void onDrawableFFTSignalAvailable(double[] absSignal);
}
