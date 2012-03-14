#ifndef FFT_NATIVE_HELPER_HH
#define FFT_NATIVE_HELPER_HH

#include <math.h>
#include "Fft.hh"

class FFTNativeHelper {

	private:
		double mPeakFreq;
		int mPeakPos;
		double mSampleRateInHz;
		int mNumberOfFFTPoints;
		double mMaxFFTSample;
		double *re;
		double *im;
		double *mAbsSignal;
		Fft *mFFT;

	public:
		FFTNativeHelper(double, int);
		~FFTNativeHelper();
		double* calculateFFT(char*, int);
		double getPeakFrequency();
		int getPeakFrequencyPosition();
		double getPeakFrequency(int*);
		double getMaxFFTSample();
		int getFFTSize();
		void setSamplingRate(double);
		void setNumberOfFFTPoints(int);
};

#endif //FFT_NATIVE_HELPER_HH
