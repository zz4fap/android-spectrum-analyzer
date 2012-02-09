package com.spectrumanalyzer.dsp;

public interface AudioProcessingListener {

    void onDrawableFFTSignalAvailable(double[] absSignal);
}
