package com.spectrumanalyzer.fft;

public class NativeFFTHelper {
	
	public NativeFFTHelper(double sampleRate, int numberOfFFTPoints) throws Exception {
		if(!initFft(sampleRate, numberOfFFTPoints)) {
			throw new FFTException("Impossible to initialize Native FTT.");
		}
	}
	
    /** Native methods, implemented in jni folder */
    private native boolean initFft(double sampleRate, int fftSize);
    public native double[] calculateFFT(byte[] signal, int numberOfReadBytes);
    
    public class FFTException extends Exception {
		private static final long serialVersionUID = 990899067193042344L;

		public FFTException(String msg) {
			super(msg); 
		}
		
		public FFTException(String msg, Throwable t){ 
			super(msg,t); 
		} 
    }
}
