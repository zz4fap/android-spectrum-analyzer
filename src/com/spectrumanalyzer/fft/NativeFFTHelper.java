package com.spectrumanalyzer.fft;

import android.util.Log;

public class NativeFFTHelper {
	
	public NativeFFTHelper(double sampleRate, int numberOfFFTPoints) throws Exception {
		if(!initFft(sampleRate, numberOfFFTPoints)) {
			throw new FFTException("Impossible to initialize Native FTT.");
		}
	}
	
    /** Native methods, implemented in jni folder */
	public native int getNativeFftVersion();
    private native boolean initFft(double sampleRate, int fftSize);
    public native double[] calculateFFT(byte[] signal, int numberOfReadBytes);
    public native double getPeakFrequency();
    public native int getPeakFrequencyPosition();
    public native double getPeakFrequency(int[] absSignal);
    public native double getMaxFFTSample();
    public native void setSamplingRate(double sampleRate);
    public native void setNumberOfFFTPoints(int numberOfFFTPoints);
    
    public static class FFTException extends Exception {
		private static final long serialVersionUID = 990899067193042344L;

		public FFTException(String msg) {
			super(msg); 
		}
		
		public FFTException(String msg, Throwable t){ 
			super(msg,t); 
		} 
    }
}
