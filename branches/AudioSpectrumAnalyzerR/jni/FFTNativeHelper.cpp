#include "Fft.hh"
#include "Complex.hh"

class FFTHelper {

	private:
		double mPeakFreq;
		int mPeakPos;
		double mSampleRateInHz;
		int mNumberOfFFTPoints;
		double mMaxFFTSample;
		Complex mComplexSignal[];
		double mAbsSignal[];
	
	public:
	
	FFTHelper(double sampleRate, int numberOfFFTPoints) {
		mSampleRateInHz = sampleRate;
		mNumberOfFFTPoints = numberOfFFTPoints;
		mComplexSignal = new Complex[numberOfFFTPoints];
		mAbsSignal = new double[numberOfFFTPoints/2];
	}
	
	FFTHelper() {
		mSampleRateInHz = Constants.SAMPLING_FREQUENCY;
		mNumberOfFFTPoints = Constants.NUMBER_OF_FFT_POINTS;
		mComplexSignal = new Complex[Constants.NUMBER_OF_FFT_POINTS];
		mAbsSignal = new double[Constants.NUMBER_OF_FFT_POINTS/2];
	}

	double calculateFFT(char signal[], int numberOfReadBytes) {
		double temp;
		Complex y[];
		
		for(int i = 0; i < mNumberOfFFTPoints; i++) {
			if((2*i+1) < numberOfReadBytes) {
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
	
	double getPeakFrequency() {
		mPeakFreq = mPeakPos*(mSampleRateInHz/mNumberOfFFTPoints);
		return mPeakFreq;
	}
	
	int getPeakFrequencyPosition() {
		return mPeakPos;
	}
	
	double getPeakFrequency(int absSignal[]) {
		
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
	
	double getMaxFFTSample() {
		return mMaxFFTSample;
	}
	
	void setSamplingRate(double sampleRate) {
		mSampleRateInHz = sampleRate;
	}
			
	void setNumberOfFFTPoints(int numberOfFFTPoints) {
		mNumberOfFFTPoints = numberOfFFTPoints;
		mComplexSignal = new Complex[numberOfFFTPoints];
		mAbsSignal = new double[numberOfFFTPoints/2];
	}		
}
