package fft;

public class FFTHelper {
	
	private double mPeakFreq;
	private int mPeakPos;
	private double mSampleRateInHz;
	private int mNumberOfFFTPoints;
	private double mMaxFFTSample;
	private Complex[] mComplexSignal;
	private double[] mAbsSignal;
	
	public FFTHelper(double sampleRate, int numberOfFFTPoints) {
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mComplexSignal = new Complex[numberOfFFTPoints];
		mAbsSignal = new double[numberOfFFTPoints/2];
	}
	
	public double[] calculateFFT(byte[] signal) {			
		double temp;
		Complex[] y;
		
		for(int i = 0; i < mNumberOfFFTPoints; i++) {
			if((2*i+1) < signal.length) {
				temp = (double)((signal[2*i] & 0xFF) | (signal[2*i+1] << 8)) / 32768.0F;
				mComplexSignal[i] = new Complex(temp,0.0);
			} else {
				mComplexSignal[i] = new Complex(0.0,0.0);
			}
		}

		y = FFT.fft(mComplexSignal);
		
		mMaxFFTSample = 0.0;
		mPeakPos = 0;
		for(int i = 0; i < (mNumberOfFFTPoints/2); i++)
		{
			mAbsSignal[i] = Math.sqrt(Math.pow(y[i].re(), 2) + Math.pow(y[i].im(), 2));
			 
			 if(mAbsSignal[i] > mMaxFFTSample)
			 {
				 mMaxFFTSample = mAbsSignal[i];
				 mPeakPos = i;
			 }
		}
		
		return mAbsSignal;
	}
	
	public double getPeakFrequency() {
		mPeakFreq = mPeakPos*(mSampleRateInHz/mNumberOfFFTPoints);
		return mPeakFreq;
	}
	
	public int getPeakFrequencyPosition() {
		return mPeakPos;
	}
	
	public double getPeakFrequency(int[] absSignal) {
		
		int peakPos = 0, max = absSignal[0];
		
		for(int i=1; i < (mNumberOfFFTPoints/2); i++)
		{
			 if(absSignal[i] > max)
			 {
				 max = absSignal[i];
				 peakPos = i;
			 }
		}
		
		return peakPos*(mSampleRateInHz/mNumberOfFFTPoints);
	}
	
	public double getMaxFFTSample() {
		return mMaxFFTSample;
	}
}
